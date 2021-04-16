#/usr/bin/env bash

cd "$(dirname $0)"

sbt webClient/fullOptJS::webpack lambdaApi/fullOptJS
