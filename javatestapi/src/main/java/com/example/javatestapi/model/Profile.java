package com.example.javatestapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "profiles") // maps to your PostgreSQL table
public class Profile {
    @Id
    private String id;
    @Column(name = "full_name")
    private String name;
    private String title;
    private String location;
    private String summary;
    private String image;
    private boolean active;

    // Default constructor for JPA
    public Profile() {}

    // Constructor for manual instantiation
    public Profile(String id, String name, String title, String location, String summary, String image, boolean active) {

        this.id = id;
        this.name = name;
        this.title = title;
        this.location = location;
        this.summary = summary;
        this.image = image;
        this.active = active;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getSummary() { return summary; }
    public String getImage() { return image; }
    public boolean isActive() { return active; }

    // Setters
}

