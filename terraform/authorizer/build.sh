#!/bin/sh

cd "$(dirname $0)"

yarn install
yarn build
zip index.zip index.js
