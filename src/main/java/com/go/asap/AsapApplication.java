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

	/*
	public static String getFirstSegment(String str, char delimiter) {
		int index = str.indexOf(delimiter);
		if (index == -1) {
			return str; // Return the whole string if the delimiter is not found
		}
		return str.substring(0, index);
	}

	public static String getSecondSegment(String str, char delimiter) {
		int index = str.indexOf(delimiter);
		if (index == -1) {
			return ""; // Return an empty string if the delimiter is not found
		}
		return str.substring(index + 1);
	}


	public static void updateMemoizationValues(
			TreeMap<String, List<BitSet>> memoizationPossiblesValues,
			String key,
			List<BitSet> newBitsets) {

		memoizationPossiblesValues.merge(key, new ArrayList<>(newBitsets), (existingList, newList) -> {
			existingList.addAll(newList);
			return existingList;
		});
	}

	public static void updateMemoizationValuesForAll(
			TreeMap<String, List<BitSet>> memoizationPossiblesValues,
			Set<String> keys,
			Map<String, PseudoTable> aggregationPseudoTables,
			String tableName) {

		keys.forEach(k -> {
			List<BitSet> bitsetList = aggregationPseudoTables.get(tableName).getValue().get(k).getBitsetList();
			updateMemoizationValues(memoizationPossiblesValues, k, bitsetList);
		});
	}

	public static TreeMap<String, List<BitSet>> filterTreeMapBySignature(Map<String, List<BitSet>> originalMap, Set<String> signature) {
		TreeMap<String, List<BitSet>> filteredMap = new TreeMap<>();
		for (Map.Entry<String, List<BitSet>> entry : originalMap.entrySet()) {
			String key = entry.getKey();
			String firstSegment = key.split("_")[0]; // Extraire le premier segment de la clé
			if (signature.contains(firstSegment)) { // Vérifier si le premier segment est dans la signature
				filteredMap.put(key, entry.getValue());
			}
		}
		return filteredMap;
	}

	public static Set<BitSet> invalidateCombinations(Map<String, List<BitSet>> originalMap, Set<String> signature) {
		Set<BitSet> invalidBitSets = new HashSet<>();
		for (Map.Entry<String, List<BitSet>> entry : originalMap.entrySet()) {
			String key = entry.getKey();
			if (!signature.contains(getFirstSegment(key, '_'))) {
				System.out.println("Exclude in possible values but not part of signature, value : " + key + " with bitsets : " + entry.getValue());
				invalidBitSets.addAll(entry.getValue());
			}
		}
		return invalidBitSets;
	}


	public static Set<String> generateCombinationsForSignature(TreeMap<String, List<BitSet>> filteredValues, Set<BitSet> invalidBitSets, List<String> signature) {
		Set<String> combinations = new HashSet<>();
		List<String> families = new ArrayList<>(signature);
		List<List<String>> groupedValues = new ArrayList<>();

		// Grouper les valeurs par famille
		for (String family : families) {
			List<String> familyValues = new ArrayList<>();
			for (String key : filteredValues.keySet()) {
				if (key.startsWith(family)) {
					familyValues.add(key);
				}
			}
			groupedValues.add(familyValues);
		}

		// Générer les combinaisons
		generateCombinationsLoop(groupedValues, filteredValues, invalidBitSets, combinations);
		return combinations;
	}

	private static void generateCombinationsLoop(List<List<String>> groupedValues, TreeMap<String, List<BitSet>> filteredValues, Set<BitSet> invalidBitSets, Set<String> combinations) {
		int[] indices = new int[groupedValues.size()];

		while (true) {
			List<String> currentCombination = new ArrayList<>();
			BitSet currentBitSet = new BitSet();
			boolean isValid = true;

			for (int i = 0; i < groupedValues.size(); i++) {
				String key = groupedValues.get(i).get(indices[i]);
				BitSet bitSet = filteredValues.get(key).get(0); // Assuming each key has at least one BitSet

				if (currentCombination.isEmpty()) {
					currentBitSet.or(bitSet);
				} else {
					BitSet newBitSet = (BitSet) currentBitSet.clone();
					newBitSet.and(bitSet);
					if (newBitSet.isEmpty() || invalidBitSets.contains(newBitSet)) {
						isValid = false;
						break;
					}
					currentBitSet = newBitSet;
				}
				currentCombination.add(key);
			}

			if (isValid) {
				combinations.add(String.join(" ", currentCombination));
			}

			// Increment indices
			int k = groupedValues.size() - 1;
			while (k >= 0 && indices[k] == groupedValues.get(k).size() - 1) {
				indices[k] = 0;
				k--;
			}

			if (k < 0) {
				break;
			}

			indices[k]++;
		}
	}
	*/

	public static void main(String[] args) {
		SpringApplication.run(AsapApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {


		// Exemple de données fournies
		String[][] table1 = {
				{"ZI", "0F", "0Q"},
				{"ZI", "0F", "0J"},
				{"JH", "0W", "0P"},
				{"ZI", "0F", "0U"},
				{"JG", "0W", "0T"},
				{"JG", "0W", "0P"},
				{"JG", "0W", "0H"},
				{"L6", "0K", "0H"},
				{"M6", "01", "0P"},
				{"JU", "0J", "0H"},
				{"JH", "0W", "0T"},
				{"L8", "01", "0P"},
				{"ES", "0F", "0U"},
				{"LY", "0M", "0T"},
				{"LY", "0M", "0P"},
				{"L6", "0K", "0P"},
				{"PR", "05", "0H"}
		};

		String[][] table2 = {
				{"P6", "0K", "0H"},
				{"P6", "0K", "0P"},
				{"ES", "0F", "0J"},
				{"ES", "0F", "0Q"},
				{"MT", "01", "0P"},
				{"L6", "0K", "0T"},
				{"P6", "0K", "0T"},
				{"MT", "01", "0T"},
				{"JG", "0W", "0L"},
				{"NP", "01", "0T"},
				{"NP", "01", "0R"},
				{"MZ", "0J", "0L"},
				{"M5", "01", "0T"},
				{"M6", "01", "0T"},
				{"L8", "01", "0T"},
				{"L7", "01", "0T"},
				{"L7", "01", "0P"},
				{"JU", "0J", "0L"},
				{"ZI", "0F", "0K"},
				{"ZI", "0F", "0M"},
				{"L8", "01", "0L"},
				{"L6", "0K", "0L"},
				{"L6", "0K", "0R"},
				{"M5", "01", "0R"},
				{"M6", "01", "0R"},
				{"JU", "0J", "0R"},
				{"L8", "01", "0R"},
				{"P6", "0K", "0L"},
				{"P6", "0K", "0R"},
				{"L7", "01", "0L"},
				{"L7", "01", "0R"},
				{"Q5", "05", "0L"},
				{"Q5", "05", "0R"},
				{"LY", "0M", "0R"},
				{"LY", "0M", "0L"},
				{"MT", "01", "0L"},
				{"MT", "01", "0R"},
				{"MU", "06", "0R"},
				{"PR", "05", "0L"},
				{"PR", "05", "0R"},
				{"MN", "0K", "0L"},
				{"MU", "06", "0L"},
				{"MK", "01", "0R"},
				{"MK", "01", "0L"},
				{"ES", "0F", "0K"},
				{"M5", "01", "0P"},
				{"JU", "0J", "0P"},
				{"JU", "0J", "0B"},
				{"JU", "0J", "0A"},
				{"JU", "0J", "0F"},
				{"L8", "01", "0H"},
				{"P6", "0K", "0F"},
				{"L8", "01", "0A"},
				{"P6", "0K", "0B"},
				{"P6", "0K", "0A"},
				{"L7", "01", "0H"},
				{"Q5", "05", "0F"},
				{"ES", "0F", "0M"},
				{"PR", "05", "0F"}
		};

		// Construire les pseudo-tables
		Map<String, List<BitSet>> pseudoTable = new HashMap<>();
		constructPseudoTable(pseudoTable, table1, 1);
		constructPseudoTable(pseudoTable, table2, 2);

		// Définir la signature cible
		List<String> signature = Arrays.asList("B0F", "B0G");

		// Filtrer les valeurs par famille selon la signature
		TreeMap<String, List<BitSet>> filteredValues = filterTreeMapBySignature(pseudoTable, new HashSet<>(signature));

		// Invalider les combinaisons en cascade
		Set<BitSet> invalidBitSets = invalidateCombinations(pseudoTable, new HashSet<>(signature));

		// Générer les combinaisons valides
		Set<String> combinations = generateCombinationsForSignature(filteredValues, invalidBitSets, signature);

		// Afficher les combinaisons uniques
		System.out.println("Nombre de combinaisons : " + combinations.size());
		for (String combination : combinations) {
			System.out.println(combination);
		}
	}

	public static void constructPseudoTable(Map<String, List<BitSet>> pseudoTable, String[][] table, int tableIndex) {
		for (int i = 0; i < table.length; i++) {
			String key = table[i][0] + "_" + table[i][1];
			BitSet bitSet = new BitSet();
			bitSet.set(tableIndex);
			pseudoTable.computeIfAbsent(key, k -> new ArrayList<>()).add(bitSet);
		}
	}

	public static TreeMap<String, List<BitSet>> filterTreeMapBySignature(Map<String, List<BitSet>> originalMap, Set<String> signature) {
		TreeMap<String, List<BitSet>> filteredMap = new TreeMap<>();
		for (Map.Entry<String, List<BitSet>> entry : originalMap.entrySet()) {
			String key = entry.getKey();
			String firstSegment = key.split("_")[0]; // Extraire le premier segment de la clé
			if (signature.contains(firstSegment)) { // Vérifier si le premier segment est dans la signature
				filteredMap.put(key, entry.getValue());
			}
		}
		return filteredMap;
	}

	public static Set<BitSet> invalidateCombinations(Map<String, List<BitSet>> originalMap, Set<String> signature) {
		Set<BitSet> invalidBitSets = new HashSet<>();
		for (Map.Entry<String, List<BitSet>> entry : originalMap.entrySet()) {
			String key = entry.getKey();
			String firstSegment = key.split("_")[0];
			if (!signature.contains(firstSegment)) {
				invalidBitSets.addAll(entry.getValue());
			}
		}
		return invalidBitSets;
	}

	public static Set<String> generateCombinationsForSignature(TreeMap<String, List<BitSet>> filteredValues, Set<BitSet> invalidBitSets, List<String> signature) {
		Set<String> combinations = new HashSet<>();
		List<String> families = new ArrayList<>(signature);
		List<List<String>> groupedValues = new ArrayList<>();

		// Grouper les valeurs par famille
		for (String family : families) {
			List<String> familyValues = new ArrayList<>();
			for (String key : filteredValues.keySet()) {
				if (key.startsWith(family)) {
					familyValues.add(key);
				}
			}
			groupedValues.add(familyValues);
		}

		// Générer les combinaisons
		generateCombinationsLoop(groupedValues, filteredValues, invalidBitSets, combinations);
		return combinations;
	}

	private static void generateCombinationsLoop(List<List<String>> groupedValues, TreeMap<String, List<BitSet>> filteredValues, Set<BitSet> invalidBitSets, Set<String> combinations) {
		int[] indices = new int[groupedValues.size()];

		while (true) {
			List<String> currentCombination = new ArrayList<>();
			BitSet currentBitSet = new BitSet();
			boolean isValid = true;

			for (int i = 0; i < groupedValues.size(); i++) {
				String key = groupedValues.get(i).get(indices[i]);
				BitSet bitSet = filteredValues.get(key).get(0); // Assuming each key has at least one BitSet

				if (i == 0) {
					currentBitSet.or(bitSet);
				} else {
					BitSet newBitSet = (BitSet) currentBitSet.clone();
					newBitSet.and(bitSet);
					if (newBitSet.isEmpty() || invalidBitSets.contains(newBitSet)) {
						isValid = false;
						break;
					}
					currentBitSet = newBitSet;
				}
				currentCombination.add(key);
			}

			if (isValid) {
				combinations.add(String.join(" ", currentCombination));
			}

			// Increment indices
			int k = groupedValues.size() - 1;
			while (k >= 0 && indices[k] == groupedValues.get(k).size() - 1) {
				indices[k] = 0;
				k--;
			}

			if (k < 0) {
				break;
			}

			indices[k]++;
		}


		/*
		String signature = "F1 F2";

		// Index building
		TreeMap<String, Integer> valueToIndex = new TreeMap<>();
		valueToIndex.put("F1_A1", 1);
		valueToIndex.put("F1_A2", 2);
		valueToIndex.put("F2_B1", 3);
		valueToIndex.put("F2_B2", 4);
		valueToIndex.put("F3_C1", 5);

		TreeMap<Integer, String> indexToValue = new TreeMap<>();
		indexToValue.put(1, "F1_A1");
		indexToValue.put(2, "F1_A2");
		indexToValue.put(3, "F2_B1");
		indexToValue.put(4, "F2_B2");
		indexToValue.put(5, "F3_C1");


		// Row bitset mapping
		var r1 = new BitSet();
		r1.set(valueToIndex.get("F1_A1"));
		r1.set(valueToIndex.get("F2_B1"));

		var r2 = new BitSet();
		r2.set(valueToIndex.get("F1_A2"));
		r2.set(valueToIndex.get("F2_B1"));

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

		List<String> rawTableParsed = new ArrayList<>();
		TreeMap<String, Row> deletedLine = new TreeMap<>();
		for ( String tableName : tableNames ) {
			if ( aggregationPseudoTables.isEmpty() ) {
				LOG.info("Initializing pseudoTable with the first table {} ...", tableName);

				PseudoTable pseudoTable = new PseudoTable(tableName);
				TreeMap<String, Row> values = new TreeMap<>();
				aggregationRawTables.get(tableName).getValue().forEach((k, v) -> {
					Row row = new Row(v.isValid(), v.getBitsetList().stream().map(b -> (BitSet) b.clone()).collect(Collectors.toList()));
					values.put(k, row);
				});
				pseudoTable.setValue(values);
				aggregationPseudoTables.put(tableName, pseudoTable);

				// Update possibles values
				updateMemoizationValuesForAll(memoizationPossiblesValues, values.keySet(), aggregationPseudoTables, tableName);

				rawTableParsed.add(tableName);

			} else {

				// New table ( raw ) and compare with existing aggregation pseud table to see if we need to validate line or not

				LOG.info("Current aggregation pseudo table : {} ", aggregationPseudoTables);


				LOG.info("Verse table : {} dans pseudo tables ...", tableName);

				Table newTableToVerse = aggregationRawTables.get(tableName);

				newTableToVerse.getValue().forEach((value, row) -> {
					System.out.println("----> incoming value : " + value + " - bitset " + row.getBitsetList());

					for ( String parsedTable: rawTableParsed ) {
						System.out.println("-----> Verify if value existing in " + parsedTable);

						PseudoTable existingPseudoTable = aggregationPseudoTables.get(parsedTable);
						if ( existingPseudoTable.getValue().containsKey(value) ) {
							// OK => can add safely to possible values and validate the line
							System.out.println("OK for already exists " + value );
							row.setIsValid(Boolean.TRUE);
							updateMemoizationValues(memoizationPossiblesValues, value, row.getBitsetList());
						} else {

							// Not found, check if family exist from the value
							boolean characteristicExists = Boolean.FALSE;
							String characteristic = getFirstSegment(value, '_');
							for (String key : existingPseudoTable.getValue().keySet()) {
								if (getFirstSegment(key, '_').equals(characteristic)) {
									characteristicExists = Boolean.TRUE;
									break;
								}
							}

							if (!characteristicExists) {
								// OK => can add safely to possible values and validate the line
								System.out.println("OK, not exists (new) " + value);
								row.setIsValid(Boolean.TRUE);
								updateMemoizationValues(memoizationPossiblesValues, value, row.getBitsetList());
							} else {
								// NOK => tag the line as invalid, does not add to possible values
								System.out.println("NOK, family " + characteristic + " exist, but not the value " + getSecondSegment(value, '_'));
								row.setIsValid(Boolean.FALSE);
								deletedLine.put(value, row);
							}

						}

					}
					System.out.println( " ------------------------------------------------" );
				});


				// Add pseudo table to the aggregation ( contains validation on each line )
				PseudoTable tableToVerse = new PseudoTable(tableName);
				TreeMap<String, Row> values = new TreeMap<>();
				newTableToVerse.getValue().forEach((k, v) -> {
					Row row = new Row(v.isValid(), v.getBitsetList().stream().map(b -> (BitSet) b.clone()).collect(Collectors.toList()));
					values.put(k, row);
				});
				tableToVerse.setValue(values);
				aggregationPseudoTables.put(tableName, tableToVerse);


				rawTableParsed.add(tableName);

				// go next table to verse
			}

		}

		// fin boucle sur les tables
		LOG.info("Possibles values : {}",memoizationPossiblesValues);
		LOG.info("Generate combinations for signature : {}",signature);

		// In possibles values defined by pseudo tables pr processing, keep only the characteristic for the signature provided
		TreeMap<String, List<BitSet>> memoizationPossiblesValuesFiltered = filterTreeMapBySignature(memoizationPossiblesValues, new HashSet<>(Arrays.asList(signature.split(" "))));
		LOG.info("Possibles values after filtering by signature : {}", memoizationPossiblesValuesFiltered);

		var invalidBitSets = invalidateCombinations(memoizationPossiblesValues, new HashSet<>(Arrays.asList(signature.split(" "))));

		// Generate valid combinations
		Set<String> combinations = generateCombinationsForSignature(memoizationPossiblesValuesFiltered, invalidBitSets, Arrays.asList(signature.split(" ")));

		for (String combination : combinations) {
			System.out.println(combination);
		}


		*/

	}
}
