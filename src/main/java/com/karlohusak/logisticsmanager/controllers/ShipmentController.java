package com.karlohusak.logisticsmanager.controllers;

//import com.gluonhq.maps.MapView;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import com.karlohusak.logisticsmanager.database.Database;
import com.karlohusak.logisticsmanager.entities.*;
import com.karlohusak.logisticsmanager.exceptions.DatabaseException;
import com.karlohusak.logisticsmanager.maps.MapsHelper;
import com.karlohusak.logisticsmanager.maps.RouteLayer;
import com.karlohusak.logisticsmanager.threads.GetCoordinatesThread;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class ShipmentController {
    static final Logger logger = LoggerFactory.getLogger(ShipmentController.class);
    private List<Shipment> shipmentList = new ArrayList<>();

    final static Double DARUVAR_LAT = 45.59056;
    final static Double DARUVAR_LON = 17.225;

    List<String> addressList = new ArrayList<>();

    MultipleSelectionModel<Shipment> shipment;

    protected MultipleSelectionModel<Shipment> getShipmentSelection() {
        return shipment;
    }

    private ObservableList<Shipment> shipmentObservableList;

    protected ObservableList<Shipment> getShipmentObservableList() {
        return shipmentObservableList;
    }

    @FXML
    private MapView mapview;
    private MapLayer newRoute;

    @FXML
    private ListView<Shipment> shipmentLV;


    List<Address> addresses;

    @FXML
    private void initialize() throws DatabaseException {
        shipmentList = Database.getAllShipments();
        shipmentLV.setFixedCellSize(70);
        shipmentObservableList = FXCollections.observableList(shipmentList);
        shipmentLV.setItems(shipmentObservableList);

        Platform.runLater(() -> {
            mapview.setCenter(new MapPoint(DARUVAR_LAT, DARUVAR_LON));
            mapview.setZoom(15);

            shipmentLV.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    try {
                        mapview.removeLayer(newRoute);
                        newRoute = routeLayer();
                        mapview.addLayer(newRoute);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
    }

    @FXML
    private void addShipment() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addShipmentPopup.fxml"));
        Parent pane = loader.load();

        AddShipmentPopupController addShipmentPopupController = loader.getController();
        addShipmentPopupController.setPrimaryController(this);

        Stage popupStage = new Stage();
        addShipmentPopupController.setSecondaryStage(popupStage);
        Scene popupScene = new Scene(pane, 250, 400);
        popupStage.setTitle("Dodavanje nove pošiljke");
        popupStage.setScene(popupScene);
        popupStage.show();

    }

    @FXML
    private void updateShipment() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/updateShipmentPopup.fxml"));
        Parent parent = loader.load();

        UpdateShipmentPopupController updateShipmentPopupController = loader.getController();
        updateShipmentPopupController.setPrimaryController(this);

        shipment = shipmentLV.getSelectionModel();
        shipment.setSelectionMode(SelectionMode.SINGLE);

        Stage popupStage = new Stage();
        updateShipmentPopupController.setSecondaryStage(popupStage);
        Scene popupScene = new Scene(parent, 300, 400);
        popupStage.setTitle("Ažuriranje pošiljke");
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    @FXML
    private void deleteShipment() throws DatabaseException {
        shipment = shipmentLV.getSelectionModel();
        shipment.setSelectionMode(SelectionMode.SINGLE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Brisanje");
        alert.setHeaderText("Potvrdite brisanje pošiljke " +
                 shipment.getSelectedItem().getCargo() +
                 " za klijenta " + shipment.getSelectedItem().getCargo().getClient().getLastname() +
                shipment.getSelectedItem().getCargo().getClient().getName());
        alert.setContentText("Želite obrisati ovu pošiljku?");

        ButtonType confirmBtn = new ButtonType("Potvrdi");
        ButtonType cancelBtn = new ButtonType("Odustani");
        alert.getButtonTypes().setAll(confirmBtn, cancelBtn);

        Optional<ButtonType> choice = alert.showAndWait();
        if(choice.get() == confirmBtn){
            Database.deleteShipment(shipment.getSelectedItem());



            alert.close();

            Alert deletedAlert = new Alert(Alert.AlertType.INFORMATION);
            deletedAlert.setHeaderText("Pošiljka je izbrisana!");
            deletedAlert.show();

            shipmentObservableList.remove(shipment.getSelectedIndex());
            shipmentLV.setItems(shipmentObservableList);

        } else if (choice.get() == cancelBtn) {
            alert.close();
        }
    }

    private MapLayer routeLayer() throws IOException, InterruptedException {

        GetCoordinatesThread getCoordinatesStart =
                new GetCoordinatesThread(shipmentLV
                        .getSelectionModel()
                        .getSelectedItem()
                        .getAddressFrom()
                );

        Thread thread = new Thread(getCoordinatesStart);
        thread.start();

        try {
            Thread.sleep(1100);
            thread.join();
        } catch (InterruptedException e) {
            logger.error("Došlo je do pogreške sa niti!", e);
            throw new RuntimeException(e);
        }
        String startCrd = getCoordinatesStart.getCoordinate();

        GetCoordinatesThread getCoordinatesEnd =
                new GetCoordinatesThread(shipmentLV
                        .getSelectionModel()
                        .getSelectedItem()
                        .getAddressTo()
                );

        thread = new Thread(getCoordinatesEnd);
        thread.start();
        try {
            Thread.sleep(1100);
            thread.join();
        } catch (InterruptedException e) {
            logger.error("Došlo je do pogreške sa niti!", e);
            throw new RuntimeException(e);

        }
        String endCrd = getCoordinatesEnd.getCoordinate();

        List<MapPoint> polPoints = MapsHelper
                .getPathCoordinates(startCrd,
                        endCrd);

        RouteLayer poi = new RouteLayer();
        for (MapPoint mapPoint : polPoints) {
            poi.addPoint(mapPoint, new Circle(5, Color.BLUE));
        }
        return poi;
    }
}
