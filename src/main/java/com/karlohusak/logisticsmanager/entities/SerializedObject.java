package com.karlohusak.logisticsmanager.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SerializedObject<T> implements Serializable {
    private static final long serialVersionUID = 1234567L;
    T objBefore;
    T objAfter;
    String role;
    LocalDateTime changeTime;

    public SerializedObject(T objBefore, T objAfter, String role, LocalDateTime changeTime) {
        this.objBefore = objBefore;
        this.objAfter = objAfter;
        this.role = role;
        this.changeTime = changeTime;
    }

    @Override
    public String toString() {
        return "SerializedObject{" +
                "objBefore=" + objBefore +
                ", objAfter=" + objAfter +
                ", role='" + role + '\'' +
                ", changeTime=" + changeTime +
                '}';
    }
}
