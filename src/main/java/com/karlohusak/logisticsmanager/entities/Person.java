package com.karlohusak.logisticsmanager.entities;

public abstract class Person extends Entity{

    protected String name;
    protected String lastname;
    protected String phoneNumber;

    public Person(Long id, String name, String lastname, String phoneNumber) {
        super(id);
        this.name = name;
        this.lastname = lastname;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
