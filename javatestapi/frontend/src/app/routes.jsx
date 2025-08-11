import { createBrowserRouter } from "react-router-dom";
import Layout from "./Layout";
import ProfilesPage from "../modules/profiles/pages/ProfilesPage";
import LeadsPage from "../modules/leads/pages/LeadsPage";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <Layout />,
    children: [
      { index: true, element: <ProfilesPage /> },
      { path: "profiles", element: <ProfilesPage /> },
      { path: "crm/leads", element: <LeadsPage /> },
      { path: "*", element: <div>Not Found</div> }
    ]
  }
]);
