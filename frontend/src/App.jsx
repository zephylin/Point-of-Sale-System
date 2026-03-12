import { Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import Stores from './pages/Stores';
import Items from './pages/Items';
import Persons from './pages/Persons';
import Cashiers from './pages/Cashiers';
import TaxCategories from './pages/TaxCategories';
import Registers from './pages/Registers';
import POSTerminal from './pages/POSTerminal';
import './index.css';

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<Dashboard />} />
        <Route path="/stores" element={<Stores />} />
        <Route path="/items" element={<Items />} />
        <Route path="/persons" element={<Persons />} />
        <Route path="/cashiers" element={<Cashiers />} />
        <Route path="/tax-categories" element={<TaxCategories />} />
        <Route path="/registers" element={<Registers />} />
        <Route path="/pos" element={<POSTerminal />} />
      </Route>
    </Routes>
  );
}
