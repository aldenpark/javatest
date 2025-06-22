package com.example.javatestapi.controller;

import com.example.javatestapi.model.Profile;
import com.example.javatestapi.repository.ProfileRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
// @CrossOrigin // Optional during development
public class ProfileController {

    private final ProfileRepository profileRepository;

    public ProfileController(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @GetMapping("/profiles")
    public List<Profile> getProfiles() {
        return profileRepository.findByActiveTrue();
    }
}
