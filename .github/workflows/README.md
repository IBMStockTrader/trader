# This folder contains GitHub Actions workflows

Workflows are used to build `trader` service.
For IBM Code Risk Analyzer workflows description and configuration check [README-CRA](README-CRA.md)

This file describes simple workflow that is used to compile app, build image and publish it to registry (Quay).

Workflow is defined in the [java-build-push-git-template.yaml](java-build-push-git-template.yaml) file.

Copy that file to other microservices and change the following settings in the `env` section of the workflow file: 
```
  # EDIT secrets with with your registry, registry path, and apikey
  REGISTRY: quay.io
  REGISTRY_NAMESPACE: gas_stocktrader
  REGISTRY_USER: yourUSER
  REGISTRY_PASSWORD: ${{ secrets.REGISTRY_PASSWORD }}
  IMAGE_NAME: trader

  # GITOPS REGISTRY PARAMS
  GITOPS_REPO: stocktrader-ops/stocktrader-gitops
  GITOPS_DIR: application
  GITOPS_USERNAME: ${{ secrets.GITOPS_USERNAME }}
  GITOPS_TOKEN: ${{ secrets.GITOPS_TOKEN }}
  ```

Gitops registry is where the StockTrader custom resource file is stored.
Workflow updates that file with the new image location and tag.

Additionally you need to configure following Secrets in your application git repo:

```
REGISTRY_PASSWORD - password/token to image registry that allows write
GITOPS_USERNAME - username for gitops repo
GITOPS_TOKEN - token for gitops repo
```

For sample gitops repo and workflow that is used to deploy app check - https://github.com/stocktrader-ops/stocktrader-gitops

## Azure Workflow
We also have an Azure-specific workflow defined in [build-test-push-azure-acr.yml](build-test-push-azure-acr.yml) that:
- Builds the application using Maven
- Pushes the Docker image to Azure Container Registry (ACR)
- Updates the GitOps repository with the new image tag for AKS deployment

### Required Azure Setup
1. Create an Azure Container Registry (ACR)
2. Set up AKS cluster with StockTrader operator
3. Fork or create a GitOps repo for deployment manifests

### Required Azure Secrets
The following secrets need to be configured in your GitHub repository:
```
AZURE_CLIENT_ID - Azure App Registration or Managed Identity client ID
AZURE_TENANT_ID - Azure tenant ID
AZURE_SUBSCRIPTION_ID - Azure subscription ID
GITOPS_TOKEN - GitHub PAT with write access to your GitOps repo
GITOPS_USERNAME - Your GitHub username
ACR_LOGIN_SERVER - Your ACR login server (e.g., <acr-name>.azurecr.io)
```

### Environment Variables
The workflow uses these environment variables:
```
ACR_NAME - Your Azure Container Registry name
GITOPS_REPO - Your GitOps repository (format: username/repo)
GITOPS_DIR - Directory containing deployment manifests
IMAGE_NAME - Name of your Docker image
APP_NAME - Name of your application
IMAGE_TAG - Image tag (defaults to GitHub commit SHA)
```

## Disable other workflows
As this repo contains also CRA related workflows, disable them, or remove completely, if you are only interested in basic functionality.

To disable workflow go to `Actions`, select given workflow, click `...` menu and click `Disable workflow`.
In similar way you can reenable the workflow later.