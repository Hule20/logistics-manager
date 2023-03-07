package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.database.Database;
import com.karlohusak.logisticsmanager.entities.Driver;
import com.karlohusak.logisticsmanager.exceptions.DatabaseException;
import com.karlohusak.logisticsmanager.util.Gettable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DriversTabController implements Gettable {
    private List<Driver> drivers = getData();

    protected ObservableList<Driver> getDriversObservableList() {
        return driverObservableList;
    }

    TableView.TableViewSelectionModel<Driver> selectionModel;

    TableView.TableViewSelectionModel<Driver> getDriverSelection() {
        return selectionModel;
    }

    private ObservableList<Driver> driverObservableList = FXCollections.observableList(drivers);

    @FXML
    private TableView<Driver> driversTableview;
    @FXML
    private TableColumn<Driver, String> nameCol;
    @FXML
    private TableColumn<Driver, String> lastNameCol;
    @FXML
    private TableColumn<Driver, String> phoneCol;
    @FXML
    private TableColumn<Driver, String> vehicleCol;

    @FXML
    private TextField filterText;
    @FXML
    private SplitMenuButton filterSplitMenu;
    @FXML
    private MenuItem nameMenuItem;
    @FXML
    private MenuItem lastNameMenuItem;
    @FXML
    private MenuItem phoneMenuItem;
    @FXML
    private MenuItem vehicleMenuItem;

    @FXML
    public void initialize() {

        nameCol.setCellValueFactory(celldata -> {
            return new SimpleStringProperty(celldata.getValue().getName());
        });

        lastNameCol.setCellValueFactory(celldata -> {
            return new SimpleStringProperty(celldata.getValue().getLastname());
        });

        phoneCol.setCellValueFactory(celldata -> {
            return new SimpleStringProperty(celldata.getValue().getPhoneNumber());
        });

        vehicleCol.setCellValueFactory(celldata -> {
            return new SimpleStringProperty(celldata.getValue().getVehicle().toString());
        });

        driversTableview.setItems(driverObservableList);

        filterSplitMenu.getItems()
                .forEach(menuItem -> menuItem
                        .setOnAction(actionEvent ->
                                filterSplitMenu.setText(menuItem.getText())
                        ));

    }

    @FXML
    private void deleteDriverBtn() throws DatabaseException {
        TableView.TableViewSelectionModel<Driver> selectionModel =
                driversTableview.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Brisanje");
        alert.setHeaderText("Potvrdite brisanje vozača "
                + selectionModel.getSelectedItem().getName()
                + " " + selectionModel.getSelectedItem().getLastname());
        alert.setContentText("Želite obrisati ovog vozača?");

        ButtonType confirmBtn = new ButtonType("Potvrdi");
        ButtonType cancelBtn = new ButtonType("Odustani");
        alert.getButtonTypes().setAll(confirmBtn, cancelBtn);

        Optional<ButtonType> choice = alert.showAndWait();
        if(choice.get() == confirmBtn){
            Database.deleteDriver(selectionModel.getSelectedItem());
            alert.close();

            Alert deletedAlert = new Alert(Alert.AlertType.INFORMATION);
            deletedAlert.setHeaderText("Vozač je izbrisan!");
            deletedAlert.show();

            ObservableList<Driver> deletedList = FXCollections.observableList(getData());
            driversTableview.setItems(deletedList);

        } else if (choice.get() == cancelBtn) {
            alert.close();
        }
    }

    @FXML
    private void updateDriverBtn() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/updateDriverPopup.fxml"));
        Parent parent = loader.load();

        UpdateDriverController updateDriverController = loader.getController();
        updateDriverController.setPrimaryController(this);

        selectionModel = driversTableview.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        Stage popupStage = new Stage();
        updateDriverController.setSecondaryStage(popupStage);
        Scene popupScene = new Scene(parent, 300, 250);
        popupStage.setTitle("Ažuriranje vozača");
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    @FXML
    private void addNewDriver() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addDriverPopup.fxml"));
        Parent pane = loader.load();


        Stage popupStage = new Stage();
        Scene popupScene = new Scene(pane, 300, 250);
        popupStage.setTitle("Dodavanje novog vozača");
        popupStage.setScene(popupScene);
        popupStage.show();

        AddDriverPopupController addDriverPopupController = loader.getController();
        addDriverPopupController.setPrimaryController(this);
        addDriverPopupController.setSecondaryStage(popupStage);
    }

    @FXML
    public void onFilterButtonClick() {
        if (filterSplitMenu.getText().equals(nameMenuItem.getText())) {
            filterByName();
        } else if (filterSplitMenu.getText().equals(lastNameMenuItem.getText())) {
            filterByLastName();
        } else if (filterSplitMenu.getText().equals(phoneMenuItem.getText())) {
            filterByPhone();
        } else if (filterSplitMenu.getText().equals(vehicleMenuItem.getText())) {
            filterByVehicle();
        }
    }

    //TODO possibly extract helpers
    private void filterByName() {
        List<Driver> filteredList = drivers.stream()
                .filter(d -> d.getName().toLowerCase().contains(filterText.getText().toLowerCase()))
                .toList();

        ObservableList<Driver> observableFilteredList = FXCollections.observableList(filteredList);
        driversTableview.setItems(observableFilteredList);
    }

    private void filterByLastName() {
        List<Driver> filteredList = drivers.stream()
                .filter(d -> d.getLastname().toLowerCase().contains(filterText.getText().toLowerCase()))
                .toList();

        ObservableList<Driver> observableFilteredList = FXCollections.observableList(filteredList);
        driversTableview.setItems(observableFilteredList);
    }

    private void filterByPhone() {
        List<Driver> filteredList = drivers.stream()
                .filter(d -> d.getPhoneNumber().toLowerCase().contains(filterText.getText().toLowerCase()))
                .toList();

        ObservableList<Driver> observableFilteredList = FXCollections.observableList(filteredList);
        driversTableview.setItems(observableFilteredList);
    }

    private void filterByVehicle() {
        List<Driver> filteredList = drivers.stream()
                .filter(d -> d.getVehicle().toString().toLowerCase().contains(filterText.getText().toLowerCase()))
                .toList();

        ObservableList<Driver> observableFilteredList = FXCollections.observableList(filteredList);
        driversTableview.setItems(observableFilteredList);
    }

    @Override
    public List<Driver> getData() {
        List<Driver> driverList = new ArrayList<>();
        try {
            driverList = Database.getAllDrivers();
        } catch (DatabaseException e) {
            e.getMessage();
        }
        return driverList;
    }
}
