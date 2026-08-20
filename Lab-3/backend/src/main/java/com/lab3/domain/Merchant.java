package com.lab3.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Merchant {
    @Id private String id;
    private String name;
    private boolean active;
    protected Merchant() {}
    public Merchant(String id, String name, boolean active) { this.id=id; this.name=name; this.active=active; }
    public String getId(){return id;} public String getName(){return name;} public boolean isActive(){return active;}
}
