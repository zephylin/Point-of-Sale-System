import { useMemo, useState } from 'react';
import {
  BarChart, Bar, LineChart, Line, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
  AreaChart, Area,
} from 'recharts';
import { BarChart3, TrendingUp, PieChart as PieIcon } from 'lucide-react';

const COLORS = ['#4f46e5', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];

function formatCurrency(value) {
  return `$${Number(value || 0).toFixed(2)}`;
}

function getDateKey(iso, granularity) {
  const d = new Date(iso);
  if (granularity === 'hour') {
    return d.toLocaleString('en-US', { month: 'short', day: 'numeric', hour: 'numeric' });
  }
  if (granularity === 'day') {
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }
  if (granularity === 'week') {
    // ISO week start (Monday)
    const day = d.getDay();
    const diff = d.getDate() - day + (day === 0 ? -6 : 1);
    const monday = new Date(d.setDate(diff));
    return `Week of ${monday.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}`;
  }
  // month
  return d.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
}

export default function SalesCharts({ sales }) {
  const [timeRange, setTimeRange] = useState('day');

  const completedSales = useMemo(() =>
    sales.filter(s => s.status === 'COMPLETED'),
    [sales]
  );

  // ── Revenue over time ──
  const revenueData = useMemo(() => {
    if (!completedSales.length) return [];
    const map = {};
    completedSales.forEach(s => {
      const key = getDateKey(s.dateTime, timeRange);
      if (!map[key]) map[key] = { period: key, revenue: 0, count: 0 };
      map[key].revenue += s.total ?? 0;
      map[key].count += 1;
    });
    return Object.values(map).sort((a, b) => {
      // sort chronologically by finding original dates
      const aDate = completedSales.find(s => getDateKey(s.dateTime, timeRange) === a.period)?.dateTime;
      const bDate = completedSales.find(s => getDateKey(s.dateTime, timeRange) === b.period)?.dateTime;
      return new Date(aDate) - new Date(bDate);
    });
  }, [completedSales, timeRange]);

  // ── Payment method breakdown (pie) ──
  const paymentData = useMemo(() => {
    if (!completedSales.length) return [];
    const map = {};
    completedSales.forEach(s => {
      const method = s.paymentMethod || 'UNKNOWN';
      if (!map[method]) map[method] = { name: method, value: 0, count: 0 };
      map[method].value += s.total ?? 0;
      map[method].count += 1;
    });
    return Object.values(map);
  }, [completedSales]);

  // ── Hourly sales pattern (bar) ──
  const hourlyData = useMemo(() => {
    if (!completedSales.length) return [];
    const hours = Array.from({ length: 24 }, (_, i) => ({
      hour: `${i.toString().padStart(2, '0')}:00`,
      sales: 0,
      revenue: 0,
    }));
    completedSales.forEach(s => {
      const h = new Date(s.dateTime).getHours();
      hours[h].sales += 1;
      hours[h].revenue += s.total ?? 0;
    });
    // Only return hours that have data or are between first and last active hour
    const firstActive = hours.findIndex(h => h.sales > 0);
    const lastActive = hours.length - 1 - [...hours].reverse().findIndex(h => h.sales > 0);
    if (firstActive === -1) return [];
    return hours.slice(Math.max(0, firstActive - 1), Math.min(24, lastActive + 2));
  }, [completedSales]);

  // ── Top cashiers (bar) ──
  const cashierData = useMemo(() => {
    if (!completedSales.length) return [];
    const map = {};
    completedSales.forEach(s => {
      const name = s.cashierName || 'Unknown';
      if (!map[name]) map[name] = { name, revenue: 0, count: 0 };
      map[name].revenue += s.total ?? 0;
      map[name].count += 1;
    });
    return Object.values(map).sort((a, b) => b.revenue - a.revenue).slice(0, 10);
  }, [completedSales]);

  if (!completedSales.length) {
    return null;
  }

  return (
    <div className="charts-section">
      <div className="charts-header">
        <h3><BarChart3 size={18} /> Sales Analytics</h3>
        <div className="charts-time-toggle">
          {[
            { key: 'hour', label: 'Hourly' },
            { key: 'day', label: 'Daily' },
            { key: 'week', label: 'Weekly' },
            { key: 'month', label: 'Monthly' },
          ].map(({ key, label }) => (
            <button
              key={key}
              className={`btn btn-sm ${timeRange === key ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => setTimeRange(key)}
            >
              {label}
            </button>
          ))}
        </div>
      </div>

      <div className="charts-grid">
        {/* Revenue Over Time */}
        <div className="chart-card">
          <div className="chart-card-header">
            <TrendingUp size={16} />
            <span>Revenue Over Time</span>
          </div>
          <div className="chart-container">
            <ResponsiveContainer width="100%" height={260}>
              <AreaChart data={revenueData} margin={{ top: 10, right: 20, left: 10, bottom: 0 }}>
                <defs>
                  <linearGradient id="revenueGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#4f46e5" stopOpacity={0.3} />
                    <stop offset="95%" stopColor="#4f46e5" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                <XAxis dataKey="period" fontSize={11} tick={{ fill: '#6b7280' }} />
                <YAxis fontSize={11} tick={{ fill: '#6b7280' }} tickFormatter={v => `$${v}`} />
                <Tooltip
                  formatter={(value, name) => [formatCurrency(value), name === 'revenue' ? 'Revenue' : name]}
                  labelStyle={{ fontWeight: 600 }}
                  contentStyle={{ borderRadius: 8, border: '1px solid #e5e7eb' }}
                />
                <Area
                  type="monotone"
                  dataKey="revenue"
                  stroke="#4f46e5"
                  strokeWidth={2}
                  fill="url(#revenueGradient)"
                  name="Revenue"
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Payment Method Breakdown */}
        <div className="chart-card">
          <div className="chart-card-header">
            <PieIcon size={16} />
            <span>Payment Methods</span>
          </div>
          <div className="chart-container">
            <ResponsiveContainer width="100%" height={260}>
              <PieChart>
                <Pie
                  data={paymentData}
                  cx="50%"
                  cy="50%"
                  innerRadius={55}
                  outerRadius={90}
                  paddingAngle={4}
                  dataKey="value"
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  labelLine={false}
                >
                  {paymentData.map((_, idx) => (
                    <Cell key={idx} fill={COLORS[idx % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value) => [formatCurrency(value), 'Revenue']} />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Hourly Sales Pattern */}
        <div className="chart-card">
          <div className="chart-card-header">
            <BarChart3 size={16} />
            <span>Sales by Hour</span>
          </div>
          <div className="chart-container">
            <ResponsiveContainer width="100%" height={260}>
              <BarChart data={hourlyData} margin={{ top: 10, right: 20, left: 10, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                <XAxis dataKey="hour" fontSize={11} tick={{ fill: '#6b7280' }} />
                <YAxis fontSize={11} tick={{ fill: '#6b7280' }} />
                <Tooltip
                  formatter={(value, name) => [name === 'revenue' ? formatCurrency(value) : value, name === 'revenue' ? 'Revenue' : 'Transactions']}
                  contentStyle={{ borderRadius: 8, border: '1px solid #e5e7eb' }}
                />
                <Bar dataKey="sales" fill="#10b981" radius={[4, 4, 0, 0]} name="Transactions" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Top Cashiers */}
        <div className="chart-card">
          <div className="chart-card-header">
            <TrendingUp size={16} />
            <span>Top Cashiers by Revenue</span>
          </div>
          <div className="chart-container">
            <ResponsiveContainer width="100%" height={260}>
              <BarChart data={cashierData} layout="vertical" margin={{ top: 10, right: 20, left: 60, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
                <XAxis type="number" fontSize={11} tick={{ fill: '#6b7280' }} tickFormatter={v => `$${v}`} />
                <YAxis type="category" dataKey="name" fontSize={11} tick={{ fill: '#6b7280' }} />
                <Tooltip
                  formatter={(value, name) => [name === 'revenue' ? formatCurrency(value) : value, name === 'revenue' ? 'Revenue' : 'Sales']}
                  contentStyle={{ borderRadius: 8, border: '1px solid #e5e7eb' }}
                />
                <Bar dataKey="revenue" fill="#8b5cf6" radius={[0, 4, 4, 0]} name="Revenue" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
}
