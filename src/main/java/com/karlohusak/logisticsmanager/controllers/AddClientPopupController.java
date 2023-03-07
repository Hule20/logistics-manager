package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.util.Initializable;
import com.karlohusak.logisticsmanager.database.Database;
import com.karlohusak.logisticsmanager.entities.*;
import com.karlohusak.logisticsmanager.exceptions.DatabaseException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.*;

public class AddClientPopupController implements Initializable<ClientTabController> {
    List<Client> clientList = new ArrayList<>();
    private ClientTabController controller;
    @Override
    public void setPrimaryController(ClientTabController controller) {
        this.controller = controller;
    }
    private Stage secondaryStage;
    @Override
    public void setSecondaryStage(Stage secondaryStage) {
        this.secondaryStage = secondaryStage;
    }

    @FXML
    private TextField clientName;
    @FXML
    private TextField clientLastName;
    @FXML
    private TextField clientPhone;

    @FXML
    private void initialize() throws DatabaseException {
        clientList = Database.getAllClients();
    }

    @FXML
    private void addClientOnAction() throws DatabaseException {
        Long client_id = (long) (clientList.size() + 1);
        String client_name = clientName.getText();
        String client_lastName = clientLastName.getText();
        String client_phone = clientPhone.getText();

        StringBuilder errorMessage = new StringBuilder();
        if (client_name.isEmpty()) {
            errorMessage.append("Ime je obavezno!");
        }
        if (client_lastName.isEmpty()) {
            errorMessage.append("Prezime je obavezno!");
        }
        if (client_phone.isEmpty()) {
            errorMessage.append("Broj mobitela je obavezan!");
        }

        if (errorMessage.isEmpty()) {
            Client newClient = new Client(client_id, client_name, client_lastName, client_phone);
            Database.addNewClient(newClient);
            controller.getClientObservableList().add(newClient);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Novi klijent uspješno spremljen!");
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
