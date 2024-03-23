import { CategoryResponse } from "./category";

export interface UploadLog {
  id: string;
  originalFileName: string;
  md5Checksum: string;
  identifiedAs: string;
  identifiedCategories: CategoryResponse[];
}

export async function getAllLogs(): Promise<UploadLog[]> {
  const res = await fetch(`${import.meta.env.VITE_BACKEND}/logs`, {
    method: "GET",
    mode: "cors",
  });

  const data = await res.json();
  if (!res.ok) {
    throw new Error("Error while fetching categories");
  } else {
    return data;
  }
}
