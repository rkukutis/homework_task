export interface APIErrorResponse {
  title: string;
  status: number;
  detail: string;
  instance: string;
  prohibitedCategories?: string[];
}
