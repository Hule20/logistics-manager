package com.karlohusak.logisticsmanager.maps;

import com.gluonhq.maps.MapPoint;
import com.karlohusak.logisticsmanager.entities.Address;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MapsHelper {


    public static List<Address> getAutocompleteAddress(String input) throws IOException {
        String response;

        if (input.contains(" ")) {
            input = input.replace(" ", "+");
        }

        HttpsURLConnection connection =
                (HttpsURLConnection) new URL("https://nominatim.openstreetmap.org/search.php?q="
                        + input +
                        "&format=jsonv2&addressdetails=1")
                        .openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        response = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        JSONArray jsonArrObject = new JSONArray(response);
        List<JSONObject> addressesFoundObjList = new ArrayList<>();

        for (int i = 0; i < jsonArrObject.length(); i++) {
            JSONObject obj = jsonArrObject.getJSONObject(i);
            addressesFoundObjList.add(obj);
        }

        List<Address> addresses = new ArrayList<>();
        for (int i = 0; i < addressesFoundObjList.size(); i++) {
            Address newAddress = null;

            String lat = addressesFoundObjList.get(i).getString("lat");
            String lon = addressesFoundObjList.get(i).getString("lon");
            String displayName = addressesFoundObjList.get(i).getString("display_name");

            JSONObject addressArr = addressesFoundObjList.get(i).getJSONObject("address");


            String street = addressArr.getString("road");

            String country = addressArr.getString("country");
            String city = "No city";
            if (addressArr.has("city")) {
                city = addressArr.getString("city");
            } else if (addressArr.has("town")) {
                city = addressArr.getString("town");
            } else if (addressArr.has("village")) {
                city = addressArr.getString("village");
            }

            String houseNumber;
            if (addressArr.has("house_number")) {
                houseNumber = addressArr.getString("house_number");

                newAddress = new Address.AddressBuilder()
                        .setLatitude(lat).setLongitude(lon)
                        .setDisplayName(displayName)
                        .setCountry(country)
                        .setStreet(street)
                        .setCity(city)
                        .setHouseNumber(houseNumber)
                        .build();
            } else {
                newAddress = new Address.AddressBuilder()
                        .setLatitude(lat).setLongitude(lon)
                        .setDisplayName(displayName)
                        .setCountry(country)
                        .setStreet(street)
                        .setCity(city)
                        .build();
            }

            addresses.add(newAddress);
        }

        connection.disconnect();
        return addresses;
    }

    public synchronized static String getCoordinatesFromAddress(String address) throws IOException{
        String resp;
        String coordinate;

        if (address.contains(" ")) {
            address = address.replace(" ", "+");
        }
        URLConnection connection = new URL("https://nominatim.openstreetmap.org/search.php?q=" + address + "&format=jsonv2&addressdetails=1")
                .openConnection();
        connection.connect();
        InputStream inputStream = connection.getInputStream();

        resp = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        JSONArray jsonArrObject = new JSONArray(resp);

        String lat = "noLat";
        String lon = "noLon";
        for (int i = 0; i < jsonArrObject.length(); i++) {
            JSONObject obj = jsonArrObject.getJSONObject(i);
            lat = obj.getString("lat");
            lon = obj.getString("lon");
        }

        coordinate = lon.concat(",").concat(lat);
        System.out.println("In getCoordinatesFromAddress: " + coordinate);
        return coordinate;
    }

    public static List<MapPoint> getPathCoordinates(String startPoint, String endPoint) throws IOException {

        URLConnection connection;
        String response;
            connection = new URL("https://router.project-osrm.org/route/v1/driving/" +
                    startPoint + ";" +
                    endPoint +
                    "?geometries=geojson")
                    .openConnection();


            InputStream responseStream = connection.getInputStream();
            response = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);

        JSONObject jsonObject = new JSONObject(response);
        JSONArray routesArr = jsonObject.getJSONArray("routes");

        JSONObject geometry = routesArr.getJSONObject(0)
                .getJSONObject("geometry");
        JSONArray coordinates = geometry.getJSONArray("coordinates");

        List<MapPoint> mapPoints = new ArrayList<>();
        for (Object coordinate : coordinates) {
            JSONArray corArr = (JSONArray) coordinate;
            Double lon = corArr.getDouble(0);
            Double lat = corArr.getDouble(1);
            MapPoint mapPoint = new MapPoint(lat, lon);
            mapPoints.add(mapPoint);
            System.out.println("lat:" + mapPoint.getLatitude() + ", long:" + mapPoint.getLongitude());
        }

        return mapPoints;
    }
}
