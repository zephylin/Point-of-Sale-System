import { useEffect, useState, useCallback } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { itemApi, storeApi } from '../api';
import Modal from '../components/Modal';
import Alert from '../components/Alert';
import ConfirmDialog from '../components/ConfirmDialog';

const emptyForm = {
  number: '', description: '', price: '', cost: '', quantity: '', minQuantity: '', maxQuantity: '',
  taxCategoryId: '', storeId: '', barcode: '', sku: '', brand: '', category: '', isTaxable: true,
};

export default function Items() {
  const [items, setItems] = useState([]);
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
      const [itemData, storeData] = await Promise.all([itemApi.getAll(), storeApi.getAll().catch(() => [])]);
      setItems(itemData);
      setStores(storeData);
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

  function openEdit(item) {
    setEditing(item);
    setForm({
      number: item.number || '',
      description: item.description || '',
      price: item.price ?? '',
      cost: item.cost ?? '',
      quantity: item.quantity ?? '',
      minQuantity: item.minQuantity ?? '',
      maxQuantity: item.maxQuantity ?? '',
      taxCategoryId: item.taxCategoryId ?? '',
      storeId: item.storeId ?? '',
      barcode: item.barcode || '',
      sku: item.sku || '',
      brand: item.brand || '',
      category: item.category || '',
      isTaxable: item.isTaxable ?? true,
    });
    setErrors({});
    setModalOpen(true);
  }

  async function handleSave() {
    setSaving(true); setErrors({});
    const payload = {
      ...form,
      price: form.price !== '' ? Number(form.price) : null,
      cost: form.cost !== '' ? Number(form.cost) : null,
      quantity: form.quantity !== '' ? Number(form.quantity) : null,
      minQuantity: form.minQuantity !== '' ? Number(form.minQuantity) : null,
      maxQuantity: form.maxQuantity !== '' ? Number(form.maxQuantity) : null,
      taxCategoryId: form.taxCategoryId !== '' ? Number(form.taxCategoryId) : null,
      storeId: form.storeId !== '' ? Number(form.storeId) : null,
    };
    try {
      if (editing) {
        await itemApi.update(editing.id, payload);
        setAlert({ type: 'success', message: 'Item updated' });
      } else {
        await itemApi.create(payload);
        setAlert({ type: 'success', message: 'Item created' });
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
      await itemApi.delete(deleteTarget.id);
      setAlert({ type: 'success', message: 'Item deleted' });
      setDeleteTarget(null);
      load();
    } catch (e) {
      setAlert({ type: 'error', message: e.message });
    } finally {
      setDeleting(false);
    }
  }

  const filtered = items.filter(i =>
    !search || [i.number, i.description, i.brand, i.category, i.sku, i.barcode]
      .filter(Boolean).some(v => v.toLowerCase().includes(search.toLowerCase()))
  );

  const storeMap = Object.fromEntries(stores.map(s => [s.id, s.name]));

  function field(name, label, type = 'text') {
    return (
      <div className="form-group">
        <label>{label}</label>
        <input type={type} value={form[name]} onChange={e => setForm({ ...form, [name]: e.target.value })} />
        {errors[name] && <div className="error-text">{errors[name]}</div>}
      </div>
    );
  }

  return (
    <div>
      <div className="page-header">
        <div><h2>Items</h2><p>Manage inventory items</p></div>
        <button className="btn btn-primary" onClick={openCreate}><Plus size={16} /> Add Item</button>
      </div>

      <Alert type={alert?.type} message={alert?.message} onDismiss={() => setAlert(null)} />

      <div className="toolbar">
        <input className="search-input" placeholder="Search items..." value={search} onChange={e => setSearch(e.target.value)} />
        <span style={{ fontSize: 13, color: 'var(--gray-500)' }}>{filtered.length} item{filtered.length !== 1 ? 's' : ''}</span>
      </div>

      <div className="card">
        {loading ? (
          <div className="loading"><div className="spinner" /> Loading items...</div>
        ) : filtered.length === 0 ? (
          <div className="empty-state"><h3>No items found</h3><p>{search ? 'Try a different search' : 'Add your first item'}</p></div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Number</th>
                  <th>Description</th>
                  <th>Price</th>
                  <th>Qty</th>
                  <th>Category</th>
                  <th>Store</th>
                  <th>Status</th>
                  <th style={{ width: 90 }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map(i => (
                  <tr key={i.id}>
                    <td><strong>{i.number}</strong></td>
                    <td>{i.description}</td>
                    <td>${Number(i.price).toFixed(2)}</td>
                    <td>
                      <span className={`badge ${i.quantity <= 0 ? 'badge-red' : i.quantity <= (i.minQuantity || 5) ? 'badge-yellow' : 'badge-green'}`}>
                        {i.quantity}
                      </span>
                    </td>
                    <td>{i.category || '—'}</td>
                    <td>{storeMap[i.storeId] || i.storeId || '—'}</td>
                    <td>
                      <span className={`badge ${(i.isActive ?? i.active) ? 'badge-green' : 'badge-red'}`}>
                        {(i.isActive ?? i.active) ? 'Active' : 'Inactive'}
                      </span>
                    </td>
                    <td>
                      <div style={{ display: 'flex', gap: 4 }}>
                        <button className="btn-icon" title="Edit" onClick={() => openEdit(i)}><Pencil size={16} /></button>
                        <button className="btn-icon danger" title="Delete" onClick={() => setDeleteTarget(i)}><Trash2 size={16} /></button>
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
        title={editing ? 'Edit Item' : 'Create Item'}
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
          {field('number', 'Item Number')}
          {field('description', 'Description')}
          {field('price', 'Price', 'number')}
          {field('cost', 'Cost', 'number')}
          {field('quantity', 'Quantity', 'number')}
          {field('minQuantity', 'Min Quantity', 'number')}
          {field('maxQuantity', 'Max Quantity', 'number')}
          {field('barcode', 'Barcode')}
          {field('sku', 'SKU')}
          {field('brand', 'Brand')}
          {field('category', 'Category')}
          <div className="form-group">
            <label>Store</label>
            <select value={form.storeId} onChange={e => setForm({ ...form, storeId: e.target.value })}>
              <option value="">— Select Store —</option>
              {stores.map(s => <option key={s.id} value={s.id}>{s.name} ({s.number})</option>)}
            </select>
            {errors.storeId && <div className="error-text">{errors.storeId}</div>}
          </div>
        </div>
        <div className="form-group">
          <label style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <input type="checkbox" checked={form.isTaxable} onChange={e => setForm({ ...form, isTaxable: e.target.checked })} />
            Taxable
          </label>
        </div>
      </Modal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete Item"
        message={`Delete item "${deleteTarget?.description}"?`}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
        loading={deleting}
      />
    </div>
  );
}
