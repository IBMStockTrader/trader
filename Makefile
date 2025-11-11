# Makefile for building, testing, and pushing trader image
# Supports both Docker and Podman

# Detect container runtime (podman or docker)
CONTAINER_RUNTIME ?= $(shell command -v podman 2>/dev/null || command -v docker 2>/dev/null)
ifeq ($(CONTAINER_RUNTIME),)
$(error Neither podman nor docker found in PATH)
endif

# Image configuration
IMAGE_NAME ?= trader
IMAGE_TAG ?= latest
LOCAL_IMAGE := $(IMAGE_NAME):$(IMAGE_TAG)

# Registry configuration
DEV_REGISTRY ?= stocktraderotel.azurecr.io
PROD_REGISTRY ?= ghrc.io
DEV_IMAGE := $(DEV_REGISTRY)/$(IMAGE_NAME):$(IMAGE_TAG)
PROD_IMAGE := $(PROD_REGISTRY)/$(IMAGE_NAME):$(IMAGE_TAG)

# Test container configuration
TEST_CONTAINER ?= trader-test
TEST_PORT ?= 9080
HEALTH_ENDPOINT ?= /health

# Colors for output
GREEN := \033[0;32m
YELLOW := \033[0;33m
RED := \033[0;31m
NC := \033[0m # No Color

.PHONY: help
help: ## Show this help message
	@echo "$(GREEN)Trader Build and Deploy Makefile$(NC)"
	@echo "Container runtime: $(CONTAINER_RUNTIME)"
	@echo ""
	@echo "$(YELLOW)Image Tags:$(NC)"
	@echo "  Local:       $(LOCAL_IMAGE)"
	@echo "  Development: $(DEV_IMAGE)"
	@echo "  Production:  $(PROD_IMAGE)"
	@echo ""
	@echo "Available targets:"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}' $(MAKEFILE_LIST)

.PHONY: build
build: ## Build the trader application (Maven + Container image)
	@echo "$(GREEN)Building Maven package...$(NC)"
	mvn clean package
	@echo "$(GREEN)Building container image...$(NC)"
	$(CONTAINER_RUNTIME) build -t $(LOCAL_IMAGE) .
	@echo "$(GREEN)Build complete: $(LOCAL_IMAGE)$(NC)"

.PHONY: build-maven
build-maven: ## Build only the Maven package
	@echo "$(GREEN)Running Maven build...$(NC)"
	mvn clean package
	@echo "$(GREEN)Maven build complete$(NC)"

.PHONY: build-image
build-image: ## Build only the container image (requires prior Maven build)
	@echo "$(GREEN)Building container image...$(NC)"
	$(CONTAINER_RUNTIME) build -t $(LOCAL_IMAGE) .
	@echo "$(GREEN)Image built: $(LOCAL_IMAGE)$(NC)"

.PHONY: run
run: ## Run the image locally for testing
	@echo "$(GREEN)Starting trader container...$(NC)"
	@$(CONTAINER_RUNTIME) rm -f $(TEST_CONTAINER) 2>/dev/null || true
	$(CONTAINER_RUNTIME) run -d \
		--name $(TEST_CONTAINER) \
		-p $(TEST_PORT):9080 \
		-e JWT_AUDIENCE=stock-trader \
		-e JWT_ISSUER=http://stock-trader.ibm.com \
		$(LOCAL_IMAGE)
	@echo "$(GREEN)Container started: $(TEST_CONTAINER)$(NC)"
	@echo "$(GREEN)Access at: http://localhost:$(TEST_PORT)/trader$(NC)"

.PHONY: validate
validate: ## Validate that the container is running correctly
	@echo "$(GREEN)Validating container...$(NC)"
	@sleep 5
	@echo "$(YELLOW)Checking container status...$(NC)"
	@$(CONTAINER_RUNTIME) ps --filter name=$(TEST_CONTAINER) --format "table {{.Names}}\t{{.Status}}" || \
		(echo "$(RED)Container not running!$(NC)" && exit 1)
	@echo "$(YELLOW)Testing health endpoint...$(NC)"
	@curl -f -s http://localhost:$(TEST_PORT)$(HEALTH_ENDPOINT) > /dev/null && \
		echo "$(GREEN)✓ Health check passed$(NC)" || \
		(echo "$(RED)✗ Health check failed$(NC)" && exit 1)
	@echo "$(YELLOW)Checking container logs...$(NC)"
	@$(CONTAINER_RUNTIME) logs $(TEST_CONTAINER) 2>&1 | tail -10
	@echo "$(GREEN)Validation complete!$(NC)"

.PHONY: stop
stop: ## Stop the test container
	@echo "$(YELLOW)Stopping test container...$(NC)"
	@$(CONTAINER_RUNTIME) stop $(TEST_CONTAINER) 2>/dev/null || true
	@echo "$(GREEN)Container stopped$(NC)"

.PHONY: clean
clean: stop ## Remove the test container
	@echo "$(YELLOW)Removing test container...$(NC)"
	@$(CONTAINER_RUNTIME) rm -f $(TEST_CONTAINER) 2>/dev/null || true
	@echo "$(GREEN)Cleanup complete$(NC)"

.PHONY: logs
logs: ## Show container logs
	@$(CONTAINER_RUNTIME) logs $(TEST_CONTAINER)

.PHONY: logs-follow
logs-follow: ## Follow container logs
	@$(CONTAINER_RUNTIME) logs -f $(TEST_CONTAINER)

.PHONY: shell
shell: ## Get a shell in the running container
	@$(CONTAINER_RUNTIME) exec -it $(TEST_CONTAINER) /bin/bash

.PHONY: tag-dev
tag-dev: ## Tag image for development registry
	@echo "$(GREEN)Tagging image for development registry...$(NC)"
	$(CONTAINER_RUNTIME) tag $(LOCAL_IMAGE) $(DEV_IMAGE)
	@echo "$(GREEN)Tagged: $(DEV_IMAGE)$(NC)"

.PHONY: tag-prod
tag-prod: ## Tag image for production registry
	@echo "$(GREEN)Tagging image for production registry...$(NC)"
	$(CONTAINER_RUNTIME) tag $(LOCAL_IMAGE) $(PROD_IMAGE)
	@echo "$(GREEN)Tagged: $(PROD_IMAGE)$(NC)"

.PHONY: push-dev
push-dev: tag-dev ## Push image to development registry
	@echo "$(GREEN)Pushing to development registry...$(NC)"
	$(CONTAINER_RUNTIME) push $(DEV_IMAGE)
	@echo "$(GREEN)Pushed: $(DEV_IMAGE)$(NC)"

.PHONY: push-prod
push-prod: tag-prod ## Push image to production registry
	@echo "$(GREEN)Pushing to production registry...$(NC)"
	$(CONTAINER_RUNTIME) push $(PROD_IMAGE)
	@echo "$(GREEN)Pushed: $(PROD_IMAGE)$(NC)"

.PHONY: test
test: run validate stop ## Build, run, and validate the image locally

.PHONY: all
all: build test ## Build and test the image

.PHONY: deploy-dev
deploy-dev: build test push-dev ## Build, test, and push to development registry
	@echo "$(GREEN)Deployment to dev complete!$(NC)"

.PHONY: deploy-prod
deploy-prod: build test push-prod ## Build, test, and push to production registry
	@echo "$(GREEN)Deployment to prod complete!$(NC)"

.DEFAULT_GOAL := help
