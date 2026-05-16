package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.TcgCollectionCardUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.TcgCollectionUseCase;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgCollection;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.model.TcgCollectionCard;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.AddCardToCollectionReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.CreateCollectionReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.UpdateCollectionCardReq;
import com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model.tcg.UpdateCollectionReq;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tcg/collections")
public class TcgCollectionController {

    private final TcgCollectionUseCase collectionUseCase;
    private final TcgCollectionCardUseCase cardUseCase;

    public TcgCollectionController(TcgCollectionUseCase collectionUseCase,
                                   TcgCollectionCardUseCase cardUseCase) {
        this.collectionUseCase = collectionUseCase;
        this.cardUseCase = cardUseCase;
    }

    @GetMapping
    public ResponseEntity<List<TcgCollection>> findAll() {
        return ResponseEntity.ok(collectionUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TcgCollection> findById(@PathVariable Long id) {
        return ResponseEntity.ok(collectionUseCase.findById(id));
    }

    @PostMapping
    public ResponseEntity<TcgCollection> create(@Valid @RequestBody CreateCollectionReq req) {
        return ResponseEntity.ok(collectionUseCase.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TcgCollection> update(@PathVariable Long id,
                                                @Valid @RequestBody UpdateCollectionReq req) {
        return ResponseEntity.ok(collectionUseCase.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        collectionUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<List<TcgCollectionCard>> getCards(@PathVariable Long id) {
        return ResponseEntity.ok(cardUseCase.findByCollection(id));
    }

    @PostMapping("/{id}/cards")
    public ResponseEntity<TcgCollectionCard> addCard(@PathVariable Long id,
                                                     @Valid @RequestBody AddCardToCollectionReq req) {
        return ResponseEntity.ok(cardUseCase.addCard(id, req));
    }

    @PutMapping("/{id}/cards/{cardId}")
    public ResponseEntity<TcgCollectionCard> updateCard(@PathVariable Long id,
                                                        @PathVariable Long cardId,
                                                        @Valid @RequestBody UpdateCollectionCardReq req) {
        return ResponseEntity.ok(cardUseCase.updateCard(id, cardId, req));
    }

    @DeleteMapping("/{id}/cards/{cardId}")
    public ResponseEntity<Void> removeCard(@PathVariable Long id, @PathVariable Long cardId) {
        cardUseCase.removeCard(id, cardId);
        return ResponseEntity.noContent().build();
    }
}
