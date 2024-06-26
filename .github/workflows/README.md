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

## Disable other workflows
As this repo contains also CRA related workflows, disable them, or remove completely, if you are only interested in basic functionality.

To disable workflow go to `Actions`, select given workflow, click `...` menu and click `Disable workflow`.
In similar way you can reenable the workflow later.