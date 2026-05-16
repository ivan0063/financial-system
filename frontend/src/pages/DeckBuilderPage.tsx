import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { decksApi } from '../api/decks';
import type { Deck, DeckCard, DeckBoard, ScryfallCard } from '../types';
import { Modal } from '../components/Modal';
import { CardSearch } from '../components/CardSearch';
import { CardImage } from '../components/CardImage';

function getImageUri(card: ScryfallCard): string | undefined {
  return card.image_uris?.normal ?? card.card_faces?.[0]?.image_uris?.normal;
}

function OwnershipBadge({ card }: { card: DeckCard }) {
  if (card.owned) {
    return <span className="badge badge-owned">✓ Owned ({card.ownedQuantity})</span>;
  }
  if (card.ownedQuantity > 0) {
    return <span className="badge badge-partial">⚠ Have {card.ownedQuantity}/{card.quantity}</span>;
  }
  return <span className="badge badge-missing">✕ Need to buy</span>;
}

const BOARDS: DeckBoard[] = ['MAINBOARD', 'SIDEBOARD', 'COMMANDER'];

export function DeckBuilderPage() {
  const { id } = useParams<{ id: string }>();
  const deckId = Number(id);

  const [deck, setDeck] = useState<Deck | null>(null);
  const [cards, setCards] = useState<DeckCard[]>([]);
  const [loading, setLoading] = useState(true);
  const [showSearch, setShowSearch] = useState(false);
  const [pendingCard, setPendingCard] = useState<ScryfallCard | null>(null);
  const [pendingQty, setPendingQty] = useState(1);
  const [pendingBoard, setPendingBoard] = useState<DeckBoard>('MAINBOARD');
  const [adding, setAdding] = useState(false);
  const [activeBoard, setActiveBoard] = useState<DeckBoard | 'ALL'>('ALL');

  const load = async () => {
    try {
      const [d, cardList] = await Promise.all([
        decksApi.getById(deckId),
        decksApi.getCards(deckId),
      ]);
      setDeck(d);
      setCards(cardList);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, [deckId]);

  const handleCardSelect = (card: ScryfallCard) => {
    setPendingCard(card);
    setPendingQty(1);
    setPendingBoard('MAINBOARD');
  };

  const confirmAdd = async () => {
    if (!pendingCard) return;
    setAdding(true);
    try {
      await decksApi.addCard(deckId, {
        scryfallId: pendingCard.id,
        cardName: pendingCard.name,
        setCode: pendingCard.set,
        collectorNumber: pendingCard.collector_number,
        imageUri: getImageUri(pendingCard),
        quantity: pendingQty,
        board: pendingBoard,
      });
      setPendingCard(null);
      setShowSearch(false);
      load();
    } finally {
      setAdding(false);
    }
  };

  const updateCard = async (card: DeckCard, qty: number, board: DeckBoard) => {
    await decksApi.updateCard(deckId, card.id, { quantity: qty, board });
    load();
  };

  const removeCard = async (card: DeckCard) => {
    if (!confirm(`Remove ${card.cardName} from deck?`)) return;
    await decksApi.removeCard(deckId, card.id);
    setCards(prev => prev.filter(c => c.id !== card.id));
  };

  const displayed = activeBoard === 'ALL' ? cards : cards.filter(c => c.board === activeBoard);
  const ownedCount = cards.filter(c => c.owned).length;
  const totalCount = cards.length;

  if (loading) return <div className="page"><div className="spinner" /></div>;

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <Link to="/decks" style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>← Decks</Link>
          <h1 className="page-title" style={{ marginTop: '0.25rem' }}>{deck?.name}</h1>
          {deck?.format && (
            <span className="badge" style={{ background: 'var(--surface2)', color: 'var(--accent-light)' }}>
              {deck.format}
            </span>
          )}
        </div>
        <button className="btn-primary" onClick={() => setShowSearch(true)}>+ Add Card</button>
      </div>

      {totalCount > 0 && (
        <div className="surface" style={{ marginBottom: '1rem', display: 'flex', gap: '1.5rem', alignItems: 'center', flexWrap: 'wrap' }}>
          <span style={{ fontSize: '0.9rem' }}>
            <strong>{totalCount}</strong> card{totalCount !== 1 ? 's' : ''} in deck
          </span>
          <span style={{ fontSize: '0.9rem', color: 'var(--green)' }}>
            <strong>{ownedCount}</strong> owned
          </span>
          <span style={{ fontSize: '0.9rem', color: 'var(--red)' }}>
            <strong>{totalCount - ownedCount}</strong> to buy
          </span>
        </div>
      )}

      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1rem' }}>
        {(['ALL', ...BOARDS] as const).map(board => (
          <button
            key={board}
            onClick={() => setActiveBoard(board)}
            className={activeBoard === board ? 'btn-primary' : 'btn-ghost'}
            style={{ fontSize: '0.8rem', padding: '0.3rem 0.8rem' }}
          >
            {board === 'ALL' ? `All (${cards.length})` : `${board} (${cards.filter(c => c.board === board).length})`}
          </button>
        ))}
      </div>

      {displayed.length === 0 && (
        <p style={{ color: 'var(--text-muted)', textAlign: 'center', marginTop: '3rem' }}>
          No cards in this section. Add cards using the button above.
        </p>
      )}

      <div className="card-grid">
        {displayed.map(card => (
          <div key={card.id} style={{ position: 'relative' }}>
            <div style={{ position: 'relative' }}>
              <CardImage imageUri={card.imageUri} name={card.cardName} />
              {!card.owned && (
                <div style={{
                  position: 'absolute', inset: 0,
                  background: 'rgba(231,76,60,0.15)',
                  border: '2px solid var(--red)',
                  borderRadius: 6,
                  pointerEvents: 'none',
                }} />
              )}
            </div>
            <div style={{ background: 'rgba(0,0,0,0.85)', padding: '0.4rem', borderBottomLeftRadius: 6, borderBottomRightRadius: 6 }}>
              <div style={{ fontSize: '0.7rem', fontWeight: 600, marginBottom: '0.3rem' }}>{card.cardName}</div>
              <OwnershipBadge card={card} />
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.25rem', marginTop: '0.3rem', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.2rem' }}>
                  <button className="btn-ghost"
                    onClick={() => card.quantity > 1 && updateCard(card, card.quantity - 1, card.board)}
                    style={{ padding: '0.1rem 0.35rem', fontSize: '0.85rem' }}
                    disabled={card.quantity <= 1}>−</button>
                  <span style={{ fontSize: '0.8rem', minWidth: '1.5rem', textAlign: 'center' }}>×{card.quantity}</span>
                  <button className="btn-ghost"
                    onClick={() => updateCard(card, card.quantity + 1, card.board)}
                    style={{ padding: '0.1rem 0.35rem', fontSize: '0.85rem' }}>+</button>
                </div>
                <button className="btn-danger" onClick={() => removeCard(card)}
                  style={{ padding: '0.1rem 0.4rem', fontSize: '0.7rem' }}>✕</button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {showSearch && (
        <Modal title="Add Card to Deck" onClose={() => { setShowSearch(false); setPendingCard(null); }} wide>
          {pendingCard ? (
            <div style={{ display: 'flex', gap: '1.5rem', alignItems: 'flex-start' }}>
              <div style={{ width: 160, flexShrink: 0 }}>
                <CardImage imageUri={getImageUri(pendingCard)} name={pendingCard.name} />
              </div>
              <div style={{ flex: 1 }}>
                <h3 style={{ marginBottom: '0.25rem' }}>{pendingCard.name}</h3>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>
                  {pendingCard.set_name} · {pendingCard.type_line}
                </p>
                <div className="form-group">
                  <label>Board</label>
                  <select value={pendingBoard} onChange={e => setPendingBoard(e.target.value as DeckBoard)}>
                    {BOARDS.map(b => <option key={b} value={b}>{b}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label>Quantity</label>
                  <input type="number" min={1} value={pendingQty}
                    onChange={e => setPendingQty(Math.max(1, parseInt(e.target.value) || 1))}
                    style={{ maxWidth: 120 }} />
                </div>
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <button className="btn-ghost" onClick={() => setPendingCard(null)}>← Back</button>
                  <button className="btn-primary" onClick={confirmAdd} disabled={adding}>
                    {adding ? 'Adding…' : 'Add to Deck'}
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
