import CategoryList from "../component/category/CategoryList";
import CreateCategory from "../component/category/CreateCategory";

export default function CategoryPage() {
  return (
    <div className="xl:grid xl:grid-cols-6 mt-2">
      <div className="col-span-4 bg-white xl:mr-4 rounded-lg shadow p-2">
        <div className="bg-slate-50 rounded-lg">
          <CategoryList />
        </div>
      </div>
      <div className="text col-span-2 bg-white rounded-lg shadow">
        <CreateCategory />
      </div>
    </div>
  );
}
