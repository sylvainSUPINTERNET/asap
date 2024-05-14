package com.go.asap.m;

import java.util.BitSet;
import java.util.List;

public class Row {

    private boolean isValid;

    private List<BitSet> bitsetList;

    public Row(boolean isValid, List<BitSet> bitsetList) {
        this.isValid = isValid;
        this.bitsetList = bitsetList;
    }


    public boolean isValid(){
        return this.isValid;
    };

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public List<BitSet> getBitsetList(){
        return this.bitsetList;
    };

    public void setBitsetList(List<BitSet> bitsetList) {
        this.bitsetList = bitsetList;
    }

}