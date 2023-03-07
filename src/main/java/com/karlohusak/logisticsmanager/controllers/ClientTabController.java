package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.database.Database;
import com.karlohusak.logisticsmanager.entities.Client;
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

public class ClientTabController implements Gettable {
    private List<Client> clientList = getData();

    ObservableList<Client> getClientObservableList() {
        return clientObservableList;
    }

    TableView.TableViewSelectionModel<Client> selectionModel;

    TableView.TableViewSelectionModel<Client> getClientSelection() {
        return selectionModel;
    }

    private ObservableList<Client> clientObservableList = FXCollections.observableList(clientList);

    @FXML
    private TableView<Client> clientTableView;
    @FXML
    private TableColumn<Client, String> firstName;
    @FXML
    private TableColumn<Client, String> lastName;
    @FXML
    private TableColumn<Client, String> phoneNumber;

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
    public void initialize() {

        firstName.setCellValueFactory(celldata -> {
            return new SimpleStringProperty(celldata.getValue().getName());
        });

        lastName.setCellValueFactory(celldata -> {
            return new SimpleStringProperty(celldata.getValue().getLastname().toString());
        });

        phoneNumber.setCellValueFactory(celldata -> {
            return new SimpleStringProperty(celldata.getValue().getPhoneNumber());
        });

        clientTableView.setItems(clientObservableList);

        filterSplitMenu.getItems()
                .forEach(menuItem -> menuItem
                        .setOnAction(actionEvent ->
                                filterSplitMenu.setText(menuItem.getText())
                        ));

    }

    @FXML
    private void deleteClientBtn() throws DatabaseException {
        TableView.TableViewSelectionModel<Client> selectionModel =
                clientTableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Brisanje");
        alert.setHeaderText("Potvrdite brisanje klijenta "
                + selectionModel.getSelectedItem().getLastname() + " "
                + selectionModel.getSelectedItem().getName());
        alert.setContentText("Želite obrisati ovog klijenta?");

        ButtonType confirmBtn = new ButtonType("Potvrdi");
        ButtonType cancelBtn = new ButtonType("Odustani");
        alert.getButtonTypes().setAll(confirmBtn, cancelBtn);

        Optional<ButtonType> choice = alert.showAndWait();
        if(choice.get() == confirmBtn){
            Database.deleteClient(selectionModel.getSelectedItem());
            alert.close();

            Alert deletedAlert = new Alert(Alert.AlertType.INFORMATION);
            deletedAlert.setHeaderText("Klijent je izbrisan iz baze!");
            deletedAlert.show();

            ObservableList<Client> deletedList = FXCollections.observableList(getData());
            clientTableView.setItems(deletedList);

        } else if (choice.get() == cancelBtn) {
            alert.close();
        }
    }

    @FXML
    private void updateClientBtn() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/updateClientPopup.fxml"));
        Parent parent = loader.load();

        UpdateClientController updateClientController = loader.getController();
        updateClientController.setPrimaryController(this);

        selectionModel = clientTableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        Stage popupStage = new Stage();
        updateClientController.setSecondaryStage(popupStage);
        Scene popupScene = new Scene(parent, 300, 250);
        popupStage.setTitle("Ažuriranje klijenta");
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    @FXML
    private void addNewClient() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addClientPopup.fxml"));
        Parent pane = loader.load();


        Stage popupStage = new Stage();
        Scene popupScene = new Scene(pane, 300, 250);
        popupStage.setTitle("Dodavanje novog klijenta");
        popupStage.setScene(popupScene);
        popupStage.show();

        AddClientPopupController addClientPopupController = loader.getController();
        addClientPopupController.setPrimaryController(this);
        addClientPopupController.setSecondaryStage(popupStage);
    }

    @FXML
    public void onFilterButtonClick() {
        if (filterSplitMenu.getText().equals(nameMenuItem.getText())) {
            filterByName();
        } else if (filterSplitMenu.getText().equals(lastNameMenuItem.getText())) {
            filterByLastName();
        } else if (filterSplitMenu.getText().equals(phoneMenuItem.getText())) {
            filterByPhone();
        }
    }

    //TODO possibly extract helpers
    private void filterByName() {
        List<Client> filteredList = clientList.stream()
                .filter(c -> c.getName().toLowerCase().contains(filterText.getText().toLowerCase()))
                .toList();

        ObservableList<Client> observableFilteredList = FXCollections.observableList(filteredList);
        clientTableView.setItems(observableFilteredList);
    }

    private void filterByLastName() {
        List<Client> filteredList = clientList.stream()
                .filter(c -> c.getLastname().contains(filterText.getText().toLowerCase()))
                .toList();

        ObservableList<Client> observableFilteredList = FXCollections.observableList(filteredList);
        clientTableView.setItems(observableFilteredList);
    }

    private void filterByPhone() {
        List<Client> filteredList = clientList.stream()
                .filter(c -> c.getPhoneNumber().toLowerCase().contains(filterText.getText().toLowerCase()))
                .toList();

        ObservableList<Client> observableFilteredList = FXCollections.observableList(filteredList);
        clientTableView.setItems(observableFilteredList);
    }

    @Override
    public List<Client> getData() {
        List<Client> clientList = new ArrayList<>();
        try {
            clientList = Database.getAllClients();
        } catch (DatabaseException e) {
            e.getMessage();
        }
        return clientList;
    }
}
