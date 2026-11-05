"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.readSMS = readSMS;
var _reactNative = require("react-native");
const LINKING_ERROR = `The package 'react-native-get-sms-list' doesn't seem to be linked. Make sure: \n\n` + '- You rebuilt the app after installing the package\n' + '- You are not using Expo Go\n';
const GetSmsList = _reactNative.NativeModules.GetSmsList ? _reactNative.NativeModules.GetSmsList : new Proxy({}, {
  get() {
    throw new Error(LINKING_ERROR);
  }
});

/**
 * Reads SMS messages from the device.
 *
 * Android only. iOS does not expose any public API for reading SMS or
 * iMessage, so on every other platform this resolves to an empty array.
 */
function readSMS(args) {
  if (_reactNative.Platform.OS !== 'android') {
    return Promise.resolve([]);
  }
  return GetSmsList.readSMS(args ?? {});
}
//# sourceMappingURL=index.js.map