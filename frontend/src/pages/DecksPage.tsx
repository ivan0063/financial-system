import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { decksApi } from '../api/decks';
import type { Deck } from '../types';
import { Modal } from '../components/Modal';

const FORMATS = ['Standard', 'Pioneer', 'Modern', 'Legacy', 'Vintage', 'Commander', 'Pauper', 'Draft'];

function DeckForm({ initial, onSave, onCancel }: {
  initial?: Deck;
  onSave: (data: { name: string; description?: string; format?: string }) => Promise<void>;
  onCancel: () => void;
}) {
  const [name, setName] = useState(initial?.name ?? '');
  const [description, setDescription] = useState(initial?.description ?? '');
  const [format, setFormat] = useState(initial?.format ?? '');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      await onSave({ name: name.trim(), description: description.trim() || undefined, format: format || undefined });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to save');
    } finally {
      setSaving(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="form-group">
        <label>Name *</label>
        <input value={name} onChange={e => setName(e.target.value)} required />
      </div>
      <div className="form-group">
        <label>Format</label>
        <select value={format} onChange={e => setFormat(e.target.value)}>
          <option value="">— select format —</option>
          {FORMATS.map(f => <option key={f} value={f}>{f}</option>)}
        </select>
      </div>
      <div className="form-group">
        <label>Description</label>
        <textarea
          value={description}
          onChange={e => setDescription(e.target.value)}
          rows={2}
          style={{ resize: 'vertical' }}
        />
      </div>
      {error && <div className="error-msg">{error}</div>}
      <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
        <button type="button" className="btn-ghost" onClick={onCancel}>Cancel</button>
        <button type="submit" className="btn-primary" disabled={saving || !name.trim()}>
          {saving ? 'Saving…' : 'Save'}
        </button>
      </div>
    </form>
  );
}

export function DecksPage() {
  const [decks, setDecks] = useState<Deck[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [editing, setEditing] = useState<Deck | null>(null);

  const load = async () => {
    try {
      setDecks(await decksApi.getAll());
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleCreate = async (data: { name: string; description?: string; format?: string }) => {
    await decksApi.create(data);
    setShowCreate(false);
    load();
  };

  const handleUpdate = async (data: { name: string; description?: string; format?: string }) => {
    if (!editing) return;
    await decksApi.update(editing.id, data);
    setEditing(null);
    load();
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this deck?')) return;
    await decksApi.delete(id);
    load();
  };

  return (
    <div className="page">
      <div className="page-header">
        <h1 className="page-title">Decks</h1>
        <button className="btn-primary" onClick={() => setShowCreate(true)}>+ New Deck</button>
      </div>

      {loading && <div className="spinner" />}

      {!loading && decks.length === 0 && (
        <p style={{ color: 'var(--text-muted)', textAlign: 'center', marginTop: '3rem' }}>
          No decks yet. Build your first deck!
        </p>
      )}

      <div style={{ display: 'grid', gap: '1rem', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))' }}>
        {decks.map(deck => (
          <div key={deck.id} className="surface" style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <Link to={`/decks/${deck.id}`} style={{ fontWeight: 600, fontSize: '1.05rem' }}>
                {deck.name}
              </Link>
              <div style={{ display: 'flex', gap: '0.4rem' }}>
                <button className="btn-ghost" onClick={() => setEditing(deck)}
                  style={{ padding: '0.2rem 0.5rem', fontSize: '0.8rem' }}>Edit</button>
                <button className="btn-danger" onClick={() => handleDelete(deck.id)}
                  style={{ padding: '0.2rem 0.5rem', fontSize: '0.8rem' }}>Delete</button>
              </div>
            </div>
            {deck.format && (
              <span className="badge" style={{ background: 'var(--surface2)', color: 'var(--accent-light)', alignSelf: 'flex-start' }}>
                {deck.format}
              </span>
            )}
            {deck.description && <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{deck.description}</p>}
            <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
              Created {new Date(deck.createdAt).toLocaleDateString()}
            </p>
          </div>
        ))}
      </div>

      {showCreate && (
        <Modal title="New Deck" onClose={() => setShowCreate(false)}>
          <DeckForm onSave={handleCreate} onCancel={() => setShowCreate(false)} />
        </Modal>
      )}

      {editing && (
        <Modal title="Edit Deck" onClose={() => setEditing(null)}>
          <DeckForm initial={editing} onSave={handleUpdate} onCancel={() => setEditing(null)} />
        </Modal>
      )}
    </div>
  );
}
