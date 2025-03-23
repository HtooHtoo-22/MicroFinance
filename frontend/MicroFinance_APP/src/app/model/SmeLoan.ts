import { CollateralDTO } from "./CollateralDTO";

export interface Smeloan {
  id?: number;
  loanId?: string;              // Corresponds to loanId in Java DTO
  loanAmount?: number;          // Corresponds to loanAmount in Java DTO
  interestRate?: number;        // Corresponds to interestRate in Java DTO
  gracePeriod?: number;         // Corresponds to gracePeriod in Java DTO (you might not have it in the TypeScript DTO)
  loanPurpose?: string;         // Corresponds to loanPurpose in Java DTO
  registeredDate?: string;      // Corresponds to registeredDate (String) in Java DTO
  approvedDate: string;         // Corresponds to approvedDate (String) in Java DTO
  status: string;               // Corresponds to status in Java DTO
  documentFee?: number;         // Corresponds to documentFee in Java DTO (if not needed, leave it out)
  serviceCharge?: number;       // Corresponds to serviceCharge in Java DTO (if not needed, leave it out)
  collateralIds?: number[];     // Corresponds to collateralIds (List<Integer>) in Java DTO
  expiredDate?: string;         // Corresponds to expiredDate (LocalDate) in Java DTO, use String here
  duration?: number;            // Corresponds to duration in Java DTO
  principal?: number;           // Corresponds to principal in Java DTO
  entryUserId?: number;         // Corresponds to entryUserId in Java DTO
  entryUserGenerateId?: string; // Corresponds to entryUserGenerateId in Java DTO (you might not have this field)
  entryUserName?: string;       // Corresponds to entryUserName in Java DTO
  approvedUserId?: number;      // Corresponds to approvedUserId in Java DTO
  approvedUserName?: string;    // Corresponds to approvedUserName in Java DTO
  currentAccountId?: number;    // Corresponds to currentAccountId in Java DTO
  currentAccountaccId?: string; // Corresponds to currentAccountaccId in Java DTO
  borrowerName?: string;        // Corresponds to borrowerName in Java DTO
  cifId: number;
  cifIdNumber: string;
  usedCollaterals: CollateralDTO[];
  loanStatus: string;
}
