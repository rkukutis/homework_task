import ImageList from "../component/image/ImageList";
import ImageUpload from "../component/image/ImageUpload";
import UploadLogList from "../component/image/UploadLog";

export default function ImagePage() {
  return (
    <div className="xl:grid xl:grid-cols-5 xl:grid-rows-4 xl:grid-flow-col py-3 flex flex-col h-[80vh]">
      <div className="bg-white xl:col-span-2 xl:row-span-4 xl:mr-4 rounded-lg shadow overflow-auto">
        <ImageList />
      </div>
      <div className="p-4 bg-white rounded-lg shadow xl:col-span-3 xl:row-span-1">
        <ImageUpload />
      </div>
      <div className="bg-white rounded-lg p-4 mt-4 col-span-3 xl:row-span-3 shadow overflow-auto">
        <UploadLogList />
      </div>
    </div>
  );
}
