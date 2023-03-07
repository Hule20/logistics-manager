package com.karlohusak.logisticsmanager.entities;

import java.io.Serializable;

public abstract class Entity implements Serializable{
    private static final long serialVersionUID = 1234567891L;
    private Long id;

    public Entity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
