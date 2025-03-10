export interface CollateralDTO {
    id?: number;
    description?: string;
    address?: string;
    collateralTypeName?: string;
    image?: string;
    value?: number;
    remainingValue: number; // BigDecimal will be handled as number in TypeScript
    smeLoanIds: string[];  // List of loan IDs as strings
    cifId:string;
    ownerName: string;


    
    imageFile?: File;
    collateralTypeId?: number;
    currentAccountId?: number;
    name:string;

  }
  