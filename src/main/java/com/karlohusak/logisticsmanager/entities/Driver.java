package com.karlohusak.logisticsmanager.entities;

import com.karlohusak.logisticsmanager.database.Database;
import com.karlohusak.logisticsmanager.exceptions.DatabaseException;
import javafx.scene.control.cell.CheckBoxListCell;

import java.io.Serializable;
import java.util.List;

public class Driver extends Person implements Serializable {
    private static final long serialVersionUID = 12345678912L;
    private Vehicle vehicle;
    private boolean hasShipment;

    public Driver(Long id, String name, String lastname, String phoneNumber, Vehicle vehicle) {
        super(id, name, lastname, phoneNumber);
        this.vehicle = vehicle;
    }

    public Driver(Long id, String name, String lastname, String phoneNumber, Vehicle vehicle, boolean hasShipment) {
        super(id, name, lastname, phoneNumber);
        this.vehicle = vehicle;
        this.hasShipment = hasShipment;
    }

    @Override
    public String toString() {
        return "Vozač: " + getLastname() + " " + getName();
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public boolean getHasShipment() {
        return hasShipment;
    }

    public void setHasShipment(boolean hasShipment) {
        this.hasShipment = hasShipment;
    }
}
