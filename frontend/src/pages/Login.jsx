import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { LogIn } from 'lucide-react';
import api from '../api/client';

export default function Login({ onLogin }) {
  const [cashierNumber, setCashierNumber] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const { data } = await api.post('/auth/login', { cashierNumber, password });
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify({
        cashierNumber: data.cashierNumber,
        name: data.name,
        role: data.role,
      }));
      onLogin(data);
      navigate('/');
    } catch (err) {
      setError(err.status === 401 ? 'Invalid cashier number or password' : err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-header">
          <LogIn size={32} />
          <h1>POS System</h1>
          <p>Sign in to continue</p>
        </div>

        {error && <div className="login-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="login-field">
            <label htmlFor="cashierNumber">Cashier Number</label>
            <input
              id="cashierNumber"
              type="text"
              value={cashierNumber}
              onChange={(e) => setCashierNumber(e.target.value)}
              placeholder="e.g. C001"
              required
              autoFocus
            />
          </div>
          <div className="login-field">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter password"
              required
            />
          </div>
          <button type="submit" className="login-btn" disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>

        <div className="login-hint">
          <p>Demo accounts: C001 / password1 (Supervisor), C002 / password2 (Cashier)</p>
        </div>
      </div>
    </div>
  );
}
