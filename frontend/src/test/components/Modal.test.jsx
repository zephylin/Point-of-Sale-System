import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, it, expect, vi } from 'vitest';
import Modal from '../../components/Modal';

describe('Modal', () => {
  it('renders nothing when open is false', () => {
    const { container } = render(
      <Modal open={false} title="Test" onClose={vi.fn()}>
        <p>Content</p>
      </Modal>
    );
    expect(container).toBeEmptyDOMElement();
  });

  it('renders title and children when open', () => {
    render(
      <Modal open={true} title="Edit Item" onClose={vi.fn()}>
        <p>Form here</p>
      </Modal>
    );
    expect(screen.getByText('Edit Item')).toBeInTheDocument();
    expect(screen.getByText('Form here')).toBeInTheDocument();
  });

  it('renders footer when provided', () => {
    render(
      <Modal open={true} title="Test" onClose={vi.fn()} footer={<button>Save</button>}>
        <p>Body</p>
      </Modal>
    );
    expect(screen.getByText('Save')).toBeInTheDocument();
  });

  it('does not render footer when not provided', () => {
    render(
      <Modal open={true} title="Test" onClose={vi.fn()}>
        <p>Body</p>
      </Modal>
    );
    expect(screen.queryByText('Save')).not.toBeInTheDocument();
  });

  it('calls onClose when close button is clicked', async () => {
    const user = userEvent.setup();
    const onClose = vi.fn();
    render(
      <Modal open={true} title="Test" onClose={onClose}>
        <p>Body</p>
      </Modal>
    );
    await user.click(screen.getByRole('button'));
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('calls onClose when clicking overlay', async () => {
    const user = userEvent.setup();
    const onClose = vi.fn();
    const { container } = render(
      <Modal open={true} title="Test" onClose={onClose}>
        <p>Body</p>
      </Modal>
    );
    const overlay = container.querySelector('.modal-overlay');
    await user.click(overlay);
    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('does not call onClose when clicking inside modal', async () => {
    const user = userEvent.setup();
    const onClose = vi.fn();
    render(
      <Modal open={true} title="Test" onClose={onClose}>
        <p>Body</p>
      </Modal>
    );
    await user.click(screen.getByText('Body'));
    expect(onClose).not.toHaveBeenCalled();
  });
});
