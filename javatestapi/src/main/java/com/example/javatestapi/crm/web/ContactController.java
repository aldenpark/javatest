package com.example.javatestapi.crm.web;

import com.example.javatestapi.crm.model.Account;
import com.example.javatestapi.crm.model.Contact;
import com.example.javatestapi.crm.repository.AccountRepository;
import com.example.javatestapi.crm.repository.ContactRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ContactController handles HTTP requests related to Contact entities.
 * It provides endpoints for listing, retrieving, creating, updating, and deleting contacts.
 * The controller uses ContactRepository to interact with the database.
 */
@RestController
@RequestMapping("/api/contacts")
public class ContactController {
    private final ContactRepository repo;
    private final AccountRepository accounts;

    public ContactController(ContactRepository repo, AccountRepository accounts) {
        this.repo = repo;
        this.accounts = accounts;
    }

    @GetMapping public Page<Contact> list(Pageable p) { return repo.findAll(p); }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Contact body, @RequestParam("accountId") String accountId) {
        Account account = accounts.findById(accountId).orElse(null);
        if (account == null) return ResponseEntity.badRequest().body("Invalid accountId");
        body.setAccount(account);
        return ResponseEntity.ok(repo.save(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody Contact body,
                                    @RequestParam(value = "accountId", required = false) String accountId) {
        return repo.findById(id).map(existing -> {
            if (accountId != null) {
                Account acc = accounts.findById(accountId).orElse(null);
                if (acc == null) return ResponseEntity.badRequest().body("Invalid accountId");
                existing.setAccount(acc);
            }
            existing.setFirstName(body.getFirstName());
            existing.setLastName(body.getLastName());
            existing.setEmail(body.getEmail());
            existing.setPhone(body.getPhone());
            existing.setTitle(body.getTitle());
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
