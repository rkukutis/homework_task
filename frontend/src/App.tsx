import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import Layout from "./page/Layout";
import { Toaster } from "react-hot-toast";
import ImagePage from "./page/ImagePage";
import CategoryPage from "./page/CategoryPage";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: true,
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Toaster
        position="top-center"
        gutter={12}
        containerStyle={{ margin: "8px" }}
        toastOptions={{
          success: { duration: 3000 },
          error: { duration: 5000 },
          style: {
            fontSize: "16px",
            maxWidth: "500px",
            padding: "16px 24px",
          },
        }}
      />
      <BrowserRouter>
        <Routes>
          <Route index element={<Navigate to="/images" />} />
          <Route path="/images" element={<Layout />}>
            <Route path="categories" element={<CategoryPage />} />
            <Route path="upload" element={<ImagePage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  );
}

export default App;
