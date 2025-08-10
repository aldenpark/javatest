package com.example.javatestapi.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Contact represents a person associated with an Account in the CRM system.
 * It extends BaseEntity to inherit common fields like id, createdAt, and updatedAt.
 * Each contact is linked to an account and contains personal details like name, email, and phone.
 */
@Entity
@Table(name = "contacts")
public class Contact extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)            // Many contacts can belong to one account, but an account must have at least one contact.
    @JoinColumn(name = "account_id", nullable = false)              // This column is a foreign key to the Account entity, ensuring that each contact is associated with an account.
    private Account account;

    @NotBlank @Column(nullable = false) private String firstName;   // First name of the contact, cannot be blank.
    @NotBlank @Column(nullable = false) private String lastName;    // Last name of the contact, cannot be blank.
    @Email private String email;                                    // Email of the contact, must be a valid email format.
    private String phone;
    private String title;
    @Column(nullable = false) private boolean active = true;        // Indicates if the contact is active, default is true.

    public Account getAccount() { return account; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getTitle() { return title; }
    public boolean isActive() { return active; }

    public void setAccount(Account v) { this.account = v; }
    public void setFirstName(String v) { this.firstName = v; }
    public void setLastName(String v) { this.lastName = v; }
    public void setEmail(String v) { this.email = v; }
    public void setPhone(String v) { this.phone = v; }
    public void setTitle(String v) { this.title = v; }
    public void setActive(boolean v) { this.active = v; }
}
