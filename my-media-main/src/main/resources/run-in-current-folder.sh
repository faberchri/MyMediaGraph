#!/bin/bash

dryRun=false

# Find Java
if [ "$JAVA_HOME" = "" ] ; then
    JAVA="java -server"
else
    JAVA="$JAVA_HOME/bin/java -server"
fi

# find jar
jarCount=$(find . -name '*.jar' | wc -l)
if  [ "$jarCount" -ne "1" ] ; then
	echo "None or multiple jar files were found in current directory."
	exit -1
fi
JAR=$(find . -name '*.jar')
if  [ "$JAR" = "" ] ; then
	echo "No jar file was found in current directory."
	exit -1
fi
echo "Jar file to run: $JAR"

# start a new java process for each passed recommender (run in parallel)
set -m # Enable Job Control
for recommenderName in "$@"
do
	# Set Java options if contained in trailing '[]' of recommender name
	specialJavaOptions=$(echo $recommenderName | grep -o "\[.*\]$" | sed -e 's/^ *\[//'  -e 's/ *\]$//')
	if [ ! -z "$specialJavaOptions" ] ; then
		JAVA_OPTIONS=$specialJavaOptions
		recommenderName=$(echo $recommenderName | grep -o "^.*\[" | sed -e 's/ *\[$//')
	else
		JAVA_OPTIONS="-Xms1g -Xmx8g -Xss100m"
	fi
	timestamp=$(date +%s)
	logFile="./$recommenderName-$timestamp.log"
	startCommand="$JAVA $JAVA_OPTIONS -jar $JAR $recommenderName ./ training.csv testing.csv attributes.csv"	
	echo "executing: $startCommand 2>&1 > $logFile &"
	if [[ "$dryRun" != "true" ]] ; then
		$startCommand  2>&1 > $logFile &
	fi
	sleep 5
done

# Wait for all parallel jobs to finish
while [ 1 ]; do fg 2> /dev/null; [ $? == 1 ] && break; done

# we are done
echo "All submitted jobs completed. App terminates."

# Return the program's exit code
exit $?