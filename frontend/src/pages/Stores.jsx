import { useEffect, useState, useCallback } from 'react';
import { Plus, Pencil, Trash2, Search, Power, PowerOff } from 'lucide-react';
import { storeApi } from '../api';
import Modal from '../components/Modal';
import Alert from '../components/Alert';
import ConfirmDialog from '../components/ConfirmDialog';

const emptyForm = {
  number: '', name: '', address: '', city: '', state: '', zip: '', phone: '', email: '', manager: '',
};

export default function Stores() {
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
      const data = await storeApi.getAll();
      setStores(data);
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  function openCreate() {
    setEditing(null);
    setForm(emptyForm);
    setErrors({});
    setModalOpen(true);
  }

  function openEdit(store) {
    setEditing(store);
    setForm({
      number: store.number || '',
      name: store.name || '',
      address: store.address || '',
      city: store.city || '',
      state: store.state || '',
      zip: store.zip || '',
      phone: store.phone || '',
      email: store.email || '',
      manager: store.manager || '',
    });
    setErrors({});
    setModalOpen(true);
  }

  async function handleSave() {
    setSaving(true);
    setErrors({});
    try {
      if (editing) {
        await storeApi.update(editing.id, form);
        setAlert({ type: 'success', message: 'Store updated successfully' });
      } else {
        await storeApi.create(form);
        setAlert({ type: 'success', message: 'Store created successfully' });
      }
      setModalOpen(false);
      load();
    } catch (e) {
      if (e.fieldErrors) {
        const fieldMap = {};
        e.fieldErrors.forEach(fe => { fieldMap[fe.field] = fe.message; });
        setErrors(fieldMap);
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
      await storeApi.delete(deleteTarget.id);
      setAlert({ type: 'success', message: 'Store deleted' });
      setDeleteTarget(null);
      load();
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    } finally {
      setDeleting(false);
    }
  }

  async function toggleActive(store) {
    try {
      if (store.isActive || store.active) {
        await storeApi.deactivate(store.id);
      } else {
        await storeApi.activate(store.id);
      }
      load();
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    }
  }

  const filtered = stores.filter(s =>
    !search || [s.name, s.number, s.city, s.state, s.manager]
      .filter(Boolean).some(v => v.toLowerCase().includes(search.toLowerCase()))
  );

  function field(name, label, type = 'text') {
    return (
      <div className="form-group">
        <label>{label}</label>
        <input
          type={type}
          value={form[name]}
          onChange={e => setForm({ ...form, [name]: e.target.value })}
        />
        {errors[name] && <div className="error-text">{errors[name]}</div>}
      </div>
    );
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <h2>Stores</h2>
          <p>Manage your store locations</p>
        </div>
        <button className="btn btn-primary" onClick={openCreate}>
          <Plus size={16} /> Add Store
        </button>
      </div>

      <Alert type={alert?.type} message={alert?.message} onDismiss={() => setAlert(null)} />

      <div className="toolbar">
        <input
          className="search-input"
          placeholder="Search stores..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
        <span style={{ fontSize: 13, color: 'var(--gray-500)' }}>
          {filtered.length} store{filtered.length !== 1 ? 's' : ''}
        </span>
      </div>

      <div className="card">
        {loading ? (
          <div className="loading"><div className="spinner" /> Loading stores...</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <h3>No stores found</h3>
            <p>{search ? 'Try a different search term' : 'Create your first store to get started'}</p>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Number</th>
                  <th>Name</th>
                  <th>City</th>
                  <th>State</th>
                  <th>Manager</th>
                  <th>Status</th>
                  <th style={{ width: 120 }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map(s => (
                  <tr key={s.id}>
                    <td><strong>{s.number}</strong></td>
                    <td>{s.name}</td>
                    <td>{s.city || '—'}</td>
                    <td>{s.state || '—'}</td>
                    <td>{s.manager || '—'}</td>
                    <td>
                      <span className={`badge ${(s.isActive ?? s.active) ? 'badge-green' : 'badge-red'}`}>
                        {(s.isActive ?? s.active) ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: 4 }}>
                        <button className="btn-icon" title="Edit" onClick={() => openEdit(s)}>
                          <Pencil size={16} />
                        </button>
                        <button
                          className="btn-icon"
                          title={(s.isActive ?? s.active) ? 'Deactivate' : 'Activate'}
                          onClick={() => toggleActive(s)}
                        >
                          {(s.isActive ?? s.active) ? <PowerOff size={16} /> : <Power size={16} />}
                        </button>
                        <button className="btn-icon danger" title="Delete" onClick={() => setDeleteTarget(s)}>
                          <Trash2 size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Create/Edit Modal */}
      <Modal
        open={modalOpen}
        title={editing ? 'Edit Store' : 'Create Store'}
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
          {field('number', 'Store Number')}
          {field('name', 'Store Name')}
          {field('address', 'Address')}
          {field('city', 'City')}
          {field('state', 'State')}
          {field('zip', 'ZIP Code')}
          {field('phone', 'Phone')}
          {field('email', 'Email', 'email')}
        </div>
        {field('manager', 'Manager')}
      </Modal>

      {/* Delete Confirmation */}
      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Store"
        message={`Are you sure you want to delete store "${deleteTarget?.name}"? This action cannot be undone.`}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
        loading={deleting}
      />
    </div>
  );
}
