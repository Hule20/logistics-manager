package com.karlohusak.logisticsmanager.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Vehicle extends Entity implements Serializable {
    private static final long serialVersionUID = 12345678L;
    private String model;
    private BigDecimal maxLoad;
    private String VIN;

    public Vehicle(Long id, String model, BigDecimal maxLoad, String VIN) {
        super(id);
        this.model = model;
        this.maxLoad = maxLoad;
        this.VIN = VIN;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getMaxLoad() {
        return maxLoad;
    }

    public void setMaxLoad(BigDecimal maxLoad) {
        this.maxLoad = maxLoad;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    @Override
    public String toString() {
        return this.getModel().concat(" ").concat(this.getVIN());
    }
}
