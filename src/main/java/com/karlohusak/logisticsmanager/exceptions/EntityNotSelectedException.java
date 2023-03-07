package com.karlohusak.logisticsmanager.exceptions;

public class EntityNotSelectedException extends RuntimeException{
    public EntityNotSelectedException(String errorMsg, Throwable throwable){
        super(errorMsg, throwable);
    }

}
