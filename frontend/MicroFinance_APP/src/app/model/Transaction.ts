// src/app/model/Transaction.ts
export enum TransactionType {
  DR = 'DR', // Debit
  CR = 'CR'  // Credit
}
  
  export interface Transaction {
    id?: number;
    type: TransactionType;
    amount: number;
    date?: string;
    currentAccountId: number;
  }
  