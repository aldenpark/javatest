package com.example.javatestapi.controller;

import com.example.javatestapi.util.GraphQLClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/about-me")
public class AboutMeController {

    @GetMapping("/{userId}")
    public ResponseEntity<String> getAboutMe(@PathVariable int userId) {
        try {
            String aboutMe = GraphQLClient.fetchAboutMeProfile(userId);
            return ResponseEntity.ok(aboutMe);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
