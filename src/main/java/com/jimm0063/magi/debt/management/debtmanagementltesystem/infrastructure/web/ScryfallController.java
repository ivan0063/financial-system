package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.web;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.application.port.in.ScryfallSearchUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tcg/scryfall")
public class ScryfallController {

    private final ScryfallSearchUseCase scryfallSearchUseCase;

    public ScryfallController(ScryfallSearchUseCase scryfallSearchUseCase) {
        this.scryfallSearchUseCase = scryfallSearchUseCase;
    }

    @GetMapping("/cards/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(scryfallSearchUseCase.search(q, page));
    }

    @GetMapping("/cards/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable String id) {
        return ResponseEntity.ok(scryfallSearchUseCase.getById(id));
    }

    @GetMapping("/cards/named")
    public ResponseEntity<Map<String, Object>> getByName(@RequestParam String name) {
        return ResponseEntity.ok(scryfallSearchUseCase.getByName(name));
    }
}
