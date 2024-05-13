package com.go.asap;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootApplication
public class AsapApplication implements CommandLineRunner {

	public static HashMap<String, Integer> characteristicValueIndexMap = new HashMap<>();


	public static void main(String[] args) {
		SpringApplication.run(AsapApplication.class, args);
	}


	static class IterationState {
		int index;
		List<String> currentCombination;

		IterationState(int index, List<String> currentCombination) {
			this.index = index;
			this.currentCombination = currentCombination;
		}
	}

	private static Map<String, List<Integer>> preprocessReverseIndexMap(Map<Integer, String> reverseIndexMap) {
		Map<String, List<Integer>> signatureToIndices = new HashMap<>();
		for (Map.Entry<Integer, String> entry : reverseIndexMap.entrySet()) {
			String key = entry.getValue().split("_")[0];  // Extract characteristic part before '_'
			signatureToIndices.computeIfAbsent(key, k -> new ArrayList<>()).add(entry.getKey());
		}
		return signatureToIndices;
	}



	static AbstractMap.SimpleEntry<List<List<String>>, Double> generateCombinations(BitSet combination, Map<Integer, String> reverseIndexMap, List<String> signature) {
		List<List<String>> allCombinations = new ArrayList<>();
		Map<String, List<Integer>> preprocessedMap = preprocessReverseIndexMap(reverseIndexMap);
		long startTime = System.nanoTime();

		// Structure pour stocker l'état de chaque étape
		Stack<IterationState> stack = new Stack<>();
		stack.push(new IterationState(0, new ArrayList<>()));

		while (!stack.isEmpty()) {
			IterationState state = stack.pop();
			int index = state.index;
			List<String> currentCombination = new ArrayList<>(state.currentCombination);

			if (index == signature.size()) {
				allCombinations.add(currentCombination);
			} else {
				String currentSignatureItem = signature.get(index);
				if (preprocessedMap.containsKey(currentSignatureItem)) {
					List<Integer> indices = preprocessedMap.get(currentSignatureItem);
					for (int i = indices.size() - 1; i >= 0; i--) {
						Integer idx = indices.get(i);
						if (combination.get(idx)) {
							List<String> newCombination = new ArrayList<>(currentCombination);
							newCombination.add(reverseIndexMap.get(idx));
							stack.push(new IterationState(index + 1, newCombination));
						}
					}
				}
			}
		}

		long endTimeTotal = System.nanoTime();
		double totalDurationInMilliseconds = (endTimeTotal - startTime) / 1e6;
		//System.out.println("Temps pour générer les combinaisons : " + String.format("%.10f ms", totalDurationInMilliseconds));
		return new AbstractMap.SimpleEntry<>(allCombinations, totalDurationInMilliseconds);

	}

	/*
	private static Map<String, List<Integer>> preprocessReverseIndexMap(Map<Integer, String> reverseIndexMap) {
		Map<String, List<Integer>> signatureToIndices = new HashMap<>();
		for (Map.Entry<Integer, String> entry : reverseIndexMap.entrySet()) {
			String key = entry.getValue().split("_")[0];  // Extract characteristic part before '_'
			signatureToIndices.computeIfAbsent(key, k -> new ArrayList<>()).add(entry.getKey());
		}
		return signatureToIndices;
	}

	static List<List<String>> generateCombinations(BitSet combination, Map<Integer, String> reverseIndexMap, List<String> signature) {
		List<List<String>> allCombinations = new ArrayList<>();
		List<String> currentCombination = new ArrayList<>();


		Map<String, List<Integer>> preprocessedMap = preprocessReverseIndexMap(reverseIndexMap);

		long startTime = System.nanoTime();
		generateCombinationsRecursive(0, currentCombination, allCombinations, combination, preprocessedMap, signature, reverseIndexMap, startTime);
		long endTimeTotal = System.nanoTime();
		double totalDurationInMilliseconds = (endTimeTotal - startTime) / 1e6;
		System.out.println("Temps pour générer les combinaisons : " + String.format("%.10f ms", totalDurationInMilliseconds));

		return allCombinations;
	}

	static void generateCombinationsRecursive(int index, List<String> currentCombination, List<List<String>> allCombinations, BitSet combination, Map<String, List<Integer>> preprocessedMap, List<String> signature, Map<Integer, String> reverseIndexMap,  long startTime) {
		if (index == signature.size()) {
			long endTime = System.nanoTime();
			double durationInMilliseconds = (endTime - startTime) / 1e6;

			System.out.println("Temps pour générer cette combinaison : " + String.format("%.10f ms", durationInMilliseconds));
			allCombinations.add(new ArrayList<>(currentCombination));  // Copy of currentCombination to save the state
			return;
		}

		String currentSignatureItem = signature.get(index);
		if (preprocessedMap.containsKey(currentSignatureItem)) {
			for (Integer idx : preprocessedMap.get(currentSignatureItem)) {
				if (combination.get(idx)) {
					currentCombination.add(reverseIndexMap.get(idx));
					generateCombinationsRecursive(index + 1, currentCombination, allCombinations, combination, preprocessedMap, signature, reverseIndexMap, System.nanoTime());
					currentCombination.remove(currentCombination.size() - 1);  // Backtrack
				}
			}
		}
	}*/

