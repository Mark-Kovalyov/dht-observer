#!/bin/bash -ev

./build-all

cd ./bin

java \
 -XX:+HeapDumpOnOutOfMemoryError \
 -XX:HeapDumpPath="dht-observer.hprof" \
 -XX:+UnlockExperimentalVMOptions \
 -XX:+UseG1GC \
 -jar dht-observer.jar
