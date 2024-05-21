package com.go.asap;

import com.go.asap.m.*;
import com.go.asap.m.Loader;
import com.go.asap.m.Row;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@SpringBootApplication
public class AsapApplication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(AsapApplication.class);

	private static Map<String, String[]> convertToMap(String possibleValuesString) {
		Map<String, String[]> possibleValuesMap = new HashMap<>();
		String[] lines = possibleValuesString.split("\n");
		for (String line : lines) {
			String[] parts = line.split("\t");
			String key = parts[0];
			String[] values = parts[1].trim().split(" ");
			possibleValuesMap.put(key, values);
		}
		return possibleValuesMap;
	}


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
				//System.out.println("Exclude in possible values but not part of signature, value : " + key + " with bitsets : " + entry.getValue());
				invalidBitSets.addAll(entry.getValue());
			}
		}
		return invalidBitSets;
	}


	private static BitSet createBitSet(int... indices) {
		BitSet bitSet = new BitSet();
		for (int index : indices) {
			bitSet.set(index);
		}
		return bitSet;
	}


	public static BitSet combineBitSets(List<BitSet> bitSetList, Set<BitSet> invalidBitSetList) {
		BitSet combinedBitSet = new BitSet();

		for (BitSet bitSet : bitSetList) {
			combinedBitSet.or(bitSet);
		}

		return combinedBitSet;
	}


	public static int traverseGroupsV2(Map<String, Map<String, BitSet>> groups, String signature, Set<BitSet> invalidBitSets, TreeMap<String, Integer> valueToIndex) {
		String[] groupOrder = signature.split(" ");
		int combinationCount = 0;

		// Commencez par le premier groupe
		Map<String, BitSet> firstGroup = groups.get(groupOrder[0]);
		for (Map.Entry<String, BitSet> entry1 : firstGroup.entrySet()) {
			String key1 = entry1.getKey();
			BitSet bitSet1 = entry1.getValue();
			if (!isValid(bitSet1, invalidBitSets)) {
				continue;
			}
			// Liste pour accumuler les clés de la combinaison
			List<String> combination = new ArrayList<>();
			combination.add(key1);
			// Appelez la fonction récursive pour traiter les groupes suivants
			combinationCount += traverseNextGroups(groups, groupOrder, 1, key1, bitSet1, invalidBitSets, valueToIndex, combination);
		}

		return combinationCount;
	}

	private static int traverseNextGroups(Map<String, Map<String, BitSet>> groups, String[] groupOrder, int currentGroupIndex, String previousKey, BitSet previousBitSet, Set<BitSet> invalidBitSets, TreeMap<String, Integer> valueToIndex, List<String> combination) {
		// Si nous avons atteint la fin de la chaîne de groupes, affichez la combinaison et retournez 1 pour indiquer une combinaison valide
		if (currentGroupIndex >= groupOrder.length) {
			//if ( combination.stream().filter( c -> c.contains("B0C_P2") ).count() == 1) {
				//String display = String.join(" -> ", combination);
				//System.out.println(display);
			//}

			//String display = String.join(" -> ", combination);
			String display = String.join(" ", combination);
			display = display.replace("_", "");
			System.out.println(display);

			return 1;
		}

		Map<String, BitSet> currentGroup = groups.get(groupOrder[currentGroupIndex]);
		int combinationCount = 0;
		int previousIndex = valueToIndex.get(previousKey);

		for (Map.Entry<String, BitSet> entry : currentGroup.entrySet()) {
			String key = entry.getKey();
			BitSet currentBitSet = entry.getValue();

			if (!isValid(currentBitSet, invalidBitSets)) {
				continue;
			}

			// Vérifiez si l'index précédent est présent dans le bitset actuel
			if (currentBitSet.get(previousIndex)) {
				// Ajoutez la clé actuelle à la combinaison
				combination.add(key);
				// Appelez récursivement la fonction pour le prochain groupe
				combinationCount += traverseNextGroups(groups, groupOrder, currentGroupIndex + 1, key, currentBitSet, invalidBitSets, valueToIndex, combination);
				// Retirez la clé actuelle après le retour de la récursion pour essayer la prochaine clé
				combination.remove(combination.size() - 1);
			}
		}

		return combinationCount;
	}


	private static boolean isValid(BitSet bitSet, Set<BitSet> invalidBitSets) {
		for (BitSet invalidBitSet : invalidBitSets) {
			if (bitSet.equals(invalidBitSet)) {
				return false;
			}
		}
		return true;
	}



	// TODO to remov logic here
	// Here very basic exemple for 2 groups ( 2 characteristics )
	public static int traverseGroups(Map<String, Map<String, BitSet>> groups, String signature, Set<BitSet> invalidBitSets, TreeMap<String, Integer> valueToIndex) {
		String[] groupOrder = signature.split(" ");
		int combinationCount = 0;

		Map<String, BitSet> firstGroup = groups.get(groupOrder[0]);
		Map<String, BitSet> secondGroup = groups.get(groupOrder[1]);

		for (Map.Entry<String, BitSet> entry1 : firstGroup.entrySet()) {
			String key1 = entry1.getKey();
			BitSet bitSet1 = entry1.getValue();
			if (!isValid(bitSet1, invalidBitSets)) {
				continue;
			}

			for (Map.Entry<String, BitSet> entry2 : secondGroup.entrySet()) {
				String key2 = entry2.getKey();
				BitSet bitSet2 = entry2.getValue();


				// key1 index
				int idx = valueToIndex.get(key1);

				// Si je retrouve bien l'index dans le bitset du key2
				if ( !bitSet2.get(idx) ) {
					continue;
				} else {
					// TODO: so here obviously, we have to repeat the same thing :
					// TODO : wego on 3 groups, check if we have key1 index value (BOH_01) from group 1, and index value ( BOA_00)  from group 2 in key3 bitset for new index ( B0Z_XX )
					// TODO : So yes, we can do it recursively !
					//System.out.println(key1 + " -> " + key2);
					System.out.println(key1.replace("_","") + " " + key2.replace("_",""));
					combinationCount++;

				}


			}
		}

		return combinationCount;
	}




	/*
	public static void generateCombinationsLoop(List<List<String>> groupedValues, TreeMap<String, List<BitSet>> filteredValues, Set<BitSet> invalidBitSets, Set<String> combinations) {

		groupedValues.forEach( group -> {
			System.out.println("Group : " + group);

			group.forEach( value -> {

			});


		});

		int[] indices = new int[groupedValues.size()];

		while (true) {
			List<String> currentCombination = new ArrayList<>();
			List<BitSet> activeBitSets = new ArrayList<>();
			boolean isValid = true;

			for (int i = 0; i < groupedValues.size(); i++) {
				String key = groupedValues.get(i).get(indices[i]);
				List<BitSet> bitSetsForKey = filteredValues.get(key);
				BitSet combinedBitSet = new BitSet();

				for (BitSet bitSet : bitSetsForKey) {
					if (!invalidBitSets.contains(bitSet)) {
						combinedBitSet.or(bitSet);
					}
				}

				if (i == 0) {
					for (BitSet bitSet : bitSetsForKey) {
						if (!invalidBitSets.contains(bitSet)) {
							activeBitSets.add((BitSet) bitSet.clone());
						}
					}
				} else {
					List<BitSet> newActiveBitSets = new ArrayList<>();
					for (BitSet activeBitSet : activeBitSets) {
						BitSet intersectedBitSet = (BitSet) activeBitSet.clone();
						intersectedBitSet.and(combinedBitSet);
						if (!intersectedBitSet.isEmpty() && !invalidBitSets.contains(intersectedBitSet)) {
							newActiveBitSets.add(intersectedBitSet);
						}
					}
					activeBitSets = newActiveBitSets;
				}

				if (activeBitSets.isEmpty()) {
					isValid = false;
					break;
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


		// QUICK MOCK
		// Index building
		/*
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
		 */


		// Row bitset mapping

		/*
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
		 */


		// Raw tables aggregation
		/*
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
		*/


	/*
		TreeMap<String, Integer> indexMap = new TreeMap<>();
		indexMap.put("B0G_01", 1);
		indexMap.put("B0H_DB", 2);
		indexMap.put("DGM_04", 3);
		indexMap.put("DGM_05", 4);


		TreeMap<String, List<BitSet>> memoizationPossiblesValues = new TreeMap<>();

		List<BitSet> BOG_01_BITSET_LIST = new ArrayList<>();
		BitSet b = new BitSet();
		b.set(indexMap.get("B0G_01"));
		b.set(indexMap.get("B0H_DB"));
		b.set(indexMap.get("DGM_04"));
		b.set(100);
		BOG_01_BITSET_LIST.add(b);
		memoizationPossiblesValues.put("B0G_01", BOG_01_BITSET_LIST);

		List<BitSet> BOH_DB_BITSET_LIST = new ArrayList<>();
		BitSet b2 = new BitSet();
		b2.set(indexMap.get("B0G_01"));
		b2.set(indexMap.get("B0H_DB"));
		b2.set(indexMap.get("DGM_04"));
		BOH_DB_BITSET_LIST.add(b2);
		memoizationPossiblesValues.put("B0H_DB", BOH_DB_BITSET_LIST);

		List<BitSet> DGM_04_BITSET_LIST = new ArrayList<>();
		BitSet b3 = new BitSet();
		b3.set(indexMap.get("B0G_01"));
		b3.set(indexMap.get("B0H_DB"));
		b3.set(indexMap.get("DGM_04"));
		DGM_04_BITSET_LIST.add(b3);
		memoizationPossiblesValues.put("DGM_04", DGM_04_BITSET_LIST);

		var signature = "B0G B0H DGM";
		var possiblesValues = List.of("B0G_01", "B0H_DB", "DGM_04", "DGM_05");


		HashMap<String, List<List<BitSet>>> groups = new HashMap<>();
		for (String possibleValue : possiblesValues) {
			if ( groups.containsKey(getFirstSegment(possibleValue, '_')) ) {
				groups.get(getFirstSegment(possibleValue, '_')).add(memoizationPossiblesValues.get(possibleValue));
			} else {
				groups.put(getFirstSegment(possibleValue, '_'), new ArrayList<>(List.of(memoizationPossiblesValues.get(possibleValue))));
			}
		}

		System.out.println(groups);

		*/

		String signature = "B0C B0F B0G B0H DAQ"; //"B0E B0F DFH REG"; //"B0E B0H B0J";

		String tables = "P22"; // "m"
		String folder = "P22"; // "m"

		ObjectArrayList<String> filePrdList = new ObjectArrayList<>();
		if ( tables.equalsIgnoreCase("ZZK9") || tables.equalsIgnoreCase("P22") || tables.equalsIgnoreCase("test") || tables.equalsIgnoreCase("test2") || tables.equalsIgnoreCase("tmp") || tables.equalsIgnoreCase("m") || tables.equalsIgnoreCase("spe")  || tables.equalsIgnoreCase("m2")) {
			if ( tables.equalsIgnoreCase("ZZK9") )
				filePrdList.addAll(Arrays.stream(ZZK9Table.TABLES.split("\n")).toList());
			else if ( tables.equalsIgnoreCase("P22") ) {
				filePrdList.addAll(Arrays.stream(P22Table.TABLES.split("\n")).toList());
			} else if ( tables.equalsIgnoreCase("spe") )  {
				filePrdList.addAll(Arrays.stream(TestTable.TEST_TABLES_SPE.split("\n")).toList());
			} else {
				filePrdList.addAll(Arrays.stream(TestTable.TEST_TABLES.split("\n")).toList());
			}
		} else {
			throw new IllegalArgumentException("Table not supported, only support P22 ZZK9");
		}


		AtomicInteger globalIdx = new AtomicInteger(1);
		HashSet<String> characteristic_value_for_tables = new HashSet<>();
		TreeMap<String, Integer> valueToIndex = new TreeMap<>();
		TreeMap<Integer, String> indexToValue = new TreeMap<>();

		for (String file : filePrdList) {
			Loader.readTableAndBuildIndexMap(folder, file, characteristic_value_for_tables, valueToIndex, indexToValue, globalIdx);
		}

		// Raw tables aggregation
		TreeMap<String, Table> aggregationRawTables = new TreeMap<>();
		TreeSet<String> tableNames = new TreeSet<>();
		for (String file: filePrdList ) {
			Table table = new Table(file);
			TreeMap<String, Row> valuesTable = new TreeMap<>();
			Loader.readTableAndBuildTableAggregation(folder, file, valueToIndex, indexToValue, file, table, valuesTable);
			tableNames.add(file);
			aggregationRawTables.put(file, table);
		}

		LOG.info("Build index map : {}", valueToIndex);

		// Build pseudo tables
		TreeMap<String, PseudoTable> aggregationPseudoTables = new TreeMap<>();
		TreeMap<String, List<BitSet>> memoizationPossiblesValues = new TreeMap<>();

		List<String> rawTableParsed = new ArrayList<>();
		TreeMap<String, Row> invalidValues = new TreeMap<>();
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
					if ( value.equalsIgnoreCase("B0C_4M")) {
						System.out.println("PROBLEM");
					}
					System.out.println("----> incoming value : "+value);
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
									invalidValues.put(value, row);
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
		LOG.info("Possibles values : ");
		// Map temporaire pour regrouper les valeurs par préfixe
		TreeMap<String, List<String>> groupedValues = new TreeMap<>();

		// Parcourir chaque entrée du TreeMap
		for (Map.Entry<String, List<BitSet>> entry : memoizationPossiblesValues.entrySet()) {
			String key = entry.getKey();
			String prefix = key.split("_")[0];
			String suffix = key.split("_")[1];

			// Ajouter le suffixe à la liste correspondante dans le groupedValues
			groupedValues.computeIfAbsent(prefix, k -> new ArrayList<>()).add(suffix);
		}

		// Afficher les résultats formatés
		for (Map.Entry<String, List<String>> entry : groupedValues.entrySet()) {
			System.out.print(entry.getKey() + ": ");
			for (String value : entry.getValue()) {
				System.out.print(value + " ");
			}
			System.out.println();
		}


		LOG.info("Generate combinations for signature : {}",signature);

		// In possibles values defined by pseudo tables pr processing, keep only the characteristic for the signature provided
		TreeMap<String, List<BitSet>> memoizationPossiblesValuesFiltered = filterTreeMapBySignature(memoizationPossiblesValues, new HashSet<>(Arrays.asList(signature.split(" "))));
		//LOG.info("Possibles values after filtering by signature : {}", memoizationPossiblesValuesFiltered);

		LOG.info("Possibles values after filtering by signature :");
		TreeMap<String, List<String>> x = new TreeMap<>();

		// Parcourir chaque entrée du TreeMap
		for (Map.Entry<String, List<BitSet>> entry : memoizationPossiblesValuesFiltered.entrySet()) {
			String key = entry.getKey();
			String prefix = key.split("_")[0];
			String suffix = key.split("_")[1];

			// Ajouter le suffixe à la liste correspondante dans le groupedValues
			x.computeIfAbsent(prefix, k -> new ArrayList<>()).add(suffix);
		}

		// Afficher les résultats formatés
		for (Map.Entry<String, List<String>> entry : x.entrySet()) {
			System.out.print(entry.getKey() + ": ");
			for (String value : entry.getValue()) {
				System.out.print(value + " ");
			}
			System.out.println();
		}



		// TODO ? ça supprime toute la ligne je sais pas si c'est ok ça pour moi il faut garder l'index plutot, pas le bitset je pense
		//var invalidBitSets = invalidateCombinations(memoizationPossiblesValues, new HashSet<>(Arrays.asList(signature.split(" "))));

		// Generate valid combinations
		//Set<String> combinations = generateCombinationsForSignature(memoizationPossiblesValuesFiltered, invalidBitSets, Arrays.asList(signature.split(" ")));
		/*
		for (String combination : combinations) {
			System.out.println(combination);
		}

		System.out.println(combinations.size() + " combinations for signature " + signature);
		 */


		// Build invalid bitset from index deleted
		Set<BitSet> invalidBitSets = new HashSet<>();
		BitSet ib = new BitSet();
		invalidValues.forEach((key, value) -> {
			System.out.println("Deleted value " + key);
			ib.set(valueToIndex.get(key));
		});
		invalidBitSets.add(ib);


		// Build group for traverse
		// We regroup for each char of signature, value found in table and bitset ( filter keep only valid bitset )
		Map<String, Map<String, BitSet>> groups = new LinkedHashMap<>();
		String[] signatureSplited = signature.split(" ");

		Arrays.asList(signatureSplited).forEach( s -> {
			groups.computeIfAbsent(s, k -> new LinkedHashMap<>());
		});

		memoizationPossiblesValuesFiltered.forEach( (k, bitsetList) -> {
			String firstSegment = getFirstSegment(k, '_');
			if ( groups.containsKey(firstSegment) ) {
				groups.get(firstSegment).put(k, combineBitSets(bitsetList, invalidBitSets));
			}
		});

		long start = System.currentTimeMillis();
		int combinationCount = traverseGroupsV2(groups, signature, invalidBitSets, valueToIndex);
		long end = System.currentTimeMillis();
		System.out.println("Nombre de combinaisons valides: " + combinationCount + " in " + (end - start) + " ms");




	}
}
