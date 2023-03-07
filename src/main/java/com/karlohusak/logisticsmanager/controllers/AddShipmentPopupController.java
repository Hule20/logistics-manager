package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.exceptions.EntityNotSelectedException;
import com.karlohusak.logisticsmanager.util.Initializable;
import com.karlohusak.logisticsmanager.database.Database;
import com.karlohusak.logisticsmanager.entities.*;
import com.karlohusak.logisticsmanager.exceptions.DatabaseException;
import com.karlohusak.logisticsmanager.threads.InitAddressListThread;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tornadofx.control.DateTimePicker;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AddShipmentPopupController implements Initializable<ShipmentController> {

    private List<Cargo> cargoList = new ArrayList<>();
    private List<Shipment> shipmentList = new ArrayList<>();
    private List<Driver> driverList = new ArrayList<>();
    private Driver driver;
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
    private void searchFinishAddress() {
        Platform.runLater(new InitAddressListThread(addressList, finishListView, finishAddressInput));

        finishListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {

                    finishAddress = finishListView.getSelectionModel().getSelectedItem();
                    finishAddressInput.setText(finishAddress.toString());
                });
    }

    @FXML
    private void addShipmentBtn() throws DatabaseException {
        Long shipment_id = Long.valueOf(shipmentList.size() + 1);
        Driver chosenDriver = driverCB.getSelectionModel().getSelectedItem();
        Cargo newCargo = cargoCB.getSelectionModel().getSelectedItem();

        String startAddress = startAddressInput.getText();
        String finishAddress = finishAddressInput.getText();

        Boolean isActive = false;

        LocalDateTime start_date = startDateTimePicker.getDateTimeValue();
        LocalDateTime end_date = LocalDateTime.now();

        Shipment newShipment = new Shipment(shipment_id, newCargo, chosenDriver, startAddress, finishAddress,
                isActive, start_date, end_date);
        try{
            Database.addNewShipment(newShipment);
        }catch (NullPointerException e){
            throw new EntityNotSelectedException("Entity is not selected!", e.getCause());
        }

        controller.getShipmentObservableList().add(newShipment);
        secondaryStage.close();
    }
}
