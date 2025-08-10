package com.example.javatestapi.crm.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

/**
 * BaseEntity serves as a base class for all entities in the CRM module.
 * It provides common fields like id, createdAt, and updatedAt.
 * 
 * The id field is a UUID, ensuring unique identification across the system.
 * The createdAt and updatedAt fields are automatically managed by Hibernate.
 */
@MappedSuperclass
public abstract class BaseEntity {
    @Id                                                         // This annotation marks the field as the primary key of the entity.
    @GeneratedValue                                             // This annotation indicates that the value of the id field will be generated automatically.
    @UuidGenerator                                              // This annotation specifies that the id will be generated as a UUID.
    @Column(updatable = false, nullable = false, length = 36)   // The id column is non-updatable and non-nullable, with a length of 36 characters.
    private String id;

    @CreationTimestamp                                          // This annotation automatically sets the createdAt field to the current timestamp when the entity is created.
    @Column(updatable = false)                                  // The createdAt column is non-updatable, meaning it cannot be changed after the entity is created.
    private Instant createdAt;

    @UpdateTimestamp                                            // This annotation automatically sets the updatedAt field to the current timestamp when the entity is updated.
    private Instant updatedAt;

    public String getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setId(String id) { this.id = id; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
