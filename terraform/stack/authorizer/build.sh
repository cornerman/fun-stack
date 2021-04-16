#!/bin/sh

cd "$(dirname $0)"

yarn install
yarn build
zip authorizer.zip index.js
