import React from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import {
  LayoutDashboard,
  Store,
  Package,
  Users,
  UserCheck,
  Receipt,
  Monitor,
  Percent,
  ShoppingCart,
} from 'lucide-react';

const navItems = [
  { to: '/',               icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/pos',            icon: ShoppingCart,    label: 'POS Terminal', section: 'Sales' },
  { to: '/stores',         icon: Store,           label: 'Stores',         section: 'Management' },
  { to: '/items',          icon: Package,         label: 'Items' },
  { to: '/persons',        icon: Users,           label: 'Persons' },
  { to: '/cashiers',       icon: UserCheck,       label: 'Cashiers' },
  { to: '/tax-categories', icon: Percent,         label: 'Tax Categories' },
  { to: '/registers',      icon: Monitor,         label: 'Registers' },
];

export default function Layout() {
  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-header">
          <h1>POS System</h1>
          <p>Management Console</p>
        </div>
        <nav className="sidebar-nav">
          <div className="sidebar-section-label">Main</div>
          {navItems.map(({ to, icon: Icon, label, section }) => (
            <React.Fragment key={to}>
              {section && <div className="sidebar-section-label">{section}</div>}
              <NavLink
                to={to}
                end={to === '/'}
                className={({ isActive }) => isActive ? 'active' : ''}
              >
                <Icon />
                {label}
              </NavLink>
            </React.Fragment>
          ))}
        </nav>
        <div className="sidebar-footer">
          POS Backend v1.0.0
        </div>
      </aside>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
