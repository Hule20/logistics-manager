package com.karlohusak.logisticsmanager.files;

import com.karlohusak.logisticsmanager.entities.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SerializationHelper {

    private static final String CHANGELOG = "dat/changelog.dat";

    public static <T> void serializeData(T entityBefore, T entityAfter, String role, LocalDateTime changeTime) throws FileNotFoundException {
        List<Object> objToAdd = new ArrayList<>();
        objToAdd.add(entityBefore);
        objToAdd.add(entityAfter);
        objToAdd.add(role);
        objToAdd.add(changeTime);
        List<List<Object>> serializedData = new ArrayList<>();
        serializedData.add(objToAdd);

        try(ObjectOutputStream outputStream = new ObjectOutputStream(
                new FileOutputStream(CHANGELOG)
        )){
            outputStream.writeObject(serializedData);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static List<SerializedObject> deserializeData() throws FileNotFoundException {
        List<SerializedObject> deserializedData = new ArrayList<>();

        try(ObjectInputStream inputStream = new ObjectInputStream(
                new FileInputStream(CHANGELOG)
        )){
            SerializedObject obj = (SerializedObject)inputStream.readObject();
            deserializedData.add(obj);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return deserializedData;
    }
}
