#!/bin/bash -ev

mvn clean package -DskipTests
mvn install dependency:copy-dependencies
cp -f target/dht-observer.jar ./bin
rsync -avh target/dependency/* ./bin --delete

cd ./bin

java -jar dht-observer.jar
