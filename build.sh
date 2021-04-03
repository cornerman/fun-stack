#/usr/bin/env bash

cd "$(dirname $0)"
build_dir="./build"

sbt clean webClient/fullOptJS::webpack lambdaApi/fullOptJS

rm -rf $build_dir
mkdir -p $build_dir

zip -j $build_dir/lambda-api.zip lambda-api/target/scala-2.13/scalajs-bundler/main/*.js

cp -r web-client/target/scala-2.13/scalajs-bundler/main/prod $build_dir/web-client
