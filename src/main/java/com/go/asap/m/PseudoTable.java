package com.go.asap.m;

import java.util.HashMap;
import java.util.TreeMap;

public class PseudoTable {

    public String table;

    public TreeMap<String, Row> value;


    public PseudoTable(String name) {
        this.table = name;
    }

    public String getName(){
        return this.table;
    };

    public void setName(String name) {
        this.table = name;
    }

    public TreeMap<String, Row> getValue(){
        return this.value;
    };

    public void setValue(TreeMap<String, Row> value) {
        this.value = value;
    }



}
