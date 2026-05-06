import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import Dashboard from '../../pages/Dashboard';

// Mock all API modules
vi.mock('../../api', () => ({
  storeApi: { count: vi.fn() },
  itemApi: { count: vi.fn() },
  personApi: { count: vi.fn() },
  cashierApi: { count: vi.fn() },
  registerApi: { count: vi.fn() },
  taxCategoryApi: { count: vi.fn() },
  saleApi: { getAll: vi.fn() },
  sessionApi: { getAll: vi.fn() },
}));

import {
  storeApi, itemApi, personApi, cashierApi, registerApi, taxCategoryApi,
  saleApi, sessionApi,
} from '../../api';

describe('Dashboard', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  function renderDashboard() {
    return render(
      <MemoryRouter>
        <Dashboard />
      </MemoryRouter>
    );
  }

  it('shows loading spinner initially', () => {
    // Never resolve the promises
    storeApi.count.mockReturnValue(new Promise(() => {}));
    itemApi.count.mockReturnValue(new Promise(() => {}));
    personApi.count.mockReturnValue(new Promise(() => {}));
    cashierApi.count.mockReturnValue(new Promise(() => {}));
    registerApi.count.mockReturnValue(new Promise(() => {}));
    taxCategoryApi.count.mockReturnValue(new Promise(() => {}));
    saleApi.getAll.mockReturnValue(new Promise(() => {}));
    sessionApi.getAll.mockReturnValue(new Promise(() => {}));

    renderDashboard();
    expect(screen.getByText(/loading dashboard/i)).toBeInTheDocument();
  });

  it('shows entity counts after loading', async () => {
    storeApi.count.mockResolvedValue(2);
    itemApi.count.mockResolvedValue(10);
    personApi.count.mockResolvedValue(3);
    cashierApi.count.mockResolvedValue(3);
    registerApi.count.mockResolvedValue(4);
    taxCategoryApi.count.mockResolvedValue(5);
    saleApi.getAll.mockResolvedValue([]);
    sessionApi.getAll.mockResolvedValue([]);

    renderDashboard();

    await waitFor(() => {
      expect(screen.queryByText(/loading dashboard/i)).not.toBeInTheDocument();
    });

    expect(screen.getByText('2')).toBeInTheDocument();
    expect(screen.getByText('10')).toBeInTheDocument();
    expect(screen.getByText('Stores')).toBeInTheDocument();
    expect(screen.getByText('Items')).toBeInTheDocument();
  });

  it('shows API connected status on success', async () => {
    storeApi.count.mockResolvedValue(1);
    itemApi.count.mockResolvedValue(1);
    personApi.count.mockResolvedValue(1);
    cashierApi.count.mockResolvedValue(1);
    registerApi.count.mockResolvedValue(1);
    taxCategoryApi.count.mockResolvedValue(1);
    saleApi.getAll.mockResolvedValue([]);
    sessionApi.getAll.mockResolvedValue([]);

    renderDashboard();

    await waitFor(() => {
      expect(screen.getByText(/connected/i)).toBeInTheDocument();
    });
  });

  it('displays today revenue and sales count', async () => {
    const now = new Date();
    const todaySale = {
      id: 1,
      dateTime: now.toISOString(),
      total: 25.50,
      status: 'COMPLETED',
      cashierName: 'David',
    };
    const yesterdaySale = {
      id: 2,
      dateTime: new Date(now.getTime() - 86400000).toISOString(),
      total: 100.00,
      status: 'COMPLETED',
      cashierName: 'Sally',
    };

    storeApi.count.mockResolvedValue(1);
    itemApi.count.mockResolvedValue(1);
    personApi.count.mockResolvedValue(1);
    cashierApi.count.mockResolvedValue(1);
    registerApi.count.mockResolvedValue(1);
    taxCategoryApi.count.mockResolvedValue(1);
    saleApi.getAll.mockResolvedValue([todaySale, yesterdaySale]);
    sessionApi.getAll.mockResolvedValue([]);

    renderDashboard();

    await waitFor(() => {
      expect(screen.getByText("Today's Revenue")).toBeInTheDocument();
    });
    // Today's count should be 1
    expect(screen.getByText("Today's Sales")).toBeInTheDocument();
  });

  it('shows active sessions count', async () => {
    storeApi.count.mockResolvedValue(1);
    itemApi.count.mockResolvedValue(1);
    personApi.count.mockResolvedValue(1);
    cashierApi.count.mockResolvedValue(1);
    registerApi.count.mockResolvedValue(1);
    taxCategoryApi.count.mockResolvedValue(1);
    saleApi.getAll.mockResolvedValue([]);
    sessionApi.getAll.mockResolvedValue([
      { id: 1, status: 'ACTIVE', cashierName: 'David', registerNumber: 'R01', startDateTime: new Date().toISOString() },
      { id: 2, status: 'CLOSED', cashierName: 'Sally', registerNumber: 'R02', startDateTime: new Date().toISOString() },
    ]);

    renderDashboard();

    await waitFor(() => {
      expect(screen.getAllByText('Active Sessions').length).toBeGreaterThan(0);
    });
    // Should show the active session in the table
    await waitFor(() => {
      expect(screen.getByText('David')).toBeInTheDocument();
    });
    // Sally's session is CLOSED, should not appear in active sessions panel
    expect(screen.queryByText('Sally')).not.toBeInTheDocument();
  });

  it('shows recent sales in the table', async () => {
    const sales = [
      { id: 1, dateTime: new Date().toISOString(), total: 10, status: 'COMPLETED', cashierName: 'David' },
      { id: 2, dateTime: new Date().toISOString(), total: 20, status: 'IN_PROGRESS', cashierName: 'Sally' },
    ];

    storeApi.count.mockResolvedValue(1);
    itemApi.count.mockResolvedValue(1);
    personApi.count.mockResolvedValue(1);
    cashierApi.count.mockResolvedValue(1);
    registerApi.count.mockResolvedValue(1);
    taxCategoryApi.count.mockResolvedValue(1);
    saleApi.getAll.mockResolvedValue(sales);
    sessionApi.getAll.mockResolvedValue([]);

    renderDashboard();

    await waitFor(() => {
      expect(screen.getByText('Recent Sales')).toBeInTheDocument();
    });
    expect(screen.getByText('#1')).toBeInTheDocument();
    expect(screen.getByText('#2')).toBeInTheDocument();
    expect(screen.getByText('David')).toBeInTheDocument();
    expect(screen.getByText('Sally')).toBeInTheDocument();
  });

  it('shows quick action links', async () => {
    storeApi.count.mockResolvedValue(1);
    itemApi.count.mockResolvedValue(1);
    personApi.count.mockResolvedValue(1);
    cashierApi.count.mockResolvedValue(1);
    registerApi.count.mockResolvedValue(1);
    taxCategoryApi.count.mockResolvedValue(1);
    saleApi.getAll.mockResolvedValue([]);
    sessionApi.getAll.mockResolvedValue([]);

    renderDashboard();

    await waitFor(() => {
      expect(screen.getByText(/open pos terminal/i)).toBeInTheDocument();
    });
    expect(screen.getByText(/view reports/i)).toBeInTheDocument();
    expect(screen.getByText(/manage items/i)).toBeInTheDocument();
  });

  it('shows empty state when no sales exist', async () => {
    storeApi.count.mockResolvedValue(0);
    itemApi.count.mockResolvedValue(0);
    personApi.count.mockResolvedValue(0);
    cashierApi.count.mockResolvedValue(0);
    registerApi.count.mockResolvedValue(0);
    taxCategoryApi.count.mockResolvedValue(0);
    saleApi.getAll.mockResolvedValue([]);
    sessionApi.getAll.mockResolvedValue([]);

    renderDashboard();

    await waitFor(() => {
      expect(screen.getByText('No sales yet')).toBeInTheDocument();
    });
    expect(screen.getByText('No active sessions')).toBeInTheDocument();
  });
});
