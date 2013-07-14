#!/bin/bash

if [ -f .pid ];
then
    echo It seems the server is already running. Run stop.sh first.
    exit 1
fi

nohup java -Dapp=internal-npm-server -cp server -Dlogback.configurationFile=logback.xml -DLOG_TYPE=FILE com.danielflower.internalnpmserver.App config.properties 1>nohup.out 2>nohupp.err </dev/null &

echo $! > .pid

echo Started server
