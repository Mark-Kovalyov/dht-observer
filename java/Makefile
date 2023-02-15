run: build
	java -jar bin/dht-observer.jar

update-h2:
	# JAVA_OPTS=-Dliquibase.changelogFile=<string>
	mvn liquibase:update

update-pg:
	mvn -Dliquibase.changelogFile=liquibase/pg/changelog-pg.yaml liquibase:update

update-cassandra:
	# JAVA_OPTS=-Dliquibase.changelogFile=<string>
	mvn liquibase:update

test:
	mvn clean test

build:
	mvn clean package -DskipTests
	mvn install dependency:copy-dependencies
	mkdir -p bin
	cp -f target/dht-observer.jar ./bin
	rsync -avh target/dependency/* ./bin --delete

benchmark:
	mvn clean package -DskipTests -Dtargetbinary=benchmark
	mvn install dependency:copy-dependencies -Dtargetbinary=benchmark
	cp -f target/benchmark.jar ./benchmark-bin
	rsync -avh target/dependency/* ./benchmark-bin --delete
	cd ./benchmark-bin
	java -jar benchmark.jar -rf latex

jfr:
	cdate=$(date +"%Y-%m-%d-%H-%M-%S")
	java -XX:+FlightRecorder \
		-XX:StartFlightRecording=maxsize=0,maxage=0,disk=true,dumponexit=true,name=dht-observer-$cdate,filename=dht-observer-$cdate.jfr \
		-jar dht-observer.jar $*