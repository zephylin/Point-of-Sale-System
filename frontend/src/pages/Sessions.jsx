import { useEffect, useState, useCallback, useMemo } from 'react';
import {
  Clock, Filter, Eye, X, XCircle, CheckCircle, ChevronLeft, ChevronRight,
  DollarSign, AlertTriangle,
} from 'lucide-react';
import { sessionApi, saleApi } from '../api';
import Modal from '../components/Modal';
import Alert from '../components/Alert';
import ConfirmDialog from '../components/ConfirmDialog';

const PAGE_SIZE = 12;

function formatDateTime(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric',
    hour: 'numeric', minute: '2-digit',
  });
}

function formatCurrency(amount) {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount ?? 0);
}

function formatDuration(start, end) {
  if (!start) return '—';
  const s = new Date(start);
  const e = end ? new Date(end) : new Date();
  const diffMs = e - s;
  const hours = Math.floor(diffMs / 3600000);
  const minutes = Math.floor((diffMs % 3600000) / 60000);
  if (hours > 0) return `${hours}h ${minutes}m`;
  return `${minutes}m`;
}

function statusBadge(status) {
  const map = {
    ACTIVE: 'badge-green',
    CLOSED: 'badge-gray',
    SUSPENDED: 'badge-yellow',
  };
  return <span className={`badge ${map[status] || 'badge-gray'}`}>{status ?? 'UNKNOWN'}</span>;
}

