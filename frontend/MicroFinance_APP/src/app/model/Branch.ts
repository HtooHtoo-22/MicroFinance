export interface Branch {
  id: number;
  code: string; 
  name: string;
  address: string;
  state: string;
  township: string;
  status: string;
  created_at: Date;
  updated_at: Date;
  activeUserCount?: number;
  activeAccountCount?: number; // Add this new property

}