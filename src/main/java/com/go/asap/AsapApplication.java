package com.go.asap;

import com.go.asap.m.PseudoTable;
import com.go.asap.m.Row;
import com.go.asap.m.Table;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootApplication
public class AsapApplication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(AsapApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AsapApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {


		// Index building
		TreeMap<String, Integer> valueToIndex = new TreeMap<>();
		valueToIndex.put("F1_A1", 0);
		valueToIndex.put("F1_A2", 1);
		valueToIndex.put("F2_B1", 2);
		valueToIndex.put("F2_B2", 3);
		valueToIndex.put("F3_C1", 4);

		TreeMap<Integer, String> indexToValue = new TreeMap<>();
		indexToValue.put(0, "F1_A1");
		indexToValue.put(1, "F1_A2");
		indexToValue.put(2, "F2_B1");
		indexToValue.put(3, "F2_B2");
		indexToValue.put(4, "F3_C1");


		// Row bitset mapping
		var r1 = new BitSet();
		r1.set(valueToIndex.get("F1_A1"));
		r1.set(valueToIndex.get("F2_B1"));

		var r2 = new BitSet();
		r2.set(valueToIndex.get("F1_A2"));
		r2.set(valueToIndex.get("F2_B2"));

		var r3 = new BitSet();
		r3.set(valueToIndex.get("F2_B1"));
		r3.set(valueToIndex.get("F3_C1"));

		var r4 = new BitSet();
		r4.set(valueToIndex.get("F2_B2"));
		r4.set(valueToIndex.get("F3_C1"));


		// Raw tables aggregation
		TreeMap<String, Table> aggregationRawTables = new TreeMap<>();
		TreeSet<String> tableNames = new TreeSet<>();


        Table table1 = new Table("table_1");
		TreeMap<String, Row> valuesTable1 = new TreeMap<>();
		var r1Clone = (BitSet) r1.clone();
		var r2Clone = (BitSet) r2.clone();
		valuesTable1.put("F1_A1", new Row(Boolean.TRUE, List.of(r1Clone)));
		valuesTable1.put("F1_A2", new Row(Boolean.TRUE, List.of(r2Clone)));
		valuesTable1.put("F2_B1", new Row(Boolean.TRUE, List.of(r1Clone, r2Clone)));
		table1.setValue(valuesTable1);

		Table table2 = new Table("table_2");
		TreeMap<String, Row> valuesTable2 = new TreeMap<>();
		var r3Clone = (BitSet) r3.clone();
		var r4Clone = (BitSet) r4.clone();
		valuesTable2.put("F2_B1", new Row(Boolean.TRUE, List.of(r3Clone)));
		valuesTable2.put("F2_B2", new Row(Boolean.TRUE, List.of(r4Clone)));
		valuesTable2.put("F3_C1", new Row(Boolean.TRUE, List.of(r3Clone, r4Clone)));
		table2.setValue(valuesTable2);

		tableNames.add("table_1");
		tableNames.add("table_2");

		aggregationRawTables.put("table_1", table1);
		aggregationRawTables.put("table_2", table2);



		// Build pseudo tables
		TreeMap<String, PseudoTable> aggregationPseudoTables = new TreeMap<>();
		TreeMap<String, List<BitSet>> memoizationPossiblesValues = new TreeMap<>();

		for ( String tableName : tableNames ) {
			if ( aggregationPseudoTables.isEmpty() ) {
				LOG.info("Initializing pseudoTable with the first table ...");

				PseudoTable pseudoTable = new PseudoTable(tableName);
				TreeMap<String, Row> values = new TreeMap<>();
				aggregationRawTables.get(tableName).getValue().forEach((k, v) -> {
					Row row = new Row(v.isValid(), v.getBitsetList().stream().map(b -> (BitSet) b.clone()).collect(Collectors.toList()));
					values.put(k, row);
				});
				pseudoTable.setValue(values);
				aggregationPseudoTables.put(tableName, pseudoTable);

				// Update possibles values
				memoizationPossiblesValues.putAll(values.keySet().stream()
						.collect(Collectors.toMap(
								k -> k,
								v -> aggregationPseudoTables.get(tableName).getValue().get(v).getBitsetList(),
								(existing, replacement) -> {
									existing.addAll(replacement);
									return existing;
								}
						)));
			} else {
				LOG.info("Verse table : {} dans pseudo tables ...", tableName);


				aggregationRawTables.get(tableName).getValue().entrySet().forEach(entry -> {
					String key = entry.getKey();
					Row value = entry.getValue();

					// TODO => pas bon ici car pour vérifier que la clé est dans aggregationPseudoTables il faut faire un containsKey mais à la racine la clé est le nom de la table sur aggregationPseudoTables ...
					// TODO => le contains doit être fait au niveau du TreeMap ( values ) de la pseudo table

					System.out.println(key);
					System.out.println(value);




					// TODO => possibles value update
					/*
					memoizationPossiblesValues.putAll(values.keySet().stream()
							.collect(Collectors.toMap(
									k -> k,
									v -> aggregationPseudoTables.get(tableName).getValue().get(v).getBitsetList(),
									(existing, replacement) -> {
										existing.addAll(replacement);
										return existing;
									}
							)));
					 */
				});

				// TODO donc là on entame une nouvelle table :
				// Il faut memoizer les valeur possibles
				// Surtout faut regarder si c'est possible
				// pas possible ? retirer la ligne, et en repartir de 0 pour revalider toutes les lignes, et du coup mettre à jour les valeurs possibles

			}
		}





	}
}
