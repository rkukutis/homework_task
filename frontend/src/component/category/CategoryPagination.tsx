import { ChangeEvent } from "react";
import { PaginationSettings } from "./CategoryList";

interface CategoryPaginationProps {
  pagination: PaginationSettings;
  setPagination: (pagination: PaginationSettings) => void;
  first: boolean;
  last: boolean;
  totalPages: number;
}

export default function CategoryPagination({
  pagination,
  setPagination,
  first,
  last,
  totalPages,
}: CategoryPaginationProps) {
  function handleNextPage() {
    if (last) return;
    setPagination({ ...pagination, page: pagination.page + 1 });
  }
  function handlePreviuosPage() {
    if (first) return;
    setPagination({ ...pagination, page: pagination.page - 1 });
  }

  function handleLimitChange(e: ChangeEvent<HTMLSelectElement>) {
    setPagination({
      ...pagination,
      limit: Number.parseInt(e.target.value),
      page: 0,
    });
  }

  function handleSortByChange(e: ChangeEvent<HTMLSelectElement>) {
    setPagination({
      ...pagination,
      sortBy: e.target.value,
      page: 0,
    });
  }

  function handleSortDescChange(e: ChangeEvent<HTMLSelectElement>) {
    console.log(e.target.value == "true");
    setPagination({
      ...pagination,
      sortDesc: e.target.value == "true",
      page: 0,
    });
  }

  return (
    <div className="xl:grid xl:grid-cols-4 w-full gap-3 bg-white p-3 rounded-lg shadow my-1">
      <section className="flex flex-col space-y-1">
        <label className="flex flex-col space-y-1">Page</label>
        <div className="flex space-x-2">
          <button
            className="bg-blue-500 hover:to-blue-400 transition text-white rounded px-2 py-1 w-full"
            onClick={handlePreviuosPage}
          >
            Previous
          </button>
          <span className=" bg-slate-50 p-1 rounded">
            {pagination.page + 1}/{totalPages}
          </span>
          <button
            className="bg-blue-500 hover:to-blue-400 transition text-white rounded px-2 py-1 w-full"
            onClick={handleNextPage}
          >
            Next
          </button>
        </div>
      </section>
      <section className="flex flex-col space-y-1">
        <label>Results per page</label>
        <select
          className="rounded p-1"
          value={pagination.limit}
          onChange={handleLimitChange}
        >
          <option value={10}>10 categories</option>
          <option value={25}>25 categories</option>
          <option value={50}>50 categories</option>
        </select>
      </section>
      <section className="flex flex-col space-y-1">
        <label>Sort by field</label>
        <select className="rounded p-1" onChange={handleSortByChange}>
          <option value="createdAt">Creation date</option>
          <option value="name">Name</option>
          <option value="type">Type</option>
        </select>
      </section>
      <section className="flex flex-col space-y-1">
        <label>Sort direction</label>
        <select
          value={`${pagination.sortDesc}`}
          className="rounded p-1"
          onChange={handleSortDescChange}
        >
          <option value="true">Descending</option>
          <option value="false">Ascending</option>
        </select>
      </section>
    </div>
  );
}
