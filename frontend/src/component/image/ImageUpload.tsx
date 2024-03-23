import { useMutation, useQueryClient } from "@tanstack/react-query";
import { ChangeEvent, useState } from "react";
import { uploadImage } from "../../service/image";
import toast from "react-hot-toast";

export default function ImageUpload() {
  const [file, setFile] = useState<File | null>(null);
  const queryCLient = useQueryClient();

  function handleFileInputChange(e: ChangeEvent<HTMLInputElement>): void {
    if (e.target.files?.length != 1) {
      throw new Error("Only one file must be provided");
    }
    const file: File = e.target.files[0];
    setFile(file);
  }

  const { mutate, isPending } = useMutation({
    mutationFn: (file: File) => uploadImage(file),
    onSuccess: () => {
      toast.success("Image uploaded successfully!");
      setFile(null);
      queryCLient.invalidateQueries();
    },
    onError: (error) => {
      toast.error(error.message);
      setFile(null);
      queryCLient.invalidateQueries();
    },
  });

  function handleSendClick() {
    if (file != null) mutate(file);
  }

  return (
    <div className="flex flex-col h-full justify-between">
      <h3 className="text-xl">Upload an image</h3>
      <p>
        Images of items that belong to permissible categories will be displayed
        on the left of this window. Max image size: 2MB
      </p>
      <input
        className="bg-slate-50 py-3 mb-2 rounded"
        onChange={(e) => handleFileInputChange(e)}
        type="file"
      />
      <button
        disabled={file == null}
        className={`bg-blue-500 hover:to-blue-400 transition py-1 px-2 text-white rounded ${
          file == null ? "hover:cursor-not-allowed bg-blue-400" : ""
        }`}
        onClick={handleSendClick}
      >
        {isPending ? "Uploading..." : "Upload"}
      </button>
    </div>
  );
}
