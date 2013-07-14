#!/bin/bash

if [ ! -f .pid ];
then
    echo No .pid file - the server probably is not running.
    exit 1
fi

kill `cat .pid`
rm .pid

echo Server killed
