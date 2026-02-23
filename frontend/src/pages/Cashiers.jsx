import { useEffect, useState, useCallback } from 'react';
import { Plus, Pencil, Trash2, UserX } from 'lucide-react';
import { cashierApi, personApi, storeApi } from '../api';
import Modal from '../components/Modal';
import Alert from '../components/Alert';
import ConfirmDialog from '../components/ConfirmDialog';

const emptyForm = { number: '', password: '', personId: '', storeId: '', role: '' };

export default function Cashiers() {
  const [cashiers, setCashiers] = useState([]);
  const [persons, setPersons] = useState([]);
  const [stores, setStores] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [alert, setAlert] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [c, p, s] = await Promise.all([
        cashierApi.getAll(),
        personApi.getAll().catch(() => []),
        storeApi.getAll().catch(() => []),
      ]);
      setCashiers(c);
      setPersons(p);
      setStores(s);
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const personMap = Object.fromEntries(persons.map(p => [p.id, `${p.firstName} ${p.lastName}`]));
  const storeMap = Object.fromEntries(stores.map(s => [s.id, s.name]));

  function openCreate() {
    setEditing(null); setForm(emptyForm); setErrors({}); setModalOpen(true);
  }

  function openEdit(c) {
    setEditing(c);
    setForm({
      number: c.number || '',
      password: '', // never prefill
      personId: c.personId ?? '',
      storeId: c.storeId ?? '',
      role: c.role || '',
    });
    setErrors({});
    setModalOpen(true);
  }

  async function handleSave() {
    setSaving(true); setErrors({});
    const payload = {
      ...form,
      personId: form.personId !== '' ? Number(form.personId) : null,
      storeId: form.storeId !== '' ? Number(form.storeId) : null,
    };
    if (editing && !payload.password) delete payload.password;
    try {
      if (editing) {
        await cashierApi.update(editing.id, payload);
        setAlert({ type: 'success', message: 'Cashier updated' });
      } else {
        await cashierApi.create(payload);
        setAlert({ type: 'success', message: 'Cashier created' });
      }
      setModalOpen(false);
      load();
    } catch (e) {
      if (e.fieldErrors) {
        const m = {};
        e.fieldErrors.forEach(fe => { m[fe.field] = fe.message; });
        setErrors(m);
      } else {
        setAlert({ type: 'error', message: e.message });
      }
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete() {
    setDeleting(true);
    try {
      await cashierApi.delete(deleteTarget.id);
      setAlert({ type: 'success', message: 'Cashier deleted' });
      setDeleteTarget(null);
      load();
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    } finally {
      setDeleting(false);
    }
  }

  async function handleTerminate(c) {
    try {
      await cashierApi.terminate(c.id);
      setAlert({ type: 'success', message: `Cashier ${c.number} terminated` });
      load();
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    }
  }

  const filtered = cashiers.filter(c =>
    !search || [c.number, c.role, personMap[c.personId], storeMap[c.storeId]]
      .filter(Boolean).some(v => v.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div>
      <div className="page-header">
        <div><h2>Cashiers</h2><p>Manage cashier accounts</p></div>
        <button className="btn btn-primary" onClick={openCreate}><Plus size={16} /> Add Cashier</button>
      </div>

      <Alert type={alert?.type} message={alert?.message} onDismiss={() => setAlert(null)} />

      <div className="toolbar">
        <input className="search-input" placeholder="Search cashiers..." value={search} onChange={e => setSearch(e.target.value)} />
        <span style={{ fontSize: 13, color: 'var(--gray-500)' }}>{filtered.length} cashier{filtered.length !== 1 ? 's' : ''}</span>
      </div>

      <div className="card">
        {loading ? (
          <div className="loading"><div className="spinner" /> Loading cashiers...</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state"><h3>No cashiers found</h3></div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Number</th>
                  <th>Person</th>
                  <th>Store</th>
                  <th>Role</th>
                  <th>Status</th>
                  <th>Hire Date</th>
                  <th style={{ width: 120 }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map(c => (
                  <tr key={c.id}>
                    <td><strong>{c.number}</strong></td>
                    <td>{personMap[c.personId] || c.personId || '—'}</td>
                    <td>{storeMap[c.storeId] || c.storeId || '—'}</td>
                    <td><span className="badge badge-blue">{c.role || '—'}</span></td>
                    <td>
                      <span className={`badge ${(c.isActive ?? c.active) ? 'badge-green' : 'badge-red'}`}>
                        {(c.isActive ?? c.active) ? 'Active' : 'Terminated'}
                      </span>
                    </td>
                    <td>{c.hireDate ? new Date(c.hireDate).toLocaleDateString() : '—'}</td>
                    <td>
                      <div style={{ display: 'flex', gap: 4 }}>
                        <button className="btn-icon" title="Edit" onClick={() => openEdit(c)}><Pencil size={16} /></button>
                        {(c.isActive ?? c.active) && (
                          <button className="btn-icon" title="Terminate" onClick={() => handleTerminate(c)}>
                            <UserX size={16} />
                          </button>
                        )}
                        <button className="btn-icon danger" title="Delete" onClick={() => setDeleteTarget(c)}><Trash2 size={16} /></button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <Modal
        open={modalOpen}
        title={editing ? 'Edit Cashier' : 'Create Cashier'}
        onClose={() => setModalOpen(false)}
        footer={
          <>
            <button className="btn btn-secondary" onClick={() => setModalOpen(false)}>Cancel</button>
            <button className="btn btn-primary" onClick={handleSave} disabled={saving}>
              {saving ? 'Saving...' : editing ? 'Update' : 'Create'}
            </button>
          </>
        }
      >
        <div className="form-grid">
          <div className="form-group">
            <label>Cashier Number</label>
            <input value={form.number} onChange={e => setForm({ ...form, number: e.target.value })} />
            {errors.number && <div className="error-text">{errors.number}</div>}
          </div>
          <div className="form-group">
            <label>Password</label>
            <input type="password" value={form.password}
              placeholder={editing ? 'Leave blank to keep current' : ''}
              onChange={e => setForm({ ...form, password: e.target.value })} />
            {errors.password && <div className="error-text">{errors.password}</div>}
          </div>
          <div className="form-group">
            <label>Person</label>
            <select value={form.personId} onChange={e => setForm({ ...form, personId: e.target.value })}>
              <option value="">— Select Person —</option>
              {persons.map(p => <option key={p.id} value={p.id}>{p.firstName} {p.lastName}</option>)}
            </select>
            {errors.personId && <div className="error-text">{errors.personId}</div>}
          </div>
          <div className="form-group">
            <label>Store</label>
            <select value={form.storeId} onChange={e => setForm({ ...form, storeId: e.target.value })}>
              <option value="">— Select Store —</option>
              {stores.map(s => <option key={s.id} value={s.id}>{s.name} ({s.number})</option>)}
            </select>
            {errors.storeId && <div className="error-text">{errors.storeId}</div>}
          </div>
          <div className="form-group">
            <label>Role</label>
            <select value={form.role} onChange={e => setForm({ ...form, role: e.target.value })}>
              <option value="">— Select Role —</option>
              <option value="cashier">Cashier</option>
              <option value="senior_cashier">Senior Cashier</option>
              <option value="supervisor">Supervisor</option>
              <option value="manager">Manager</option>
            </select>
            {errors.role && <div className="error-text">{errors.role}</div>}
          </div>
        </div>
      </Modal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Cashier"
        message={`Delete cashier "${deleteTarget?.number}"?`}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
        loading={deleting}
      />
    </div>
  );
}
