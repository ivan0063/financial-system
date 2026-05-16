export interface Collection {
  id: number;
  name: string;
  description?: string;
  createdAt: string;
}

export interface CollectionCard {
  id: number;
  collectionId: number;
  scryfallId: string;
  cardName: string;
  setCode: string;
  collectorNumber?: string;
  imageUri?: string;
  quantity: number;
}

export interface Deck {
  id: number;
  name: string;
  description?: string;
  format?: string;
  createdAt: string;
}

export type DeckBoard = 'MAINBOARD' | 'SIDEBOARD' | 'COMMANDER';

export interface DeckCard {
  id: number;
  deckId: number;
  scryfallId: string;
  cardName: string;
  setCode: string;
  collectorNumber?: string;
  imageUri?: string;
  quantity: number;
  board: DeckBoard;
  owned: boolean;
  ownedQuantity: number;
}

export interface ScryfallImageUris {
  small: string;
  normal: string;
  large: string;
  art_crop: string;
}

export interface ScryfallCard {
  id: string;
  name: string;
  mana_cost?: string;
  type_line: string;
  oracle_text?: string;
  image_uris?: ScryfallImageUris;
  card_faces?: Array<{
    name: string;
    image_uris?: ScryfallImageUris;
  }>;
  set: string;
  set_name: string;
  collector_number: string;
  rarity: string;
}

export interface ScryfallSearchResult {
  object: string;
  total_cards: number;
  has_more: boolean;
  data: ScryfallCard[];
}
