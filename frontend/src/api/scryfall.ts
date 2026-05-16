import { api } from './client';
import type { ScryfallCard, ScryfallSearchResult } from '../types';

export const scryfallApi = {
  search: (q: string, page = 1) =>
    api.get<ScryfallSearchResult>(`/scryfall/cards/search?q=${encodeURIComponent(q)}&page=${page}`),
  getById: (id: string) => api.get<ScryfallCard>(`/scryfall/cards/${id}`),
  getByName: (name: string) =>
    api.get<ScryfallCard>(`/scryfall/cards/named?name=${encodeURIComponent(name)}`),
};
