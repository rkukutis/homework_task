import { CategoryResponse, removeCategory } from "../../service/category";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import deleteIcon from "../../assets/delete_FILL0_wght400_GRAD0_opsz24.svg";
import toast from "react-hot-toast";

export function CategoryListItem({ category }: { category: CategoryResponse }) {
  const queryClient = useQueryClient();
  const { mutate } = useMutation({
    mutationFn: removeCategory,
    onSuccess: () => {
      toast.success(`Category ${category.name} deleted successfully`);
      queryClient.invalidateQueries();
    },
    onError: (error) => toast.error(error.message),
  });

  function handleDeleteClick() {
    mutate(category.id);
  }

  return (
    <div
      key={category.id}
      className="xl:grid xl:grid-cols-12 flex flex-col px-2 mt-1 py-2 shadow bg-white rounded items-center"
    >
      <h1 className="col-span-3 break-all">{category.name}</h1>
      <h1 className="text-center col-span-2">
        <span
          className={`${
            category.type == "ALLOWED" ? "bg-green-400" : "bg-red-500"
          } text-white px-2 py-1 rounded self`}
        >
          {category.type}
        </span>
      </h1>
      <h1 className="xl:col-span-6 flex flex-wrap xl:space-x-1 overflow-auto">
        {category.aliases.map((alias) => (
          <span key={alias.id} className="px-2 py-1 bg-slate-200 rounded">
            {alias.aliasString}
          </span>
        ))}
      </h1>
      <div className="justify-self-end w-full">
        <button
          onClick={handleDeleteClick}
          className="bg-red-500 hover:bg-red-400 transition p-1 rounded w-full flex justify-center items-center"
        >
          <img src={deleteIcon} alt="delete-category" />
        </button>
      </div>
    </div>
  );
}
