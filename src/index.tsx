import { NativeModules, Platform } from 'react-native';
import type { FilterSMS, SMS } from 'react-native-get-sms-list';

const LINKING_ERROR =
  `The package 'react-native-get-sms-list' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
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

export function readSMS(args?: FilterSMS): Promise<SMS[]> {
  return GetSmsList.readSMS(args ?? {});
}
