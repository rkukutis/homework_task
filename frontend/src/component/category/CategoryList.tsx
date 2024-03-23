import { getAllCategories } from "../../service/category";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import CategoryPagination from "./CategoryPagination";
import { CategoryListItem } from "./CategoryListItem";

export interface PaginationSettings {
  limit: number;
  page: number;
  sortBy: string;
  sortDesc: boolean;
}

const defaultPagination: PaginationSettings = {
  limit: 25,
  page: 0,
  sortBy: "createdAt",
  sortDesc: true,
};

export default function CategoryList() {
  const [pagination, setPagination] = useState(defaultPagination);
  const { data } = useQuery({
    queryKey: [
      "categories",
      pagination.limit,
      pagination.page,
      pagination.sortBy,
      pagination.sortDesc,
    ],
    queryFn: () => getAllCategories(pagination),
  });

  return (
    <div className="w-full">
      <CategoryPagination
        pagination={pagination}
        setPagination={setPagination}
        first={data?.first ?? true}
        last={data?.last ?? true}
        totalPages={data?.totalPages ?? 0}
      />
      <div className="w-full">
        <div className="w-full hidden == xl:grid xl:grid-cols-12 shadow py-2 bg-white px-2 rounded">
          <h1 className="font-semibold col-span-3">Name</h1>
          <h1 className="text-center font-semibold col-span-2">Type</h1>
          <h1 className="font-semibold col-span-7">Keywords</h1>
        </div>
        <div className="h-[65vh] overflow-auto">
          {data?.content.map((category) => (
            <CategoryListItem category={category} />
          ))}
        </div>
      </div>
    </div>
  );
}
