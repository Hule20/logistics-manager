package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.StartManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;


import java.io.IOException;

public class MenuController {

    public void showManagementView() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/management-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 500);

        StartManager.getStage().setScene(scene);
        StartManager.getStage().show();
    }

    public void showShipmentsView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/shipment-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 500);

        StartManager.getStage().setScene(scene);
        StartManager.getStage().show();
    }

    public void showClientsView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ShipmentController.class.getResource("/views/clients-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 500);

        StartManager.getStage().setScene(scene);
        StartManager.getStage().show();
    }

    public void showUsersView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/users-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 500);

        StartManager.getStage().setScene(scene);
        StartManager.getStage().show();
    }
}
