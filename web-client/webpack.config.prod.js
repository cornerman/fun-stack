const webpack = require('webpack');
const Path = require('path');
const CopyPlugin = require("copy-webpack-plugin");
const CleanPlugin = require("clean-webpack-plugin");

const rootDir = Path.resolve(__dirname, '../../../..');
const outputDir = Path.join(__dirname, "prod");
const htmlDir = Path.join(rootDir, "src/html");
const assetsDir = Path.join(rootDir, "src/assets");

module.exports = require('./scalajs.webpack.config');

module.exports.output.path = outputDir;

module.exports.plugins = [
  new CleanPlugin(outputDir),
  new CopyPlugin([
    { "from": Path.join(htmlDir, 'index.prod.html'), "context": htmlDir, "to": 'index.html', "force": true},
    { "from": assetsDir, "context": assetsDir, "to": '', "force": true }
  ])
];
