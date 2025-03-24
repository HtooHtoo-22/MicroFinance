// dealer.model.ts
export interface Dealer {
  id: number;
  businessName: string;
  address: string;
  phone: string;
  email: string;
  registerDate: string;
  status: string;
  currentAccount: {
    accountId: string;
  };
  companyValue: number;
  userPhotoURL?: string; // Add this line
  information: string

}