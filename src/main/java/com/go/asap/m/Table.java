package com.go.asap.m;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class Table {

    public String table;

    public TreeMap<String, Row> value;


    public Table(String name) {
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
