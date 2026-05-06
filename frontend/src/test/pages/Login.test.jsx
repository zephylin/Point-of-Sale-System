import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import Login from '../../pages/Login';

// Mock the api client
vi.mock('../../api/client', () => ({
  default: {
    post: vi.fn(),
  },
}));

// Mock useNavigate
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useNavigate: () => mockNavigate };
});

import api from '../../api/client';

describe('Login', () => {
  const mockOnLogin = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    localStorage.clear();
  });

  function renderLogin() {
    return render(
      <MemoryRouter>
        <Login onLogin={mockOnLogin} />
      </MemoryRouter>
    );
  }

  it('renders login form with required fields', () => {
    renderLogin();
    expect(screen.getByLabelText(/cashier number/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
  });

  it('renders the POS System heading', () => {
    renderLogin();
    expect(screen.getByText('POS System')).toBeInTheDocument();
    expect(screen.getByText('Sign in to continue')).toBeInTheDocument();
  });

  it('renders demo account hints', () => {
    renderLogin();
    expect(screen.getByText(/Demo accounts/)).toBeInTheDocument();
  });

  it('allows typing in cashier number and password fields', async () => {
    const user = userEvent.setup();
    renderLogin();
    const numberInput = screen.getByLabelText(/cashier number/i);
    const passwordInput = screen.getByLabelText(/password/i);

    await user.type(numberInput, 'C001');
    await user.type(passwordInput, 'password1');

    expect(numberInput).toHaveValue('C001');
    expect(passwordInput).toHaveValue('password1');
  });

  it('calls API and onLogin on successful submit', async () => {
    const user = userEvent.setup();
    const loginResponse = {
      token: 'jwt-token-123',
      cashierNumber: 'C001',
      name: 'David',
      role: 'SUPERVISOR',
    };
    api.post.mockResolvedValueOnce({ data: loginResponse });

    renderLogin();
    await user.type(screen.getByLabelText(/cashier number/i), 'C001');
    await user.type(screen.getByLabelText(/password/i), 'password1');
    await user.click(screen.getByRole('button', { name: /sign in/i }));

    await waitFor(() => {
      expect(api.post).toHaveBeenCalledWith('/auth/login', {
        cashierNumber: 'C001',
        password: 'password1',
      });
    });

    await waitFor(() => {
      expect(mockOnLogin).toHaveBeenCalledWith(loginResponse);
    });

    expect(localStorage.getItem('token')).toBe('jwt-token-123');
    expect(mockNavigate).toHaveBeenCalledWith('/');
  });

  it('displays error message on 401 response', async () => {
    const user = userEvent.setup();
    api.post.mockRejectedValueOnce({ message: 'Unauthorized', status: 401 });

    renderLogin();
    await user.type(screen.getByLabelText(/cashier number/i), 'C001');
    await user.type(screen.getByLabelText(/password/i), 'wrong');
    await user.click(screen.getByRole('button', { name: /sign in/i }));

    await waitFor(() => {
      expect(screen.getByText('Invalid cashier number or password')).toBeInTheDocument();
    });
    expect(mockOnLogin).not.toHaveBeenCalled();
  });

  it('displays generic error message on network failure', async () => {
    const user = userEvent.setup();
    api.post.mockRejectedValueOnce({ message: 'Network Error', status: undefined });

    renderLogin();
    await user.type(screen.getByLabelText(/cashier number/i), 'C001');
    await user.type(screen.getByLabelText(/password/i), 'test');
    await user.click(screen.getByRole('button', { name: /sign in/i }));

    await waitFor(() => {
      expect(screen.getByText('Network Error')).toBeInTheDocument();
    });
  });

  it('shows loading state while submitting', async () => {
    const user = userEvent.setup();
    let resolveLogin;
    api.post.mockReturnValueOnce(new Promise((resolve) => { resolveLogin = resolve; }));

    renderLogin();
    await user.type(screen.getByLabelText(/cashier number/i), 'C001');
    await user.type(screen.getByLabelText(/password/i), 'pass');
    await user.click(screen.getByRole('button', { name: /sign in/i }));

    expect(screen.getByText('Signing in...')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /signing in/i })).toBeDisabled();

    // Resolve to clean up
    resolveLogin({ data: { token: 't', cashierNumber: 'C001', name: 'Test', role: 'CASHIER' } });
    await waitFor(() => {
      expect(screen.queryByText('Signing in...')).not.toBeInTheDocument();
    });
  });
});
