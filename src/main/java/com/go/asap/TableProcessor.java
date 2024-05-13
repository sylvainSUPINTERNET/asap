package com.go.asap;

import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TableProcessor {
    public List<Row> processTables(List<List<Row>> allTables) {
        LinkedList<Row> pseudoTable = new LinkedList<>();

        for (List<Row> incomingTable : allTables) {
            if (pseudoTable.isEmpty()) {
                System.out.println("Initializing pseudoTable with the first table.");
                for (Row row : incomingTable) {
                    pseudoTable.add(cloneRow(row));
                }
                continue;
            }

            Iterator<Row> it = pseudoTable.iterator();
            while (it.hasNext()) {
                Row pseudoTableRow = it.next();
                if (!pseudoTableRow.isActive()) {
                    it.remove();
                    continue;
                }

                boolean anyIntersection = false;
                for (Row incomingTableRow : incomingTable) {
                    BitSet intersection = (BitSet) pseudoTableRow.getBitset().clone();
                    intersection.and(incomingTableRow.getBitset());

                    if (!intersection.isEmpty()) {
                        anyIntersection = true;
                        pseudoTableRow.getBitset().or(incomingTableRow.getBitset());
                    }
                }

                if (!anyIntersection) {
                    pseudoTableRow.setActive(false);
                }
            }

            pseudoTable.removeIf(row -> !row.isActive());
        }

        return pseudoTable;
    }

    private Row cloneRow(Row row) {
        Row newRow = new Row();
        newRow.setActive(row.isActive());
        newRow.setBitset((BitSet) row.getBitset().clone());
        return newRow;
    }
}
