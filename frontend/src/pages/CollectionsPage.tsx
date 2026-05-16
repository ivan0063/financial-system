import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { collectionsApi } from '../api/collections';
import type { Collection } from '../types';
import { Modal } from '../components/Modal';

function CollectionForm({ initial, onSave, onCancel }: {
  initial?: Collection;
  onSave: (data: { name: string; description?: string }) => Promise<void>;
  onCancel: () => void;
}) {
  const [name, setName] = useState(initial?.name ?? '');
  const [description, setDescription] = useState(initial?.description ?? '');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      await onSave({ name: name.trim(), description: description.trim() || undefined });
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

export function CollectionsPage() {
  const [collections, setCollections] = useState<Collection[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [editing, setEditing] = useState<Collection | null>(null);

  const load = async () => {
    try {
      setCollections(await collectionsApi.getAll());
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const handleCreate = async (data: { name: string; description?: string }) => {
    await collectionsApi.create(data);
    setShowCreate(false);
    load();
  };

  const handleUpdate = async (data: { name: string; description?: string }) => {
    if (!editing) return;
    await collectionsApi.update(editing.id, data);
    setEditing(null);
    load();
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this collection? This will remove all its cards.')) return;
    await collectionsApi.delete(id);
    load();
  };

  return (
    <div className="page">
      <div className="page-header">
        <h1 className="page-title">Collections</h1>
        <button className="btn-primary" onClick={() => setShowCreate(true)}>+ New Collection</button>
      </div>

      {loading && <div className="spinner" />}

      {!loading && collections.length === 0 && (
        <p style={{ color: 'var(--text-muted)', textAlign: 'center', marginTop: '3rem' }}>
          No collections yet. Create one to start tracking your cards!
        </p>
      )}

      <div style={{ display: 'grid', gap: '1rem', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))' }}>
        {collections.map(col => (
          <div key={col.id} className="surface" style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <Link to={`/collections/${col.id}`} style={{ fontWeight: 600, fontSize: '1.05rem' }}>
                {col.name}
              </Link>
              <div style={{ display: 'flex', gap: '0.4rem' }}>
                <button className="btn-ghost" onClick={() => setEditing(col)}
                  style={{ padding: '0.2rem 0.5rem', fontSize: '0.8rem' }}>Edit</button>
                <button className="btn-danger" onClick={() => handleDelete(col.id)}
                  style={{ padding: '0.2rem 0.5rem', fontSize: '0.8rem' }}>Delete</button>
              </div>
            </div>
            {col.description && <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{col.description}</p>}
            <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
              Created {new Date(col.createdAt).toLocaleDateString()}
            </p>
          </div>
        ))}
      </div>

      {showCreate && (
        <Modal title="New Collection" onClose={() => setShowCreate(false)}>
          <CollectionForm onSave={handleCreate} onCancel={() => setShowCreate(false)} />
        </Modal>
      )}

      {editing && (
        <Modal title="Edit Collection" onClose={() => setEditing(null)}>
          <CollectionForm initial={editing} onSave={handleUpdate} onCancel={() => setEditing(null)} />
        </Modal>
      )}
    </div>
  );
}
