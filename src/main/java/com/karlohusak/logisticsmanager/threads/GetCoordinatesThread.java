package com.karlohusak.logisticsmanager.threads;

import com.karlohusak.logisticsmanager.maps.MapsHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.util.TimerTask;

public class GetCoordinatesThread implements Runnable {
    private volatile String coordinate;
    private String address;

    public GetCoordinatesThread(String address){
        this.address = address;
    }

    @Override
    public void run(){
        try {
            coordinate = MapsHelper.getCoordinatesFromAddress(address);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCoordinate(){
        return coordinate;
    }
}
