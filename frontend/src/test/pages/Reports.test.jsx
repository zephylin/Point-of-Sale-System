import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import Reports from '../../pages/Reports';

// Mock API modules
vi.mock('../../api', () => ({
  saleApi: { getAll: vi.fn() },
  saleLineItemApi: { getBySale: vi.fn() },
  paymentApi: { getBySale: vi.fn() },
}));

import { saleApi, saleLineItemApi, paymentApi } from '../../api';

const mockSales = [
  {
    id: 1,
    dateTime: '2026-05-05T10:30:00',
    cashierName: 'David',
    storeName: "David's Quick Mart",
    subtotal: 15.00,
    tax: 1.20,
    total: 16.20,
    paymentMethod: 'CASH',
    status: 'COMPLETED',
    taxFree: false,
  },
  {
    id: 2,
    dateTime: '2026-05-05T11:00:00',
    cashierName: 'Sally',
    storeName: "David's Quick Mart",
    subtotal: 8.50,
    tax: 0.68,
    total: 9.18,
    paymentMethod: 'CREDIT',
    status: 'COMPLETED',
    taxFree: false,
  },
  {
    id: 3,
    dateTime: '2026-05-04T09:00:00',
    cashierName: 'Mike',
    storeName: "David's Quick Mart",
    subtotal: 25.00,
    tax: 0,
    total: 25.00,
    paymentMethod: 'CHECK',
    status: 'VOIDED',
    taxFree: true,
  },
];

