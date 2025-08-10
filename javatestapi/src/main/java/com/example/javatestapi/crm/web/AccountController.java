package com.example.javatestapi.crm.web;

import com.example.javatestapi.crm.model.Account;
import com.example.javatestapi.crm.repository.AccountRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AccountController handles HTTP requests related to Account entities.
 * It provides endpoints for listing, retrieving, creating, updating, and deleting accounts.
 * The controller uses AccountRepository to interact with the database.
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountRepository repo;
    public AccountController(AccountRepository repo) { this.repo = repo; }

    @GetMapping public Page<Account> list(Pageable p) { return repo.findAll(p); }

    @GetMapping("/{id}")
    public ResponseEntity<Account> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Account body) {
        if (repo.existsByName(body.getName())) {
            return ResponseEntity.badRequest().body("Account with that name already exists");
        }
        return ResponseEntity.ok(repo.save(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> update(@PathVariable String id, @Valid @RequestBody Account body) {
        return repo.findById(id).map(existing -> {
            existing.setName(body.getName());
            existing.setIndustry(body.getIndustry());
            existing.setWebsite(body.getWebsite());
            existing.setBillingAddress(body.getBillingAddress());
            existing.setShippingAddress(body.getShippingAddress());
            existing.setActive(body.isActive());
            return ResponseEntity.ok(repo.save(existing));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
