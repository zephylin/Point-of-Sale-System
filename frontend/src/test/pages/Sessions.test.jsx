import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import Sessions from '../../pages/Sessions';

vi.mock('../../api', () => ({
  sessionApi: {
    getAll: vi.fn(),
    close: vi.fn(),
    delete: vi.fn(),
  },
  saleApi: {
    getBySession: vi.fn(),
  },
}));

import { sessionApi, saleApi } from '../../api';

const mockSessions = [
  {
    id: 1,
    cashierId: 10,
    cashierName: 'David',
    registerId: 1,
    registerNumber: 'R001',
    startDateTime: '2026-05-10T08:00:00',
    endDateTime: null,
    startingCash: 100.00,
    endingCash: null,
    expectedCash: null,
    cashVariance: null,
    status: 'ACTIVE',
    notes: null,
  },
  {
    id: 2,
    cashierId: 11,
    cashierName: 'Sally',
    registerId: 2,
    registerNumber: 'R002',
    startDateTime: '2026-05-09T09:00:00',
    endDateTime: '2026-05-09T17:30:00',
    startingCash: 100.00,
    endingCash: 345.50,
    expectedCash: 350.00,
    cashVariance: -4.50,
    status: 'CLOSED',
    notes: 'Shift went smoothly',
  },
  {
    id: 3,
    cashierId: 10,
    cashierName: 'David',
    registerId: 1,
    registerNumber: 'R001',
    startDateTime: '2026-05-08T08:00:00',
    endDateTime: '2026-05-08T16:00:00',
    startingCash: 100.00,
    endingCash: 250.00,
    expectedCash: 250.00,
    cashVariance: 0,
    status: 'CLOSED',
    notes: null,
  },
];

describe('Sessions', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  function renderSessions() {
    return render(
      <MemoryRouter>
        <Sessions />
      </MemoryRouter>
    );
  }

  it('shows loading state initially', () => {
    sessionApi.getAll.mockReturnValue(new Promise(() => {}));
    renderSessions();
    expect(screen.getByText(/loading sessions/i)).toBeInTheDocument();
  });

  it('shows error when API fails', async () => {
    sessionApi.getAll.mockRejectedValue(new Error('Network error'));
    renderSessions();
    await waitFor(() => {
      expect(screen.getByText(/failed to load sessions/i)).toBeInTheDocument();
    });
  });

  it('displays sessions table after loading', async () => {
    sessionApi.getAll.mockResolvedValue(mockSessions);
    renderSessions();
    await waitFor(() => {
      expect(screen.getAllByText('David').length).toBeGreaterThan(0);
    });
    expect(screen.getByText('Sally')).toBeInTheDocument();
    expect(screen.getAllByText('R001').length).toBeGreaterThan(0);
    expect(screen.getByText('R002')).toBeInTheDocument();
  });

  it('shows active sessions count in stats', async () => {
    sessionApi.getAll.mockResolvedValue(mockSessions);
    renderSessions();
    await waitFor(() => {
      expect(screen.getByText('Active Sessions')).toBeInTheDocument();
    });
    // 1 active session
    const statCards = document.querySelectorAll('.stat-card');
    expect(statCards[0].querySelector('.stat-value').textContent).toBe('1');
  });

  it('filters sessions by status', async () => {
    const user = userEvent.setup();
    sessionApi.getAll.mockResolvedValue(mockSessions);
    renderSessions();

    await waitFor(() => {
      expect(screen.getAllByText('David').length).toBeGreaterThan(0);
    });

    const statusSelect = screen.getAllByRole('combobox')[0];
    await user.selectOptions(statusSelect, 'CLOSED');

    // David's ACTIVE session should not be in first row anymore
    // Sally (CLOSED) should be visible
    expect(screen.getByText('Sally')).toBeInTheDocument();
  });

  it('filters sessions by search text', async () => {
    const user = userEvent.setup();
    sessionApi.getAll.mockResolvedValue(mockSessions);
    renderSessions();

    await waitFor(() => {
      expect(screen.getByText('Sally')).toBeInTheDocument();
    });

    const searchInput = screen.getByPlaceholderText(/search by cashier/i);
    await user.type(searchInput, 'Sally');

    // Only Sally's session should be visible
    expect(screen.getByText('Sally')).toBeInTheDocument();
    expect(screen.queryByText('R001')).not.toBeInTheDocument();
  });

  it('opens session detail modal', async () => {
    const user = userEvent.setup();
    sessionApi.getAll.mockResolvedValue(mockSessions);
    saleApi.getBySession.mockResolvedValue([
      { id: 100, dateTime: '2026-05-10T10:30:00', total: 25.50, paymentMethod: 'CASH', status: 'COMPLETED' },
    ]);
    renderSessions();

    await waitFor(() => {
      expect(screen.getAllByText('David').length).toBeGreaterThan(0);
    });

    // Click the first eye icon (David's active session, sorted first)
    const viewButtons = screen.getAllByTitle('View details');
    await user.click(viewButtons[0]);

    await waitFor(() => {
      expect(screen.getByText('$25.50')).toBeInTheDocument();
    });
    expect(screen.getByText('Sales in Session (1)')).toBeInTheDocument();
  });

  it('opens close session dialog for active sessions', async () => {
    const user = userEvent.setup();
    sessionApi.getAll.mockResolvedValue(mockSessions);
    renderSessions();

    await waitFor(() => {
      expect(screen.getAllByText('David').length).toBeGreaterThan(0);
    });

    const closeButtons = screen.getAllByTitle('Close session');
    await user.click(closeButtons[0]);

    expect(screen.getByText(/closing session for/i)).toBeInTheDocument();
    expect(screen.getByText('Close Session #1')).toBeInTheDocument();
  });

  it('closes session successfully', async () => {
    const user = userEvent.setup();
    sessionApi.getAll.mockResolvedValue(mockSessions);
    sessionApi.close.mockResolvedValue({ ...mockSessions[0], status: 'CLOSED', endingCash: 250.00 });
    renderSessions();

    await waitFor(() => {
      expect(screen.getAllByText('David').length).toBeGreaterThan(0);
    });

    const closeButtons = screen.getAllByTitle('Close session');
    await user.click(closeButtons[0]);

    // Wait for modal
    await waitFor(() => {
      expect(screen.getByText('Close Session #1')).toBeInTheDocument();
    });

    const endingCashInput = screen.getByPlaceholderText(/enter ending cash/i);
    await user.clear(endingCashInput);
    await user.type(endingCashInput, '250');

    // Click the primary button in the modal footer
    const footer = document.querySelector('.modal-footer');
    const confirmBtn = footer.querySelector('.btn-primary');
    expect(confirmBtn).not.toBeNull();
    expect(confirmBtn.disabled).toBe(false);
    await user.click(confirmBtn);

    await waitFor(() => {
      expect(sessionApi.close).toHaveBeenCalledWith(1, { endingCash: '250' });
    });
  });

  it('shows empty state when no sessions', async () => {
    sessionApi.getAll.mockResolvedValue([]);
    renderSessions();
    await waitFor(() => {
      expect(screen.getByText(/no sessions found/i)).toBeInTheDocument();
    });
  });

  it('shows delete button only for closed sessions', async () => {
    sessionApi.getAll.mockResolvedValue(mockSessions);
    renderSessions();

    await waitFor(() => {
      expect(screen.getAllByText('David').length).toBeGreaterThan(0);
    });

    // Should have close-session button for active and delete for closed
    expect(screen.getAllByTitle('Close session')).toHaveLength(1);
    expect(screen.getAllByTitle('Delete')).toHaveLength(2); // Two closed sessions
  });
});
