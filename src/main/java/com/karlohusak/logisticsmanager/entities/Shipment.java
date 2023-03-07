package com.karlohusak.logisticsmanager.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

public class Shipment extends Entity implements Serializable {
    private static final long serialVersionUID = 123456789L;
    private Cargo cargo;
    private Driver driver;
    private String addressFrom;
    private String addressTo;
    private Boolean isActive;
    private LocalDateTime pickupDateTime;
    private LocalDateTime deliveryDateTime;

    public Shipment(Long id, Cargo cargo, Driver driver, String addressFrom, String addressTo, Boolean isActive, LocalDateTime pickupDateTime, LocalDateTime deliveryDateTime) {
        super(id);
        this.cargo = cargo;
        this.driver = driver;
        this.addressFrom = addressFrom;
        this.addressTo = addressTo;
        this.isActive = isActive;
        this.pickupDateTime = pickupDateTime;
        this.deliveryDateTime = deliveryDateTime;
    }

    @Override
    public String toString() {
        return "Roba: " + cargo.getDescription() + "\n" +
                "Vozač: " + driver.getLastname() + "\n" +
                "Start: " + addressFrom + "\n" +
                "Odredište: " + addressTo;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getAddressFrom() {
        return addressFrom;
    }

    public void setAddressFrom(String addressFrom) {
        this.addressFrom = addressFrom;
    }

    public String getAddressTo() {
        return addressTo;
    }

    public void setAddressTo(String addressTo) {
        this.addressTo = addressTo;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getPickupDateTime() {
        return pickupDateTime;
    }

    public void setPickupDateTime(LocalDateTime pickupDateTime) {
        this.pickupDateTime = pickupDateTime;
    }

    public LocalDateTime getDeliveryDateTime() {
        return deliveryDateTime;
    }

    public void setDeliveryDateTime(LocalDateTime deliveryDateTime) {
        this.deliveryDateTime = deliveryDateTime;
    }
}
