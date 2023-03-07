package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.util.Initializable;
import com.karlohusak.logisticsmanager.database.Database;
import com.karlohusak.logisticsmanager.entities.Address;
import com.karlohusak.logisticsmanager.entities.Cargo;
import com.karlohusak.logisticsmanager.entities.Driver;
import com.karlohusak.logisticsmanager.entities.Shipment;
import com.karlohusak.logisticsmanager.exceptions.DatabaseException;
import com.karlohusak.logisticsmanager.threads.InitAddressListThread;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tornadofx.control.DateTimePicker;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UpdateShipmentPopupController implements Initializable<ShipmentController> {

    private List<Cargo> cargoList = new ArrayList<>();
    private List<Shipment> shipmentList = new ArrayList<>();
    private List<Driver> driverList = new ArrayList<>();

    private ShipmentController controller;
    private Stage secondaryStage;

    @Override
    public void setPrimaryController(ShipmentController controller) {
        this.controller = controller;
    }

    @Override
    public void setSecondaryStage(Stage secondaryStage) {
        this.secondaryStage = secondaryStage;
    }

    private List<Address> addressList;

    @FXML
    private ComboBox<Cargo> cargoCB;
    @FXML
    private ComboBox<Driver> driverCB;

    @FXML
    private TextField startAddressInput;
    @FXML
    private TextField finishAddressInput;
    private Address startAddress;
    private Address finishAddress;
    @FXML
    private ListView<Address> startAddressListView;
    @FXML
    private ListView<Address> finishListView;

    @FXML
    private DateTimePicker startDateTimePicker;
    @FXML
    private DatePicker finishDatePicker;




    @FXML
    private void initialize() throws DatabaseException {
        driverList = Database.getAllDrivers().stream().filter(d -> !d.getHasShipment())
                .toList();

        ObservableList<Driver> drObservableList =
                FXCollections.observableList(driverList);
        driverCB.setItems(drObservableList);

        cargoList = Database.getAllCargo();
        cargoList.forEach(System.out::println);

        ObservableList<Cargo> cargoObservableList =
                FXCollections.observableList(cargoList);
        cargoCB.setItems(cargoObservableList);

        //defaultne vrijednosti izvucene od odabranog retka iz tableviewa
        Platform.runLater(() -> {
            cargoCB.getSelectionModel().select(controller.getShipmentSelection().getSelectedItem().getCargo());
            driverCB.getSelectionModel().select(controller.getShipmentSelection().getSelectedItem().getDriver());

            startAddressInput.setText(controller.getShipmentSelection().getSelectedItem().getAddressFrom());
            finishAddressInput.setText(controller.getShipmentSelection().getSelectedItem().getAddressTo());
            startDateTimePicker.setDateTimeValue(controller.getShipmentSelection().getSelectedItem().getPickupDateTime());
        });
    }

    @FXML
    private void searchStartAddress() throws IOException {
        Platform.runLater(new InitAddressListThread(addressList, startAddressListView, startAddressInput));

        startAddressListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    startAddress = startAddressListView.getSelectionModel().getSelectedItem();
                    startAddressInput.setText(startAddress.toString());
                });
    }

    @FXML
    private void searchFinishAddress() throws IOException {
        Platform.runLater(new InitAddressListThread(addressList, finishListView, finishAddressInput));

        finishListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {

                    finishAddress = finishListView.getSelectionModel().getSelectedItem();
                    finishAddressInput.setText(finishAddress.toString());
                });
    }

    @FXML
    private void updateShipmentBtn() throws DatabaseException {
        Long chosenCargoId = cargoCB.getSelectionModel().getSelectedItem().getId();
        Long chosenDriverId = driverCB.getSelectionModel().getSelectedItem().getId();
        String startAddress = startAddressInput.getText();
        String finishAddress = finishAddressInput.getText();
        Boolean isActive = false;
        LocalDateTime start_date = startDateTimePicker.getDateTimeValue();
        LocalDateTime end_date = LocalDateTime.now();

        Shipment updatedShipment = new Shipment(controller.getShipmentSelection().getSelectedItem().getId(), cargoCB.getSelectionModel().getSelectedItem(),
                driverCB.getSelectionModel().getSelectedItem(), startAddress, finishAddress, isActive, start_date, end_date);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ažuriranje");
        alert.setHeaderText("Potvrdite ažuriranje pošiljke "
                + controller.getShipmentSelection().getSelectedItem().getCargo().getDescription()
                + " klijenta " + controller.getShipmentSelection().getSelectedItem().getCargo().getClient().getLastname());
        alert.setContentText("Želite ažurirati ovu pošiljku?");

        ButtonType confirmBtn = new ButtonType("Potvrdi");
        ButtonType cancelBtn = new ButtonType("Odustani");
        alert.getButtonTypes().setAll(confirmBtn, cancelBtn);

        Optional<ButtonType> choice = alert.showAndWait();
        if(choice.get() == confirmBtn){
            Database.updateShipment(controller.getShipmentSelection().getSelectedItem(),
                    chosenCargoId, chosenDriverId, startAddress, finishAddress, isActive, start_date, end_date);
            alert.close();

            Alert deletedAlert = new Alert(Alert.AlertType.INFORMATION);
            deletedAlert.setHeaderText("Pošiljka je ažurirana!");
            deletedAlert.show();
            secondaryStage.close();
            controller.getShipmentObservableList().set(controller.getShipmentSelection().getSelectedIndex(), updatedShipment);
        } else if (choice.get() == cancelBtn) {
            alert.close();
        }
    }
}
