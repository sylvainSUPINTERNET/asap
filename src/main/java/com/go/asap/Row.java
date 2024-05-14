package com.go.asap;

import java.util.BitSet;

public class Row {

    private boolean isActive;

    private BitSet bitset;

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setBitset(BitSet bitset) {
        this.bitset = bitset;
    }

    public BitSet getBitset() {
        return bitset;
    }
}