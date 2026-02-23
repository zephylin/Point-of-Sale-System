import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  Store, Package, Users, UserCheck, Monitor, Percent, Activity,
  ArrowRight, CheckCircle, XCircle
} from 'lucide-react';
import { storeApi, itemApi, personApi, cashierApi, registerApi, taxCategoryApi } from '../api';

export default function Dashboard() {
  const [stats, setStats] = useState({});
  const [loading, setLoading] = useState(true);
  const [apiStatus, setApiStatus] = useState(null);

  useEffect(() => {
    loadStats();
  }, []);

  async function loadStats() {
    setLoading(true);
    try {
      const [stores, items, persons, cashiers, registers, taxCats] = await Promise.all([
        storeApi.count().catch(() => '?'),
        itemApi.count().catch(() => '?'),
        personApi.count().catch(() => '?'),
        cashierApi.count().catch(() => '?'),
        registerApi.count().catch(() => '?'),
        taxCategoryApi.count().catch(() => '?'),
      ]);
      setStats({ stores, items, persons, cashiers, registers, taxCats });
      setApiStatus('online');
    } catch {
      setApiStatus('offline');
    } finally {
      setLoading(false);
    }
  }

  const cards = [
    { label: 'Stores',         value: stats.stores,   icon: Store,     color: 'blue',   to: '/stores' },
    { label: 'Items',          value: stats.items,    icon: Package,   color: 'green',  to: '/items' },
    { label: 'Persons',        value: stats.persons,  icon: Users,     color: 'yellow', to: '/persons' },
    { label: 'Cashiers',       value: stats.cashiers, icon: UserCheck, color: 'red',    to: '/cashiers' },
    { label: 'Registers',      value: stats.registers,    icon: Monitor,   color: 'blue',   to: '/registers' },
    { label: 'Tax Categories', value: stats.taxCats,  icon: Percent,   color: 'green',  to: '/tax-categories' },
  ];

  return (
    <div>
      <div className="page-header">
        <div>
          <h2>Dashboard</h2>
          <p>POS System overview</p>
        </div>
        <div className="api-status">
          <span className={`dot ${apiStatus === 'online' ? 'online' : apiStatus === 'offline' ? 'offline' : ''}`} />
          <span>
            API: {apiStatus === 'online' ? 'Connected' : apiStatus === 'offline' ? 'Disconnected' : 'Checking...'}
          </span>
          <button className="btn btn-sm btn-secondary" onClick={loadStats} style={{ marginLeft: 8 }}>
            Refresh
          </button>
        </div>
      </div>

      {loading ? (
        <div className="loading"><div className="spinner" /> Loading dashboard...</div>
      ) : (
        <>
          <div className="stats-grid">
            {cards.map((card) => (
              <Link key={card.label} to={card.to} style={{ textDecoration: 'none', color: 'inherit' }}>
                <div className="stat-card">
                  <div className={`stat-icon ${card.color}`}>
                    <card.icon size={20} />
                  </div>
                  <div className="stat-label">{card.label}</div>
                  <div className="stat-value">{card.value ?? '—'}</div>
                </div>
              </Link>
            ))}
          </div>

          <div className="card" style={{ marginTop: 24 }}>
            <div className="card-header">
              <span>Quick Actions</span>
            </div>
            <div className="card-body">
              <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
                <Link to="/stores" className="btn btn-primary"><Store size={16} /> Manage Stores</Link>
                <Link to="/items" className="btn btn-primary"><Package size={16} /> Manage Items</Link>
                <Link to="/persons" className="btn btn-secondary"><Users size={16} /> Manage Persons</Link>
                <Link to="/cashiers" className="btn btn-secondary"><UserCheck size={16} /> Manage Cashiers</Link>
              </div>
            </div>
          </div>

          <div className="card" style={{ marginTop: 16 }}>
            <div className="card-header">
              <span>API Health Check</span>
            </div>
            <div className="card-body">
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                {apiStatus === 'online' ? (
                  <><CheckCircle size={20} style={{ color: 'var(--success)' }} /> <span style={{ color: 'var(--success)' }}>Backend is running on port 8080</span></>
                ) : (
                  <><XCircle size={20} style={{ color: 'var(--danger)' }} /> <span style={{ color: 'var(--danger)' }}>Cannot reach backend — make sure Spring Boot is running</span></>
                )}
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
