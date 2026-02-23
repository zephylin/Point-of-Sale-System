import { CheckCircle, XCircle, Info, X } from 'lucide-react';

export default function Alert({ type = 'info', message, onDismiss }) {
  if (!message) return null;
  const icons = { success: CheckCircle, error: XCircle, info: Info };
  const Icon = icons[type] || Info;

  return (
    <div className={`alert alert-${type}`}>
      <Icon size={18} />
      <span style={{ flex: 1 }}>{message}</span>
      {onDismiss && (
        <button className="btn-icon" onClick={onDismiss} style={{ marginLeft: 'auto' }}>
          <X size={16} />
        </button>
      )}
    </div>
  );
}
