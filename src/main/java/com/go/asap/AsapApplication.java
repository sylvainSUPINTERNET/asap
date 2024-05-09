package com.go.asap;

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class AsapApplication implements CommandLineRunner {

	public static HashMap<String, Integer> characteristicValueIndexMap = new HashMap<>();


	public static void main(String[] args) {
		SpringApplication.run(AsapApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Object2ObjectRBTreeMap<String, Integer> characteristicValueIndex = new Object2ObjectRBTreeMap<>();
		characteristicValueIndex.put("AA_01", 0);
		characteristicValueIndex.put("AA_02", 1);
		characteristicValueIndex.put("BB_02", 2);
		characteristicValueIndex.put("BB_03", 3);
		characteristicValueIndex.put("CC_01", 4);
		characteristicValueIndex.put("CC_02", 5);
		characteristicValueIndex.put("XX_01", 6);


		List<Row> pseudoTable = new ArrayList<>();


		List<List<Row>> allTables = new ArrayList<>();
		/*
		 * AA | BB
		 * --------
		 * 01 | 02
		 * 02 | 03
		 */
		List<Row> table1 = new ArrayList<>();
		Row row1 = new Row();
		row1.setActive(true);
		var b1 = new BitSet();
		b1.set(characteristicValueIndex.get("AA_01"));
		b1.set(characteristicValueIndex.get("BB_02"));
		row1.setBitset(b1);
		table1.add(row1);

		Row row2 = new Row();
		row2.setActive(true);
		var b2 = new BitSet();
		b2.set(characteristicValueIndex.get("AA_02"));
		b2.set(characteristicValueIndex.get("BB_03"));
		row2.setBitset(b2);
		table1.add(row2);

		/*
		 * AA | BB | CC
		 * ------------
		 * 01 | 02 | 01
		 * 02 | 03 | 02
		 */
		List<Row> table2 = new ArrayList<>();
		Row row3 = new Row();
		row3.setActive(true);
		var b3 = new BitSet();
		b3.set(characteristicValueIndex.get("AA_01"));
		b3.set(characteristicValueIndex.get("BB_02"));
		b3.set(characteristicValueIndex.get("CC_01"));
		row3.setBitset(b3);
		table2.add(row3);

		Row row4 = new Row();
		row4.setActive(true);
		var b4 = new BitSet();
		b4.set(characteristicValueIndex.get("AA_02"));
		b4.set(characteristicValueIndex.get("BB_03"));
		b4.set(characteristicValueIndex.get("CC_02"));
		row4.setBitset(b4);
		table2.add(row4);


		List<Row> table3 = new ArrayList<>();
		Row row5 = new Row();
		row5.setActive(true);
		var b5 = new BitSet();
		b5.set(characteristicValueIndex.get("AA_01"));
		b5.set(characteristicValueIndex.get("BB_02"));
		b5.set(characteristicValueIndex.get("CC_01"));
		b5.set(characteristicValueIndex.get("XX_01"));
		row5.setBitset(b5);
		table3.add(row5);

		/*
		 * AA | BB | CC | XX
		 * ------------
		 * 01 | 02 | 01   01
		 */

		allTables.add(table1);
		allTables.add(table2);
		allTables.add(table3);



		for (List<Row> incomingTable : allTables) {
			if (pseudoTable.isEmpty()) {
				System.out.println("Initializing pseudoTable with the first table.");
				pseudoTable.addAll(incomingTable);
				continue;
			}

			boolean hasDisabledLine;
			do {
				hasDisabledLine = false;
				Set<Row> rowsToKeep = new HashSet<>();

				for (Row incomingTableRow : incomingTable) {
					System.out.println("Incoming: " + incomingTableRow.getBitset());
					for (Row pseudoTableRow : pseudoTable) {
						if (!pseudoTableRow.isActive()) {
							System.out.println("Skipped (disabled): " + pseudoTableRow.getBitset());
							continue;
						}

						BitSet intersection = (BitSet) pseudoTableRow.getBitset().clone();
						intersection.and(incomingTableRow.getBitset());

						if (!intersection.isEmpty()) {
							System.out.println("Valid intersection found: " + intersection);
							// Fusionner le BitSet de pseudoTableRow avec celui de incomingTableRow
							BitSet mergedBitSet = (BitSet) pseudoTableRow.getBitset().clone();
							mergedBitSet.or(incomingTableRow.getBitset());  // Union des deux BitSets
							pseudoTableRow.setBitset(mergedBitSet);  // Mettre à jour le BitSet de pseudoTableRow
							rowsToKeep.add(pseudoTableRow);
							System.out.println("Merged BitSet: " + mergedBitSet);

						} else {
							System.out.println("No valid intersection for: " + pseudoTableRow.getBitset());
						}
					}
				}

				// Disable rows that are not in rowsToKeep
				for (Row pseudoTableRow : pseudoTable) {
					if (pseudoTableRow.isActive() && !rowsToKeep.contains(pseudoTableRow)) {
						pseudoTableRow.setActive(false);
						System.out.println("Disabling row: " + pseudoTableRow.getBitset());
						hasDisabledLine = true;
					}
				}
			} while (hasDisabledLine);
		}



		System.out.println(" -- Active Rows in Pseudo Table -- ");
		for (Row row : pseudoTable) {
			if (row.isActive()) {  // Vérifier si la ligne est active
				System.out.println("Active Row: " + row.getBitset());
			}
		}





	}
}
