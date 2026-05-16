package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.TcgDeckCardUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.TcgDeckUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.dto.DeckCardOwnershipDto;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgDeck;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgDeckCard;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.AddCardToDeckReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.CreateDeckReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.UpdateDeckCardReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.UpdateDeckReq;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tcg/decks")
public class TcgDeckController {

    private final TcgDeckUseCase deckUseCase;
    private final TcgDeckCardUseCase deckCardUseCase;

    public TcgDeckController(TcgDeckUseCase deckUseCase, TcgDeckCardUseCase deckCardUseCase) {
        this.deckUseCase = deckUseCase;
        this.deckCardUseCase = deckCardUseCase;
    }

    @GetMapping
    public ResponseEntity<List<TcgDeck>> findAll() {
        return ResponseEntity.ok(deckUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TcgDeck> findById(@PathVariable Long id) {
        return ResponseEntity.ok(deckUseCase.findById(id));
    }

    @PostMapping
    public ResponseEntity<TcgDeck> create(@Valid @RequestBody CreateDeckReq req) {
        return ResponseEntity.ok(deckUseCase.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TcgDeck> update(@PathVariable Long id,
                                          @Valid @RequestBody UpdateDeckReq req) {
        return ResponseEntity.ok(deckUseCase.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deckUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<List<DeckCardOwnershipDto>> getDeckCards(@PathVariable Long id) {
        return ResponseEntity.ok(deckCardUseCase.getDeckWithOwnership(id));
    }

    @PostMapping("/{id}/cards")
    public ResponseEntity<TcgDeckCard> addCard(@PathVariable Long id,
                                               @Valid @RequestBody AddCardToDeckReq req) {
        return ResponseEntity.ok(deckCardUseCase.addCard(id, req));
    }

    @PutMapping("/{id}/cards/{cardId}")
    public ResponseEntity<TcgDeckCard> updateCard(@PathVariable Long id,
                                                  @PathVariable Long cardId,
                                                  @Valid @RequestBody UpdateDeckCardReq req) {
        return ResponseEntity.ok(deckCardUseCase.updateCard(id, cardId, req));
    }

    @DeleteMapping("/{id}/cards/{cardId}")
    public ResponseEntity<Void> removeCard(@PathVariable Long id, @PathVariable Long cardId) {
        deckCardUseCase.removeCard(id, cardId);
        return ResponseEntity.noContent().build();
    }
}
