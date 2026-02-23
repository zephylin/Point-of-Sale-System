import { Trash2, AlertCircle } from 'lucide-react';
import Modal from './Modal';

export default function ConfirmDialog({ open, title, message, onConfirm, onCancel, loading }) {
  return (
    <Modal
      open={open}
      title={title || 'Confirm Action'}
      onClose={onCancel}
      footer={
        <>
          <button className="btn btn-secondary" onClick={onCancel} disabled={loading}>
            Cancel
          </button>
          <button className="btn btn-danger" onClick={onConfirm} disabled={loading}>
            {loading ? 'Deleting...' : 'Delete'}
          </button>
        </>
      }
    >
      <div style={{ display: 'flex', gap: 12, alignItems: 'flex-start' }}>
        <AlertCircle size={20} style={{ color: 'var(--danger)', flexShrink: 0, marginTop: 2 }} />
        <p style={{ fontSize: 14, color: 'var(--gray-600)' }}>
          {message || 'Are you sure you want to delete this item? This action cannot be undone.'}
        </p>
      </div>
    </Modal>
  );
}
