import { useEffect, useState, useMemo } from 'react';
import { Link } from 'react-router-dom';
import {
  Store, Package, Users, UserCheck, Monitor, Percent,
  DollarSign, ShoppingCart, TrendingUp, Activity, BarChart3,
  CheckCircle, XCircle, Eye, Clock,
} from 'lucide-react';
import {
  storeApi, itemApi, personApi, cashierApi, registerApi, taxCategoryApi,
  saleApi, sessionApi,
} from '../api';

function formatCurrency(amount) {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount ?? 0);
}

function formatTime(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleString('en-US', {
    hour: 'numeric', minute: '2-digit', hour12: true,
  });
}

function formatDateTime(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleString('en-US', {
    month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit',
  });
}

function statusBadge(status) {
  const map = {
    COMPLETED: 'badge-green',
    IN_PROGRESS: 'badge-blue',
    VOIDED: 'badge-red',
    PENDING: 'badge-yellow',
  };
  return <span className={`badge ${map[status] || 'badge-gray'}`}>{status ?? '—'}</span>;
}

export default function Dashboard() {
  const [loading, setLoading] = useState(true);
  const [apiStatus, setApiStatus] = useState(null);

  // Entity counts
  const [entityCounts, setEntityCounts] = useState({});
  // Sales data
  const [recentSales, setRecentSales] = useState([]);
  const [activeSessions, setActiveSessions] = useState([]);

  useEffect(() => { loadAll(); }, []);

  async function loadAll() {
    setLoading(true);
    try {
      const [stores, items, persons, cashiers, registers, taxCats, sales, sessions] = await Promise.all([
        storeApi.count().catch(() => '?'),
        itemApi.count().catch(() => '?'),
        personApi.count().catch(() => '?'),
        cashierApi.count().catch(() => '?'),
        registerApi.count().catch(() => '?'),
        taxCategoryApi.count().catch(() => '?'),
        saleApi.getAll().catch(() => []),
        sessionApi.getAll().catch(() => []),
      ]);
      setEntityCounts({ stores, items, persons, cashiers, registers, taxCats });
      setRecentSales(Array.isArray(sales) ? sales : []);
      setActiveSessions(Array.isArray(sessions) ? sessions.filter(s => s.status === 'ACTIVE') : []);
      setApiStatus('online');
    } catch {
      setApiStatus('offline');
    } finally {
      setLoading(false);
    }
  }

  // Today's stats derived from sales data
  const todayStats = useMemo(() => {
    const now = new Date();
    const startOfDay = new Date(now.getFullYear(), now.getMonth(), now.getDate()).toISOString();
    const todaySales = recentSales.filter(s => s.dateTime && new Date(s.dateTime) >= new Date(startOfDay));
    const completed = todaySales.filter(s => s.status === 'COMPLETED');
    const revenue = completed.reduce((sum, s) => sum + (s.total ?? 0), 0);
    return { count: completed.length, revenue, all: todaySales.length };
  }, [recentSales]);

  // Recent 5 sales sorted newest first
  const latestSales = useMemo(() => {
    return [...recentSales]
      .sort((a, b) => new Date(b.dateTime) - new Date(a.dateTime))
      .slice(0, 5);
  }, [recentSales]);

  const entityCards = [
    { label: 'Stores',         value: entityCounts.stores,    icon: Store,     color: 'blue',   to: '/stores' },
    { label: 'Items',          value: entityCounts.items,     icon: Package,   color: 'green',  to: '/items' },
    { label: 'Persons',        value: entityCounts.persons,   icon: Users,     color: 'yellow', to: '/persons' },
    { label: 'Cashiers',       value: entityCounts.cashiers,  icon: UserCheck, color: 'red',    to: '/cashiers' },
    { label: 'Registers',      value: entityCounts.registers, icon: Monitor,   color: 'blue',   to: '/registers' },
    { label: 'Tax Categories', value: entityCounts.taxCats,   icon: Percent,   color: 'green',  to: '/tax-categories' },
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
          <button className="btn btn-sm btn-secondary" onClick={loadAll} style={{ marginLeft: 8 }}>
            Refresh
          </button>
        </div>
      </div>

      {loading ? (
        <div className="loading"><div className="spinner" /> Loading dashboard...</div>
      ) : (
        <>
          {/* Today's Sales Summary */}
          <div className="stats-grid" style={{ marginBottom: 24 }}>
            <div className="stat-card">
              <div className="stat-icon green"><DollarSign size={20} /></div>
              <div className="stat-label">Today's Revenue</div>
              <div className="stat-value">{formatCurrency(todayStats.revenue)}</div>
            </div>
            <div className="stat-card">
              <div className="stat-icon blue"><ShoppingCart size={20} /></div>
              <div className="stat-label">Today's Sales</div>
              <div className="stat-value">{todayStats.count}</div>
            </div>
            <div className="stat-card">
              <div className="stat-icon yellow"><Activity size={20} /></div>
              <div className="stat-label">Active Sessions</div>
              <div className="stat-value">{activeSessions.length}</div>
            </div>
            <div className="stat-card">
              <div className="stat-icon red"><TrendingUp size={20} /></div>
              <div className="stat-label">Total Sales (All Time)</div>
              <div className="stat-value">{recentSales.length}</div>
            </div>
          </div>

          {/* Quick Actions */}
          <div className="card" style={{ marginBottom: 16 }}>
            <div className="card-header"><span>Quick Actions</span></div>
            <div className="card-body">
              <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
                <Link to="/pos" className="btn btn-primary"><ShoppingCart size={16} /> Open POS Terminal</Link>
                <Link to="/reports" className="btn btn-primary"><BarChart3 size={16} /> View Reports</Link>
                <Link to="/items" className="btn btn-secondary"><Package size={16} /> Manage Items</Link>
                <Link to="/cashiers" className="btn btn-secondary"><UserCheck size={16} /> Manage Cashiers</Link>
              </div>
            </div>
          </div>

          {/* Two-column: Recent Sales + Active Sessions */}
          <div className="dash-two-col">
            {/* Recent Sales */}
            <div className="card">
              <div className="card-header">
                <span>Recent Sales</span>
                <Link to="/reports" className="btn btn-sm btn-secondary"><Eye size={14} /> View All</Link>
              </div>
              {latestSales.length === 0 ? (
                <div className="empty-state" style={{ padding: '32px 20px' }}>
                  <ShoppingCart size={36} />
                  <h3>No sales yet</h3>
                  <p>Sales will appear here once transactions are processed.</p>
                </div>
              ) : (
                <div className="table-container">
                  <table>
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>Time</th>
                        <th>Cashier</th>
                        <th style={{ textAlign: 'right' }}>Total</th>
                        <th>Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      {latestSales.map(sale => (
                        <tr key={sale.id}>
                          <td style={{ fontWeight: 600 }}>#{sale.id}</td>
                          <td>{formatDateTime(sale.dateTime)}</td>
                          <td>{sale.cashierName || '—'}</td>
                          <td style={{ textAlign: 'right', fontWeight: 600 }}>{formatCurrency(sale.total)}</td>
                          <td>{statusBadge(sale.status)}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>

            {/* Active Sessions */}
            <div className="card">
              <div className="card-header">
                <span>Active Sessions</span>
                <span className="badge badge-green">{activeSessions.length} active</span>
              </div>
              {activeSessions.length === 0 ? (
                <div className="empty-state" style={{ padding: '32px 20px' }}>
                  <Clock size={36} />
                  <h3>No active sessions</h3>
                  <p>Open the POS Terminal to start a session.</p>
                </div>
              ) : (
                <div className="table-container">
                  <table>
                    <thead>
                      <tr>
                        <th>Cashier</th>
                        <th>Register</th>
                        <th>Started</th>
                      </tr>
                    </thead>
                    <tbody>
                      {activeSessions.map(session => (
                        <tr key={session.id}>
                          <td>{session.cashierName || '—'}</td>
                          <td>{session.registerNumber || '—'}</td>
                          <td>{formatTime(session.startDateTime)}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>

          {/* Entity Counts */}
          <h3 className="dash-section-title">System Overview</h3>
          <div className="stats-grid">
            {entityCards.map((card) => (
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

          {/* API Health */}
          <div className="card" style={{ marginTop: 16 }}>
            <div className="card-header"><span>API Health Check</span></div>
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
