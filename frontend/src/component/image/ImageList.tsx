import { useQuery } from "@tanstack/react-query";
import { getApprovedImages } from "../../service/image";

export default function ImageList() {
  const { data } = useQuery({
    queryKey: ["approvedImages"],
    queryFn: () => getApprovedImages(),
  });

  return (
    <div className="flex flex-col space-y-2 p-2 h-full">
      {!data || data.length == 0 ? (
        <div className="flex justify-center items-center h-full">
          <span className="text-2xl">No images uploaded yet :)</span>
        </div>
      ) : (
        data?.map((image) => (
          <img key={image.id} className="rounded-lg" src={image.url} />
        ))
      )}
    </div>
  );
}
