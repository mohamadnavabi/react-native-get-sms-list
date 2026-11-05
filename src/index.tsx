import { NativeModules, Platform } from 'react-native';
import type { FilterSMS, SMS } from 'react-native-get-sms-list';

const LINKING_ERROR =
  `The package 'react-native-get-sms-list' doesn't seem to be linked. Make sure: \n\n` +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const GetSmsList = NativeModules.GetSmsList
  ? NativeModules.GetSmsList
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

/**
 * Reads SMS messages from the device.
 *
 * Android only. iOS does not expose any public API for reading SMS or
 * iMessage, so on every other platform this resolves to an empty array.
 */
export function readSMS(args?: FilterSMS): Promise<SMS[]> {
  if (Platform.OS !== 'android') {
    return Promise.resolve([]);
  }
  return GetSmsList.readSMS(args ?? {});
}
