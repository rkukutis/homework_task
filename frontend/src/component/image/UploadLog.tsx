import { useQuery } from "@tanstack/react-query";
import { UploadLog, getAllLogs } from "../../service/log";

function LogItem({ log }: { log: UploadLog }) {
  return (
    <div className="bg-white p-2 shadow rounded">
      <div className="flex flex-col xl:flex-row space-y-2 xl:space-x-2 xl:space-y-0 xl:w-full">
        <p>
          <b>File name: </b>
          {log.originalFileName}
        </p>
        <p className="border-l-2 pl-2">
          <b>Identified as: </b>
          {log.identifiedAs}
        </p>
        <p className="border-l-2 pl-2 break-all">
          <b>MD5: </b>
          {log.md5Checksum}
        </p>
      </div>
      {log.identifiedCategories && log.identifiedCategories.length > 0 && (
        <div className="flex items-center mt-3 border-t-2 pt-2">
          <b className="hidden xl:inline">Categories:</b>
          <div className="overflow-auto flex">
            {log.identifiedCategories.map((category) => (
              <span
                key={category.id}
                className={`${
                  log.identifiedAs == "ALLOWED" ? "bg-green-500" : "bg-red-500"
                } ml-2 rounded px-2 py-1 text-white
          `}
              >
                {category.name}
              </span>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

export default function UploadLogList() {
  const { data: logs } = useQuery({
    queryKey: ["uploadLogs"],
    queryFn: () => getAllLogs(),
  });

  return (
    <div className="bg-slate-50 rounded-lg overflow-auto space-y-1 p-1 h-full">
      {!logs || logs.length == 0 ? (
        <div className="flex justify-center items-center h-full">
          <span className="text-2xl">No images uploaded yet :)</span>
        </div>
      ) : (
        logs?.map((log) => <LogItem key={log.id} log={log} />)
      )}
    </div>
  );
}
