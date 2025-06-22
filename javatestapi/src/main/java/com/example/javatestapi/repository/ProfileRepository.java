package com.example.javatestapi.repository;

import com.example.javatestapi.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    List<Profile> findByActiveTrue();
}
