"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.readSMS = readSMS;
var _reactNative = require("react-native");
const LINKING_ERROR = `The package 'react-native-get-sms-list' doesn't seem to be linked. Make sure: \n\n` + _reactNative.Platform.select({
  ios: "- You have run 'pod install'\n",
  default: ''
}) + '- You rebuilt the app after installing the package\n' + '- You are not using Expo Go\n';
const GetSmsList = _reactNative.NativeModules.GetSmsList ? _reactNative.NativeModules.GetSmsList : new Proxy({}, {
  get() {
    throw new Error(LINKING_ERROR);
  }
});
function readSMS(args) {
  return GetSmsList.readSMS(args ?? {});
}
//# sourceMappingURL=index.js.map