#!/bin/bash -ev

mvn clean package -DskipTests
mvn install dependency:copy-dependencies
cp -f target/dht-observer.jar ./bin
rsync -avh target/dependency/* ./bin --delete

#mvn clean package -DskipTests -targetbinary=dht-get-peers
#cp -f target/dht-get-peers.jar ./bin

cd ./bin

java \
 -XX:+UnlockExperimentalVMOptions \
 -XX:+UseShenandoahGC \
 -jar dht-observer.jar
