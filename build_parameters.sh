#!/bin/bash

maven_goals="install"

image_name="trader"
image_tag="latest"
image_namespace="cicd"

deployment_create="true"
deplyoment_namespace="stocktrader"

kubectl_apply="services.yaml"
