package com.karlohusak.logisticsmanager.entities;

import java.io.Serializable;
import java.math.BigDecimal;

public class Cargo extends Entity implements Serializable {
    private static final long serialVersionUID = 1234567891234L;

    private String description;
    private BigDecimal weight;
    private Client client;

    public Cargo(Long id, String description, BigDecimal weight, Client client) {
        super(id);
        this.description = description;
        this.weight = weight;
        this.client = client;
    }

    public String getDescription() {
        return description;
    }

    public Client getClient() {
        return client;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Roba: " + getDescription() + " Klijent: " + getClient().getLastname();
    }
}
