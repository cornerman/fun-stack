const webpack = require('webpack');
const Path = require('path');
const CopyPlugin = require("copy-webpack-plugin");
const CleanPlugin = require("clean-webpack-plugin");

const rootDir = Path.resolve(__dirname, '../../../..');
const devDir = Path.join(__dirname, "dev");
const assetsDir = Path.join(rootDir, "assets");

const staticCopyFiles = [
  Path.join(__dirname, 'webclient-fastopt.js'),
  Path.join(__dirname, 'webclient-fastopt.js.map'),
  Path.join(__dirname, 'webclient-fastopt-loader.js')
];

module.exports = require('./scalajs.webpack.config');

module.exports.plugins = [
  // new CleanPlugin(devDir),
  new CopyPlugin(staticCopyFiles.map(f => { return { "from": f, "context": Path.dirname(f), "to": '', "force": true} }))
];

module.exports.output.path = devDir;

module.exports.devServer = {
  contentBase: [
    devDir,
    assetsDir
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
