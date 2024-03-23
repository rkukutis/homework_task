import { NavLink, Outlet } from "react-router-dom";

export default function Layout() {
  return (
    <div className="bg-blue-500 w-screen xl:h-screen flex items-center justify-center">
      <div className="xl:w-4/5 w-full bg-slate-50 rounded-lg p-3 xl:p-5 shadow-lg">
        <nav className="w-full xl:grid xl:grid-cols-2 xl:gap-3 flex flex-col space-y-2 xl:space-y-0">
          <NavLink
            className="bg-white shadow rounded-lg py-3 text-center text-xl"
            to="upload"
          >
            Upload Image
          </NavLink>
          <NavLink
            className=" bg-white rounded-lg py-3 text-center text shadow text-xl"
            to="categories"
          >
            Categories
          </NavLink>
        </nav>
        <main>
          <Outlet />
        </main>
      </div>
    </div>
  );
}
