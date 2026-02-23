import { useEffect, useState, useCallback } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { personApi } from '../api';
import Modal from '../components/Modal';
import Alert from '../components/Alert';
import ConfirmDialog from '../components/ConfirmDialog';

const emptyForm = {
  firstName: '', lastName: '', address: '', city: '', state: '', zip: '', phone: '', ssn: '',
};

export default function Persons() {
  const [persons, setPersons] = useState([]);
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
      setPersons(await personApi.getAll());
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  function openCreate() {
    setEditing(null); setForm(emptyForm); setErrors({}); setModalOpen(true);
  }

  function openEdit(p) {
    setEditing(p);
    setForm({
      firstName: p.firstName || '',
      lastName: p.lastName || '',
      address: p.address || '',
      city: p.city || '',
      state: p.state || '',
      zip: p.zip || '',
      phone: p.phone || '',
      ssn: '', // never prefill SSN
    });
    setErrors({});
    setModalOpen(true);
  }

  async function handleSave() {
    setSaving(true); setErrors({});
    const payload = { ...form };
    // For update, if SSN is blank, remove it so backend doesn't require it
    if (editing && !payload.ssn) delete payload.ssn;
    try {
      if (editing) {
        await personApi.update(editing.id, payload);
        setAlert({ type: 'success', message: 'Person updated' });
      } else {
        await personApi.create(payload);
        setAlert({ type: 'success', message: 'Person created' });
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
      await personApi.delete(deleteTarget.id);
      setAlert({ type: 'success', message: 'Person deleted' });
      setDeleteTarget(null);
      load();
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    } finally {
      setDeleting(false);
    }
  }

  const filtered = persons.filter(p =>
    !search || [p.firstName, p.lastName, p.city, p.state, p.phone]
      .filter(Boolean).some(v => v.toLowerCase().includes(search.toLowerCase()))
  );

  function field(name, label, type = 'text', placeholder = '') {
    return (
      <div className="form-group">
        <label>{label}</label>
        <input type={type} value={form[name]} placeholder={placeholder}
          onChange={e => setForm({ ...form, [name]: e.target.value })} />
        {errors[name] && <div className="error-text">{errors[name]}</div>}
      </div>
    );
  }

  return (
    <div>
      <div className="page-header">
        <div><h2>Persons</h2><p>Manage people in the system</p></div>
        <button className="btn btn-primary" onClick={openCreate}><Plus size={16} /> Add Person</button>
      </div>

      <Alert type={alert?.type} message={alert?.message} onDismiss={() => setAlert(null)} />

      <div className="toolbar">
        <input className="search-input" placeholder="Search persons..." value={search} onChange={e => setSearch(e.target.value)} />
        <span style={{ fontSize: 13, color: 'var(--gray-500)' }}>{filtered.length} person{filtered.length !== 1 ? 's' : ''}</span>
      </div>

      <div className="card">
        {loading ? (
          <div className="loading"><div className="spinner" /> Loading persons...</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state"><h3>No persons found</h3><p>{search ? 'Try a different search' : 'Add your first person'}</p></div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>City</th>
                  <th>State</th>
                  <th>Phone</th>
                  <th>SSN</th>
                  <th style={{ width: 90 }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map(p => (
                  <tr key={p.id}>
                    <td>{p.id}</td>
                    <td><strong>{p.firstName} {p.lastName}</strong></td>
                    <td>{p.city || '—'}</td>
                    <td>{p.state || '—'}</td>
                    <td>{p.phone || '—'}</td>
                    <td><span className="badge badge-gray">{p.maskedSsn || '—'}</span></td>
                    <td>
                      <div style={{ display: 'flex', gap: 4 }}>
                        <button className="btn-icon" title="Edit" onClick={() => openEdit(p)}><Pencil size={16} /></button>
                        <button className="btn-icon danger" title="Delete" onClick={() => setDeleteTarget(p)}><Trash2 size={16} /></button>
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
        title={editing ? 'Edit Person' : 'Create Person'}
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
          {field('firstName', 'First Name')}
          {field('lastName', 'Last Name')}
          {field('address', 'Address')}
          {field('city', 'City')}
          {field('state', 'State', 'text', 'e.g. CA')}
          {field('zip', 'ZIP Code')}
          {field('phone', 'Phone')}
          {field('ssn', 'SSN', 'text', editing ? 'Leave blank to keep current' : '123-45-6789')}
        </div>
      </Modal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Person"
        message={`Delete "${deleteTarget?.firstName} ${deleteTarget?.lastName}"?`}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
        loading={deleting}
      />
    </div>
  );
}
