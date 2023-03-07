package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.util.Initializable;
import com.karlohusak.logisticsmanager.database.Database;
import com.karlohusak.logisticsmanager.entities.Driver;
import com.karlohusak.logisticsmanager.entities.Vehicle;
import com.karlohusak.logisticsmanager.exceptions.DatabaseException;
import com.karlohusak.logisticsmanager.util.Gettable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AddDriverPopupController implements Gettable, Initializable<DriversTabController> {
    private List<Vehicle> vehicleList = new ArrayList<>();
    private List<Driver> driverList = new ArrayList<>();
    private DriversTabController controller;
    private Stage secondaryStage;
    @Override
    public void setPrimaryController(DriversTabController controller) {
        this.controller = controller;
    }

    public void setSecondaryStage(Stage secondaryStage){
        this.secondaryStage = secondaryStage;
    }

    @FXML
    private TextField name;
    @FXML
    private TextField lastname;
    @FXML
    private TextField phone;
    @FXML
    private ComboBox<Vehicle> vehicleCB;

    @FXML
    private void initialize() throws DatabaseException{

            vehicleList = Database.getAvailableVehicles();
            driverList = Database.getAllDrivers();

        ObservableList<Vehicle> vehicleObservableList =
                FXCollections.observableList(vehicleList);
        vehicleCB.setItems(vehicleObservableList);
    }

    @FXML
    private void addDriverButton() throws DatabaseException {
        Long id = Long.valueOf(driverList.size() + 1);
        String newName = name.getText();
        String newLastName = lastname.getText();
        String newPhoneNumber = phone.getText();
        Vehicle newVehicle = vehicleCB.getSelectionModel().getSelectedItem();
        //TODO possibly extract helpers
        StringBuilder errorMessage = new StringBuilder();
        if (newName.isEmpty()) {
            errorMessage.append("Ime je obavezno!");
        }
        if (newLastName.isEmpty()) {
            errorMessage.append("Prezime je obavezno!");
        }
        if (newPhoneNumber.isEmpty()) {
            errorMessage.append("Broj mobitela je obavezan!");
        }
        if (vehicleCB.getSelectionModel().isEmpty()) {
            errorMessage.append("Vozilo je obavezno!");
        }
        if (errorMessage.isEmpty()) {
            Driver newDriver = new Driver(id, newName, newLastName, newPhoneNumber, newVehicle);
            Database.addNewDriver(newDriver);
            controller.getDriversObservableList().add(newDriver);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Novi vozač uspješno spremljen!");
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
