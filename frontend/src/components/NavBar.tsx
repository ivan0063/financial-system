import { NavLink } from 'react-router-dom';

export function NavBar() {
  return (
    <nav style={{
      background: 'var(--surface)',
      borderBottom: '1px solid var(--border)',
      padding: '0.75rem 1.5rem',
      display: 'flex',
      alignItems: 'center',
      gap: '2rem',
    }}>
      <span style={{ fontWeight: 700, fontSize: '1.1rem', color: 'var(--accent-light)' }}>
        🃏 TCG Manager
      </span>
      <NavLink
        to="/collections"
        style={({ isActive }) => ({ color: isActive ? 'var(--accent-light)' : 'var(--text-muted)', fontWeight: 500 })}
      >
        Collections
      </NavLink>
      <NavLink
        to="/decks"
        style={({ isActive }) => ({ color: isActive ? 'var(--accent-light)' : 'var(--text-muted)', fontWeight: 500 })}
      >
        Decks
      </NavLink>
    </nav>
  );
}
