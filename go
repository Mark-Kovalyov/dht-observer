#!/bin/bash -v

mvn clean package
mvn install dependency:copy-dependencies
cp -f target/dht-observer-*.jar ./bin
rsync target/dependency/* ./bin

cd bin

java -jar dht-observer-0.0.1-SNAPSHOT.jar
