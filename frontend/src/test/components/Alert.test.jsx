import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import Alert from '../../components/Alert';

describe('Alert', () => {
  it('renders nothing when message is falsy', () => {
    const { container } = render(<Alert />);
    expect(container).toBeEmptyDOMElement();
  });

  it('renders success alert with message', () => {
    render(<Alert type="success" message="Item created" />);
    expect(screen.getByText('Item created')).toBeInTheDocument();
  });

  it('renders error alert with message', () => {
    render(<Alert type="error" message="Something went wrong" />);
    expect(screen.getByText('Something went wrong')).toBeInTheDocument();
  });

  it('renders info alert by default', () => {
    const { container } = render(<Alert message="FYI notice" />);
    expect(container.querySelector('.alert-info')).toBeInTheDocument();
    expect(screen.getByText('FYI notice')).toBeInTheDocument();
  });

  it('shows dismiss button when onDismiss is provided', () => {
    const onDismiss = vi.fn();
    render(<Alert type="success" message="Done" onDismiss={onDismiss} />);
    const btn = screen.getByRole('button');
    expect(btn).toBeInTheDocument();
  });

  it('calls onDismiss when dismiss button is clicked', async () => {
    const user = userEvent.setup();
    const onDismiss = vi.fn();
    render(<Alert type="error" message="Oops" onDismiss={onDismiss} />);
    await user.click(screen.getByRole('button'));
    expect(onDismiss).toHaveBeenCalledTimes(1);
  });

  it('does not render dismiss button when onDismiss is not provided', () => {
    render(<Alert type="success" message="No dismiss" />);
    expect(screen.queryByRole('button')).not.toBeInTheDocument();
  });
});
