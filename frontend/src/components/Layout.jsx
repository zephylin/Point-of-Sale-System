import React from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import {
  LayoutDashboard,
  Store,
  Package,
  Users,
  UserCheck,
  Monitor,
  Percent,
  ShoppingCart,
  BarChart3,
  LogOut,
} from 'lucide-react';

const navItems = [
  { to: '/',               icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/pos',            icon: ShoppingCart,    label: 'POS Terminal', section: 'Sales' },
  { to: '/reports',        icon: BarChart3,       label: 'Reports' },
  { to: '/stores',         icon: Store,           label: 'Stores',         section: 'Management' },
  { to: '/items',          icon: Package,         label: 'Items' },
  { to: '/persons',        icon: Users,           label: 'Persons' },
  { to: '/cashiers',       icon: UserCheck,       label: 'Cashiers' },
  { to: '/tax-categories', icon: Percent,         label: 'Tax Categories' },
  { to: '/registers',      icon: Monitor,         label: 'Registers' },
];

export default function Layout({ user, onLogout }) {
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
          {user && (
            <div className="sidebar-user">
              <div className="sidebar-user-info">
                <span className="sidebar-user-name">{user.name}</span>
                <span className="sidebar-user-role">{user.role}</span>
              </div>
              <button className="sidebar-logout-btn" onClick={onLogout} title="Sign out">
                <LogOut size={18} />
              </button>
            </div>
          )}
        </div>
      </aside>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
