#!/bin/bash

set -e

source ~/CORA_Website/BUILDER_CRED.sh

aws ecr get-login-password --region ca-central-1 | docker login --username AWS --password-stdin 353923860258.dkr.ecr.ca-central-1.amazonaws.com
# Build ringette schedule JAR and copy over
cd ../RingetteSchedule
mvn package
cd ../corawebif
cp ../RingetteSchedule/target/RingetteSchedule-0.0.1-SNAPSHOT-jar-with-dependencies.jar rs_jar
cp ../RingetteSchedule/properties/* testfiles/working/properties

# Build corawebif package to container and push
rm -r target
mvn package
docker build -t corawebif .
docker tag corawebif:latest 353923860258.dkr.ecr.ca-central-1.amazonaws.com/corawebif:latest
docker push 353923860258.dkr.ecr.ca-central-1.amazonaws.com/corawebif:latest
