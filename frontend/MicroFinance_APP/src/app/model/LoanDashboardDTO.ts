import { Observable } from "rxjs";

export interface LoanDashboardDTO {
    netCashFlow: number;
    totalDisbursements: number;
    totalRepayments: number;
    outstandingPortfolio: number;
    delinquencyRate: number;
    smeDisbursements: number;
    hpDisbursements: number;
    smeRepayments: number;
    hpRepayments: number;
    startDate: string;
    endDate: string;
  }

  export interface LoanDashboardService {
    getLoanMetrics(startDate: string, endDate: string): Observable<LoanDashboardDTO>;
  }