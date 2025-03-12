import { Smeloan } from "./SmeLoan";

export interface SMESchedule {
    id: number;
    dueDate: string;
    totalDays: number;
    termNumber: number;
    principal: number;
    interestAmount: number;
    interestODAmount: number;
    totalRepaidAmount: number;
    status: string;
    gracePeriodEndDate: string;
    fullyPaidDate: string;
    lateFeeStatus: boolean;
    smeLoanId: number;
    smeLoanDTO: Smeloan;
  }
  