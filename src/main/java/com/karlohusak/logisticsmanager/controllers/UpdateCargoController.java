package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.util.Initializable;
import com.karlohusak.logisticsmanager.database.Database;
import com.karlohusak.logisticsmanager.entities.Cargo;
import com.karlohusak.logisticsmanager.entities.Client;
import com.karlohusak.logisticsmanager.exceptions.DatabaseException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UpdateCargoController implements Initializable<CargoTabController> {
    private CargoTabController controller;
    private Stage secondaryStage;
    @Override
    public void setSecondaryStage(Stage secondaryStage) {
        this.secondaryStage = secondaryStage;
    }

    @Override
    public void setPrimaryController(CargoTabController controller) {
        this.controller = controller;
    }

    @FXML
    private TextField description;
    @FXML
    private TextField mass;
    @FXML
    private ComboBox<Client> clientCB;

    @FXML
    private void initialize(){
        List<Client> clientList = new ArrayList<>();
        try {
            clientList = Database.getAvailableClients();

        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
        }

        ObservableList<Client> clientObservableList =
                FXCollections.observableList(clientList);
        clientCB.setItems(clientObservableList);

        Platform.runLater(() -> {
            description.setText(controller.getCargoSelection().getSelectedItem().getDescription());
            mass.setText(controller.getCargoSelection().getSelectedItem().getWeight().toString());
            clientCB.getSelectionModel().select(controller.getCargoSelection().getSelectedItem().getClient());
        });
    }

    @FXML
    private void updatePopupBtn() throws DatabaseException {
        String newDescription = description.getText();
        String newMass = mass.getText();
        Client newClient = clientCB.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ažuriranje");
        alert.setHeaderText("Potvrdite ažuriranje robe"
                + controller.getCargoSelection().getSelectedItem().getDescription()
                + " " + controller.getCargoSelection().getSelectedItem().getClient().getLastname());
        alert.setContentText("Želite ažurirati ovu robu skladišta?");

        ButtonType confirmBtn = new ButtonType("Potvrdi");
        ButtonType cancelBtn = new ButtonType("Odustani");
        alert.getButtonTypes().setAll(confirmBtn, cancelBtn);

        Optional<ButtonType> choice = alert.showAndWait();
        if(choice.get() == confirmBtn){
            Database.updateCargo(controller.getCargoSelection().getSelectedItem(),
                    newDescription, BigDecimal.valueOf(Long.parseLong(newMass)), newClient.getId());
            alert.close();

            Alert deletedAlert = new Alert(Alert.AlertType.INFORMATION);
            deletedAlert.setHeaderText("Roba je ažurirana!");
            deletedAlert.show();
            secondaryStage.close();
            ObservableList<Cargo> updatedList = FXCollections.observableList(controller.getData());
            controller.getCargoSelection().getTableView().setItems(updatedList);

        } else if (choice.get() == cancelBtn) {
            alert.close();
        }
    }
}