	@Override
	public void run(String... args) throws Exception {


		// TODO
		// For some reason in table there is not B0C for ZZK9 ... so signature with it, will not work
		String singature = "B0H D2A"; //"B0H DGM DN2 DQK DRC DUB DYM";
				//"B0C B0F B0H DDZ DKA DMI DRS REG"; //"B0C B0F";
		String folder = "P22";
		String tables = "P22";
		Loader loadTable = new Loader();



		ObjectArrayList<String> filePrdList = new ObjectArrayList<>();
		if ( tables.equalsIgnoreCase("ZZK9") || tables.equalsIgnoreCase("P22") || tables.equalsIgnoreCase("test") || tables.equalsIgnoreCase("test2") ) {
			if ( tables.equalsIgnoreCase("ZZK9") )
				filePrdList.addAll(Arrays.stream(ZZK9Table.TABLES.split("\n")).toList());
			else if ( tables.equalsIgnoreCase("P22") ) {
				filePrdList.addAll(Arrays.stream(P22Table.TABLES.split("\n")).toList());
			} else {

			}
		} else {
			throw new IllegalArgumentException("Table not supported, only support P22 ZZK9");
		}

		AtomicInteger globalIdx = new AtomicInteger(0);
		Object2ObjectRBTreeMap<String, Integer> characteristicValueIndex = new Object2ObjectRBTreeMap<>();
		Object2ObjectRBTreeMap<Integer, String> characteristicValueIndexReversed = new Object2ObjectRBTreeMap<>();

		List<List<Row>> allTables = new ArrayList<>();

		for (String tablePrdFile : filePrdList) {
			AbstractMap.SimpleEntry<LinkedHashSet<String>, Integer> result = loadTable.readTable(folder, tablePrdFile);
			HashSet<String> newTable = result.getKey();
			int numberOfColumns = result.getValue();

			System.out.println("Load : " + tablePrdFile + " : " + newTable);
			System.out.println("____");

			for (String charac_value : newTable) {
				characteristicValueIndex.computeIfAbsent(charac_value, k -> {
					int index = globalIdx.getAndIncrement();
					characteristicValueIndexReversed.put(index, charac_value);
					return index;
				});
			}
			// Convert each table into list of Row ( each row is a bitset that are refer to the index map build above )
			loadTable.generateVirtualTables(folder, tablePrdFile, characteristicValueIndex, characteristicValueIndexReversed, allTables);
		}
		/*
		Object2ObjectRBTreeMap<String, Integer> characteristicValueIndex = new Object2ObjectRBTreeMap<>();
		characteristicValueIndex.put("AA_01", 0);
		characteristicValueIndex.put("AA_02", 1);
		characteristicValueIndex.put("BB_02", 2);
		characteristicValueIndex.put("BB_03", 3);
		characteristicValueIndex.put("CC_01", 4);
		characteristicValueIndex.put("CC_02", 5);
		characteristicValueIndex.put("XX_01", 6);

		Object2ObjectRBTreeMap<Integer, String> characteristicValueIndexReversed = new Object2ObjectRBTreeMap<>();
		characteristicValueIndexReversed.put(0, "AA_01");
		characteristicValueIndexReversed.put(1, "AA_02");
		characteristicValueIndexReversed.put(2, "BB_02");
		characteristicValueIndexReversed.put(3, "BB_03");
		characteristicValueIndexReversed.put(4, "CC_01");
		characteristicValueIndexReversed.put(5, "CC_02");
		characteristicValueIndexReversed.put(6, "XX_01");



		List<List<Row>> allTables = new ArrayList<>();
		 */

		/*
		 * AA | BB
		 * --------
		 * 01 | 02
		 * 02 | 03
		 */
		/*
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
		*/

		/*
		 * AA | BB | CC
		 * ------------
		 * 01 | 02 | 01
		 * 02 | 03 | 02
		 */
		/*
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
		 */

		/*
		 * AA | BB | CC | XX
		 * ------------
		 * 01 | 02 | 01   01
		 * 02 | 03 | 02   01
		 */
		/*
		List<Row> table3 = new ArrayList<>();
		Row row5 = new Row();
		row5.setActive(true);
		var b5 = new BitSet();
		b5.set(characteristicValueIndex.get("AA_01"));
		b5.set(characteristicValueIndex.get("BB_02"));
		b5.set(characteristicValueIndex.get("CC_01"));
		b5.set(characteristicValueIndex.get("XX_01"));
		row5.setBitset(b5);

		Row rowx = new Row();
		rowx.setActive(true);
		var bx = new BitSet();
		bx.set(characteristicValueIndex.get("AA_02"));
		bx.set(characteristicValueIndex.get("BB_03"));
		bx.set(characteristicValueIndex.get("CC_02"));
		bx.set(characteristicValueIndex.get("XX_01"));
		rowx.setBitset(bx);

		table3.add(row5);
		table3.add(rowx);
		 */


		/*
		 * AA | XX
		 * ------------
		 * 01 | 01
		 * 02 | 01
		 */
		/*
		List<Row> table4 = new ArrayList<>();
		Row row6 = new Row();
		row6.setActive(true);
		var b6 = new BitSet();
		b6.set(characteristicValueIndex.get("AA_01"));
		b6.set(characteristicValueIndex.get("XX_01"));
		row6.setBitset(b6);
		table4.add(row6);

		Row rowb = new Row();
		rowb.setActive(true);
		var bb = new BitSet();
		bb.set(characteristicValueIndex.get("AA_02"));
		bb.set(characteristicValueIndex.get("XX_01"));
		rowb.setBitset(bb);
		table4.add(rowb);


		allTables.add(table1);
		allTables.add(table2);
		allTables.add(table3);
		allTables.add(table4);
		 */



		TableProcessor processor = new TableProcessor();
		List<Row> pseudoTable = processor.processTables(allTables);


		// Optionally print results or further process the filtered rows
		for (List<Row> table : allTables) {
			for (Row row : table) {
				if (row.isActive()) {
					System.out.println("Active Row: " + row.getBitset());
				} else {
					System.out.println("Non active row : " + row.getBitset());
				}
			}
		}


		/*
		List<Row> pseudoTable = new ArrayList<>();

		for (List<Row> incomingTable : allTables) {
			if (pseudoTable.isEmpty()) {
				System.out.println("Initializing pseudoTable with the first table.");
				for (Row row : incomingTable) {
					Row newCopy = new Row();
					newCopy.setActive(row.isActive());
					newCopy.setBitset((BitSet) row.getBitset().clone());
					pseudoTable.add(newCopy);
				}
				continue;
			}

			boolean hasDisabledLine;
			do {
				hasDisabledLine = false;
				Set<Row> rowsToKeep = new HashSet<>();
				System.out.println("Whats going on ?");

				for (Row incomingTableRow : incomingTable) {
					for (Row pseudoTableRow : new ArrayList<>(pseudoTable)) {
						if (!pseudoTableRow.isActive()) {
							continue;
						}

						BitSet intersection = (BitSet) pseudoTableRow.getBitset().clone();
						intersection.and(incomingTableRow.getBitset());

						if (!intersection.isEmpty()) {
							Row newRow = new Row();
							newRow.setActive(true);
							BitSet newBitSet = (BitSet) pseudoTableRow.getBitset().clone();
							newBitSet.or(incomingTableRow.getBitset());
							newRow.setBitset(newBitSet);
							rowsToKeep.add(newRow);
						}
					}
				}

				// Update pseudoTable to only include rows that have intersections
				if (!rowsToKeep.isEmpty()) {
					pseudoTable.clear();
					pseudoTable.addAll(rowsToKeep);
				} else {
					// If no intersections are found, deactivate all rows
					for (Row row : pseudoTable) {
						if (row.isActive()) {
							row.setActive(false);
							hasDisabledLine = true;
							System.out.println("Disabling row: " + row.getBitset());
						}
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

		 */







		// Get possibles values for each characteristic in the pseudo table ( required to compute with the signature after )

		Map<String, Set<String>> possibleValues = new HashMap<>();
		for (Row row : pseudoTable.stream().filter(Row::isActive).collect(Collectors.toList())) {
			BitSet bitset = row.getBitset();
			for (int i = bitset.nextSetBit(0); i >= 0; i = bitset.nextSetBit(i + 1)) {
				String characteristicValue = characteristicValueIndexReversed.get(i);
				String characteristic = characteristicValue.split("_")[0];
				String value = characteristicValue.split("_")[1];
				possibleValues.computeIfAbsent(characteristic, k -> new HashSet<>()).add(value);
			}
		}

		System.out.println("Possibles values per characteristic: " + possibleValues);

		System.out.println("\n");
		System.out.println("Compute combination using pseudo table");

		System.out.println("Using signature "+ singature);


		// filter pseudo table with valid line and contains signature characteristics ( turn on the valid lines )
		List<Row> filteredWithValidLineAndSignatureCharacteristics = pseudoTable
				.stream()
				.filter(Row::isActive)
				.map(row -> {

					boolean isValid = false;
					for (int i = row.getBitset().nextSetBit(0); i >= 0; i = row.getBitset().nextSetBit(i + 1)) {
						String characteristicValue = characteristicValueIndexReversed.get(i);
						String characteristic = characteristicValue.split("_")[0];
						String value = characteristicValue.split("_")[1];

						if (singature.contains(characteristic)) {
							isValid = true;
						}
					}

					if ( isValid ) {
						return row;
					} else {
						return null;
					}
				}).collect(Collectors.toList());



		// for each characteristic of the signature, generate a mask thanks to index map
		// goal is to have X bitset mask for each characteristic and to generate valid combination, using this mask to filter the valid values in pseudo table

		// Step from characteristic build mask

		// Pre processing mask
		// remove extra index
		Set<String> expectedPossibles = new HashSet();
		if ( folder.equalsIgnoreCase("P22") ) {
			expectedPossibles = P22Table.expectedPossiblesValues(P22Table.EXPECTED_POSSIBLES_VALUES);
		}
		if ( folder.equalsIgnoreCase("ZZK9") ) {
			expectedPossibles = ZZK9Table.expectedPossiblesValues(ZZK9Table.EXPECTED_POSSIBLES_VALUES);
		}
		final Set<String> expectedPossibleValus = expectedPossibles;
		Object2ObjectOpenHashMap<String, BitSet> masks = new Object2ObjectOpenHashMap<>(); // Assurez-vous que c'est déclaré quelque part approprié

		Arrays.stream(singature.split(" ")).forEach(charac -> {
			characteristicValueIndex.forEach((key, value) -> {
				if ( !expectedPossibleValus.contains(key) ) {
					return;
				}
				String characteristic = key.split("_")[0]; // Nom de la caractéristique, e.g., "AA"
				if (charac.equals(characteristic)) { // Utiliser equals pour une comparaison exacte si nécessaire
					BitSet bitSet = masks.getOrDefault(characteristic, new BitSet());
					bitSet.set(value); // Active le bit correspondant à la valeur
					masks.put(characteristic, bitSet); // Sauvegarde le BitSet mis à jour dans la map
				}
			});
		});




		System.out.println(masks);



		long startTimeX = System.nanoTime();

		ObjectArrayList<BitSet> combinations = new ObjectArrayList<>();
		filteredWithValidLineAndSignatureCharacteristics
				.forEach(pseudoTableRow -> {
					BitSet originalRowBitSet = (BitSet) pseudoTableRow.getBitset().clone();
					System.out.println("Processing row: " + originalRowBitSet);

					boolean allMasksValid = true;
					BitSet combinedBitSet = new BitSet();

					for (Map.Entry<String, BitSet> entry : masks.entrySet()) {
						String maskKey = entry.getKey();
						BitSet mask = (BitSet) entry.getValue().clone();
						BitSet intersection = (BitSet) originalRowBitSet.clone();
						intersection.and(mask);

						if (!intersection.isEmpty()) {
							System.out.println("Valid intersection with mask " + maskKey + ": " + intersection);
							combinedBitSet.or(intersection);
						} else {
							System.out.println("No valid intersection for mask " + maskKey);
							allMasksValid = false;
							break;
						}
					}

					if (allMasksValid) {
						System.out.println("All masks matched for this row. Adding to combinations: " + combinedBitSet);
						combinations.add((BitSet) combinedBitSet.clone()); // Clone again to ensure uniqueness
					}
				});

		System.out.println("Total combinations found: " + combinations.size());




		System.out.println("\n Result : \nCombinations generated from pseudo table rules : " + combinations);



		int totalCount = 0;
		System.out.println(combinations);

		for ( BitSet lineBitset : new HashSet<>(combinations) ) {

			System.out.println("Combination: " + lineBitset);

			var listCombiGeneratedForLine = generateCombinations(lineBitset, characteristicValueIndexReversed, Arrays.asList(singature.split(" ")));

			for ( List<String> l : new HashSet<>(listCombiGeneratedForLine.getKey()) ) {
				System.out.println(l.toString());
				totalCount++;
			}

			//System.out.println("Time all combination: " + listCombiGeneratedForLine.getValue());

		}


		var endTimeX = System.nanoTime();
		System.out.println("Total time : " + (endTimeX - startTimeX) / 1e6);
		System.out.println("Total combination " + totalCount);
	}
}
