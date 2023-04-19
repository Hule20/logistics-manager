package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.StartManager;
import com.karlohusak.logisticsmanager.files.LoginHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class LoginController {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private void initialize(){

    }

    @FXML
    private void loginAction() throws IOException, NoSuchAlgorithmException {
        String enteredUsername = username.getText();
        String enteredPassword = password.getText();

        if(enteredUsername.isEmpty() || enteredPassword.isEmpty()){
            System.out.println("Empty username or password");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Neuspješna prijava!");
            alert.setHeaderText("Potrebno je popuniti sva polja prijave");
            alert.showAndWait();
        }

        boolean isAuthorised = LoginHelper.checkCredentials(enteredUsername, enteredPassword);

        if(isAuthorised){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/shipment-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 500);

            StartManager.getStage().setScene(scene);
            StartManager.getStage().show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Neuspješna prijava!");
            alert.setHeaderText("Nema korisnika sa unesenim podacima");
            alert.showAndWait();
        }
    }
}
