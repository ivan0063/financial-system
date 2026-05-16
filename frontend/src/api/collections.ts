import { api } from './client';
import type { Collection, CollectionCard } from '../types';

export const collectionsApi = {
  getAll: () => api.get<Collection[]>('/collections'),
  getById: (id: number) => api.get<Collection>(`/collections/${id}`),
  create: (data: { name: string; description?: string }) =>
    api.post<Collection>('/collections', data),
  update: (id: number, data: { name: string; description?: string }) =>
    api.put<Collection>(`/collections/${id}`, data),
  delete: (id: number) => api.delete(`/collections/${id}`),

  getCards: (id: number) => api.get<CollectionCard[]>(`/collections/${id}/cards`),
  addCard: (
    id: number,
    data: {
      scryfallId: string;
      cardName: string;
      setCode: string;
      collectorNumber?: string;
      imageUri?: string;
      quantity: number;
    }
  ) => api.post<CollectionCard>(`/collections/${id}/cards`, data),
  updateCard: (id: number, cardId: number, quantity: number) =>
    api.put<CollectionCard>(`/collections/${id}/cards/${cardId}`, { quantity }),
  removeCard: (id: number, cardId: number) =>
    api.delete(`/collections/${id}/cards/${cardId}`),
};
