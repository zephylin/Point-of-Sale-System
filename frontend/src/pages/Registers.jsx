import { useEffect, useState, useCallback } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { registerApi, storeApi } from '../api';
import Modal from '../components/Modal';
import Alert from '../components/Alert';
import ConfirmDialog from '../components/ConfirmDialog';

const emptyForm = { number: '', storeId: '', description: '' };

export default function Registers() {
  const [registers, setRegisters] = useState([]);
  const [stores, setStores] = useState([]);
  const [loading, setLoading] = useState(true);
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
      const [r, s] = await Promise.all([registerApi.getAll(), storeApi.getAll().catch(() => [])]);
      setRegisters(r);
      setStores(s);
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const storeMap = Object.fromEntries(stores.map(s => [s.id, s.name]));

  function openCreate() {
    setEditing(null); setForm(emptyForm); setErrors({}); setModalOpen(true);
  }

  function openEdit(r) {
    setEditing(r);
    setForm({
      number: r.number || '',
      storeId: r.storeId ?? '',
      description: r.description || '',
    });
    setErrors({});
    setModalOpen(true);
  }

  async function handleSave() {
    setSaving(true); setErrors({});
    const payload = {
      ...form,
      storeId: form.storeId !== '' ? Number(form.storeId) : null,
    };
    try {
      if (editing) {
        await registerApi.update(editing.id, payload);
        setAlert({ type: 'success', message: 'Register updated' });
      } else {
        await registerApi.create(payload);
        setAlert({ type: 'success', message: 'Register created' });
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
      await registerApi.delete(deleteTarget.id);
      setAlert({ type: 'success', message: 'Register deleted' });
      setDeleteTarget(null);
      load();
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    } finally {
      setDeleting(false);
    }
  }

  return (
    <div>
      <div className="page-header">
        <div><h2>Registers</h2><p>Manage POS registers</p></div>
        <button className="btn btn-primary" onClick={openCreate}><Plus size={16} /> Add Register</button>
      </div>

      <Alert type={alert?.type} message={alert?.message} onDismiss={() => setAlert(null)} />

      <div className="card">
        {loading ? (
          <div className="loading"><div className="spinner" /> Loading...</div>
        ) : registers.length === 0 ? (
          <div className="empty-state"><h3>No registers</h3><p>Create one to get started</p></div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Number</th>
                  <th>Store</th>
                  <th>Description</th>
                  <th>Status</th>
                  <th>Installed</th>
                  <th style={{ width: 90 }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {registers.map(r => (
                  <tr key={r.id}>
                    <td><strong>{r.number}</strong></td>
                    <td>{storeMap[r.storeId] || r.storeId || '—'}</td>
                    <td>{r.description || '—'}</td>
                    <td>
                      <span className={`badge ${r.status === 'ACTIVE' || (r.isActive ?? r.active) ? 'badge-green' : 'badge-yellow'}`}>
                        {r.status || ((r.isActive ?? r.active) ? 'Active' : 'Inactive')}
                      </span>
                    </td>
                    <td>{r.installedDate ? new Date(r.installedDate).toLocaleDateString() : '—'}</td>
                    <td>
                      <div style={{ display: 'flex', gap: 4 }}>
                        <button className="btn-icon" title="Edit" onClick={() => openEdit(r)}><Pencil size={16} /></button>
                        <button className="btn-icon danger" title="Delete" onClick={() => setDeleteTarget(r)}><Trash2 size={16} /></button>
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
        title={editing ? 'Edit Register' : 'Create Register'}
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
        <div className="form-group">
          <label>Register Number</label>
          <input value={form.number} onChange={e => setForm({ ...form, number: e.target.value })} />
          {errors.number && <div className="error-text">{errors.number}</div>}
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
          <label>Description</label>
          <textarea rows={3} value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} />
          {errors.description && <div className="error-text">{errors.description}</div>}
        </div>
      </Modal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Register"
        message={`Delete register "${deleteTarget?.number}"?`}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
        loading={deleting}
      />
    </div>
  );
}
