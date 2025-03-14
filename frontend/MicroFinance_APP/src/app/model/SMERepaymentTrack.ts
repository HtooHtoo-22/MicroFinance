export interface SMERepaymentTrack {
    paymentDate: string;              // ISO date string (e.g., '2025-03-13')
    paymentAmount: number;            // Numeric amount paid
    paymentPurpose: string;           // e.g., 'Normal Repayment', 'OD Repayment', 'Late Fee'
    term: number;                     // Term number (0 or -1 if not applicable)
    status: string;
    lateDays:number;
    lateFees:number;                   // Status message like 'Paid Successfully', 'OD Occurred', etc.
  }
  