#!/bin/sh

CLASSPATH="./lib/ItemApi-0.0.1-SNAPSHOT.jar:./lib/*"

java -cp $CLASSPATH com.fmyblack.fmyes.bulk.BulkPerformanceTest \ 
/home/uar/black/ItemApi/target/ItemApi-0.0.1-SNAPSHOT/ \
	config/item.properties
