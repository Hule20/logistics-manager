package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.util.Initializable;
import com.karlohusak.logisticsmanager.database.Database;
import com.karlohusak.logisticsmanager.entities.Cargo;
import com.karlohusak.logisticsmanager.entities.Client;
import com.karlohusak.logisticsmanager.exceptions.DatabaseException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AddCargoPopupController implements Initializable<CargoTabController> {
    List<Cargo> cargoList = new ArrayList<>();
    List<Client> clientList = new ArrayList<>();

    private CargoTabController controller;

    @Override
    public void setPrimaryController(CargoTabController controller) {
        this.controller = controller;
    }

    private Stage secondaryStage;

    @Override
    public void setSecondaryStage(Stage secondaryStage) {
        this.secondaryStage = secondaryStage;
    }

    @FXML
    private TextField cargoName;
    @FXML
    private TextField cargoMass;
    @FXML
    private ComboBox<Client> clientCB;

    @FXML
    private void initialize() throws DatabaseException {
        cargoList = Database.getAllCargo();
        clientList = Database.getAvailableClients();

        ObservableList<Client> clientObservableList =
                FXCollections.observableList(clientList);
        clientCB.setItems(clientObservableList);
    }

    @FXML
    private void addCargoOnAction() throws DatabaseException {
        Long cargo_id = Long.valueOf(cargoList.size() + 1);
        String cargo_name = cargoName.getText();
        BigDecimal cargo_weight = BigDecimal.valueOf(Long.parseLong(cargoMass.getText()));
        Client newClient = clientCB.getSelectionModel().getSelectedItem();

        StringBuilder errorMessage = new StringBuilder();
        if (cargo_name.isEmpty()) {
            errorMessage.append("Ime je obavezno!");
        }
        if (cargo_weight.equals(null)) {
            errorMessage.append("Masa je obavezna!");
        }
        if (clientCB.getSelectionModel().isEmpty()) {
            errorMessage.append("Klijent je obavezan!");
        }

        if (errorMessage.isEmpty()) {
            Cargo cargo = new Cargo(cargo_id, cargo_name, cargo_weight, newClient);
            Database.addNewCargo(cargo);
            controller.getCargoObservableList().add(cargo);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Nova roba uspješno spremljena!");
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
}