export default function Sessions() {
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [alert, setAlert] = useState(null);

  // Filters
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [searchText, setSearchText] = useState('');
  const [page, setPage] = useState(0);

  // Detail modal
  const [detailSession, setDetailSession] = useState(null);
  const [detailSales, setDetailSales] = useState([]);
  const [detailLoading, setDetailLoading] = useState(false);

  // Close session
  const [closeTarget, setCloseTarget] = useState(null);
  const [closeForm, setCloseForm] = useState({ endingCash: '0.00' });
  const [closing, setClosing] = useState(false);

  // Delete
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await sessionApi.getAll();
      setSessions(Array.isArray(data) ? data : []);
    } catch (e) {
      setAlert({ type: 'error', message: 'Failed to load sessions. ' + (e.message || '') });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  // Auto-dismiss alerts
  useEffect(() => {
    if (alert) {
      const t = setTimeout(() => setAlert(null), 5000);
      return () => clearTimeout(t);
    }
  }, [alert]);

  // Stats
  const stats = useMemo(() => {
    const active = sessions.filter(s => s.status === 'ACTIVE').length;
    const closed = sessions.filter(s => s.status === 'CLOSED').length;
    const totalCash = sessions
      .filter(s => s.status === 'CLOSED' && s.endingCash != null)
      .reduce((sum, s) => sum + Number(s.endingCash || 0), 0);
    const withVariance = sessions.filter(s => s.cashVariance != null && Math.abs(s.cashVariance) > 0.01);
    return { active, closed, total: sessions.length, totalCash, variances: withVariance.length };
  }, [sessions]);

  // Filtering
  const filtered = useMemo(() => {
    let result = [...sessions];
    if (statusFilter !== 'ALL') result = result.filter(s => s.status === statusFilter);
    if (searchText.trim()) {
      const q = searchText.toLowerCase();
      result = result.filter(s =>
        (s.cashierName && s.cashierName.toLowerCase().includes(q)) ||
        (s.registerNumber && s.registerNumber.toLowerCase().includes(q)) ||
        String(s.id).includes(q)
      );
    }
    // Sort: active first, then by start date descending
    result.sort((a, b) => {
      if (a.status === 'ACTIVE' && b.status !== 'ACTIVE') return -1;
      if (b.status === 'ACTIVE' && a.status !== 'ACTIVE') return 1;
      return new Date(b.startDateTime) - new Date(a.startDateTime);
    });
    return result;
  }, [sessions, statusFilter, searchText]);

  const totalPages = Math.ceil(filtered.length / PAGE_SIZE);
  const paged = filtered.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE);

  useEffect(() => { setPage(0); }, [statusFilter, searchText]);

  // Detail
  async function openDetail(session) {
    setDetailSession(session);
    setDetailLoading(true);
    try {
      const sales = await saleApi.getBySession(session.id).catch(() => []);
      setDetailSales(Array.isArray(sales) ? sales : []);
    } catch {
      setDetailSales([]);
    } finally {
      setDetailLoading(false);
    }
  }

  function closeDetail() {
    setDetailSession(null);
    setDetailSales([]);
  }

  // Close session
  function openCloseDialog(session) {
    setCloseTarget(session);
    setCloseForm({ endingCash: '0.00' });
  }

  async function handleClose() {
    if (!closeTarget) return;
    setClosing(true);
    try {
      await sessionApi.close(closeTarget.id, { endingCash: closeForm.endingCash });
      setAlert({ type: 'success', message: `Session #${closeTarget.id} closed successfully.` });
      setCloseTarget(null);
      load();
    } catch (e) {
      setAlert({ type: 'error', message: e.message || 'Failed to close session.' });
    } finally {
      setClosing(false);
    }
  }

  // Delete
  async function handleDelete() {
    if (!deleteTarget) return;
    setDeleting(true);
    try {
      await sessionApi.delete(deleteTarget.id);
      setAlert({ type: 'success', message: 'Session deleted.' });
      setDeleteTarget(null);
      load();
    } catch (e) {
      setAlert({ type: 'error', message: e.message || 'Failed to delete session.' });
    } finally {
      setDeleting(false);
    }
  }

  const uniqueStatuses = [...new Set(sessions.map(s => s.status).filter(Boolean))];

  return (
    <div>
      <div className="page-header">
        <div>
          <h2><Clock size={24} style={{ verticalAlign: 'middle', marginRight: 8 }} />Sessions</h2>
          <p>View and manage cashier sessions</p>
        </div>
        <button className="btn btn-secondary" onClick={load} disabled={loading}>
          Refresh
        </button>
      </div>

      {alert && <Alert type={alert.type} message={alert.message} onDismiss={() => setAlert(null)} />}

      {/* Stats */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon green"><CheckCircle size={20} /></div>
          <div className="stat-label">Active Sessions</div>
          <div className="stat-value">{stats.active}</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon blue"><Clock size={20} /></div>
          <div className="stat-label">Total Sessions</div>
          <div className="stat-value">{stats.total}</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon yellow"><DollarSign size={20} /></div>
          <div className="stat-label">Cash Collected</div>
          <div className="stat-value">{formatCurrency(stats.totalCash)}</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon red"><AlertTriangle size={20} /></div>
          <div className="stat-label">Cash Variances</div>
          <div className="stat-value">{stats.variances}</div>
        </div>
      </div>

      {/* Filters */}
      <div className="card" style={{ marginBottom: 16 }}>
        <div className="card-body" style={{ padding: '12px 20px' }}>
          <div className="toolbar" style={{ marginBottom: 0 }}>
            <Filter size={16} style={{ color: 'var(--gray-400)' }} />
            <input
              className="search-input"
              placeholder="Search by cashier, register, or ID..."
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
            {(statusFilter !== 'ALL' || searchText) && (
              <button className="btn btn-sm btn-secondary" onClick={() => { setStatusFilter('ALL'); setSearchText(''); }}>
                Clear Filters
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Sessions Table */}
      <div className="card">
        {loading ? (
          <div className="loading"><div className="spinner" /> Loading sessions...</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <h3>No sessions found</h3>
            <p>Try adjusting your filters or start a new session from the POS Terminal.</p>
          </div>
        ) : (
          <>
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Cashier</th>
                    <th>Register</th>
                    <th>Started</th>
                    <th>Duration</th>
                    <th>Status</th>
                    <th>Starting Cash</th>
                    <th>Ending Cash</th>
                    <th style={{ width: 120 }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {paged.map(session => (
                    <tr key={session.id}>
                      <td style={{ fontWeight: 600 }}>#{session.id}</td>
                      <td>{session.cashierName || '—'}</td>
                      <td>{session.registerNumber || '—'}</td>
                      <td>{formatDateTime(session.startDateTime)}</td>
                      <td>{formatDuration(session.startDateTime, session.endDateTime)}</td>
                      <td>{statusBadge(session.status)}</td>
                      <td>{session.startingCash != null ? formatCurrency(session.startingCash) : '—'}</td>
                      <td>{session.endingCash != null ? formatCurrency(session.endingCash) : '—'}</td>
                      <td>
                        <div style={{ display: 'flex', gap: 4 }}>
                          <button className="btn-icon" title="View details" onClick={() => openDetail(session)}>
                            <Eye size={16} />
                          </button>
                          {session.status === 'ACTIVE' && (
                            <button className="btn-icon" title="Close session" onClick={() => openCloseDialog(session)}>
                              <XCircle size={16} />
                            </button>
                          )}
                          {session.status !== 'ACTIVE' && (
                            <button className="btn-icon danger" title="Delete" onClick={() => setDeleteTarget(session)}>
                              <X size={16} />
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="pagination">
                <button className="btn btn-sm btn-secondary" disabled={page === 0} onClick={() => setPage(p => p - 1)}>
                  <ChevronLeft size={14} /> Prev
                </button>
                <span className="pagination-info">
                  Page {page + 1} of {totalPages} ({filtered.length} sessions)
                </span>
                <button className="btn btn-sm btn-secondary" disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}>
                  Next <ChevronRight size={14} />
                </button>
              </div>
            )}
          </>
        )}
      </div>

      {/* Detail Modal */}
      {detailSession && (
        <div className="modal-overlay" onClick={closeDetail}>
          <div className="modal" style={{ maxWidth: 650 }} onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Session #{detailSession.id} Details</h3>
              <button className="btn-icon" onClick={closeDetail}><X size={18} /></button>
            </div>
            <div className="modal-body">
              {detailLoading ? (
                <div className="loading"><div className="spinner" /> Loading details...</div>
              ) : (
                <>
                  <div className="reports-detail-grid">
                    <div className="reports-detail-item">
                      <span className="reports-detail-label">Cashier</span>
                      <span className="reports-detail-value">{detailSession.cashierName || '—'}</span>
                    </div>
                    <div className="reports-detail-item">
                      <span className="reports-detail-label">Register</span>
                      <span className="reports-detail-value">{detailSession.registerNumber || '—'}</span>
                    </div>
                    <div className="reports-detail-item">
                      <span className="reports-detail-label">Status</span>
                      <span className="reports-detail-value">{statusBadge(detailSession.status)}</span>
                    </div>
                    <div className="reports-detail-item">
                      <span className="reports-detail-label">Started</span>
                      <span className="reports-detail-value">{formatDateTime(detailSession.startDateTime)}</span>
                    </div>
                    <div className="reports-detail-item">
                      <span className="reports-detail-label">Ended</span>
                      <span className="reports-detail-value">{formatDateTime(detailSession.endDateTime)}</span>
                    </div>
                    <div className="reports-detail-item">
                      <span className="reports-detail-label">Duration</span>
                      <span className="reports-detail-value">{formatDuration(detailSession.startDateTime, detailSession.endDateTime)}</span>
                    </div>
                    <div className="reports-detail-item">
                      <span className="reports-detail-label">Starting Cash</span>
                      <span className="reports-detail-value">{formatCurrency(detailSession.startingCash)}</span>
                    </div>
                    <div className="reports-detail-item">
                      <span className="reports-detail-label">Ending Cash</span>
                      <span className="reports-detail-value">{detailSession.endingCash != null ? formatCurrency(detailSession.endingCash) : '—'}</span>
                    </div>
                    {detailSession.cashVariance != null && (
                      <div className="reports-detail-item">
                        <span className="reports-detail-label">Cash Variance</span>
                        <span className="reports-detail-value" style={{
                          color: Math.abs(detailSession.cashVariance) > 0.01 ? 'var(--danger)' : 'var(--success)',
                          fontWeight: 600,
                        }}>
                          {formatCurrency(detailSession.cashVariance)}
                        </span>
                      </div>
                    )}
                  </div>

                  {detailSession.notes && (
                    <div style={{ marginTop: 16, padding: '10px 14px', background: 'var(--gray-50)', borderRadius: 'var(--radius)', fontSize: 13 }}>
                      <strong>Notes:</strong> {detailSession.notes}
                    </div>
                  )}

                  {/* Sales in session */}
                  <h4 style={{ marginTop: 20, marginBottom: 10, fontSize: 14, color: 'var(--gray-600)' }}>
                    Sales in Session ({detailSales.length})
                  </h4>
                  {detailSales.length === 0 ? (
                    <p style={{ color: 'var(--gray-400)', fontSize: 13 }}>No sales recorded in this session.</p>
                  ) : (
                    <div className="table-container" style={{ maxHeight: 250, overflow: 'auto' }}>
                      <table>
                        <thead>
                          <tr>
                            <th>Sale #</th>
                            <th>Time</th>
                            <th>Total</th>
                            <th>Payment</th>
                            <th>Status</th>
                          </tr>
                        </thead>
                        <tbody>
                          {detailSales.map(sale => (
                            <tr key={sale.id}>
                              <td>#{sale.id}</td>
                              <td>{formatDateTime(sale.dateTime)}</td>
                              <td style={{ fontWeight: 600 }}>{formatCurrency(sale.total)}</td>
                              <td>
                                <span className={`badge ${sale.paymentMethod === 'CASH' ? 'badge-green' : sale.paymentMethod === 'CREDIT' ? 'badge-blue' : 'badge-yellow'}`}>
                                  {sale.paymentMethod || '—'}
                                </span>
                              </td>
                              <td>
                                <span className={`badge ${sale.status === 'COMPLETED' ? 'badge-green' : sale.status === 'VOIDED' ? 'badge-red' : 'badge-yellow'}`}>
                                  {sale.status}
                                </span>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}
                </>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Close Session Modal */}
      <Modal
        open={!!closeTarget}
        title={`Close Session #${closeTarget?.id}`}
        onClose={() => setCloseTarget(null)}
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setCloseTarget(null)}>Cancel</button>
            <button className="btn btn-primary" onClick={handleClose} disabled={closing}>
              {closing ? 'Closing...' : 'Close Session'}
            </button>
          </>
        }
      >
        <p style={{ marginBottom: 12, color: 'var(--gray-600)' }}>
          Closing session for <strong>{closeTarget?.cashierName}</strong> on Register <strong>{closeTarget?.registerNumber}</strong>.
        </p>
        <div className="form-group">
          <label>Ending Cash Amount ($)</label>
          <input
            type="number"
            step="0.01"
            min="0"
            value={closeForm.endingCash}
            onChange={e => setCloseForm({ endingCash: e.target.value })}
            placeholder="Enter ending cash drawer amount"
          />
          <small style={{ color: 'var(--gray-400)', fontSize: 12 }}>
            Starting cash was {formatCurrency(closeTarget?.startingCash)}
          </small>
        </div>
      </Modal>

      {/* Delete Confirm */}
      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Session"
        message={`Are you sure you want to delete session #${deleteTarget?.id}? This action cannot be undone.`}
        confirmLabel={deleting ? 'Deleting...' : 'Delete'}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
        danger
      />
    </div>
  );
}
