#!/bin/bash -ev

rm -fr bin

mkdir bin

mvn clean package -DskipTests
mvn install dependency:copy-dependencies

cp -f target/dht-observer.jar ./bin
rsync -avh target/dependency/* ./bin --delete

mvn clean package -DskipTests -Dtargetbinary=dhtgetpeers
cp -f target/dht-get-peers.jar ./bin

cd ./bin

java \
 -XX:+HeapDumpOnOutOfMemoryError \
 -XX:HeapDumpPath="dht-observer.hprof" \
 -XX:+UnlockExperimentalVMOptions \
 -XX:+UseShenandoahGC \
 -jar dht-observer.jar
