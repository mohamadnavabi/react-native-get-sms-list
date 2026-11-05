# react-native-get-sms-list

A React Native library for reading SMS messages from the device on **Android**.

## Features

- Read SMS messages from the inbox, sent, draft, outbox, failed or queued folders.
- Filter by sender address, message ID, thread ID and date range.
- Sort by date and limit the number of results.
- TypeScript types included.

## Platform support

| Platform | Supported |
| -------- | --------- |
| Android  | ✅        |
| iOS      | ❌        |

### Why not iOS?

iOS gives third-party apps no way to read SMS or iMessage. Messages live in the system Messages app, outside your app's sandbox, and Apple has no public API for reading them. Private APIs that get around this are rejected in App Store review.

What iOS does let you do with messages:

- **Autofill one-time codes**: set `textContentType="oneTimeCode"` (and `autoComplete="sms-otp"`) on a `TextInput` and iOS suggests the code from an incoming SMS above the keyboard. Your app never gets to read the message itself.
- **Filter messages from unknown senders**: a Message Filter app extension (`ILMessageFilterExtension`) can sort incoming SMS from unknown senders into junk or other categories. It runs in its own sandbox and cannot pass message content back to your app.
- **Send messages**: `MFMessageComposeViewController` opens a compose sheet that the user has to send themselves.

On iOS (and any platform other than Android), `readSMS` resolves to an empty array, so shared code doesn't need a platform check to avoid crashing. Wrap the call in `Platform.OS === 'android'` if you need to tell "no messages" apart from "not supported".

## Installation

```sh
npm install react-native-get-sms-list
# OR
yarn add react-native-get-sms-list
```

This package contains native code, so it won't run in Expo Go. Rebuild your app after installing it (for Expo, use a [development build](https://docs.expo.dev/develop/development-builds/introduction/)).

### Android setup

Add the permission to your `android/app/src/main/AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.READ_SMS" />
```

> **Google Play policy:** `READ_SMS` is a restricted permission. Apps distributed on Google Play may only request it if they meet the [SMS and Call Log permissions policy](https://support.google.com/googleplay/android-developer/answer/10208820) (for example, as the default SMS handler or an approved exception) and submit a Permissions Declaration Form.

## Usage

`READ_SMS` is a runtime permission. Request it from JavaScript before calling `readSMS`, otherwise the promise rejects with code `NO_PERMISSION`.

```typescript
import { PermissionsAndroid, Platform } from 'react-native';
import { readSMS } from 'react-native-get-sms-list';

async function fetchMessages() {
  if (Platform.OS !== 'android') {
    return [];
  }

  const granted = await PermissionsAndroid.request(
    PermissionsAndroid.PERMISSIONS.READ_SMS
  );
  if (granted !== PermissionsAndroid.RESULTS.GRANTED) {
    throw new Error('READ_SMS permission denied');
  }

  return readSMS({
    type: 'inbox',
    limit: 10,
    orderBy: 'date desc',
  });
}
```

### Filters

All fields are optional. Calling `readSMS()` with no arguments returns every message.

| Field       | Type                                                               | Description                                            |
| ----------- | ------------------------------------------------------------------ | ------------------------------------------------------ |
| `type`      | `'inbox' \| 'sent' \| 'draft' \| 'outbox' \| 'failed' \| 'queued'` | Message folder. Leave it out to search all folders.    |
| `address`   | `string`                                                           | Exact sender or recipient address.                     |
| `id`        | `string`                                                           | Message ID.                                            |
| `thread_id` | `string`                                                           | Conversation thread ID.                                |
| `minDate`   | `string`                                                           | Only messages after this time (epoch milliseconds).    |
| `maxDate`   | `string`                                                           | Only messages before this time (epoch milliseconds).   |
| `orderBy`   | `'date asc' \| 'date desc'`                                        | Sort order. Defaults to ascending by date.             |
| `limit`     | `number`                                                           | Maximum number of messages to return.                  |

### Result

`readSMS` resolves to an array of:

```typescript
type SMS = {
  id: string;
  address: string;
  body: string;
  date: string; // epoch milliseconds
  thread_id: string;
};
```

### Errors

| Code             | Meaning                                                |
| ---------------- | ------------------------------------------------------ |
| `NO_PERMISSION`  | `READ_SMS` has not been granted.                       |
| `READ_SMS_ERROR` | The query failed. The original exception is attached.  |

## Contributing

Feel free to open issues or submit pull requests to improve this package. See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

MIT
