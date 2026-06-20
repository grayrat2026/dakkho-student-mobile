const { getDefaultConfig } = require('expo/metro-config');

const config = getDefaultConfig(__dirname);

// Ensure SVG and other assets are properly handled
config.resolver.assetExts = [
  ...config.resolver.assetExts.filter((ext) => ext !== 'svg'),
];

config.resolver.sourceExts = [
  ...config.resolver.sourceExts,
  'svg',
];

module.exports = config;
