package com.example.javatestapi.crm.repository;

import com.example.javatestapi.crm.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * AccountRepository provides CRUD operations for Account entities.
 * It extends JpaRepository to leverage Spring Data JPA's capabilities.
 */
public interface AccountRepository extends JpaRepository<Account, String> {
    boolean existsByName(String name);
}
