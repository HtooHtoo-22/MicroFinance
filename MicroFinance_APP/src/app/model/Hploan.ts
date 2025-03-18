export interface HPLoan {
    id?: number;
    loanId?: string;
    loanAmount: number;
    interestRate: number;
    gracePeriod: number;
    registeredDate?: string;
    approvedDate?: string;
    status?: string;
    endDate?: string;
    duration: number;
    entryUserId: number;
    approvedUserId?: number;
    currentAccountId: number;
    productId: number;
    downPaymentRate?: number;
    dealerCommissionRate: number;
  
  
    productName?: string; // NEW
    productValue?: number; // NEW
    currentCode?: string; // NEW
  
  }