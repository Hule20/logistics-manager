package com.karlohusak.logisticsmanager.entities;

import java.io.Serializable;
import java.util.List;

public class Client extends Person implements Serializable {
    private static final long serialVersionUID = 123456789123L;
    public Client(Long id, String name, String lastname, String phoneNumber) {
        super(id, name, lastname, phoneNumber);
    }

    @Override
    public String toString() {
        return name + " " + lastname;
    }
}
