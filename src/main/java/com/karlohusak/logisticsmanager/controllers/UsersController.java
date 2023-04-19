package com.karlohusak.logisticsmanager.controllers;

import com.karlohusak.logisticsmanager.entities.SerializedObject;
import com.karlohusak.logisticsmanager.files.SerializationHelper;
import javafx.fxml.FXML;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class UsersController {

    List<SerializedObject> deserObj = new ArrayList<>();

    @FXML
    private void initialize() throws FileNotFoundException {
        deserObj = SerializationHelper.deserializeData();
    }
}
