#!/bin/bash
RES="../res/"

if [ $# -eq 0 ] ; then
	echo "Missing arg!"
	exit 1
fi

pcregrep -M -o "[0-9]* cases\n\[main\][\w :.-]*" $1 | egrep -o "^([0-9]* cases)|(result: [0-9]*\.[-E0-9]*)$" >> $RES$1

echo "---------------------------------------------------" >> $RES$1
