import { useEffect, useState, useCallback } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { taxCategoryApi } from '../api';
import Modal from '../components/Modal';
import Alert from '../components/Alert';
import ConfirmDialog from '../components/ConfirmDialog';

const emptyForm = { category: '', description: '' };

export default function TaxCategories() {
  const [categories, setCategories] = useState([]);
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
      setCategories(await taxCategoryApi.getAll());
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

  function openEdit(tc) {
    setEditing(tc);
    setForm({ category: tc.category || '', description: tc.description || '' });
    setErrors({});
    setModalOpen(true);
  }

  async function handleSave() {
    setSaving(true); setErrors({});
    try {
      if (editing) {
        await taxCategoryApi.update(editing.id, form);
        setAlert({ type: 'success', message: 'Tax category updated' });
      } else {
        await taxCategoryApi.create(form);
        setAlert({ type: 'success', message: 'Tax category created' });
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
      await taxCategoryApi.delete(deleteTarget.id);
      setAlert({ type: 'success', message: 'Tax category deleted' });
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
        <div><h2>Tax Categories</h2><p>Manage tax classifications</p></div>
        <button className="btn btn-primary" onClick={openCreate}><Plus size={16} /> Add Category</button>
      </div>

      <Alert type={alert?.type} message={alert?.message} onDismiss={() => setAlert(null)} />

      <div className="card">
        {loading ? (
          <div className="loading"><div className="spinner" /> Loading...</div>
        ) : categories.length === 0 ? (
          <div className="empty-state"><h3>No tax categories</h3><p>Create one to get started</p></div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Category</th>
                  <th>Description</th>
                  <th>Status</th>
                  <th style={{ width: 90 }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {categories.map(tc => (
                  <tr key={tc.id}>
                    <td>{tc.id}</td>
                    <td><strong>{tc.category}</strong></td>
                    <td>{tc.description || '—'}</td>
                    <td>
                      <span className={`badge ${(tc.isActive ?? tc.active) ? 'badge-green' : 'badge-red'}`}>
                        {(tc.isActive ?? tc.active) ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: 4 }}>
                        <button className="btn-icon" title="Edit" onClick={() => openEdit(tc)}><Pencil size={16} /></button>
                        <button className="btn-icon danger" title="Delete" onClick={() => setDeleteTarget(tc)}><Trash2 size={16} /></button>
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
        title={editing ? 'Edit Tax Category' : 'Create Tax Category'}
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
          <label>Category Name</label>
          <input value={form.category} onChange={e => setForm({ ...form, category: e.target.value })} />
          {errors.category && <div className="error-text">{errors.category}</div>}
        </div>
        <div className="form-group">
          <label>Description</label>
          <textarea rows={3} value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} />
          {errors.description && <div className="error-text">{errors.description}</div>}
        </div>
      </Modal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Tax Category"
        message={`Delete "${deleteTarget?.category}"?`}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
        loading={deleting}
      />
    </div>
  );
}
