import type { FilterSMS, SMS } from 'react-native-get-sms-list';
/**
 * Reads SMS messages from the device.
 *
 * Android only. iOS does not expose any public API for reading SMS or
 * iMessage, so on every other platform this resolves to an empty array.
 */
export declare function readSMS(args?: FilterSMS): Promise<SMS[]>;
//# sourceMappingURL=index.d.ts.map