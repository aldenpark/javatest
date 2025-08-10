package com.example.javatestapi.crm.web;

import com.example.javatestapi.crm.model.Lead;
import com.example.javatestapi.crm.repository.LeadRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/** 
 * LeadController handles HTTP requests related to Lead entities.
 * It provides endpoints for listing, retrieving, creating, updating, and deleting leads.
 * The controller uses LeadRepository to interact with the database.
 */
@RestController
@RequestMapping("/api/leads")
public class LeadController {
    private final LeadRepository repo;
    public LeadController(LeadRepository repo) { this.repo = repo; }

    @GetMapping public Page<Lead> list(Pageable pageable) { return repo.findAll(pageable); }

    @GetMapping("/{id}")
    public ResponseEntity<Lead> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Lead> create(@Valid @RequestBody Lead body) {
        return ResponseEntity.ok(repo.save(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lead> update(@PathVariable String id, @Valid @RequestBody Lead body) {
        return repo.findById(id).map(existing -> {
            existing.setFirstName(body.getFirstName());
            existing.setLastName(body.getLastName());
            existing.setEmail(body.getEmail());
            existing.setPhone(body.getPhone());
            existing.setCompany(body.getCompany());
            existing.setSource(body.getSource());
            existing.setStatus(body.getStatus());
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
