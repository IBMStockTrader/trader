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

# Pipeline install
* Select the OCP project you want to use this in using `oc project <project>` e.g. `oc project stock-trader`
* Run `oc apply -f pipeline-template.yaml`
* Run `oc new-app --template=stocktrader-trader-pipeline  -p GIT_SOURCE_URL=https://github.com/IBMStockTrader/trader.git -p GIT_SOURCE_REF=master`
* Install the trader app using the yaml from `manifests/deploy-openshift.yaml` which includes a `DeploymentConfig`
  * Change the `namespace` field in line 22 and the namespace in the path of the Docker registry in line 36 of the file to the namespace where your `ImageStream` is deployed to
* You should now have a BuildConfig called `stocktrader-trader` and a ImageStream in your OCP project

# Github Webhook for the pipeline
After applying the pipeline file, you have to create a secret. This can be easily done from the CLI for testing purposes:
* execute (repalce <your secret> by a random string) `oc create secret generic github-webhook --from-literal=WebHookSecretKey=<your secret>`
* execute `oc describe bc stocktrader-trader`
* note down the webhook url and replace <secret> with <your secret>
* configure a webhook as described here: https://developer.github.com/webhooks/creating/
