import { useState, useCallback } from 'react';
import { scryfallApi } from '../api/scryfall';
import type { ScryfallCard } from '../types';
import { CardImage } from './CardImage';

interface CardSearchProps {
  onSelect: (card: ScryfallCard) => void;
}

function getImageUri(card: ScryfallCard): string | undefined {
  return card.image_uris?.normal ?? card.card_faces?.[0]?.image_uris?.normal;
}

export function CardSearch({ onSelect }: CardSearchProps) {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<ScryfallCard[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(false);
  const [totalCards, setTotalCards] = useState(0);

  const search = useCallback(async (q: string, p: number) => {
    if (!q.trim()) return;
    setLoading(true);
    setError('');
    try {
      const result = await scryfallApi.search(q, p);
      setResults(prev => p === 1 ? result.data : [...prev, ...result.data]);
      setHasMore(result.has_more);
      setTotalCards(result.total_cards);
    } catch {
      setError('Search failed. Try a different query.');
    } finally {
      setLoading(false);
    }
  }, []);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(1);
    setResults([]);
    search(query, 1);
  };

  const loadMore = () => {
    const next = page + 1;
    setPage(next);
    search(query, next);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
      <form onSubmit={handleSearch} style={{ display: 'flex', gap: '0.5rem' }}>
        <input
          value={query}
          onChange={e => setQuery(e.target.value)}
          placeholder="Search cards (e.g. Lightning Bolt, t:creature c:red)"
          autoFocus
        />
        <button className="btn-primary" type="submit" disabled={loading || !query.trim()}>
          Search
        </button>
      </form>

      {error && <div className="error-msg">{error}</div>}

      {results.length > 0 && (
        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
          {totalCards} result{totalCards !== 1 ? 's' : ''}
        </p>
      )}

      <div className="card-grid">
        {results.map(card => (
          <div
            key={card.id}
            onClick={() => onSelect(card)}
            title={`${card.name} — click to add`}
            style={{ cursor: 'pointer', position: 'relative' }}
          >
            <CardImage imageUri={getImageUri(card)} name={card.name} />
            <div style={{
              position: 'absolute', bottom: 0, left: 0, right: 0,
              background: 'rgba(0,0,0,0.75)', padding: '0.25rem 0.4rem',
              fontSize: '0.7rem', borderBottomLeftRadius: 6, borderBottomRightRadius: 6,
            }}>
              {card.name}
            </div>
          </div>
        ))}
      </div>

      {loading && <div className="spinner" />}

      {hasMore && !loading && (
        <button className="btn-ghost" onClick={loadMore} style={{ alignSelf: 'center' }}>
          Load more
        </button>
      )}
    </div>
  );
}
