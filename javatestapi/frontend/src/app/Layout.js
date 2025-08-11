import { NavLink, Outlet } from "react-router-dom";
export default function Layout() {
  return (
    <div className="app-shell">
      <header className="app-header">
        <nav className="nav">
          <NavLink to="/profiles" className={({isActive}) => isActive ? "active" : ""}>Profiles</NavLink>
          <NavLink to="/crm/leads" className={({isActive}) => isActive ? "active" : ""}>Leads</NavLink>
        </nav>
      </header>
      <main className="app-main">
        <Outlet />
      </main>
    </div>
  );
}
