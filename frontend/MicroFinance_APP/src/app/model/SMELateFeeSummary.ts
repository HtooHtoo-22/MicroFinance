import { SMESchedule } from "./SMESchedule";

export interface SMELateFeeSummary {
    odSchedules: SMESchedule[];  // List of schedules
    lateDays: number;               // Number of late days
    lateFees: number;               // Total late fees
    outStandingAmount: number;      // Outstanding amount
    holdAmount: number;            // Hold amount
    lateFeeRateBf90: number;
    lateFeeRateAf90: number;
}