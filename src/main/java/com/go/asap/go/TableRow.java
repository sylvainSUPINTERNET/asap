package com.go.asap.go;

import java.util.BitSet;

public class TableRow {
    private boolean isValid;

    private BitSet lineBitset;


    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public boolean getIsValid() {
        return isValid;
    }

    public void setLineBitset(BitSet lineBitset) {
        this.lineBitset = lineBitset;
    }

    public BitSet getLineBitset() {
        return lineBitset;
    }
}
