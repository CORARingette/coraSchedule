#!/bin/bash

aws ecr get-login-password --region ca-central-1 | docker login --username AWS --password-stdin 353923860258.dkr.ecr.ca-central-1.amazonaws.com
mvn package
docker build -t corawebif .
docker tag corawebif:latest 353923860258.dkr.ecr.ca-central-1.amazonaws.com/corawebif:latest
docker push 353923860258.dkr.ecr.ca-central-1.amazonaws.com/corawebif:latest
