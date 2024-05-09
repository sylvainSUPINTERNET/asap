package com.go.asap;

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

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
		var b4 = new BitSet();
		b4.set(characteristicValueIndex.get("AA_02"));
		b4.set(characteristicValueIndex.get("BB_03"));
		b4.set(characteristicValueIndex.get("CC_02"));
		row4.setBitset(b4);
		table2.add(row4);

		allTables.add(table1);
		allTables.add(table2);


		for ( List<Row> incomingTable : allTables ) {
			if ( pseudoTable.isEmpty() ) {
				System.out.println("Init");
				pseudoTable.addAll(incomingTable);
			} else {

				System.out.println("Merge check coherance");
				for ( Row rowsIncomingTable : incomingTable ) {
					System.out.println("________________________________________");
					System.out.println("Compare incoming row : " + rowsIncomingTable.getBitset());

					for ( Row rowsPseudoTable : pseudoTable ) {
						var intersection = (BitSet) rowsPseudoTable.getBitset().clone();
						intersection.and(rowsIncomingTable.getBitset());

						System.out.println(" VS existing : " + rowsPseudoTable.getBitset() + " => " + intersection);
						if ( intersection.isEmpty() ) {
							System.out.println("No intersection for row : " + rowsPseudoTable.getBitset() + " and " + rowsIncomingTable.getBitset());
							System.out.println("Remove row : " + rowsPseudoTable.getBitset());
						}

					}
				}
			}
		}


		System.out.println(pseudoTable);






	}
}
