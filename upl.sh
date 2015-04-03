#!/bin/bash

SRCJAR="recommender/target/recommender-1-jar-with-dependencies.jar"

DESTJAR="recommender.jar"
SCRIPT="uloha.sh"

cp $SRCJAR $DESTJAR

scp $DESTJAR $META:
scp $SCRIPT $META:
