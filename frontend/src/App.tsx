import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { NavBar } from './components/NavBar';
import { CollectionsPage } from './pages/CollectionsPage';
import { CollectionDetailPage } from './pages/CollectionDetailPage';
import { DecksPage } from './pages/DecksPage';
import { DeckBuilderPage } from './pages/DeckBuilderPage';

export function App() {
  return (
    <BrowserRouter>
      <NavBar />
      <Routes>
        <Route path="/" element={<Navigate to="/collections" replace />} />
        <Route path="/collections" element={<CollectionsPage />} />
        <Route path="/collections/:id" element={<CollectionDetailPage />} />
        <Route path="/decks" element={<DecksPage />} />
        <Route path="/decks/:id" element={<DeckBuilderPage />} />
      </Routes>
    </BrowserRouter>
  );
}
