package com.example.javatestapi.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Account represents a business entity in the CRM system.
 * It extends BaseEntity to inherit common fields like id, createdAt, and updatedAt.
 * Each account can have multiple contacts and leads associated with it.
 */
@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {
    @NotBlank @Column(nullable = false, unique = true)
    private String name;

    private String industry;
    private String website;
    private String billingAddress;
    private String shippingAddress;
    @Column(nullable = false) private boolean active = true;

    public String getName() { return name; }
    public String getIndustry() { return industry; }
    public String getWebsite() { return website; }
    public String getBillingAddress() { return billingAddress; }
    public String getShippingAddress() { return shippingAddress; }
    public boolean isActive() { return active; }

    public void setName(String v) { this.name = v; }
    public void setIndustry(String v) { this.industry = v; }
    public void setWebsite(String v) { this.website = v; }
    public void setBillingAddress(String v) { this.billingAddress = v; }
    public void setShippingAddress(String v) { this.shippingAddress = v; }
    public void setActive(boolean v) { this.active = v; }
}
