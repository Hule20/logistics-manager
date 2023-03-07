package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.util.Initializable;
import com.karlohusak.logisticsmanager.database.Database;
import com.karlohusak.logisticsmanager.entities.Driver;
import com.karlohusak.logisticsmanager.entities.Vehicle;
import com.karlohusak.logisticsmanager.exceptions.DatabaseException;
import com.karlohusak.logisticsmanager.files.LoginHelper;
import com.karlohusak.logisticsmanager.files.SerializationHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UpdateDriverController implements Initializable<DriversTabController> {

    private DriversTabController controller;
    private Stage secondaryStage;

    @Override
    public void setSecondaryStage(Stage secondaryStage) {
        this.secondaryStage = secondaryStage;
    }

    @Override
    public void setPrimaryController(DriversTabController controller) {
        this.controller = controller;
    }

    @FXML
    private TextField name;
    @FXML
    private TextField lastName;
    @FXML
    private TextField phone;
    @FXML
    private ComboBox<Vehicle> vehicleCB;

    @FXML
    private void initialize(){
        List<Vehicle> vehicleList = new ArrayList<>();
        try {
            vehicleList = Database.getAvailableVehicles();

        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
        }

        ObservableList<Vehicle> vehicleObservableList =
                FXCollections.observableList(vehicleList);
        vehicleCB.setItems(vehicleObservableList);

        //defaultne vrijednosti izvucene od odabranog retka iz tableviewa
        Platform.runLater(() -> {
        name.setText(controller.getDriverSelection().getSelectedItem().getName());
        lastName.setText(controller.getDriverSelection().getSelectedItem().getLastname());
        phone.setText(controller.getDriverSelection().getSelectedItem().getPhoneNumber());
        vehicleCB.getSelectionModel().select(controller.getDriverSelection().getSelectedItem().getVehicle());
        });
    }

    @FXML
    private void updatePopupBtn() throws DatabaseException, FileNotFoundException {
        String newName = name.getText();
        String newLastName = lastName.getText();
        String newPhoneNumber = phone.getText();
        Vehicle newVehicle = vehicleCB.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ažuriranje");
        alert.setHeaderText("Potvrdite ažuriranje vozača "
                + controller.getDriverSelection().getSelectedItem().getName()
                + " " + controller.getDriverSelection().getSelectedItem().getLastname());
        alert.setContentText("Želite ažurirati ovog vozača?");

        ButtonType confirmBtn = new ButtonType("Potvrdi");
        ButtonType cancelBtn = new ButtonType("Odustani");
        alert.getButtonTypes().setAll(confirmBtn, cancelBtn);

        Optional<ButtonType> choice = alert.showAndWait();
        if(choice.get() == confirmBtn){
            Driver updatedDriver = new Driver(controller.getDriverSelection().getSelectedItem().getId(),
                    newName, newLastName, newPhoneNumber, newVehicle);

            SerializationHelper.serializeData(
                    controller.getDriverSelection().getSelectedItem(),
                    updatedDriver,
                    Database.getRole(LoginHelper.user),
                    LocalDateTime.now());

            Database.updateDriver(controller.getDriverSelection().getSelectedItem(),
                    newName, newLastName, newPhoneNumber, newVehicle.getId());
            alert.close();

            Alert deletedAlert = new Alert(Alert.AlertType.INFORMATION);
            deletedAlert.setHeaderText("Vozač je ažuriran!");
            deletedAlert.show();
            secondaryStage.close();
            ObservableList<Driver> updatedList = FXCollections.observableList(controller.getData());
            controller.getDriverSelection().getTableView().setItems(updatedList);

        } else if (choice.get() == cancelBtn) {
            alert.close();
        }
    }
}
