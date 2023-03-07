package com.karlohusak.logisticsmanager.threads;

import com.karlohusak.logisticsmanager.entities.Address;
import com.karlohusak.logisticsmanager.exceptions.NoAddressFoundException;
import com.karlohusak.logisticsmanager.maps.MapsHelper;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class InitAddressListThread implements Runnable{

    private List<Address> addressList;
    private ListView<Address> listView;
    private TextField searchInput;

    public InitAddressListThread(List<Address> addressList, ListView<Address> listView, TextField searchInput){
        this.addressList = addressList;
        this.listView = listView;
        this.searchInput = searchInput;
    }

    @Override
    public void run() {

        try {
            addressList = MapsHelper.getAutocompleteAddress(searchInput.getText())
                    .stream().limit(5).toList();

            listView.getItems().addAll(addressList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e){
            throw new NoAddressFoundException("No address found!", e.getCause());
        }
    }
}
