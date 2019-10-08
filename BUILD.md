<!--
       Copyright 2017 IBM Corp All Rights Reserved

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->

# Quickstart
* Install the trader app using the modified yaml from `manifests/deploy.yaml` which includes a `DeploymentConfig`
* Change the `namespace` field in line 22 of the file to the namespace where your `ImageStream` is deployed to
* modify the git repo and branch to your liking in the `pipeline-template.yaml`
* Select the OCP project you want to use this in
* execute `oc apply -f pipeline-template.yaml`
* execute `oc new-app --template=stocktrader-trader-pipeline`
* you should have a BuildConfig called "stocktrader-trader" in your OCP

# Multi-stage Docker
This project is built and run using a multi-stage Dockerfile.

# Github Webhook
After applying the pipeline file, you have to create a secret. This can be easily done from the CLI for testing purposes:
* execute (repalce <your secret> by a random string) `oc create secret generic github-webhook --from-literal=WebHookSecretKey=<your secret>`
* execute `oc describe bc stocktrader-trader`
* note down the webhook url and replace <secret> with <your secret>
* configure a webhook as described here: https://developer.github.com/webhooks/creating/
