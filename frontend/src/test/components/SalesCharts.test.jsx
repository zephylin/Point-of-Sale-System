import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect } from 'vitest';
import SalesCharts from '../../components/SalesCharts';

const mockSales = [
  {
    id: 1,
    dateTime: '2026-05-10T10:30:00',
    cashierName: 'David',
    total: 25.50,
    paymentMethod: 'CASH',
    status: 'COMPLETED',
  },
  {
    id: 2,
    dateTime: '2026-05-10T14:00:00',
    cashierName: 'Sally',
    total: 42.00,
    paymentMethod: 'CREDIT',
    status: 'COMPLETED',
  },
  {
    id: 3,
    dateTime: '2026-05-09T09:15:00',
    cashierName: 'David',
    total: 18.75,
    paymentMethod: 'CASH',
    status: 'COMPLETED',
  },
  {
    id: 4,
    dateTime: '2026-05-10T11:00:00',
    cashierName: 'Mike',
    total: 9.99,
    paymentMethod: 'CHECK',
    status: 'VOIDED',
  },
];

describe('SalesCharts', () => {
  it('renders nothing when no completed sales', () => {
    const { container } = render(<SalesCharts sales={[]} />);
    expect(container.innerHTML).toBe('');
  });

  it('renders nothing when only voided sales', () => {
    const voidedOnly = [{ ...mockSales[3], status: 'VOIDED' }];
    const { container } = render(<SalesCharts sales={voidedOnly} />);
    expect(container.innerHTML).toBe('');
  });

  it('renders charts section when there are completed sales', () => {
    render(<SalesCharts sales={mockSales} />);
    expect(screen.getByText('Sales Analytics')).toBeInTheDocument();
  });

  it('renders all four chart cards', () => {
    render(<SalesCharts sales={mockSales} />);
    expect(screen.getByText('Revenue Over Time')).toBeInTheDocument();
    expect(screen.getByText('Payment Methods')).toBeInTheDocument();
    expect(screen.getByText('Sales by Hour')).toBeInTheDocument();
    expect(screen.getByText('Top Cashiers by Revenue')).toBeInTheDocument();
  });

  it('renders time range toggle buttons', () => {
    render(<SalesCharts sales={mockSales} />);
    expect(screen.getByText('Hourly')).toBeInTheDocument();
    expect(screen.getByText('Daily')).toBeInTheDocument();
    expect(screen.getByText('Weekly')).toBeInTheDocument();
    expect(screen.getByText('Monthly')).toBeInTheDocument();
  });

  it('changes active time range on button click', async () => {
    const user = userEvent.setup();
    render(<SalesCharts sales={mockSales} />);

    const weeklyBtn = screen.getByText('Weekly');
    await user.click(weeklyBtn);

    // The Weekly button should now have primary styling (active)
    expect(weeklyBtn.className).toContain('btn-primary');
    // Daily should revert to secondary
    expect(screen.getByText('Daily').className).toContain('btn-secondary');
  });
});
