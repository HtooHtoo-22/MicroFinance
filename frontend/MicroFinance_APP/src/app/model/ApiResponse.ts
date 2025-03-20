// src/app/model/ApiResponse.ts
export interface ApiResponse<T> {
  
  httpStatus: string;
  statusCode: number;
  message: string;
  status: string | null;
  data: T;
  token: string | null;
}