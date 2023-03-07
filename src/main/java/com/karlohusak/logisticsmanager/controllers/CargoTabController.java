package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.database.Database;
import com.karlohusak.logisticsmanager.entities.Cargo;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CargoTabController implements Gettable {
    static final Logger logger = LoggerFactory.getLogger(CargoTabController.class);
    private List<Cargo> cargoList = getData();

    ObservableList<Cargo> getCargoObservableList() {
        return cargoObservableList;
    }

    TableView.TableViewSelectionModel<Cargo> selectionModel;

    TableView.TableViewSelectionModel<Cargo> getCargoSelection() {
        return selectionModel;
    }

    private ObservableList<Cargo> cargoObservableList = FXCollections.observableList(cargoList);

    @FXML
    private TableView<Cargo> cargoTableView;
    @FXML
    private TableColumn<Cargo, String> descriptionCol;
    @FXML
    private TableColumn<Cargo, String> massCol;
    @FXML
    private TableColumn<Cargo, String> clientCol;

    @FXML
    private TextField filterText;
    @FXML
    private SplitMenuButton filterSplitMenu;
    @FXML
    private MenuItem descriptionMenuItem;
    @FXML
    private MenuItem massMenuItem;
    @FXML
    private MenuItem clientMenuItem;

    @FXML
    public void initialize() {

        descriptionCol.setCellValueFactory(celldata -> {
            return new SimpleStringProperty(celldata.getValue().getDescription());
        });

        massCol.setCellValueFactory(celldata -> {
            return new SimpleStringProperty(celldata.getValue().getWeight().toString());
        });

        clientCol.setCellValueFactory(celldata -> {
            return new SimpleStringProperty(celldata.getValue().getClient().getLastname());
        });

        cargoTableView.setItems(cargoObservableList);

        filterSplitMenu.getItems()
                .forEach(menuItem -> menuItem
                        .setOnAction(actionEvent ->
                                filterSplitMenu.setText(menuItem.getText())
                        ));

    }

    @FXML
    private void deleteCargoBtn() throws DatabaseException {
        TableView.TableViewSelectionModel<Cargo> selectionModel =
                cargoTableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Brisanje");
        alert.setHeaderText("Potvrdite brisanje robe "
                + selectionModel.getSelectedItem().getDescription()
                + "za korisnika " + selectionModel.getSelectedItem().getClient().getLastname() + " "
                + selectionModel.getSelectedItem().getClient().getName());
        alert.setContentText("Želite obrisati ovu robu iz inventara?");

        ButtonType confirmBtn = new ButtonType("Potvrdi");
        ButtonType cancelBtn = new ButtonType("Odustani");
        alert.getButtonTypes().setAll(confirmBtn, cancelBtn);

        Optional<ButtonType> choice = alert.showAndWait();
        if(choice.get() == confirmBtn){
            Database.deleteCargo(selectionModel.getSelectedItem());
            alert.close();

            Alert deletedAlert = new Alert(Alert.AlertType.INFORMATION);
            deletedAlert.setHeaderText("Roba je izbrisana!");
            deletedAlert.show();

            ObservableList<Cargo> deletedList = FXCollections.observableList(getData());
            cargoTableView.setItems(deletedList);

        } else if (choice.get() == cancelBtn) {
            alert.close();
        }
    }

    @FXML
    private void updateCargoBtn() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/updateCargoPopup.fxml"));
        Parent parent = loader.load();

        UpdateCargoController updateCargoPopupController = loader.getController();
        updateCargoPopupController.setPrimaryController(this);

        selectionModel = cargoTableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        Stage popupStage = new Stage();
        updateCargoPopupController.setSecondaryStage(popupStage);
        Scene popupScene = new Scene(parent, 300, 250);
        popupStage.setTitle("Ažuriranje robe");
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    @FXML
    private void addNewCargo() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addCargoPopup.fxml"));
        Parent pane = loader.load();


        Stage popupStage = new Stage();
        Scene popupScene = new Scene(pane, 300, 250);
        popupStage.setTitle("Dodavanje nove robe");
        popupStage.setScene(popupScene);
        popupStage.show();

        AddCargoPopupController addNewCargoController = loader.getController();
        addNewCargoController.setPrimaryController(this);
        addNewCargoController.setSecondaryStage(popupStage);
    }

    @FXML
    public void onFilterButtonClick() {
        if (filterSplitMenu.getText().equals(descriptionMenuItem.getText())) {
            filterByDescription();
        } else if (filterSplitMenu.getText().equals(massMenuItem.getText())) {
            filterByMass();
        } else if (filterSplitMenu.getText().equals(clientMenuItem.getText())) {
            filterByClient();
        }
    }

    private void filterByDescription() {
        List<Cargo> filteredList = cargoList.stream()
                .filter(c -> c.getDescription().toLowerCase().contains(filterText.getText().toLowerCase()))
                .toList();

        ObservableList<Cargo> observableFilteredList = FXCollections.observableList(filteredList);
        cargoTableView.setItems(observableFilteredList);
    }

    private void filterByMass() {
        List<Cargo> filteredList = cargoList.stream()
                .filter(c -> c.getWeight().toString().contains(filterText.getText().toLowerCase()))
                .toList();

        ObservableList<Cargo> observableFilteredList = FXCollections.observableList(filteredList);
        cargoTableView.setItems(observableFilteredList);
    }

    private void filterByClient() {
        List<Cargo> filteredList = cargoList.stream()
                .filter(c -> c.getClient().getLastname().toLowerCase().contains(filterText.getText().toLowerCase()))
                .toList();

        ObservableList<Cargo> observableFilteredList = FXCollections.observableList(filteredList);
        cargoTableView.setItems(observableFilteredList);
    }
    @Override
    public List<Cargo> getData() {
        List<Cargo> cargoList = new ArrayList<>();
        try {
            cargoList = Database.getAllCargo();
        } catch (DatabaseException e) {
            e.getMessage();
            logger.error("Pogreška sa bazom podataka!", e);
        }
        return cargoList;
    }
}
