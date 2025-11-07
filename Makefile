# Makefile for running trader with OpenTelemetry Collector locally
# Supports both Docker and Podman

# Detect container runtime (podman or docker)
CONTAINER_RUNTIME ?= $(shell command -v podman 2>/dev/null || command -v docker 2>/dev/null)
ifeq ($(CONTAINER_RUNTIME),)
$(error Neither podman nor docker found in PATH)
endif

# Variables
IMAGE_NAME ?= trader
IMAGE_TAG ?= latest
OTEL_IMAGE ?= docker.io/otel/opentelemetry-collector:latest
OTEL_CONFIG ?= otel-collector-config.yaml

# Network
NETWORK_NAME ?= trader-network

# Container names
TRADER_CONTAINER ?= trader
OTEL_CONTAINER ?= otel-collector

# Ports
TRADER_HTTP_PORT ?= 9080
TRADER_HTTPS_PORT ?= 9443
OTEL_GRPC_PORT ?= 4317
OTEL_HTTP_PORT ?= 4318
OTEL_METRICS_PORT ?= 8888

# Colors for output
GREEN := \033[0;32m
YELLOW := \033[0;33m
RED := \033[0;31m
NC := \033[0m # No Color

.PHONY: help
help: ## Show this help message
	@echo "$(GREEN)Trader Local Testing Makefile$(NC)"
	@echo "Container runtime: $(CONTAINER_RUNTIME)"
	@echo ""
	@echo "Available targets:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}' $(MAKEFILE_LIST)

.PHONY: build
build: ## Build the trader application (Maven + Container image)
	@echo "$(GREEN)Building trader application...$(NC)"
	mvn clean package
	$(CONTAINER_RUNTIME) build -t $(IMAGE_NAME):$(IMAGE_TAG) .
	@echo "$(GREEN)Build complete!$(NC)"

.PHONY: build-maven
build-maven: ## Build only the Maven package
	@echo "$(GREEN)Running Maven build...$(NC)"
	mvn clean package

.PHONY: build-image
build-image: ## Build only the container image (requires prior Maven build)
	@echo "$(GREEN)Building container image...$(NC)"
	$(CONTAINER_RUNTIME) build -t $(IMAGE_NAME):$(IMAGE_TAG) .

.PHONY: network-create
network-create: ## Create the custom network
	@echo "$(GREEN)Creating network $(NETWORK_NAME)...$(NC)"
	@$(CONTAINER_RUNTIME) network inspect $(NETWORK_NAME) >/dev/null 2>&1 || \
		$(CONTAINER_RUNTIME) network create $(NETWORK_NAME)
	@echo "$(GREEN)Network $(NETWORK_NAME) ready$(NC)"

.PHONY: network-remove
network-remove: ## Remove the custom network
	@echo "$(YELLOW)Removing network $(NETWORK_NAME)...$(NC)"
	@$(CONTAINER_RUNTIME) network rm $(NETWORK_NAME) 2>/dev/null || true
	@echo "$(GREEN)Network removed$(NC)"

.PHONY: start-otel
start-otel: network-create ## Start OpenTelemetry Collector
	@echo "$(GREEN)Starting OpenTelemetry Collector...$(NC)"
	@$(CONTAINER_RUNTIME) rm -f $(OTEL_CONTAINER) 2>/dev/null || true
	$(CONTAINER_RUNTIME) run -d \
		--name $(OTEL_CONTAINER) \
		--network $(NETWORK_NAME) \
		-p $(OTEL_GRPC_PORT):4317 \
		-p $(OTEL_HTTP_PORT):4318 \
		-p $(OTEL_METRICS_PORT):8888 \
		-v $(PWD)/$(OTEL_CONFIG):/etc/otelcol/config.yaml:Z \
		$(OTEL_IMAGE)
	@echo "$(GREEN)OpenTelemetry Collector started on network $(NETWORK_NAME)$(NC)"
	@echo "$(GREEN)Ports: gRPC=$(OTEL_GRPC_PORT), HTTP=$(OTEL_HTTP_PORT), Metrics=$(OTEL_METRICS_PORT)$(NC)"

.PHONY: start-trader
start-trader: network-create ## Start trader application
	@echo "$(GREEN)Starting trader application...$(NC)"
	@$(CONTAINER_RUNTIME) rm -f $(TRADER_CONTAINER) 2>/dev/null || true
	$(CONTAINER_RUNTIME) run -d \
		--name $(TRADER_CONTAINER) \
		--network $(NETWORK_NAME) \
		-p $(TRADER_HTTP_PORT):9080 \
		-p $(TRADER_HTTPS_PORT):9443 \
		-e OTEL_EXPORTER_OTLP_ENDPOINT=http://$(OTEL_CONTAINER):4317 \
		-e JWT_AUDIENCE=stock \
		-e JWT_ISSUER=trader \
		$(IMAGE_NAME):$(IMAGE_TAG)
	@echo "$(GREEN)Trader started on network $(NETWORK_NAME)$(NC)"
	@echo "$(GREEN)Access the app at: http://localhost:$(TRADER_HTTP_PORT)/trader$(NC)"

.PHONY: start
start: start-otel start-trader ## Start both OpenTelemetry Collector and trader
	@echo "$(GREEN)All services started!$(NC)"
	@echo ""
	@$(MAKE) status

.PHONY: stop
stop: ## Stop all containers
	@echo "$(YELLOW)Stopping containers...$(NC)"
	@$(CONTAINER_RUNTIME) stop $(TRADER_CONTAINER) $(OTEL_CONTAINER) 2>/dev/null || true
	@echo "$(GREEN)Containers stopped$(NC)"

.PHONY: clean
clean: ## Stop and remove all containers
	@echo "$(YELLOW)Cleaning up containers...$(NC)"
	@$(CONTAINER_RUNTIME) rm -f $(TRADER_CONTAINER) $(OTEL_CONTAINER) 2>/dev/null || true
	@echo "$(GREEN)Cleanup complete$(NC)"

.PHONY: clean-all
clean-all: clean network-remove ## Stop and remove all containers and network
	@echo "$(GREEN)Full cleanup complete$(NC)"

.PHONY: restart
restart: clean start ## Restart all services

.PHONY: status
status: ## Show status of containers
	@echo "$(GREEN)Container Status:$(NC)"
	@$(CONTAINER_RUNTIME) ps --filter name=$(OTEL_CONTAINER) --filter name=$(TRADER_CONTAINER) --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
	@echo ""
	@echo "$(GREEN)Network Status:$(NC)"
	@$(CONTAINER_RUNTIME) network inspect $(NETWORK_NAME) --format '{{.Name}}: {{len .Containers}} container(s)' 2>/dev/null || echo "$(YELLOW)Network $(NETWORK_NAME) not found$(NC)"

.PHONY: logs-trader
logs-trader: ## Follow trader logs
	$(CONTAINER_RUNTIME) logs -f $(TRADER_CONTAINER)

.PHONY: logs-otel
logs-otel: ## Follow OpenTelemetry Collector logs
	$(CONTAINER_RUNTIME) logs -f $(OTEL_CONTAINER)

.PHONY: logs
logs: ## Show recent logs from both containers
	@echo "$(GREEN)=== Trader Logs (last 20 lines) ===$(NC)"
	@$(CONTAINER_RUNTIME) logs --tail 20 $(TRADER_CONTAINER) 2>&1 || echo "$(RED)Trader not running$(NC)"
	@echo ""
	@echo "$(GREEN)=== OpenTelemetry Collector Logs (last 20 lines) ===$(NC)"
	@$(CONTAINER_RUNTIME) logs --tail 20 $(OTEL_CONTAINER) 2>&1 || echo "$(RED)OpenTelemetry Collector not running$(NC)"

.PHONY: shell-trader
shell-trader: ## Get a shell in the trader container
	$(CONTAINER_RUNTIME) exec -it $(TRADER_CONTAINER) /bin/bash

.PHONY: shell-otel
shell-otel: ## Get a shell in the OpenTelemetry Collector container
	$(CONTAINER_RUNTIME) exec -it $(OTEL_CONTAINER) /bin/sh

.PHONY: test-metrics
test-metrics: ## Test metrics endpoint
	@echo "$(GREEN)Testing trader metrics endpoint...$(NC)"
	@curl -s http://localhost:$(TRADER_HTTP_PORT)/metrics | head -20 || echo "$(RED)Failed to fetch metrics$(NC)"

.PHONY: test-health
test-health: ## Test health endpoint
	@echo "$(GREEN)Testing trader health endpoint...$(NC)"
	@curl -s http://localhost:$(TRADER_HTTP_PORT)/health || echo "$(RED)Failed to fetch health$(NC)"

.PHONY: test-app
test-app: ## Open trader app in browser
	@echo "$(GREEN)Opening trader app...$(NC)"
	@echo "URL: http://localhost:$(TRADER_HTTP_PORT)/trader"
	@command -v xdg-open >/dev/null && xdg-open "http://localhost:$(TRADER_HTTP_PORT)/trader" 2>/dev/null || \
	 command -v open >/dev/null && open "http://localhost:$(TRADER_HTTP_PORT)/trader" 2>/dev/null || \
	 echo "Please open http://localhost:$(TRADER_HTTP_PORT)/trader in your browser"

.PHONY: test-otel-metrics
test-otel-metrics: ## Test OpenTelemetry Collector metrics endpoint
	@echo "$(GREEN)Testing OpenTelemetry Collector metrics...$(NC)"
	@curl -s http://localhost:$(OTEL_METRICS_PORT)/metrics | head -20 || echo "$(RED)Failed to fetch OTEL metrics$(NC)"

.PHONY: network-inspect
network-inspect: ## Inspect the network and show connected containers
	@echo "$(GREEN)Network Details:$(NC)"
	@$(CONTAINER_RUNTIME) network inspect $(NETWORK_NAME) 2>/dev/null || echo "$(RED)Network $(NETWORK_NAME) not found$(NC)"

.PHONY: test
test: test-health test-metrics ## Run all tests

.PHONY: all
all: build start ## Build and start everything

.PHONY: dev
dev: build restart logs-trader ## Development workflow: build, restart, and follow logs

.DEFAULT_GOAL := help
