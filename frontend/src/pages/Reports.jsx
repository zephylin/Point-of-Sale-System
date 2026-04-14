import { useEffect, useState, useMemo } from 'react';
import {
  BarChart3, DollarSign, ShoppingCart, TrendingUp, CreditCard,
  Filter, Eye, X, ChevronLeft, ChevronRight,
} from 'lucide-react';
import { saleApi, paymentApi, saleLineItemApi } from '../api';

const PAGE_SIZE = 15;

function formatCurrency(amount) {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount ?? 0);
}

function formatDate(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric',
    hour: 'numeric', minute: '2-digit',
  });
}

function statusBadge(status) {
  const map = {
    COMPLETED: 'badge-green',
    IN_PROGRESS: 'badge-blue',
    VOIDED: 'badge-red',
    PENDING: 'badge-yellow',
  };
  return <span className={`badge ${map[status] || 'badge-gray'}`}>{status ?? 'UNKNOWN'}</span>;
}

function paymentBadge(method) {
  const map = { CASH: 'badge-green', CREDIT: 'badge-blue', CHECK: 'badge-yellow' };
  return method
    ? <span className={`badge ${map[method] || 'badge-gray'}`}>{method}</span>
    : <span className="badge badge-gray">—</span>;
}

export default function Reports() {
  const [sales, setSales] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filters
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [paymentFilter, setPaymentFilter] = useState('ALL');
  const [searchText, setSearchText] = useState('');
  const [page, setPage] = useState(0);

  // Detail modal
  const [detailSale, setDetailSale] = useState(null);
  const [detailLineItems, setDetailLineItems] = useState([]);
  const [detailPayments, setDetailPayments] = useState([]);
  const [detailLoading, setDetailLoading] = useState(false);

  useEffect(() => { loadSales(); }, []);

  async function loadSales() {
    setLoading(true);
    setError(null);
    try {
      const data = await saleApi.getAll();
      setSales(Array.isArray(data) ? data : []);
    } catch (err) {
      setError('Failed to load sales data. Make sure the backend is running.');
    } finally {
      setLoading(false);
    }
  }

  // Derived stats
  const stats = useMemo(() => {
    if (!sales.length) return { count: 0, revenue: 0, avg: 0, cashCount: 0, creditCount: 0, checkCount: 0 };
    const completed = sales.filter(s => s.status === 'COMPLETED');
    const revenue = completed.reduce((sum, s) => sum + (s.total ?? 0), 0);
    const cashCount = completed.filter(s => s.paymentMethod === 'CASH').length;
    const creditCount = completed.filter(s => s.paymentMethod === 'CREDIT').length;
    const checkCount = completed.filter(s => s.paymentMethod === 'CHECK').length;
    return {
      count: completed.length,
      revenue,
      avg: completed.length ? revenue / completed.length : 0,
      cashCount,
      creditCount,
      checkCount,
    };
  }, [sales]);

  // Filtering & pagination
  const filtered = useMemo(() => {
    let result = [...sales];
    if (statusFilter !== 'ALL') result = result.filter(s => s.status === statusFilter);
    if (paymentFilter !== 'ALL') result = result.filter(s => s.paymentMethod === paymentFilter);
    if (searchText.trim()) {
      const q = searchText.toLowerCase();
      result = result.filter(s =>
        (s.cashierName && s.cashierName.toLowerCase().includes(q)) ||
        (s.storeName && s.storeName.toLowerCase().includes(q)) ||
        String(s.id).includes(q)
      );
    }
    // Sort newest first
    result.sort((a, b) => new Date(b.dateTime) - new Date(a.dateTime));
    return result;
  }, [sales, statusFilter, paymentFilter, searchText]);

  const totalPages = Math.ceil(filtered.length / PAGE_SIZE);
  const paged = filtered.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE);

  // Reset page when filters change
  useEffect(() => { setPage(0); }, [statusFilter, paymentFilter, searchText]);

  // Detail view
  async function openDetail(sale) {
    setDetailSale(sale);
    setDetailLoading(true);
    try {
      const [lineItems, payments] = await Promise.all([
        saleLineItemApi.getBySale(sale.id).catch(() => []),
        paymentApi.getBySale(sale.id).catch(() => []),
      ]);
      setDetailLineItems(Array.isArray(lineItems) ? lineItems : []);
      setDetailPayments(Array.isArray(payments) ? payments : []);
    } catch {
      setDetailLineItems([]);
      setDetailPayments([]);
    } finally {
      setDetailLoading(false);
    }
  }

  function closeDetail() {
    setDetailSale(null);
    setDetailLineItems([]);
    setDetailPayments([]);
  }

  const uniqueStatuses = [...new Set(sales.map(s => s.status).filter(Boolean))];

  return (
    <div>
      <div className="page-header">
        <div>
          <h2><BarChart3 size={24} style={{ verticalAlign: 'middle', marginRight: 8 }} />Sales Reports</h2>
          <p>View sales history, revenue, and payment analytics</p>
        </div>
        <button className="btn btn-secondary" onClick={loadSales} disabled={loading}>
          Refresh
        </button>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {/* Summary stats */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon green"><DollarSign size={20} /></div>
          <div className="stat-label">Total Revenue</div>
          <div className="stat-value">{formatCurrency(stats.revenue)}</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon blue"><ShoppingCart size={20} /></div>
          <div className="stat-label">Completed Sales</div>
          <div className="stat-value">{stats.count}</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon yellow"><TrendingUp size={20} /></div>
          <div className="stat-label">Average Sale</div>
          <div className="stat-value">{formatCurrency(stats.avg)}</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon red"><CreditCard size={20} /></div>
          <div className="stat-label">Payment Breakdown</div>
          <div className="stat-value" style={{ fontSize: 14, lineHeight: '1.6' }}>
            <span style={{ color: 'var(--success)' }}>Cash: {stats.cashCount}</span>{' · '}
            <span style={{ color: 'var(--primary)' }}>Credit: {stats.creditCount}</span>{' · '}
            <span style={{ color: 'var(--warning)' }}>Check: {stats.checkCount}</span>
          </div>
        </div>
      </div>

      {/* Filters */}
      <div className="card" style={{ marginBottom: 16 }}>
        <div className="card-body" style={{ padding: '12px 20px' }}>
          <div className="toolbar" style={{ marginBottom: 0 }}>
            <Filter size={16} style={{ color: 'var(--gray-400)' }} />
            <input
              className="search-input"
              placeholder="Search by ID, cashier, or store..."
              value={searchText}
              onChange={e => setSearchText(e.target.value)}
            />
            <select
              className="search-input"
              style={{ minWidth: 150 }}
              value={statusFilter}
              onChange={e => setStatusFilter(e.target.value)}
            >
              <option value="ALL">All Statuses</option>
              {uniqueStatuses.map(s => <option key={s} value={s}>{s}</option>)}
            </select>
            <select
              className="search-input"
              style={{ minWidth: 160 }}
              value={paymentFilter}
              onChange={e => setPaymentFilter(e.target.value)}
            >
              <option value="ALL">All Payments</option>
              <option value="CASH">Cash</option>
              <option value="CREDIT">Credit</option>
              <option value="CHECK">Check</option>
            </select>
            {(statusFilter !== 'ALL' || paymentFilter !== 'ALL' || searchText) && (
              <button className="btn btn-sm btn-secondary" onClick={() => { setStatusFilter('ALL'); setPaymentFilter('ALL'); setSearchText(''); }}>
                Clear Filters
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Sales table */}
      <div className="card">
        <div className="card-header">
          <span>Sales History ({filtered.length} {filtered.length === 1 ? 'sale' : 'sales'})</span>
        </div>
        {loading ? (
          <div className="loading"><div className="spinner" /> Loading sales...</div>
        ) : paged.length === 0 ? (
          <div className="empty-state">
            <ShoppingCart size={48} />
            <h3>No sales found</h3>
            <p>{sales.length ? 'Try adjusting your filters.' : 'Sales will appear here once transactions are processed.'}</p>
          </div>
        ) : (
          <>
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Date &amp; Time</th>
                    <th>Cashier</th>
                    <th>Store</th>
                    <th>Subtotal</th>
                    <th>Tax</th>
                    <th>Total</th>
                    <th>Payment</th>
                    <th>Status</th>
                    <th style={{ width: 60 }}></th>
                  </tr>
                </thead>
                <tbody>
                  {paged.map(sale => (
                    <tr key={sale.id}>
                      <td style={{ fontWeight: 600 }}>#{sale.id}</td>
                      <td>{formatDate(sale.dateTime)}</td>
                      <td>{sale.cashierName || '—'}</td>
                      <td>{sale.storeName || '—'}</td>
                      <td>{formatCurrency(sale.subtotal)}</td>
                      <td>{formatCurrency(sale.tax)}</td>
                      <td style={{ fontWeight: 600 }}>{formatCurrency(sale.total)}</td>
                      <td>{paymentBadge(sale.paymentMethod)}</td>
                      <td>{statusBadge(sale.status)}</td>
                      <td>
                        <button className="btn-icon" title="View details" onClick={() => openDetail(sale)}>
                          <Eye size={16} />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="reports-pagination">
                <button className="btn btn-sm btn-secondary" disabled={page === 0} onClick={() => setPage(p => p - 1)}>
                  <ChevronLeft size={14} /> Prev
                </button>
                <span className="reports-pagination-info">
                  Page {page + 1} of {totalPages}
                </span>
                <button className="btn btn-sm btn-secondary" disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}>
                  Next <ChevronRight size={14} />
                </button>
              </div>
            )}
          </>
        )}
      </div>

      {/* Detail modal */}
      {detailSale && (
        <div className="modal-overlay" onClick={closeDetail}>
          <div className="modal" style={{ maxWidth: 700 }} onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Sale #{detailSale.id} Details</h3>
              <button className="btn-icon" onClick={closeDetail}><X size={18} /></button>
            </div>
            <div className="modal-body">
              {detailLoading ? (
                <div className="loading"><div className="spinner" /> Loading details...</div>
              ) : (
                <>
                  {/* Sale info */}
                  <div className="reports-detail-grid">
                    <div className="reports-detail-item">
                      <span className="reports-detail-label">Date</span>
                      <span>{formatDate(detailSale.dateTime)}</span>
                    </div>
                    <div className="reports-detail-item">
                      <span className="reports-detail-label">Cashier</span>
                      <span>{detailSale.cashierName || '—'}</span>
                    </div>
                    <div className="reports-detail-item">
                      <span className="reports-detail-label">Store</span>
                      <span>{detailSale.storeName || '—'}</span>
                    </div>
                    <div className="reports-detail-item">
                      <span className="reports-detail-label">Status</span>
                      {statusBadge(detailSale.status)}
                    </div>
                    <div className="reports-detail-item">
                      <span className="reports-detail-label">Payment</span>
                      {paymentBadge(detailSale.paymentMethod)}
                    </div>
                    <div className="reports-detail-item">
                      <span className="reports-detail-label">Tax Free</span>
                      <span>{detailSale.taxFree ? 'Yes' : 'No'}</span>
                    </div>
                  </div>

                  {/* Line items */}
                  <h4 style={{ fontSize: 14, fontWeight: 600, margin: '20px 0 8px', color: 'var(--gray-700)' }}>
                    Line Items ({detailLineItems.length})
                  </h4>
                  {detailLineItems.length > 0 ? (
                    <div className="table-container">
                      <table>
                        <thead>
                          <tr>
                            <th>Item</th>
                            <th style={{ textAlign: 'right' }}>Qty</th>
                            <th style={{ textAlign: 'right' }}>Price</th>
                            <th style={{ textAlign: 'right' }}>Subtotal</th>
                          </tr>
                        </thead>
                        <tbody>
                          {detailLineItems.map(li => (
                            <tr key={li.id}>
                              <td>{li.itemDescription || `Item #${li.itemId}`}</td>
                              <td style={{ textAlign: 'right' }}>{li.quantity}</td>
                              <td style={{ textAlign: 'right' }}>{formatCurrency(li.unitPrice)}</td>
                              <td style={{ textAlign: 'right', fontWeight: 600 }}>{formatCurrency(li.extendedPrice)}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  ) : (
                    <p style={{ fontSize: 13, color: 'var(--gray-400)' }}>No line items recorded.</p>
                  )}

                  {/* Payments */}
                  <h4 style={{ fontSize: 14, fontWeight: 600, margin: '20px 0 8px', color: 'var(--gray-700)' }}>
                    Payments ({detailPayments.length})
                  </h4>
                  {detailPayments.length > 0 ? (
                    <div className="table-container">
                      <table>
                        <thead>
                          <tr>
                            <th>Type</th>
                            <th style={{ textAlign: 'right' }}>Amount</th>
                            <th style={{ textAlign: 'right' }}>Tendered</th>
                            <th style={{ textAlign: 'right' }}>Change</th>
                            <th>Auth Code</th>
                          </tr>
                        </thead>
                        <tbody>
                          {detailPayments.map(p => (
                            <tr key={p.id}>
                              <td>{paymentBadge(p.paymentType)}</td>
                              <td style={{ textAlign: 'right' }}>{formatCurrency(p.amount)}</td>
                              <td style={{ textAlign: 'right' }}>{p.amountTendered ? formatCurrency(p.amountTendered) : '—'}</td>
                              <td style={{ textAlign: 'right' }}>{p.changeDue ? formatCurrency(p.changeDue) : '—'}</td>
                              <td>{p.authorizationCode || '—'}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  ) : (
                    <p style={{ fontSize: 13, color: 'var(--gray-400)' }}>No payments recorded.</p>
                  )}

                  {/* Totals summary */}
                  <div className="reports-totals-summary">
                    <div className="reports-totals-row">
                      <span>Subtotal</span>
                      <span>{formatCurrency(detailSale.subtotal)}</span>
                    </div>
                    <div className="reports-totals-row">
                      <span>Tax</span>
                      <span>{formatCurrency(detailSale.tax)}</span>
                    </div>
                    <div className="reports-totals-row reports-totals-grand">
                      <span>Total</span>
                      <span>{formatCurrency(detailSale.total)}</span>
                    </div>
                    {detailSale.amountPaid != null && (
                      <>
                        <div className="reports-totals-row">
                          <span>Amount Paid</span>
                          <span>{formatCurrency(detailSale.amountPaid)}</span>
                        </div>
                        <div className="reports-totals-row">
                          <span>Change</span>
                          <span>{formatCurrency(detailSale.change)}</span>
                        </div>
                      </>
                    )}
                  </div>
                </>
              )}
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={closeDetail}>Close</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
