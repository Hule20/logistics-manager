package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.database.Database;

import com.karlohusak.logisticsmanager.entities.Vehicle;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehiclesTabController implements Gettable {
    private List<Vehicle> vehicleList = getData();

    protected ObservableList<Vehicle> getVehicleObservableList() {
        return vehicleObservableList;
    }

    TableView.TableViewSelectionModel<Vehicle> selectionModel;

    TableView.TableViewSelectionModel<Vehicle> getVehicleSelection() {
        return selectionModel;
    }

    private ObservableList<Vehicle> vehicleObservableList = FXCollections.observableList(vehicleList);

    @FXML
    private TableView<Vehicle> vehicleTableView;
    @FXML
    private TableColumn<Vehicle, String> modelCol;
    @FXML
    private TableColumn<Vehicle, String> vinCol;
    @FXML
    private TableColumn<Vehicle, String> maxLoadCol;

    @FXML
    private TextField filterText;
    @FXML
    private SplitMenuButton filterSplitMenu;
    @FXML
    private MenuItem modelMenuItem;
    @FXML
    private MenuItem vinMenuItem;
    @FXML
    private MenuItem maxLoadMenuItem;

    @FXML
    public void initialize() throws FileNotFoundException {

        modelCol.setCellValueFactory(celldata -> {
            return new SimpleStringProperty(celldata.getValue().getModel());
        });

        vinCol.setCellValueFactory(celldata -> {
            return new SimpleStringProperty(celldata.getValue().getVIN());
        });

        maxLoadCol.setCellValueFactory(celldata -> {
            return new SimpleStringProperty(celldata.getValue().getMaxLoad().toString());
        });

        vehicleTableView.setItems(vehicleObservableList);
        
        filterSplitMenu.getItems()
                .forEach(menuItem -> menuItem
                        .setOnAction(actionEvent ->
                                filterSplitMenu.setText(menuItem.getText())
                        ));
    }

    @FXML
    private void addNewVehicle() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addVehiclePopup.fxml"));
        Parent pane = loader.load();


        Stage popupStage = new Stage();
        Scene popupScene = new Scene(pane, 300, 250);
        popupStage.setTitle("Dodavanje novog vozila");
        popupStage.setScene(popupScene);
        popupStage.show();

        AddVehiclePopupController addVehiclePopupController = loader.getController();
        addVehiclePopupController.setPrimaryController(this);
        addVehiclePopupController.setSecondaryStage(popupStage);
    }

    @FXML
    private void updateVehicle() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/updateVehiclePopup.fxml"));
        Parent parent = loader.load();

        UpdateVehicleController updateVehicleController = loader.getController();
        updateVehicleController.setPrimaryController(this);

        selectionModel = vehicleTableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        Stage popupStage = new Stage();
        updateVehicleController.setSecondaryStage(popupStage);
        Scene popupScene = new Scene(parent, 300, 250);
        popupStage.setTitle("Ažuriranje vozila");
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    @FXML
    private void deleteVehicle() throws DatabaseException {
        TableView.TableViewSelectionModel<Vehicle> selectionModel =
                vehicleTableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Brisanje");
        alert.setHeaderText("Potvrdite brisanje vozila"
                + selectionModel.getSelectedItem().getModel()
                + " " + selectionModel.getSelectedItem().getVIN());
        alert.setContentText("Želite obrisati ovog vozača?");

        ButtonType confirmBtn = new ButtonType("Potvrdi");
        ButtonType cancelBtn = new ButtonType("Odustani");
        alert.getButtonTypes().setAll(confirmBtn, cancelBtn);

        Optional<ButtonType> choice = alert.showAndWait();
        if(choice.get() == confirmBtn){
            Database.deleteVehicle(selectionModel.getSelectedItem());
            alert.close();

            Alert deletedAlert = new Alert(Alert.AlertType.INFORMATION);
            deletedAlert.setHeaderText("Vozač je izbrisan!");
            deletedAlert.show();

            ObservableList<Vehicle> deletedList = FXCollections.observableList(getData());
            vehicleTableView.setItems(deletedList);

        } else if (choice.get() == cancelBtn) {
            alert.close();
        }
    }

    @FXML
    public void onFilterButtonClick() {
        if (filterSplitMenu.getText().equals(modelMenuItem.getText())) {
            filterByModel();
        } else if (filterSplitMenu.getText().equals(vinMenuItem.getText())) {
            filterByVin();
        } else if (filterSplitMenu.getText().equals(maxLoadMenuItem.getText())) {
            filterByMaxLoad();
        }
    }

    //TODO possibly extract helpers
    private void filterByModel() {
        List<Vehicle> filteredList = vehicleList.stream()
                .filter(v -> v.getModel().toLowerCase().contains(filterText.getText().toLowerCase()))
                .toList();

        ObservableList<Vehicle> observableFilteredList = FXCollections.observableList(filteredList);
        vehicleTableView.setItems(observableFilteredList);
    }

    private void filterByVin() {
        List<Vehicle> filteredList = vehicleList.stream()
                .filter(v -> v.getVIN().toLowerCase().contains(filterText.getText().toLowerCase()))
                .toList();

        ObservableList<Vehicle> observableFilteredList = FXCollections.observableList(filteredList);
        vehicleTableView.setItems(observableFilteredList);
    }

    private void filterByMaxLoad() {
        List<Vehicle> filteredList = vehicleList.stream()
                .filter(v -> v.getMaxLoad().toString().toLowerCase().contains(filterText.getText().toLowerCase()))
                .toList();

        ObservableList<Vehicle> observableFilteredList = FXCollections.observableList(filteredList);
        vehicleTableView.setItems(observableFilteredList);
    }

    @Override
    public List<Vehicle> getData() {
        List<Vehicle> vehicleList = new ArrayList<>();
        try {
            vehicleList = Database.getAllVehicles();
        } catch (DatabaseException e) {
            e.getMessage();
        }
        return vehicleList;
    }
}
