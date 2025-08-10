package com.example.javatestapi.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Lead represents a potential customer in the CRM system.
 * It extends BaseEntity to inherit common fields like id, createdAt, and updatedAt.
 * Each lead has personal details, status, source, and an active flag.
 */
@Entity
@Table(name = "leads")
public class Lead extends BaseEntity {

    public enum Status { NEW, QUALIFIED, LOST, CONVERTED }

    @NotBlank @Column(nullable = false) private String firstName;
    @NotBlank @Column(nullable = false) private String lastName;
    @Email @Column(unique = false) private String email;
    private String phone;
    private String company;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.NEW;

    private String source;    // e.g. web, referral
    @Column(nullable = false) private boolean active = true;

    public String getFirstName() { return firstName; }
    public String getLastName()  { return lastName; }
    public String getEmail()     { return email; }
    public String getPhone()     { return phone; }
    public String getCompany()   { return company; }
    public Status getStatus()    { return status; }
    public String getSource()    { return source; }
    public boolean isActive()    { return active; }

    public void setFirstName(String v) { this.firstName = v; }
    public void setLastName(String v)  { this.lastName = v; }
    public void setEmail(String v)     { this.email = v; }
    public void setPhone(String v)     { this.phone = v; }
    public void setCompany(String v)   { this.company = v; }
    public void setStatus(Status v)    { this.status = v; }
    public void setSource(String v)    { this.source = v; }
    public void setActive(boolean v)   { this.active = v; }
}
