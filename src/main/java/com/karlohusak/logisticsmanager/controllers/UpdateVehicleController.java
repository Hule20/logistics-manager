package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.util.Initializable;
import com.karlohusak.logisticsmanager.database.Database;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UpdateVehicleController implements Initializable<VehiclesTabController> {

    private VehiclesTabController controller;
    private Stage secondaryStage;

    @Override
    public void setSecondaryStage(Stage secondaryStage) {
        this.secondaryStage = secondaryStage;
    }

    @Override
    public void setPrimaryController(VehiclesTabController controller) {
        this.controller = controller;
    }

    @FXML
    private TextField model;
    @FXML
    private TextField vin;
    @FXML
    private TextField mass;


    @FXML
    private void initialize(){
        List<Vehicle> vehicleList = new ArrayList<>();
        try {
            vehicleList = Database.getAvailableVehicles();

        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
        }


        //defaultne vrijednosti izvucene od odabranog retka iz tableviewa
        Platform.runLater(() -> {
        model.setText(controller.getVehicleSelection().getSelectedItem().getModel());
        vin.setText(controller.getVehicleSelection().getSelectedItem().getVIN());
        mass.setText(controller.getVehicleSelection().getSelectedItem().getMaxLoad().toEngineeringString());
        });
    }

    @FXML
    private void updatePopupBtn() throws DatabaseException, FileNotFoundException {
        String newModel = model.getText();
        String newVIN = vin.getText();
        String newMass = mass.getText();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ažuriranje");
        alert.setHeaderText("Potvrdite ažuriranje vozila"
                + controller.getVehicleSelection().getSelectedItem().getModel()
                + " " + controller.getVehicleSelection().getSelectedItem().getVIN());
        alert.setContentText("Želite ažurirati ovog vozača?");

        ButtonType confirmBtn = new ButtonType("Potvrdi");
        ButtonType cancelBtn = new ButtonType("Odustani");
        alert.getButtonTypes().setAll(confirmBtn, cancelBtn);

        Optional<ButtonType> choice = alert.showAndWait();
        if(choice.get() == confirmBtn){
            Vehicle updatedVehicle = new Vehicle(controller.getVehicleSelection().getSelectedItem().getId()
            , newModel, new BigDecimal(newMass), newVIN);

            SerializationHelper.serializeData(
                    controller.getVehicleSelection().getSelectedItem(),
                    updatedVehicle,
                    Database.getRole(LoginHelper.user),
                    LocalDateTime.now());

            Database.updateVehicle(controller.getVehicleSelection().getSelectedItem(),
                    newModel, newVIN, newMass);
            alert.close();

            Alert deletedAlert = new Alert(Alert.AlertType.INFORMATION);
            deletedAlert.setHeaderText("Vozilo je ažurirano!");
            deletedAlert.show();
            secondaryStage.close();
            ObservableList<Vehicle> updatedList = FXCollections.observableList(controller.getData());
            controller.getVehicleSelection().getTableView().setItems(updatedList);

        } else if (choice.get() == cancelBtn) {
            alert.close();
        }
    }
}
