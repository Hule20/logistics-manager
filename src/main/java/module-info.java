module com.karlohusak.logisticsmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.web;
    requires google.maps.services;
    requires java.net.http;
    requires org.json;
    requires org.controlsfx.controls;
    requires javafx.base;
    requires com.gluonhq.maps;
    requires com.gluonhq.attach.util;
    requires javafx.graphics;
    requires javafx.media;
    requires tornadofx.controls;
    requires org.slf4j;

    exports com.karlohusak.logisticsmanager to javafx.graphics;
    exports com.karlohusak.logisticsmanager.entities;
    opens com.karlohusak.logisticsmanager.controllers to javafx.fxml;
    exports com.karlohusak.logisticsmanager.controllers to javafx.fxml;
}