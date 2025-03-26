import { HPSchedule } from "./HPSchedule";


export interface HPLateFeeSummary {
    odSchedules: HPSchedule[];  // List of schedules
    lateDays: number;               // Number of late days
    interestLateFees: number;               // Total late fees
    principalLateFees: number; 
    outStandingAmount: number;      // Outstanding amount
    holdAmount: number;            // Hold amount
    lateFeeRateBf90: number;
    lateFeeRateAf90: number;
}