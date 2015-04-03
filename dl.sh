#!/bin/bash
LOGS="logs"
RES="res"

scp $META:$LOGS/* $LOGS/
scp $META:$RES/* $RES/
