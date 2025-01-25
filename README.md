# react-native-get-sms-list

A React Native library for reading SMS messages from the device inbox.

## Features
- Fetch SMS messages from the inbox.
- Filter messages by date, sender, thread ID, etc.
- Compatible with both Android and iOS.

## Installation

```sh
npm install react-native-get-sms-list
# OR
yarn add react-native-get-sms-list
```

### Android Setup

Add the following permission to your `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.READ_SMS"/>
<uses-permission android:name="android.permission.RECEIVE_SMS"/>
```

## Usage

### Import the module

```typescript
import { readSMS } from 'react-native-get-sms-list';
```

### Read SMS messages

```typescript
const fetchMessages = async () => {
  try {
    const messages = await readSMS({
      type: 'inbox',
      limit: 10,
      orderBy: 'date desc',
    });
    console.log(messages);
  } catch (error) {
    console.error(error);
  }
};
```

### TypeScript Support
This package includes TypeScript type definitions:

```typescript
type SMS = {
  address: string;
  body: string;
  date: string;
  id: string;
  thread_id: string;
};

export type FilterSMS = {
  type?: 'inbox' | 'sent' | 'draft' | 'outbox' | 'failed' | 'queued';
  id?: string;
  address?: string;
  orderBy?: 'date asc' | 'date desc';
  minDate?: string;
  maxDate?: string;
  limit?: number;
  thread_id?: string;
};
```

## Contributing
Feel free to open issues or submit pull requests to improve this package.

## License
MIT

