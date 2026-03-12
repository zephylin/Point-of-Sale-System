import { useEffect, useState, useCallback, useRef } from 'react';
import {
  ShoppingCart, Search, Plus, Minus, Trash2, DollarSign,
  CreditCard, FileText, XCircle, CheckCircle, LogIn, LogOut,
} from 'lucide-react';
import { itemApi, saleApi, saleLineItemApi, sessionApi, cashierApi, registerApi, storeApi } from '../api';
import Modal from '../components/Modal';
import Alert from '../components/Alert';

// ── helpers ──────────────────────────────────────────────
function fmt(n) {
  return Number(n || 0).toFixed(2);
}

// ── component ────────────────────────────────────────────
export default function POSTerminal() {
  // ── setup / session state ──
  const [cashiers, setCashiers] = useState([]);
  const [registers, setRegisters] = useState([]);
  const [stores, setStores] = useState([]);
  const [session, setSession] = useState(null);        // active session object
  const [sessionModalOpen, setSessionModalOpen] = useState(false);
  const [sessionForm, setSessionForm] = useState({ cashierId: '', registerId: '', storeId: '', startingCash: '100.00' });
  const [sessionLoading, setSessionLoading] = useState(false);

  // ── sale state ──
  const [sale, setSale] = useState(null);               // current sale header
  const [lineItems, setLineItems] = useState([]);        // current sale line items
  const [saleLoading, setSaleLoading] = useState(false);

  // ── item search ──
  const [query, setQuery] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [searching, setSearching] = useState(false);
  const searchRef = useRef(null);
  const debounceRef = useRef(null);

  // ── payment ──
  const [payModalOpen, setPayModalOpen] = useState(false);
  const [payMethod, setPayMethod] = useState('CASH');
  const [payAmount, setPayAmount] = useState('');
  const [paying, setPaying] = useState(false);

  // ── general ──
  const [alert, setAlert] = useState(null);
  const [initLoading, setInitLoading] = useState(true);

  // ── session history ──
  const [sessionSales, setSessionSales] = useState([]);

  // ── load reference data on mount ──
  useEffect(() => {
    (async () => {
      try {
        const [c, r, s] = await Promise.all([
          cashierApi.getActive().catch(() => cashierApi.getAll()),
          registerApi.getAll(),
          storeApi.getAll(),
        ]);
        setCashiers(c);
        setRegisters(r);
        setStores(s);
      } catch (e) {
        setAlert({ type: 'error', message: 'Failed to load reference data: ' + e.message });
      } finally {
        setInitLoading(false);
      }
    })();
  }, []);

  // ── auto-dismiss alerts ──
  useEffect(() => {
    if (alert) {
      const t = setTimeout(() => setAlert(null), 5000);
      return () => clearTimeout(t);
    }
  }, [alert]);

  // ───────────────────────────────── SESSION ─────────────────────────────────
  async function openSession() {
    setSessionLoading(true);
    try {
      const created = await sessionApi.create({
        cashierId: Number(sessionForm.cashierId),
        registerId: Number(sessionForm.registerId),
        startingCash: Number(sessionForm.startingCash),
      });
      setSession(created);
      setSessionModalOpen(false);
      setAlert({ type: 'success', message: `Session started – ${created.cashierName} on Register ${created.registerNumber}` });
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    } finally {
      setSessionLoading(false);
    }
  }

  async function closeSession() {
    if (!session) return;
    try {
      await sessionApi.close(session.id, { endingCash: '0' });
      setSession(null);
      setSale(null);
      setLineItems([]);
      setSessionSales([]);
      setAlert({ type: 'success', message: 'Session closed.' });
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    }
  }

  // ───────────────────────────────── SALE ────────────────────────────────────
  async function startNewSale() {
    if (!session) return;
    setSaleLoading(true);
    try {
      const created = await saleApi.create({
        sessionId: session.id,
        storeId: session.storeId ?? stores[0]?.id,
        cashierId: session.cashierId ?? null,
        taxFree: false,
      });
      setSale(created);
      setLineItems([]);
      setQuery('');
      setSearchResults([]);
      setTimeout(() => searchRef.current?.focus(), 100);
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    } finally {
      setSaleLoading(false);
    }
  }

  async function voidSale() {
    if (!sale) return;
    try {
      await saleApi.voidSale(sale.id, { reason: 'Voided by cashier' });
      setSale(null);
      setLineItems([]);
      setAlert({ type: 'info', message: 'Sale voided.' });
      refreshSessionSales();
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    }
  }

  // refresh the current sale header from server
  const refreshSale = useCallback(async (saleId) => {
    try {
      const updated = await saleApi.getById(saleId);
      setSale(updated);
    } catch { /* ignore */ }
  }, []);

  const refreshSessionSales = useCallback(async () => {
    if (!session) return;
    try {
      const list = await saleApi.getBySession(session.id);
      setSessionSales(list);
    } catch { /* ignore */ }
  }, [session]);

  useEffect(() => { if (session) refreshSessionSales(); }, [session, refreshSessionSales]);

  // ───────────────────────────── ITEM SEARCH ─────────────────────────────────
  function onSearchChange(value) {
    setQuery(value);
    if (debounceRef.current) clearTimeout(debounceRef.current);
    if (value.trim().length < 2) { setSearchResults([]); return; }
    debounceRef.current = setTimeout(async () => {
      setSearching(true);
      try {
        // try barcode first
        let results = [];
        try {
          const byBarcode = await itemApi.getAll().then(items =>
            items.filter(i => i.barcode?.toLowerCase().includes(value.toLowerCase()))
          );
          if (byBarcode.length > 0) { results = byBarcode; }
        } catch { /* ignore */ }
        // fallback to keyword search
        if (results.length === 0) {
          results = await itemApi.search(value);
        }
        setSearchResults(results);
      } catch {
        setSearchResults([]);
      } finally {
        setSearching(false);
      }
    }, 300);
  }

  // ───────────────────────────── ADD / REMOVE ITEMS ──────────────────────────
  async function addItem(item) {
    if (!sale) return;
    // check if item already in cart
    const existing = lineItems.find(li => li.itemId === item.id);
    if (existing) {
      await updateQuantity(existing, existing.quantity + 1);
      return;
    }
    try {
      const created = await saleLineItemApi.create({
        saleId: sale.id,
        itemId: item.id,
        quantity: 1,
        unitPrice: item.price,
      });
      setLineItems(prev => [...prev, created]);
      await refreshSale(sale.id);
      setQuery('');
      setSearchResults([]);
      searchRef.current?.focus();
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    }
  }

  async function updateQuantity(li, newQty) {
    if (newQty < 1) { await removeItem(li); return; }
    try {
      const updated = await saleLineItemApi.update(li.id, {
        saleId: sale.id,
        itemId: li.itemId,
        quantity: newQty,
        unitPrice: li.unitPrice,
      });
      setLineItems(prev => prev.map(x => x.id === li.id ? updated : x));
      await refreshSale(sale.id);
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    }
  }

  async function removeItem(li) {
    try {
      await saleLineItemApi.delete(li.id);
      setLineItems(prev => prev.filter(x => x.id !== li.id));
      await refreshSale(sale.id);
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    }
  }

  // ───────────────────────────── PAYMENT ─────────────────────────────────────
  function openPayment() {
    setPayMethod('CASH');
    setPayAmount(fmt(sale?.total));
    setPayModalOpen(true);
  }

  async function processPayment() {
    setPaying(true);
    try {
      const completed = await saleApi.complete(sale.id, {
        amountPaid: payAmount,
        paymentMethod: payMethod,
      });
      setSale(null);
      setLineItems([]);
      setPayModalOpen(false);
      const change = Number(completed.change || 0);
      setAlert({
        type: 'success',
        message: `Sale complete! Total: $${fmt(completed.total)}` +
          (payMethod === 'CASH' && change > 0 ? ` — Change: $${fmt(change)}` : ''),
      });
      refreshSessionSales();
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    } finally {
      setPaying(false);
    }
  }

  // ───────────────────────────── RENDER ──────────────────────────────────────

  if (initLoading) {
    return <div className="loading"><div className="spinner" />Loading POS Terminal…</div>;
  }

  // ── no active session → prompt to start one ──
  if (!session) {
    return (
      <div className="pos-wrapper">
        <div className="page-header">
          <div>
            <h2>POS Terminal</h2>
            <p>Start a session to begin processing sales</p>
          </div>
        </div>

        {alert && <Alert type={alert.type} message={alert.message} onDismiss={() => setAlert(null)} />}

        <div className="pos-start-card">
          <ShoppingCart size={48} strokeWidth={1.5} style={{ color: 'var(--primary)', marginBottom: 16 }} />
          <h3>No Active Session</h3>
          <p>Open a cashier session to start ringing up sales.</p>
          <button className="btn btn-primary" style={{ marginTop: 20 }} onClick={() => setSessionModalOpen(true)}>
            <LogIn size={16} /> Start Session
          </button>
        </div>

        {/* Session modal */}
        <Modal
          open={sessionModalOpen}
          title="Start Session"
          onClose={() => setSessionModalOpen(false)}
          footer={
            <>
              <button className="btn btn-secondary" onClick={() => setSessionModalOpen(false)}>Cancel</button>
              <button
                className="btn btn-primary"
                disabled={!sessionForm.cashierId || !sessionForm.registerId || sessionLoading}
                onClick={openSession}
              >
                {sessionLoading ? 'Starting…' : 'Start Session'}
              </button>
            </>
          }
        >
          <div className="form-group">
            <label>Cashier</label>
            <select value={sessionForm.cashierId} onChange={e => setSessionForm(f => ({ ...f, cashierId: e.target.value }))}>
              <option value="">Select cashier…</option>
              {cashiers.map(c => <option key={c.id} value={c.id}>{c.number} – {c.personName || `Cashier #${c.id}`}</option>)}
            </select>
          </div>
          <div className="form-group">
            <label>Register</label>
            <select value={sessionForm.registerId} onChange={e => setSessionForm(f => ({ ...f, registerId: e.target.value }))}>
              <option value="">Select register…</option>
              {registers.map(r => <option key={r.id} value={r.id}>Register {r.number}</option>)}
            </select>
          </div>
          <div className="form-group">
            <label>Starting Cash ($)</label>
            <input
              type="number"
              step="0.01"
              value={sessionForm.startingCash}
              onChange={e => setSessionForm(f => ({ ...f, startingCash: e.target.value }))}
            />
          </div>
        </Modal>
      </div>
    );
  }

  // ── active session → show POS terminal ──
  const subtotal = lineItems.reduce((s, li) => s + Number(li.extendedPrice || li.unitPrice * li.quantity || 0), 0);
  const tax = lineItems.reduce((s, li) => s + Number(li.taxAmount || 0), 0);
  const total = Number(sale?.total ?? subtotal + tax);

  return (
    <div className="pos-wrapper">
      {/* ── header bar ── */}
      <div className="pos-header-bar">
        <div className="pos-header-left">
          <h2><ShoppingCart size={22} /> POS Terminal</h2>
          <span className="badge badge-green">Session Active</span>
        </div>
        <div className="pos-header-right">
          <span className="pos-session-info">
            {session.cashierName} &middot; Register {session.registerNumber}
          </span>
          <button className="btn btn-secondary btn-sm" onClick={closeSession}>
            <LogOut size={14} /> End Session
          </button>
        </div>
      </div>

      {alert && <Alert type={alert.type} message={alert.message} onDismiss={() => setAlert(null)} />}

      <div className="pos-layout">
        {/* ════════ LEFT: item search + cart ════════ */}
        <div className="pos-main">
          {/* ── new sale / active sale controls ── */}
          <div className="pos-sale-bar">
            {!sale ? (
              <button className="btn btn-primary" onClick={startNewSale} disabled={saleLoading}>
                <Plus size={16} /> {saleLoading ? 'Creating…' : 'New Sale'}
              </button>
            ) : (
              <>
                <span className="badge badge-blue">Sale #{sale.id}</span>
                <button className="btn btn-danger btn-sm" onClick={voidSale}><XCircle size={14} /> Void</button>
              </>
            )}
          </div>

          {/* ── search ── */}
          {sale && (
            <div className="pos-search-area">
              <div className="pos-search-box">
                <Search size={18} className="pos-search-icon" />
                <input
                  ref={searchRef}
                  type="text"
                  className="pos-search-input"
                  placeholder="Scan barcode or search item…"
                  value={query}
                  onChange={e => onSearchChange(e.target.value)}
                />
              </div>

              {/* search dropdown */}
              {(searchResults.length > 0 || searching) && (
                <div className="pos-search-results">
                  {searching && <div className="pos-search-loading">Searching…</div>}
                  {searchResults.map(item => (
                    <button key={item.id} className="pos-search-item" onClick={() => addItem(item)}>
                      <div className="pos-search-item-info">
                        <strong>{item.description || item.number}</strong>
                        <span className="pos-search-item-meta">
                          {item.barcode && `Barcode: ${item.barcode}`}
                          {item.barcode && item.category ? ' · ' : ''}
                          {item.category}
                        </span>
                      </div>
                      <span className="pos-search-item-price">${fmt(item.price)}</span>
                    </button>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* ── line items table ── */}
          {sale && (
            <div className="card" style={{ marginTop: 16, flex: 1, overflow: 'auto' }}>
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Item</th>
                      <th style={{ width: 100 }}>Price</th>
                      <th style={{ width: 140 }}>Qty</th>
                      <th style={{ width: 100 }}>Subtotal</th>
                      <th style={{ width: 50 }}></th>
                    </tr>
                  </thead>
                  <tbody>
                    {lineItems.length === 0 ? (
                      <tr>
                        <td colSpan={5} style={{ textAlign: 'center', padding: 32, color: 'var(--gray-400)' }}>
                          Search and add items to start the sale
                        </td>
                      </tr>
                    ) : (
                      lineItems.map(li => (
                        <tr key={li.id}>
                          <td>
                            <strong>{li.itemDescription || `Item #${li.itemId}`}</strong>
                          </td>
                          <td>${fmt(li.unitPrice)}</td>
                          <td>
                            <div className="pos-qty-control">
                              <button className="btn-icon" onClick={() => updateQuantity(li, li.quantity - 1)}>
                                <Minus size={14} />
                              </button>
                              <span className="pos-qty-value">{li.quantity}</span>
                              <button className="btn-icon" onClick={() => updateQuantity(li, li.quantity + 1)}>
                                <Plus size={14} />
                              </button>
                            </div>
                          </td>
                          <td>${fmt(li.extendedPrice || li.unitPrice * li.quantity)}</td>
                          <td>
                            <button className="btn-icon danger" onClick={() => removeItem(li)}>
                              <Trash2 size={14} />
                            </button>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* ── recent sales in session ── */}
          {!sale && sessionSales.length > 0 && (
            <div className="card" style={{ marginTop: 16 }}>
              <div className="card-header">Recent Sales This Session</div>
              <div className="table-container">
                <table>
                  <thead>
                    <tr><th>Sale #</th><th>Status</th><th>Total</th><th>Payment</th></tr>
                  </thead>
                  <tbody>
                    {sessionSales.map(s => (
                      <tr key={s.id}>
                        <td>#{s.id}</td>
                        <td><span className={`badge ${s.status === 'COMPLETED' ? 'badge-green' : s.status === 'VOIDED' ? 'badge-red' : 'badge-yellow'}`}>{s.status}</span></td>
                        <td>${fmt(s.total)}</td>
                        <td>{s.paymentMethod || '—'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>

        {/* ════════ RIGHT: totals panel ════════ */}
        <div className="pos-sidebar-panel">
          <div className="pos-totals-card">
            <h3>Order Summary</h3>
            <div className="pos-totals-row">
              <span>Items</span>
              <span>{lineItems.reduce((s, li) => s + li.quantity, 0)}</span>
            </div>
            <div className="pos-totals-row">
              <span>Subtotal</span>
              <span>${fmt(sale?.subtotal ?? subtotal)}</span>
            </div>
            <div className="pos-totals-row">
              <span>Tax</span>
              <span>${fmt(sale?.tax ?? tax)}</span>
            </div>
            <div className="pos-totals-row pos-totals-total">
              <span>Total</span>
              <span>${fmt(total)}</span>
            </div>

            <button
              className="btn btn-primary pos-pay-btn"
              disabled={!sale || lineItems.length === 0}
              onClick={openPayment}
            >
              <DollarSign size={18} /> Pay ${fmt(total)}
            </button>
          </div>

          {/* quick actions */}
          <div className="pos-quick-actions">
            <button className="btn btn-secondary" style={{ width: '100%' }} disabled={!sale || lineItems.length === 0} onClick={openPayment}>
              <CreditCard size={16} /> Card
            </button>
            <button className="btn btn-secondary" style={{ width: '100%' }} disabled={!!sale} onClick={startNewSale}>
              <Plus size={16} /> New Sale
            </button>
          </div>
        </div>
      </div>

      {/* ════════ PAYMENT MODAL ════════ */}
      <Modal
        open={payModalOpen}
        title="Process Payment"
        onClose={() => setPayModalOpen(false)}
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setPayModalOpen(false)}>Cancel</button>
            <button
              className="btn btn-primary"
              disabled={paying || !payAmount || Number(payAmount) < total}
              onClick={processPayment}
            >
              {paying ? 'Processing…' : `Pay $${fmt(payAmount)}`}
            </button>
          </>
        }
      >
        <div className="pos-pay-methods">
          {[
            { key: 'CASH', icon: DollarSign, label: 'Cash' },
            { key: 'CREDIT', icon: CreditCard, label: 'Credit Card' },
            { key: 'CHECK', icon: FileText, label: 'Check' },
          ].map(({ key, icon: Icon, label }) => (
            <button
              key={key}
              className={`pos-pay-method-btn ${payMethod === key ? 'active' : ''}`}
              onClick={() => { setPayMethod(key); if (key !== 'CASH') setPayAmount(fmt(total)); }}
            >
              <Icon size={20} />
              {label}
            </button>
          ))}
        </div>

        <div className="pos-pay-summary">
          <div className="pos-totals-row pos-totals-total">
            <span>Total Due</span>
            <span>${fmt(total)}</span>
          </div>
        </div>

        {payMethod === 'CASH' && (
          <div className="form-group" style={{ marginTop: 16 }}>
            <label>Amount Tendered ($)</label>
            <input
              type="number"
              step="0.01"
              min={total}
              value={payAmount}
              onChange={e => setPayAmount(e.target.value)}
              autoFocus
            />
            {Number(payAmount) >= total && (
              <p style={{ marginTop: 8, fontSize: 16, fontWeight: 600, color: 'var(--success)' }}>
                Change: ${fmt(Number(payAmount) - total)}
              </p>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
}
