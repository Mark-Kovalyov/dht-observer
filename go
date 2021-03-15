#!/bin/bash -v

#gradle bootRun --no-daemon

#-Pargs=--spring.main.banner-mode=off,--customArgument=custom

gradle bootJar

# -XX:-UseGCLogFileRotation \
# -XX:NumberOfGClogFiles=1 \
# -XX:GCLogFileSize=8K \

#  -XX:+FlightRecorder -XX:StartFlightRecording=duration=200s,filename=flight.jfr \

rm -f jfr/*
rm -f logs/*
rm -f tmp/*

# -XX:FlightRecorderOptions - deprecated!!!
#  -XX:StartFlightRecording=filename=jfr/flight.jfr,defaultrecording=true,disk=true,repository=./tmp,maxage=1h,settings=default \

mkdir jfr

java \
 -XX:+FlightRecorder \
 -XX:StartFlightRecording=filename=jfr/0001.jfr \
 -Djava.io.tmpdir=./tmp \
 -jar build/libs/dht-observer-0.0.1-SNAPSHOT.jar

