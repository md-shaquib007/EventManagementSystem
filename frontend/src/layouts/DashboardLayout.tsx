import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard, Calendar, Users, Building2, Receipt,
  ClipboardCheck, Image, BarChart3, Shield, Bell, Settings,
  LogOut, Moon, Sun, Menu, X
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { useState } from 'react';
import './DashboardLayout.css';

interface NavItem {
  path: string;
  label: string;
  icon: React.ReactNode;
  roles?: string[];
}

const navItems: NavItem[] = [
  { path: '/dashboard', label: 'Dashboard', icon: <LayoutDashboard size={20} /> },
  { path: '/events', label: 'Events', icon: <Calendar size={20} /> },
  { path: '/departments', label: 'Departments', icon: <Building2 size={20} />, roles: ['SUPER_ADMIN'] },
  { path: '/bills', label: 'Bills & Expenses', icon: <Receipt size={20} />, roles: ['SUPER_ADMIN', 'OPERATOR'] },
  { path: '/attendance', label: 'Attendance', icon: <ClipboardCheck size={20} />, roles: ['SUPER_ADMIN', 'OPERATOR'] },
  { path: '/media', label: 'Media Gallery', icon: <Image size={20} /> },
  { path: '/reports', label: 'Reports', icon: <BarChart3 size={20} />, roles: ['SUPER_ADMIN', 'OPERATOR'] },
  { path: '/audit', label: 'Audit Logs', icon: <Shield size={20} />, roles: ['SUPER_ADMIN'] },
  { path: '/users', label: 'Users', icon: <Users size={20} />, roles: ['SUPER_ADMIN'] },
  { path: '/notifications', label: 'Notifications', icon: <Bell size={20} /> },
  { path: '/settings', label: 'Settings', icon: <Settings size={20} /> },
];

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const { user, logout, isAdmin, isOperator } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const role = isAdmin ? 'SUPER_ADMIN' : isOperator ? 'OPERATOR' : 'STUDENT';
  const filteredNav = navItems.filter(
    (item) => !item.roles || item.roles.includes(role)
  );

  return (
    <div className="layout">
      <aside className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
        <div className="sidebar-header">
          <h2>CEOMS</h2>
          <span className="sidebar-subtitle">Event Management</span>
        </div>
        <nav className="sidebar-nav">
          {filteredNav.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
              onClick={() => setSidebarOpen(false)}
            >
              {item.icon}
              <span>{item.label}</span>
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          <button className="nav-item" onClick={toggleTheme}>
            {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
            <span>{theme === 'dark' ? 'Light Mode' : 'Dark Mode'}</span>
          </button>
          <button className="nav-item" onClick={logout}>
            <LogOut size={20} />
            <span>Logout</span>
          </button>
        </div>
      </aside>

      <div className="main-content">
        <header className="topbar">
          <button className="menu-toggle" onClick={() => setSidebarOpen(!sidebarOpen)}>
            {sidebarOpen ? <X size={24} /> : <Menu size={24} />}
          </button>
          <div className="topbar-right">
            <span className="user-name">{user?.fullName}</span>
            <span className="user-role">{role.replace('_', ' ')}</span>
          </div>
        </header>
        <main className="page-content">{children}</main>
      </div>
      {sidebarOpen && <div className="sidebar-overlay" onClick={() => setSidebarOpen(false)} />}
    </div>
  );
}
