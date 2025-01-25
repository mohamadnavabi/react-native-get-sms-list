declare module 'react-native-get-sms-list' {
    export type SMS = {
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
  
    function readSMS(filter?: FilterSMS): Promise<SMS[]>;
  
    export { readSMS };
  }