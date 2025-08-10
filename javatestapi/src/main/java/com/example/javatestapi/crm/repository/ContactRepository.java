package com.example.javatestapi.crm.repository;

import com.example.javatestapi.crm.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ContactRepository provides CRUD operations for Contact entities.
 * It extends JpaRepository to leverage Spring Data JPA's capabilities.
 */
public interface ContactRepository extends JpaRepository<Contact, String> { }
