package com.karlohusak.logisticsmanager.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//TODO - finish this generic class and use it somewhere
public class GenericList <T> {
    private List<T> list;

    public GenericList(){
        list = new ArrayList<>();
    }

    public void pushVal(T toAdd){
        list.add(toAdd);
    }

    public void getVal(){

    }
}
