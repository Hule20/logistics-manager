package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.util.Initializable;
import com.karlohusak.logisticsmanager.database.Database;
import com.karlohusak.logisticsmanager.entities.Vehicle;
import com.karlohusak.logisticsmanager.exceptions.DatabaseException;
import com.karlohusak.logisticsmanager.util.Gettable;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AddVehiclePopupController implements Gettable, Initializable<VehiclesTabController> {
    private List<Vehicle> vehicleList = new ArrayList<>();
    private VehiclesTabController controller;
    private Stage secondaryStage;
    @Override
    public void setPrimaryController(VehiclesTabController controller) {
        this.controller = controller;
    }

    public void setSecondaryStage(Stage secondaryStage){
        this.secondaryStage = secondaryStage;
    }

    @FXML
    private TextField model;
    @FXML
    private TextField vin;
    @FXML
    private TextField maxLoad;

    @FXML
    private void initialize(){
        try {
            vehicleList = Database.getAvailableVehicles();

        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void addVehicleButton() throws DatabaseException {
        Long id = Long.valueOf(vehicleList.size() + 1);
        String newModel = model.getText();
        String newVin = vin.getText();
        String newMaxLoad = maxLoad.getText();
        StringBuilder errorMessage = new StringBuilder();
        if (newModel.isEmpty()) {
            errorMessage.append("Ime je obavezno!");
        }
        if (newVin.isEmpty()) {
            errorMessage.append("Prezime je obavezno!");
        }
        if (newMaxLoad.isEmpty()) {
            errorMessage.append("Broj mobitela je obavezan!");
        }

        if (errorMessage.isEmpty()) {
            Vehicle newVehicle = new Vehicle(id, newModel, BigDecimal.valueOf(Long.parseLong(newMaxLoad)), newVin );
            Database.addNewVehicle(newVehicle);
            controller.getVehicleObservableList().add(newVehicle);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Novo vozilo uspješno spremljeno!");
            alert.setHeaderText("Spremljeno!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Neuspješno spremanje!");
            alert.setHeaderText("Nije spremljeno!");
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
        }

        secondaryStage.close();
    }

    @Override
    public <T> List<T> getData() {
        return null;
    }
}
