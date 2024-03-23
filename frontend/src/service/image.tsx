import { APIErrorResponse } from "./types";

export interface ImageUploadResponse {
  id: string;
  originalName: string;
  url: string;
}

export async function uploadImage(
  image: File
): Promise<ImageUploadResponse | APIErrorResponse> {
  const formData = new FormData();
  formData.append("image", image);
  const res = await fetch(`${import.meta.env.VITE_BACKEND}/images`, {
    method: "POST",
    body: formData,
  });
  const response = await res.json();
  if (res.status != 200) {
    const error = response as APIErrorResponse;
    switch (error.title) {
      case "PROHIBITED_ITEM_ERROR":
        throw new Error(
          `Item belongs to prohibited categories ${error.prohibitedCategories}`
        );
      case "UNKNOWN_ITEM_ERROR":
        throw new Error("Item is not on any allowed category");
      case "FILE_CONTENT_TYPE_ERROR":
        throw new Error("File content must be of type image");
      case "IMAGE_RECOGNITION_ERROR":
        throw new Error("Could not assign any labels to image");
      case "AWS_ERROR":
        throw new Error("Could not contact AWS services");
      default:
        throw new Error("An unknown error has occured");
    }
  }
  return response;
}

export async function getApprovedImages(): Promise<ImageUploadResponse[]> {
  const res = await fetch(`${import.meta.env.VITE_BACKEND}/images`, {
    method: "GET",
  });
  if (res.status !== 200) {
    throw new Error("Could not fetch images");
  }
  const response = await res.json();
  console.log(response);
  return response;
}
