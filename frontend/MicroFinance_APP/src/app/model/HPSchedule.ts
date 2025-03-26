import { HPLoan } from "../service/hp-loan.service";

// src/app/models/hp-schedule.ts
export interface HPSchedule {
    id: number;
    dueDate: string;
    totalDays: number;
    termNumber: number;
    installment: number;
    principal: number;
    principalOdAmount: number;
    interestAmount: number;
    interestODAmount: number;
    totalRepaidAmount: number;
    status: string;
    gracePeriodEndDate: string | null;
    fullyPaidDate: string | null;
    lateFeeStatus: boolean;
    hpLoanId: number;
    hpLoanDTO: HPLoan; // Optional, define HPLoan interface if needed
  }