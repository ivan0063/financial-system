import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { collectionsApi } from '../api/collections';
import type { Collection, CollectionCard, ScryfallCard } from '../types';
import { Modal } from '../components/Modal';
import { CardSearch } from '../components/CardSearch';
import { CardImage } from '../components/CardImage';

function getImageUri(card: ScryfallCard): string | undefined {
  return card.image_uris?.normal ?? card.card_faces?.[0]?.image_uris?.normal;
}

export function CollectionDetailPage() {
  const { id } = useParams<{ id: string }>();
  const collectionId = Number(id);

  const [collection, setCollection] = useState<Collection | null>(null);
  const [cards, setCards] = useState<CollectionCard[]>([]);
  const [loading, setLoading] = useState(true);
  const [showSearch, setShowSearch] = useState(false);
  const [pendingCard, setPendingCard] = useState<ScryfallCard | null>(null);
  const [pendingQty, setPendingQty] = useState(1);
  const [adding, setAdding] = useState(false);

  const load = async () => {
    try {
      const [col, cardList] = await Promise.all([
        collectionsApi.getById(collectionId),
        collectionsApi.getCards(collectionId),
      ]);
      setCollection(col);
      setCards(cardList);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, [collectionId]);

  const handleCardSelect = (card: ScryfallCard) => {
    setPendingCard(card);
    setPendingQty(1);
  };

  const confirmAdd = async () => {
    if (!pendingCard) return;
    setAdding(true);
    try {
      await collectionsApi.addCard(collectionId, {
        scryfallId: pendingCard.id,
        cardName: pendingCard.name,
        setCode: pendingCard.set,
        collectorNumber: pendingCard.collector_number,
        imageUri: getImageUri(pendingCard),
        quantity: pendingQty,
      });
      setPendingCard(null);
      setShowSearch(false);
      load();
    } finally {
      setAdding(false);
    }
  };

  const updateQty = async (card: CollectionCard, qty: number) => {
    if (qty < 1) return;
    await collectionsApi.updateCard(collectionId, card.id, qty);
    setCards(prev => prev.map(c => c.id === card.id ? { ...c, quantity: qty } : c));
  };

  const removeCard = async (card: CollectionCard) => {
    if (!confirm(`Remove ${card.cardName} from collection?`)) return;
    await collectionsApi.removeCard(collectionId, card.id);
    setCards(prev => prev.filter(c => c.id !== card.id));
  };

  if (loading) return <div className="page"><div className="spinner" /></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <Link to="/collections" style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
            ← Collections
          </Link>
          <h1 className="page-title" style={{ marginTop: '0.25rem' }}>{collection?.name}</h1>
          {collection?.description && (
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>{collection.description}</p>
          )}
        </div>
        <button className="btn-primary" onClick={() => setShowSearch(true)}>+ Add Card</button>
      </div>

      {cards.length === 0 && (
        <p style={{ color: 'var(--text-muted)', textAlign: 'center', marginTop: '3rem' }}>
          No cards yet. Search and add cards to your collection!
        </p>
      )}

      <div className="card-grid">
        {cards.map(card => (
          <div key={card.id} style={{ position: 'relative' }}>
            <CardImage imageUri={card.imageUri} name={card.cardName} />
            <div style={{
              background: 'rgba(0,0,0,0.85)',
              padding: '0.4rem',
              borderBottomLeftRadius: 6,
              borderBottomRightRadius: 6,
            }}>
              <div style={{ fontSize: '0.7rem', marginBottom: '0.3rem', fontWeight: 600 }}>{card.cardName}</div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.2rem' }}>
                  <button
                    className="btn-ghost"
                    onClick={() => updateQty(card, card.quantity - 1)}
                    style={{ padding: '0.1rem 0.4rem', fontSize: '0.9rem' }}
                    disabled={card.quantity <= 1}
                  >−</button>
                  <span style={{ fontSize: '0.85rem', minWidth: '1.5rem', textAlign: 'center' }}>×{card.quantity}</span>
                  <button
                    className="btn-ghost"
                    onClick={() => updateQty(card, card.quantity + 1)}
                    style={{ padding: '0.1rem 0.4rem', fontSize: '0.9rem' }}
                  >+</button>
                </div>
                <button
                  className="btn-danger"
                  onClick={() => removeCard(card)}
                  style={{ padding: '0.1rem 0.4rem', fontSize: '0.75rem' }}
                >✕</button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {showSearch && (
        <Modal title="Add Card to Collection" onClose={() => { setShowSearch(false); setPendingCard(null); }} wide>
          {pendingCard ? (
            <div style={{ display: 'flex', gap: '1.5rem', alignItems: 'flex-start' }}>
              <div style={{ width: 160, flexShrink: 0 }}>
                <CardImage imageUri={getImageUri(pendingCard)} name={pendingCard.name} />
              </div>
              <div style={{ flex: 1 }}>
                <h3 style={{ marginBottom: '0.5rem' }}>{pendingCard.name}</h3>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '0.25rem' }}>
                  {pendingCard.set_name} · {pendingCard.type_line}
                </p>
                <div className="form-group" style={{ marginTop: '1rem' }}>
                  <label>Quantity</label>
                  <input
                    type="number"
                    min={1}
                    value={pendingQty}
                    onChange={e => setPendingQty(Math.max(1, parseInt(e.target.value) || 1))}
                    style={{ maxWidth: 120 }}
                  />
                </div>
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <button className="btn-ghost" onClick={() => setPendingCard(null)}>← Back</button>
                  <button className="btn-primary" onClick={confirmAdd} disabled={adding}>
                    {adding ? 'Adding…' : 'Add to Collection'}
                  </button>
                </div>
              </div>
            </div>
          ) : (
            <CardSearch onSelect={handleCardSelect} />
          )}
        </Modal>
      )}
    </div>
  );
}
