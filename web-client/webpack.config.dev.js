const webpack = require('webpack');
const Path = require('path');
const CopyPlugin = require("copy-webpack-plugin");

const rootDir = Path.resolve(__dirname, '../../../..');
const outputDir = Path.join(__dirname, "dev");
const htmlDir = Path.join(rootDir, "src/html");
const assetsDir = Path.join(rootDir, "src/assets");
const assetsDevDir = Path.join(rootDir, "src/assetsDev");

module.exports = require('./scalajs.webpack.config');

module.exports.output.path = outputDir;

module.exports.plugins = [
  new CopyPlugin([
    { "from": Path.join(__dirname, 'webclient-fastopt.js'), "context": __dirname, "to": '', "force": true},
    { "from": Path.join(__dirname, 'webclient-fastopt.js.map'), "context": __dirname, "to": '', "force": true},
    { "from": Path.join(__dirname, 'webclient-fastopt-loader.js'), "context": __dirname, "to": '', "force": true},
    { "from": Path.join(htmlDir, 'index.dev.html'), "context": htmlDir, "to": 'index.html', "force": true},
  ])
];

module.exports.devServer = {
  contentBase: [
    outputDir,
    assetsDir,
    assetsDevDir
  ],
  allowedHosts: [ ".localhost" ],
  watchContentBase: true,
  watchOptions: {
    ignored: f => f.endsWith(".tmp")
  },
  // writeToDisk: true,
  hot: false,
  hotOnly: false,
  inline: true
};
