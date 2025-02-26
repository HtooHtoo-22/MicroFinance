export enum TransactionType {
    DR = 'Debit',
    CR = 'Credit'
  }
  
  export interface Transaction {
    id?: number;
    type: TransactionType;
    amount: number;
    date?: string;
    currentAccountId: number;
  }
  