describe('Reports', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  function renderReports() {
    return render(
      <MemoryRouter>
        <Reports />
      </MemoryRouter>
    );
  }

  it('shows loading state initially', () => {
    saleApi.getAll.mockReturnValue(new Promise(() => {}));
    renderReports();
    expect(screen.getByText(/loading sales/i)).toBeInTheDocument();
  });

  it('shows error message when API fails', async () => {
    saleApi.getAll.mockRejectedValue(new Error('Network error'));
    renderReports();
    await waitFor(() => {
      expect(screen.getByText(/failed to load sales data/i)).toBeInTheDocument();
    });
  });

  it('displays sales table after loading', async () => {
    saleApi.getAll.mockResolvedValue(mockSales);
    renderReports();

    await waitFor(() => {
      expect(screen.getByText('#1')).toBeInTheDocument();
    });
    expect(screen.getByText('#2')).toBeInTheDocument();
    expect(screen.getByText('#3')).toBeInTheDocument();
    expect(screen.getByText('David')).toBeInTheDocument();
    expect(screen.getByText('Sally')).toBeInTheDocument();
  });

  it('shows total revenue from completed sales only', async () => {
    saleApi.getAll.mockResolvedValue(mockSales);
    renderReports();

    await waitFor(() => {
      expect(screen.getByText('Total Revenue')).toBeInTheDocument();
    });
    // Revenue = 16.20 + 9.18 = 25.38 (only COMPLETED)
    expect(screen.getByText('$25.38')).toBeInTheDocument();
  });

  it('shows completed sales count', async () => {
    saleApi.getAll.mockResolvedValue(mockSales);
    renderReports();

    await waitFor(() => {
      expect(screen.getByText('Completed Sales')).toBeInTheDocument();
    });
  });

  it('filters by status', async () => {
    const user = userEvent.setup();
    saleApi.getAll.mockResolvedValue(mockSales);
    renderReports();

    await waitFor(() => {
      expect(screen.getByText('#1')).toBeInTheDocument();
    });

    // Filter to VOIDED only
    const statusSelect = screen.getAllByRole('combobox')[0];
    await user.selectOptions(statusSelect, 'VOIDED');

    expect(screen.queryByText('#1')).not.toBeInTheDocument();
    expect(screen.queryByText('#2')).not.toBeInTheDocument();
    expect(screen.getByText('#3')).toBeInTheDocument();
  });

  it('filters by payment type', async () => {
    const user = userEvent.setup();
    saleApi.getAll.mockResolvedValue(mockSales);
    renderReports();

    await waitFor(() => {
      expect(screen.getByText('#1')).toBeInTheDocument();
    });

    // Filter to CREDIT only
    const paymentSelect = screen.getAllByRole('combobox')[1];
    await user.selectOptions(paymentSelect, 'CREDIT');

    expect(screen.queryByText('#1')).not.toBeInTheDocument();
    expect(screen.getByText('#2')).toBeInTheDocument();
    expect(screen.queryByText('#3')).not.toBeInTheDocument();
  });

  it('searches by cashier name', async () => {
    const user = userEvent.setup();
    saleApi.getAll.mockResolvedValue(mockSales);
    renderReports();

    await waitFor(() => {
      expect(screen.getByText('#1')).toBeInTheDocument();
    });

    const searchInput = screen.getByPlaceholderText(/search by id/i);
    await user.type(searchInput, 'Sally');

    expect(screen.queryByText('#1')).not.toBeInTheDocument();
    expect(screen.getByText('#2')).toBeInTheDocument();
    expect(screen.queryByText('#3')).not.toBeInTheDocument();
  });

  it('shows empty state when no sales match filters', async () => {
    const user = userEvent.setup();
    saleApi.getAll.mockResolvedValue(mockSales);
    renderReports();

    await waitFor(() => {
      expect(screen.getByText('#1')).toBeInTheDocument();
    });

    const searchInput = screen.getByPlaceholderText(/search by id/i);
    await user.type(searchInput, 'nonexistent');

    expect(screen.getByText('No sales found')).toBeInTheDocument();
    expect(screen.getByText('Try adjusting your filters.')).toBeInTheDocument();
  });

  it('opens sale detail modal on eye icon click', async () => {
    const user = userEvent.setup();
    saleApi.getAll.mockResolvedValue(mockSales);
    saleLineItemApi.getBySale.mockResolvedValue([]);
    paymentApi.getBySale.mockResolvedValue([]);

    renderReports();

    await waitFor(() => {
      expect(screen.getByText('#1')).toBeInTheDocument();
    });

    // Sales are sorted newest first, so #1 is the last row
    const row = screen.getByText('#1').closest('tr');
    const viewButton = row.querySelector('button[title="View details"]');
    await user.click(viewButton);

    await waitFor(() => {
      expect(document.querySelector('.modal-overlay')).toBeInTheDocument();
    });
    expect(screen.getByText((_, el) =>
      el.tagName === 'H3' && el.textContent === 'Sale #1 Details'
    )).toBeInTheDocument();
  });

  it('displays line items and payments in detail modal', async () => {
    const user = userEvent.setup();
    saleApi.getAll.mockResolvedValue(mockSales);
    saleLineItemApi.getBySale.mockResolvedValue([
      { id: 10, itemDescription: 'Turkey Sandwich', quantity: 2, unitPrice: 5.99, extendedPrice: 11.98 },
    ]);
    paymentApi.getBySale.mockResolvedValue([
      { id: 20, paymentType: 'CASH', amount: 16.20, amountTendered: 20.00, changeDue: 3.80 },
    ]);

    renderReports();

    await waitFor(() => {
      expect(screen.getByText('#1')).toBeInTheDocument();
    });

    const viewButtons = screen.getAllByTitle('View details');
    await user.click(viewButtons[0]);

    await waitFor(() => {
      expect(screen.getByText('Turkey Sandwich')).toBeInTheDocument();
    });
    expect(screen.getByText('$11.98')).toBeInTheDocument();
    expect(screen.getByText('$20.00')).toBeInTheDocument();
    expect(screen.getByText('$3.80')).toBeInTheDocument();
  });

  it('clears filters when Clear Filters button is clicked', async () => {
    const user = userEvent.setup();
    saleApi.getAll.mockResolvedValue(mockSales);
    renderReports();

    await waitFor(() => {
      expect(screen.getByText('#1')).toBeInTheDocument();
    });

    // Apply a filter
    const searchInput = screen.getByPlaceholderText(/search by id/i);
    await user.type(searchInput, 'Sally');

    expect(screen.queryByText('#1')).not.toBeInTheDocument();

    // Clear filters
    await user.click(screen.getByText('Clear Filters'));

    expect(screen.getByText('#1')).toBeInTheDocument();
    expect(screen.getByText('#2')).toBeInTheDocument();
    expect(screen.getByText('#3')).toBeInTheDocument();
  });
});
