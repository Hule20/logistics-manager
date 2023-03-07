package com.karlohusak.logisticsmanager.util;

import javafx.stage.Stage;

public interface Initializable<T> {

    void setPrimaryController(T controller);

    void setSecondaryStage(Stage secondaryStage);
}
