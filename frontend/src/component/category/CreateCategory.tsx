import { useMutation, useQueryClient } from "@tanstack/react-query";
import { CategoryRequest, addCategory } from "../../service/category";
import { FormEvent, useState } from "react";
import toast from "react-hot-toast";

export default function CreateCategory() {
  const queryClient = useQueryClient();
  const { mutate } = useMutation({
    mutationFn: (newCategory: CategoryRequest) => addCategory(newCategory),
    onSuccess: () => {
      toast.success("Category created successfully!");
      queryClient.invalidateQueries();
    },
    onError: (error) => {
      toast.error(error.message);
    },
  });
  const [name, setCategoryName] = useState("");
  const [type, setCategoryType] = useState("ALLOWED");
  const [aliasString, setAliasString] = useState("");
  const [aliases, setAliases] = useState<string[]>([]);

  function handleSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    if (name.trim().length == 0) {
      toast.error("Category name is required!");
      return;
    }
    if (aliases.length == 0) {
      toast.error("At least one category alias must be provided");
      return;
    }
    mutate({ name, type, aliases });
  }

  function handleAddAlias() {
    if (aliasString.trim().length == 0) {
      setAliasString("");
      return;
    }
    setAliases([...aliases, aliasString]);
    setAliasString("");
  }

  return (
    <div className="flex flex-col p-4 space-y-4 h-full">
      <form
        className="flex flex-col h-full justify-between"
        onSubmit={(e) => handleSubmit(e)}
      >
        <div className="flex flex-col space-y-5">
          <section className="flex flex-col space-y-2">
            <label>Category name</label>
            <input
              className="shadow px-1 py-2"
              placeholder="name"
              value={name}
              onChange={(e) => setCategoryName(e.target.value)}
              type="text"
            />
          </section>
          <section className="flex flex-col space-y-2">
            <label>Category type</label>
            <select
              className="shadow px-1 py-2"
              value={type}
              onChange={(e) => setCategoryType(e.target.value)}
            >
              <option value="ALLOWED">Allowed item</option>
              <option value="PROHIBITED">Prohibited item</option>
            </select>
          </section>
          <section className="flex flex-col space-y-2">
            <label>Add keywords</label>
            <p className="text-sm text-slate-600">
              Adding relevant keywords increases the probabilty of an image
              matching a category.
            </p>
            <input
              className="shadow px-1 py-2"
              placeholder="keyword"
              value={aliasString}
              onChange={(e) => setAliasString(e.target.value)}
              type="text"
            />
            <div className="flex space-x-2">
              <button
                className="bg-blue-500 text-white px-2 py-1 rounded w-full"
                type="button"
                onClick={handleAddAlias}
              >
                Add keyword
              </button>
              <button
                className="bg-red-500 text-white px-2 py-1 rounded w-full"
                type="button"
                onClick={() => setAliases([])}
              >
                Clear keywords
              </button>
            </div>
            <div className="flex items-center flex-wrap space-x-1">
              {aliases.length > 0 &&
                aliases.map((alias) => (
                  <span className="px-2 py-1 mt-1 bg-slate-200 rounded">
                    {alias}
                  </span>
                ))}
            </div>
          </section>
        </div>
        <input
          className="bg-blue-500 text-white p-2 rounded justify-self-end"
          value="Create category"
          type="submit"
        />
      </form>
    </div>
  );
}
