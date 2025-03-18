export interface CurrentAccount {


    id?: number;
    accountId: string;
    maxAmount: number;
    minAmount: number;
    createDate: string; // Note: This matches the API response
    totalBalance: number;
    freezeStatus: boolean;
    cifId: number;
    userName: string;
  }
