#!/bin/bash -v

mvn clean package
mvn install dependency:copy-dependencies
cp -f target/dht-observer-0.0.1-SNAPSHOT.jar ./bin
rsync -avh target/dependency/* ./bin --delete

cd bin

java -jar dht-observer-0.0.1-SNAPSHOT.jar -Xjsr305=strict
