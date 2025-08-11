package com.example.javatestapi.crm.repository;

import com.example.javatestapi.crm.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * LeadRepository provides CRUD operations for Lead entities.
 * It extends JpaRepository to leverage Spring Data JPA's capabilities.
 */
public interface LeadRepository extends JpaRepository<Lead, String> { }
