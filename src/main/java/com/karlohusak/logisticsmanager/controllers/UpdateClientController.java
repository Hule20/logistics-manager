package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.util.Initializable;
import com.karlohusak.logisticsmanager.database.Database;
import com.karlohusak.logisticsmanager.entities.Client;
import com.karlohusak.logisticsmanager.exceptions.DatabaseException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

public class UpdateClientController implements Initializable<ClientTabController> {
    private ClientTabController controller;
    private Stage secondaryStage;

    @Override
    public void setSecondaryStage(Stage secondaryStage) {
        this.secondaryStage = secondaryStage;
    }
    @Override
    public void setPrimaryController(ClientTabController controller) {
        this.controller = controller;
    }

    @FXML
    private TextField name;
    @FXML
    private TextField lastName;
    @FXML
    private TextField phone;

    @FXML
    private void initialize(){

        //defaultne vrijednosti izvucene od odabranog retka iz tableviewa
        Platform.runLater(() -> {
            name.setText(controller.getClientSelection().getSelectedItem().getName());
            lastName.setText(controller.getClientSelection().getSelectedItem().getLastname());
            phone.setText(controller.getClientSelection().getSelectedItem().getPhoneNumber());
        });
    }

    @FXML
    private void updatePopupBtn() throws DatabaseException {
        String newName = name.getText();
        String newLastName = lastName.getText();
        String newPhoneNumber = phone.getText();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ažuriranje");
        alert.setHeaderText("Potvrdite ažuriranje klijenta"
                + controller.getClientSelection().getSelectedItem().getName()
                + " " + controller.getClientSelection().getSelectedItem().getLastname());
        alert.setContentText("Želite ažurirati ovog klijenta?");

        ButtonType confirmBtn = new ButtonType("Potvrdi");
        ButtonType cancelBtn = new ButtonType("Odustani");
        alert.getButtonTypes().setAll(confirmBtn, cancelBtn);

        Optional<ButtonType> choice = alert.showAndWait();
        if(choice.get() == confirmBtn){
            Database.updateClient(controller.getClientSelection().getSelectedItem(),
                    newName, newLastName, newPhoneNumber);
            alert.close();

            Alert deletedAlert = new Alert(Alert.AlertType.INFORMATION);
            deletedAlert.setHeaderText("Klijent je ažuriran!");
            deletedAlert.show();
            secondaryStage.close();
            ObservableList<Client> updatedList = FXCollections.observableList(controller.getData());
            controller.getClientSelection().getTableView().setItems(updatedList);

        } else if (choice.get() == cancelBtn) {
            alert.close();
        }
    }
}
