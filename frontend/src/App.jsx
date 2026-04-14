import { useState } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import Stores from './pages/Stores';
import Items from './pages/Items';
import Persons from './pages/Persons';
import Cashiers from './pages/Cashiers';
import TaxCategories from './pages/TaxCategories';
import Registers from './pages/Registers';
import POSTerminal from './pages/POSTerminal';
import Reports from './pages/Reports';
import Login from './pages/Login';
import './index.css';

function ProtectedRoute({ user, children }) {
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

export default function App() {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });

  const handleLogin = (data) => {
    setUser({ cashierNumber: data.cashierNumber, name: data.name, role: data.role });
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  return (
    <Routes>
      <Route path="/login" element={
        user ? <Navigate to="/" replace /> : <Login onLogin={handleLogin} />
      } />
      <Route element={
        <ProtectedRoute user={user}>
          <Layout user={user} onLogout={handleLogout} />
        </ProtectedRoute>
      }>
        <Route path="/" element={<Dashboard />} />
        <Route path="/stores" element={<Stores />} />
        <Route path="/items" element={<Items />} />
        <Route path="/persons" element={<Persons />} />
        <Route path="/cashiers" element={<Cashiers />} />
        <Route path="/tax-categories" element={<TaxCategories />} />
        <Route path="/registers" element={<Registers />} />
        <Route path="/reports" element={<Reports />} />
        <Route path="/pos" element={<POSTerminal />} />
      </Route>
    </Routes>
  );
}
