import { api } from './client';
import type { Deck, DeckCard, DeckBoard } from '../types';

export const decksApi = {
  getAll: () => api.get<Deck[]>('/decks'),
  getById: (id: number) => api.get<Deck>(`/decks/${id}`),
  create: (data: { name: string; description?: string; format?: string }) =>
    api.post<Deck>('/decks', data),
  update: (id: number, data: { name: string; description?: string; format?: string }) =>
    api.put<Deck>(`/decks/${id}`, data),
  delete: (id: number) => api.delete(`/decks/${id}`),

  getCards: (id: number) => api.get<DeckCard[]>(`/decks/${id}/cards`),
  addCard: (
    id: number,
    data: {
      scryfallId: string;
      cardName: string;
      setCode: string;
      collectorNumber?: string;
      imageUri?: string;
      quantity: number;
      board: DeckBoard;
    }
  ) => api.post<DeckCard>(`/decks/${id}/cards`, data),
  updateCard: (id: number, cardId: number, data: { quantity: number; board?: DeckBoard }) =>
    api.put<DeckCard>(`/decks/${id}/cards/${cardId}`, data),
  removeCard: (id: number, cardId: number) => api.delete(`/decks/${id}/cards/${cardId}`),
};
