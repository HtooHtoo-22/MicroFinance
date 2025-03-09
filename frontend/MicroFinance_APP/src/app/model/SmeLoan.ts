export interface Smeloan {
    id?: number;
    loanAmount?: number;
    loanPurpose?: string;
    duration?: number;
    interestRate?: number;
    entryUserId?: number;
    approvedUserId?: number;
    currentAccountId?: number;
    collateralIds?: number[];  
  }