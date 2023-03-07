package com.karlohusak.logisticsmanager.exceptions;

public class NoAddressFoundException extends RuntimeException{

    public NoAddressFoundException(String errorMsg, Throwable throwable){
        super(errorMsg, throwable);
    }
}
