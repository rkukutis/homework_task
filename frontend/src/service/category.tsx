import { PaginationSettings } from "../component/category/CategoryList";
import { APIErrorResponse } from "./types";

export interface CategoryAlias {
  id: string;
  aliasString: string;
  createdAt: string;
}

export interface CategoryRequest {
  id?: string;
  name: string;
  type: string;
  aliases: string[];
}

export interface CategoryResponse {
  id: string;
  name: string;
  type: string;
  aliases: CategoryAlias[];
  createdAt: string;
}

export interface CategoriesResponse {
  content: CategoryResponse[];
  first: boolean;
  last: boolean;
  totalPages: number;
  pageNumber: number;
}

export async function getAllCategories(
  pagination: PaginationSettings
): Promise<CategoriesResponse> {
  const res = await fetch(
    `${import.meta.env.VITE_BACKEND}/categories?page=${pagination.page}&limit=${
      pagination.limit
    }&sortBy=${pagination.sortBy}&sortDesc=${pagination.sortDesc}`,
    {
      method: "GET",
      mode: "cors",
    }
  );

  const data = await res.json();
  if (!res.ok) {
    throw new Error("Error has occured while fetching categories");
  } else {
    return data;
  }
}

export async function addCategory(newCategory: CategoryRequest) {
  const res = await fetch(`${import.meta.env.VITE_BACKEND}/categories`, {
    method: "POST",
    mode: "cors",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(newCategory),
  });

  const data = await res.json();
  if (!res.ok) {
    const error = data as APIErrorResponse;
    switch (error.title) {
      case "ENTITY_EXISTS_ERROR":
        throw new Error("A category with this name already exists");
      default:
        throw new Error("An error has occured while creating a category");
    }
  } else {
    return data;
  }
}

export async function removeCategory(categoryId: string) {
  const res = await fetch(
    `${import.meta.env.VITE_BACKEND}/categories/${categoryId}`,
    {
      method: "DELETE",
      mode: "cors",
    }
  );
  console.log(res);
  if (res.status != 204) {
    throw new Error("An error has occured");
  }
}
