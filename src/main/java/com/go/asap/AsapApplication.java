package com.go.asap;

import com.go.asap.go.Enumerator;
import com.go.asap.go.TableRow;
import com.go.asap.m.*;
import com.go.asap.m.Loader;
import com.go.asap.m.Row;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.util.NeighborCache;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
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

	public static BitSet combineBitSets(List<BitSet> bitSetList, Set<BitSet> invalidBitSetList) {
		BitSet combinedBitSet = new BitSet();

		for (BitSet bitSet : bitSetList) {
			combinedBitSet.or(bitSet);
		}

		return combinedBitSet;
	}


    private static BitSet createBitSet(int... indices) {
        BitSet bitSet = new BitSet();
        for (int index : indices) {
            bitSet.set(index);
        }
        return bitSet;
    }

    public static int traverseGroupsV2(Map<String, Map<String, BitSet>> groups, String signature, Set<BitSet> invalidBitSets, TreeMap<String, Integer> valueToIndex, TreeMap<Integer, String> indexToValue) {
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
            combinationCount += traverseNextGroups(groups, groupOrder, 1, key1, bitSet1, invalidBitSets, valueToIndex, combination, indexToValue, new BitSet());
        }

        return combinationCount;
    }


    private static int traverseNextGroups(Map<String, Map<String, BitSet>> groups, String[] groupOrder, int currentGroupIndex, String previousKey, BitSet previousBitSet, Set<BitSet> invalidBitSets, TreeMap<String, Integer> valueToIndex, List<String> combination, TreeMap<Integer, String> indexToValue, BitSet realFilter) {
        // Si nous avons atteint la fin de la chaîne de groupes, affichez la combinaison et retournez 1 pour indiquer une combinaison valide
        if (currentGroupIndex >= groupOrder.length) {
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

			System.out.println();
			System.out.println("> compare " + previousKey + " idx : " + valueToIndex.get(previousKey) + " : " + previousBitSet);
			System.out.println(">> with " + key + " idx : " + valueToIndex.get(key) + " : "  + currentBitSet);
			System.out.println(">>> real filter : " + realFilter);
			// Filtrer le BitSet actuel en utilisant le BitSet précédent

			// update filter bitset ONLY if we do not loop over the values for a same carac... in this case keep the previous filter for each enumerated values !
			// filter change only when we go to the next carac
			BitSet filteredBitSet = (currentGroupIndex == groupOrder.length - 1) ? previousBitSet : filterBitSet(currentBitSet, previousBitSet);


			//BitSet filteredBitSet = filterBitSet(currentBitSet, previousBitSet);
			System.out.println(">>> Filter : " + filteredBitSet);



			// Enregistrer les indices non communs comme invalides pour les futures comparaisons
			/*
			Set<Integer> newInvalidIndices = new HashSet<>();
			for (int i = currentBitSet.nextSetBit(0); i >= 0; i = currentBitSet.nextSetBit(i + 1)) {
				if (!filteredBitSet.get(i)) {
					newInvalidIndices.add(i);
				}
			}
			System.out.println("===> invalid indice now : " + newInvalidIndices);
			newInvalidIndices.stream().sorted().forEach( i -> {
				System.out.println(indexToValue.get(i));
			});*/


			// Vérifiez si l'index précédent est présent dans le bitset actuel
            //if (currentBitSet.get(previousIndex)) {
			if (filteredBitSet.get(previousIndex)) {
				// Ajoutez la clé actuelle à la combinaison
                combination.add(key);

				System.out.println("CONTINUE PATH WITH " + combination);
				System.out.println();

				// Appelez récursivement la fonction pour le prochain groupe
                combinationCount += traverseNextGroups(groups, groupOrder, currentGroupIndex + 1, key, currentBitSet, invalidBitSets, valueToIndex, combination, indexToValue, filteredBitSet);
                // Retirez la clé actuelle après le retour de la récursion pour essayer la prochaine clé
                combination.remove(combination.size() - 1);
            } else {
				System.out.println("No path for " + combination + " for " + key + " continue ...");
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

	private static BitSet filterBitSet(BitSet currentBitSet, BitSet previousBitSet) {
		BitSet filteredBitSet = (BitSet) currentBitSet.clone();
		filteredBitSet.and(previousBitSet);
		return filteredBitSet;
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



	public static List<List<String>> excelFilter(List<String> signature, TreeMap<String, PseudoTable> pseudoTables,
												 TreeMap<String, Integer> valueToIndex, TreeMap<Integer, String> indexToValue,
												 HashMap<String, List<String>> groups) {

		List<List<String>> validCombinations = new ArrayList<>();
		Set<Integer> initialIndices = new HashSet<>();
		for (int i = 0; i < pseudoTables.size(); i++) {
			initialIndices.add(i);
		}

		recursiveFilter(signature, pseudoTables, groups, validCombinations, new ArrayList<>(), initialIndices, 0);

		return validCombinations;
	}

	private static void recursiveFilter(List<String> signature, TreeMap<String, PseudoTable> pseudoTables,
										HashMap<String, List<String>> groups, List<List<String>> validCombinations,
										List<String> currentCombination, Set<Integer> currentIndices, int level) {

		if (level == signature.size()) {
			validCombinations.add(new ArrayList<>(currentCombination));
			return;
		}

		String charac = signature.get(level);
		for (String value : groups.get(charac)) {
			Set<Integer> newIndices = updateValidIndices(currentIndices, value, pseudoTables);
			if (!newIndices.isEmpty()) {
				currentCombination.add(value);
				recursiveFilter(signature, pseudoTables, groups, validCombinations, currentCombination, newIndices, level + 1);
				currentCombination.remove(currentCombination.size() - 1);
			}
		}
	}

	private static Set<Integer> updateValidIndices(Set<Integer> validIndices, String characValue, TreeMap<String, PseudoTable> pseudoTables) {
		List<BitSet> bitsets = getBitsets(characValue, pseudoTables);
		Set<Integer> newValidIndices = new HashSet<>();

		for (BitSet bitset : bitsets) {
			for (int index : validIndices) {
				if (bitset.get(index)) {
					newValidIndices.add(index);
				}
			}
		}

		return newValidIndices;
	}

	private static List<BitSet> getBitsets(String characValue, TreeMap<String, PseudoTable> pseudoTables) {
		List<BitSet> bitsets = new ArrayList<>();
		for (PseudoTable table : pseudoTables.values()) {
			if (table.getValue().containsKey(characValue)) {
				bitsets.addAll(table.getValue().get(characValue).getBitsetList());
			}
		}
		return bitsets;
	}



	/*

		{AA=[01, 03], BB=[01, 02], CC=[21, 22]}


		AA_01

		j'active dans les table AA_01

		ensuite je vais suivant
		BB_01
		jaactive dans les table BB_01 mais il faut vérifier que dans les bitset list j'ai bien AA_01 index

		ensuite j'arrive à CC_21
		j'active et je regarde dans le bitset si j'ai bien : AA_01 et BB_01

		je vois j'ai une autre valeur à CC, CC_22
		donc je refais la mmême chose, j'active que CC_22 et je regarde si j'ai bien AA_01 et BB_01 quand j'active CC_22

		j'ai terminé, je repars en arrière, autre valeur à BB,

		donc j'active BB_02
		et je regarde si à BB_02 activé j'ai bien des AA_01 index

		si c'est le cas je repasse sur CC

		je dis sir j'ai CC_21 activé je regarde dans le bitset si j'ai bien AA_01 et BB_02
		idem pour CC_22, j'active je check les bitset
		termin" CC je reviens en arrière, pas d'autre valeur en BB, je repasse sur AA nouvelle valeur AA_03
		donc je repars AA_03 activé
		je passe à BB_01 esque j'ai AA_03 avec des BB_01 index dans les bitset ? oui j'avance en CC etc
	 */

	public static void traverse(Map<String, List<String>> map, TreeMap<String, Integer> valueToIndex, TreeMap<Integer, String> indexToValue, TreeMap<String, PseudoTable> pseudoTables, String prevKeyValue) {
		List<String> keys = new ArrayList<>(map.keySet());
		if (keys.isEmpty()) return;

		BitSet allowedBitsetIndex = new BitSet();
		Deque<BitSet> bitsetStack = new ArrayDeque<>();
		List<List<String>> validCombinations = new ArrayList<>();
		List<String> currentCombination = new ArrayList<>();

		traverseNextLevel(map, keys, 0, valueToIndex, indexToValue, pseudoTables, allowedBitsetIndex, bitsetStack, validCombinations, currentCombination);

		// Print all valid combinations
		System.out.println("Valid combinations:");
		for (List<String> combination : validCombinations) {
			System.out.println(combination);
		}
	}

	private static void traverseNextLevel(Map<String, List<String>> map, List<String> keys, int index, TreeMap<String, Integer> valueToIndex, TreeMap<Integer, String> indexToValue, TreeMap<String, PseudoTable> pseudoTables, BitSet allowedBitsetIndex, Deque<BitSet> bitsetStack, List<List<String>> validCombinations, List<String> currentCombination) {
		if (index >= keys.size()) {
			validCombinations.add(new ArrayList<>(currentCombination));
			return;
		}

		String currentKey = keys.get(index);
		List<String> currentList = map.get(currentKey);
		if (currentList == null) return;

		for (String currentValue : currentList) {
			BitSet currentBitSet = (BitSet) allowedBitsetIndex.clone();
			Integer currentIndex = valueToIndex.get(currentKey + "_" + currentValue);
			boolean isValid = false;

			if (allowedBitsetIndex.isEmpty()) {
				currentBitSet.set(currentIndex);
				isValid = true;
			} else {
				for (Map.Entry<String, PseudoTable> pseudoTableEntry : pseudoTables.entrySet()) {
					TreeMap<String, Row> rows = pseudoTableEntry.getValue().getValue();
					Row row = rows.get(indexToValue.get(currentIndex));
					if (row != null) {
						for (BitSet bitset : row.getBitsetList()) {
							BitSet tempBitSet = (BitSet) bitset.clone();
							tempBitSet.and(allowedBitsetIndex);
							if (!tempBitSet.isEmpty()) {
								currentBitSet.set(currentIndex);
								isValid = true;
								break;
							}
						}
					}
					if (isValid) break;
				}
			}

			if (isValid) {
				bitsetStack.push((BitSet) allowedBitsetIndex.clone());
				allowedBitsetIndex = currentBitSet;

				currentCombination.add(currentKey + "_" + currentValue);
				traverseNextLevel(map, keys, index + 1, valueToIndex, indexToValue, pseudoTables, allowedBitsetIndex, bitsetStack, validCombinations, currentCombination);
				currentCombination.remove(currentCombination.size() - 1);

				allowedBitsetIndex = bitsetStack.pop();
			}
		}
	}

    public static int traverseGroupV3(String signature, TreeMap<String, PseudoTable> pseudoTables, TreeMap<String, Integer> valueToIndex, TreeMap<Integer, String> indexToValue, TreeMap<String, List<String>> possiblesValues) {

        System.out.println("===========");
        System.out.println("===========");
        System.out.println("===========");

        String[] signatureSplited = signature.split(" ");
        TreeMap<String, List<String>> possiblesValuesFilteredBySignature = new TreeMap<>();
        for (String charInSignature : signatureSplited) {
            possiblesValuesFilteredBySignature.put(charInSignature, possiblesValues.get(charInSignature));
        }
        System.out.println(possiblesValuesFilteredBySignature);
		System.out.println(indexToValue);

		traverse(possiblesValuesFilteredBySignature, valueToIndex, indexToValue, pseudoTables, "");


		return 0;
    }


	public static void createGraph(Map<String, PseudoTable> tables, List<String> characteristics, Graph<String, DefaultEdge> graph) {
		// Créer les sommets pour chaque valeur possible dans toutes les tables
		for (PseudoTable table : tables.values()) {
			for (String rowKey : table.getValue().keySet()) {
				graph.addVertex(rowKey);
			}
		}

		// Ajouter les arêtes avec filtrage successif
		for (int i = 0; i < characteristics.size() - 1; i++) {
			String currentKeyPrefix = characteristics.get(i);
			String nextKeyPrefix = characteristics.get(i + 1);

			for (PseudoTable table : tables.values()) {
				for (String currentRowKey : table.getValue().keySet()) {
					if (currentRowKey.startsWith(currentKeyPrefix)) {
						Row currentRow = table.getValue().get(currentRowKey);

						// Appliquer le filtrage successif
						List<BitSet> filteredBitSets = new ArrayList<>(currentRow.getBitsetList());
						for (String nextRowKey : table.getValue().keySet()) {
							if (nextRowKey.startsWith(nextKeyPrefix)) {
								Row nextRow = table.getValue().get(nextRowKey);
								if (hasIntersection(filteredBitSets, nextRow.getBitsetList())) {
									filteredBitSets = filterBitSets(filteredBitSets, nextRow.getBitsetList());
									graph.addEdge(currentRowKey, nextRowKey);
								}
							}
						}
					}
				}
			}
		}
	}

	public static boolean hasIntersection(List<BitSet> list1, List<BitSet> list2) {
		for (BitSet bitset1 : list1) {
			for (BitSet bitset2 : list2) {
				BitSet intersection = (BitSet) bitset1.clone();
				intersection.and(bitset2);
				if (!intersection.isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}

	public static void traverseGraph(Graph<String, DefaultEdge> graph, String startVertex, List<String> characteristics, Map<String, PseudoTable> tables, TreeMap<Integer, String> indexToValue, TreeMap<String, Integer> valueToIndex) {
		System.out.println("Traversing from: " + startVertex);
		traverseGraphHelper(graph, startVertex, new ArrayList<>(), new HashSet<>(), characteristics, tables, indexToValue, valueToIndex, null);
	}


	private static void traverseGraphHelper(Graph<String, DefaultEdge> graph, String currentVertex, List<String> path, Set<String> visited, List<String> characteristics, Map<String, PseudoTable> tables,  TreeMap<Integer, String> indexToValue, TreeMap<String, Integer> valueToIndex, BitSet filter) {
		path.add(currentVertex);
		visited.add(currentVertex);


		if ( filter == null ) {
			filter = new BitSet();
		} else {
			// make sure we dont take MAT
			filter.clear(valueToIndex.get("MAT_0S"));
		}



		int currentIndex = characteristics.indexOf(currentVertex.split("_")[0]);
		if (currentIndex == characteristics.size() - 1 || graph.outgoingEdgesOf(currentVertex).isEmpty()) {
			// If the current vertex is the last characteristic or has no outgoing edges, it's the end of a path
			System.out.println(String.join(" ", path.stream().map(p -> p.replace("_", "")).collect(Collectors.toList())));
		} else {
			String nextKeyPrefix = characteristics.get(currentIndex + 1);
			Row currentRow = tables.values().stream()
					.flatMap(table -> table.getValue().entrySet().stream())
					.filter(entry -> entry.getKey().equals(currentVertex))
					.map(Map.Entry::getValue)
					.findFirst()
					.orElse(null);

			if (currentRow != null) {


				// Continue traversing the graph
				for (DefaultEdge edge : graph.outgoingEdgesOf(currentVertex)) {
					String nextVertex = graph.getEdgeTarget(edge);

					int indexToFind = valueToIndex.get(nextVertex);
					if ( filter.isEmpty() ) {
						List<BitSet> x = currentRow.getBitsetList().stream().filter(b->b.get(indexToFind)).collect(Collectors.toList());
						if ( !x.isEmpty() ) {
							for ( BitSet b : x ) {
								filter.or(b);
							}
						}
					}


					if (nextVertex.startsWith(nextKeyPrefix) && !visited.contains(nextVertex)) {
						System.out.println("CURRENT : " + currentVertex);
						System.out.println( combineBitSets(currentRow.getBitsetList(), new HashSet<>()));
						System.out.println(currentRow.getBitsetList());
						System.out.println();

						Row nextRow = tables.values().stream()
								.flatMap(table -> table.getValue().entrySet().stream())
								.filter(entry -> entry.getKey().equals(nextVertex))
								.map(Map.Entry::getValue)
								.findFirst()
								.orElse(null);

						System.out.println("NEXT : " + nextVertex);
						System.out.println( combineBitSets(nextRow.getBitsetList(), new HashSet<>()));
						System.out.println(nextRow.getBitsetList());
						System.out.println();

						if ( nextRow != null ) {

							int nextRowIndexToFind = valueToIndex.get(nextVertex);
							List<BitSet> y = nextRow.getBitsetList().stream().filter(b->b.get(nextRowIndexToFind)).collect(Collectors.toList());
							if ( !y.isEmpty() ) {
								for ( BitSet b : y ) {
									var c = (BitSet) b.clone();
									var f = (BitSet) filter.clone();
									c.and(f);
									if ( !c.isEmpty() ) {
										filter.or(b);
										System.out.println("Intersection on : " + c);
										System.out.println("Filter becomes : " + filter);
										traverseGraphHelper(graph, nextVertex, new ArrayList<>(path), new HashSet<>(visited), characteristics, tables, indexToValue, valueToIndex, filter);
									}
								}
							}


							/*
							for ( BitSet b : nextRow.getBitsetList() ) {
								BitSet cb = (BitSet) b.clone();
								for ( BitSet fb : filteredBitSets ) {
									BitSet fbc = (BitSet) fb.clone();
									fbc.and(cb);
									if ( !fbc.isEmpty() ) {

										traverseGraphHelper(graph, nextVertex, new ArrayList<>(path), new HashSet<>(visited), characteristics, tables, indexToValue, valueToIndex, filter);
									}
								}
							} */
						}

						/*
						if (nextRow != null && hasIntersection(filteredBitSets, nextRow.getBitsetList())) {
							List<BitSet> newFilteredBitSets = filterBitSets(filteredBitSets, nextRow.getBitsetList());
							if (!newFilteredBitSets.isEmpty()) {
								traverseGraphHelper(graph, nextVertex, new ArrayList<>(path), new HashSet<>(visited), characteristics, tables, indexToValue, valueToIndex);
							}
						}

						 */


					}
				}
			}
		}
	}

	public static List<BitSet> filterBitSets(List<BitSet> filteredBitSets, List<BitSet> list2) {
		Set<BitSet> newFilteredBitSets = new HashSet<>();
		for (BitSet bitset1 : filteredBitSets) {
			for (BitSet bitset2 : list2) {
				BitSet intersection = (BitSet) bitset1.clone();
				intersection.and(bitset2);
				if (!intersection.isEmpty()) {
					newFilteredBitSets.add(intersection);
				}
			}
		}
		return new ArrayList<>(newFilteredBitSets);
	}


	public static Graph<String, DefaultEdge> buildGraphV2(TreeMap<String, BitSet> characValueToLineBitset, String signature) {
		Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
		String[] signatureOrder = signature.split(" ");

		// Add vertices
		for (String charValue : characValueToLineBitset.keySet()) {
			graph.addVertex(charValue);
		}

		// Add edges based on BitSet intersections
		List<String> tmp = new ArrayList<>();
		for (String charValue1 : characValueToLineBitset.keySet()) {
			System.out.println("COMPARE : " + charValue1);
			System.out.println(" ==== ");
			BitSet b = (BitSet) characValueToLineBitset.get(charValue1).clone();
			tmp.add(charValue1);
			while ( tmp.size() != signature.length() ) {

			}

		}

		return graph;
	}

	// TODO => beware this graph is not properly reflected the fully relation !
	// but you can use it at least to get "connectivity" between nodes ( order enumeration )
	public static Graph<String, DefaultEdge> buildGraph(TreeMap<String, BitSet> characValueToLineBitset, String signature) {
		Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
		String[] signatureOrder = signature.split(" ");

		// Add vertices
		for (String charValue : characValueToLineBitset.keySet()) {
			graph.addVertex(charValue);
		}

		// Add edges based on BitSet intersections
		for (String charValue1 : characValueToLineBitset.keySet()) {
			for (String charValue2 : characValueToLineBitset.keySet()) {
				if (!charValue1.equals(charValue2)) {
					BitSet bitSet1 = characValueToLineBitset.get(charValue1);
					BitSet bitSet2 = characValueToLineBitset.get(charValue2);

					BitSet intersection = (BitSet) bitSet1.clone();
					intersection.and(bitSet2);

					if (!intersection.isEmpty()) {
						graph.addEdge(charValue1, charValue2);
					}

					System.out.println();
				}
			}
		}

		// Prioritize traversal order based on the signature and connectivity
		return prioritizeTraversalOrder(graph, signatureOrder);
	}

	private static Graph<String, DefaultEdge> prioritizeTraversalOrder(Graph<String, DefaultEdge> graph, String[] signatureOrder) {
		NeighborCache<String, DefaultEdge> neighborCache = new NeighborCache<>(graph);
		List<String> prioritizedNodes = new ArrayList<>();

		// Add nodes from the signature order
		for (String charValue : signatureOrder) {
			if (graph.containsVertex(charValue)) {
				prioritizedNodes.add(charValue);
			}
		}

		// Add remaining nodes by connectivity
		BreadthFirstIterator<String, DefaultEdge> bfsIterator = new BreadthFirstIterator<>(graph);
		while (bfsIterator.hasNext()) {
			String node = bfsIterator.next();
			if (!prioritizedNodes.contains(node)) {
				prioritizedNodes.add(node);
			}
		}

		// Create a new graph with prioritized traversal order
		Graph<String, DefaultEdge> prioritizedGraph = new SimpleGraph<>(DefaultEdge.class);
		for (String node : prioritizedNodes) {
			prioritizedGraph.addVertex(node);
		}
		for (String node : prioritizedNodes) {
			for (String neighbor : neighborCache.neighborsOf(node)) {
				if (prioritizedNodes.contains(neighbor)) {
					prioritizedGraph.addEdge(node, neighbor);
				}
			}
		}

		return prioritizedGraph;
	}


	public static List<List<String>> findAllCombinations(Graph<String, DefaultEdge> graph, String startNode) {
		
		System.out.println("Start with node : " + startNode);
		List<List<String>> allCombinations = new ArrayList<>();
		Set<String> visited = new HashSet<>();
		List<String> currentCombination = new ArrayList<>();

		dfs(graph, startNode, visited, currentCombination, allCombinations);

		return allCombinations;
	}

	// we could have cycle in our graph, causing infinite loop if you explore it badly !
	private static void dfs(Graph<String, DefaultEdge> graph, String currentNode, Set<String> visited, List<String> currentCombination, List<List<String>> allCombinations) {
		// Debugging print statement
		System.out.println("Exploring: " + currentNode);
		System.out.println("Visited : " + visited);
		System.out.println("Current combination " + currentCombination);

		// Mark the current node as visited
		visited.add(currentNode);
		currentCombination.add(currentNode);

		// Add a copy of the current combination to the list of all combinations
		allCombinations.add(new ArrayList<>(currentCombination));

		// Explore each adjacent node
		for (DefaultEdge edge : graph.edgesOf(currentNode)) {
			String neighbor = graph.getEdgeTarget(edge).equals(currentNode) ? graph.getEdgeSource(edge) : graph.getEdgeTarget(edge);

			// Only visit the neighbor if it hasn't been visited yet
			if (!visited.contains(neighbor)) {
				dfs(graph, neighbor, visited, currentCombination, allCombinations);
			}
		}

		// Backtrack: unmark the current node and remove it from the current combination
		visited.remove(currentNode);
		currentCombination.remove(currentCombination.size() - 1);
	}

	public static void computeCombinations(Graph<String, DefaultEdge> graph, TreeMap<String, List<String>> possiblesValues, Map<String, Integer> orderEnumeration, String signature) {

		System.out.println("Before : " + possiblesValues);
		Iterator<String> iterator = possiblesValues.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (!signature.contains(key)) {
				iterator.remove();
			}
		}
		System.out.println("After : " + possiblesValues);


		// Get possibles values for signature
		// TODO
		System.out.println("Start computation : " + signature);
		System.out.println(graph);
		System.out.println(graph.vertexSet());
		System.out.println(graph.edgeSet());
		System.out.println(orderEnumeration);
		System.out.println(possiblesValues);
		// TODO
		if ( possiblesValues.isEmpty() ) {
			System.out.print("No combination possibles !");
		} else {

			// Get first vertex
			String firstVertex = orderEnumeration.keySet().stream().findFirst().orElseThrow();
			String firstVertexValue = possiblesValues.get(firstVertex).stream().findFirst().orElseThrow();
			String combinedKey = firstVertex+"_"+firstVertexValue;

			DepthFirstIterator<String, DefaultEdge> dfsIt = new DepthFirstIterator<>(graph, combinedKey);

			Set<String> visited = new HashSet<>();

			// Parcours du graphe en profondeur
			while (dfsIt.hasNext()) {
				String vertex = dfsIt.next();
				if (visited.add(vertex)) {  // Vérifie si le sommet a été ajouté, indiquant qu'il n'était pas déjà visité
					System.out.println("Visiting: " + vertex);

					// Explorer les voisins
					graph.edgesOf(vertex).forEach(edge -> {
						String source = graph.getEdgeSource(edge);
						String target = graph.getEdgeTarget(edge);
						if (!vertex.equals(target) && visited.add(target)) {
							System.out.println(vertex + " -> " + target);
						} else if (!vertex.equals(source) && visited.add(source)) {
							System.out.println(vertex + " -> " + source);
						}
					});
				}
			}

			/*
			graph.edgesOf(combinedKey).forEach( edge -> {
				String source = graph.getEdgeSource(edge);
				String target = graph.getEdgeTarget(edge);

				if (source.equals(combinedKey)) {
					System.out.println("Voisin de : "+ combinedKey + " - " + target);
				} else if (target.equals(combinedKey)) {
					System.out.println("Voisin de : " + combinedKey + " - " + source);
				}
			});

			 */

			/*
			BreadthFirstIterator<String, DefaultEdge> iteratorDfs = new BreadthFirstIterator<>(graph, combinedKey);
			while (iteratorDfs.hasNext()) {
				String vertex = iteratorDfs.next();
				System.out.println("Visiting vertex: " + vertex);
				for (DefaultEdge edge : graph.edgesOf(vertex)) {
					var target = graph.getEdgeTarget(edge);
					if ( !vertex.equalsIgnoreCase(target) ) {
						System.out.println(vertex + " -> " + target);
					}
				}
			}*/

		}
	}


	public static void findIntersections(Map<String, BitSet> dataMap, String prefix1, String prefix2) {
		Map<String, TreeMap<String, BitSet>> groupedMap = new HashMap<>();

		// Group data by prefix
		for (Map.Entry<String, BitSet> entry : dataMap.entrySet()) {
			String prefix = entry.getKey().substring(0, 3);
			groupedMap.computeIfAbsent(prefix, k -> new TreeMap<>()).put(entry.getKey(), entry.getValue());
		}

		// Get the groups based on the specified prefixes
		TreeMap<String, BitSet> group1 = groupedMap.getOrDefault(prefix1, new TreeMap<>());
		TreeMap<String, BitSet> group2 = groupedMap.getOrDefault(prefix2, new TreeMap<>());

		// Find and print intersections
		for (Map.Entry<String, BitSet> entry1 : group1.entrySet()) {
			for (Map.Entry<String, BitSet> entry2 : group2.entrySet()) {
				BitSet intersection = (BitSet) entry1.getValue().clone();
				intersection.and(entry2.getValue());
				if (!intersection.isEmpty()) {
					System.out.println(entry1.getKey() + " intersects with " + entry2.getKey());
				}
			}
		}
	}

	public static void findMultiIntersections(Map<String, BitSet> dataMap, List<String> prefixes) {
		Map<String, TreeMap<String, BitSet>> groupedMap = new HashMap<>();

		// Group data by prefix
		for (Map.Entry<String, BitSet> entry : dataMap.entrySet()) {
			String prefix = entry.getKey().substring(0, 3);
			groupedMap.computeIfAbsent(prefix, k -> new TreeMap<>()).put(entry.getKey(), entry.getValue());
		}

		// Prepare to find intersections among all groups
		List<TreeMap<String, BitSet>> groups = new ArrayList<>();
		for (String prefix : prefixes) {
			groups.add(groupedMap.getOrDefault(prefix, new TreeMap<>()));
		}

		// Recursive function to process combinations
		recursiveFind(new ArrayList<>(), groups, 0, null);
	}



	private static void recursiveFind(List<String> currentKeys, List<TreeMap<String, BitSet>> groups, int index, BitSet currentIntersection) {
		String currentCombination = String.join(" ", currentKeys).replace("_", "");

		// Debugging points
		if (currentCombination.equalsIgnoreCase("B0C_P2 B0F_ES B0G_0F B0H_B0 DAQ_05")) {
			System.out.println("bingo");
			System.out.println(currentIntersection);
		}

		if (currentCombination.equalsIgnoreCase("B0C_P2 B0F_ES B0G_0F B0H_B0 DAQ_00")) {
			System.out.println("bingo stop");
			System.out.println(currentIntersection);
		}

		if (index == groups.size()) {
			if (currentIntersection != null && !currentIntersection.isEmpty()) {
				System.out.println(currentCombination);
			}
			return;
		}

		TreeMap<String, BitSet> currentGroup = groups.get(index);
		for (Map.Entry<String, BitSet> entry : currentGroup.entrySet()) {
			BitSet newIntersection = currentIntersection == null ? (BitSet) entry.getValue().clone() : (BitSet) currentIntersection.clone();
			if (currentIntersection != null) {
				newIntersection.and(entry.getValue());
			}

			// Update the keys only if the new intersection is not empty or it is the first group
			List<String> newKeys = new ArrayList<>(currentKeys);
			if (!newIntersection.isEmpty() || currentIntersection == null) {
				newKeys.add(entry.getKey());
			} //else {
				// Continue even if the intersection is empty
				//List<String> newKeys = new ArrayList<>(currentKeys);
				//newKeys.add(entry.getKey());
				//recursiveFind(newKeys, groups, index + 1, currentIntersection);
			//}
			recursiveFind(newKeys, groups, index + 1, newIntersection);


		}
	}

	public static void findAllPaths(Graph<String, DefaultEdge> graph, String startVertex, List<String> combinations) {
		Set<String> visited = new HashSet<>();
		Stack<String> path = new Stack<>();
		path.push(startVertex);
		dfs(graph, startVertex, visited, path, 1, combinations);  // 1 pour la profondeur initiale avec B0G_01
	}

	private static void dfs(Graph<String, DefaultEdge> graph, String currentVertex, Set<String> visited, Stack<String> path, int depth, List<String> combinations) {
		visited.add(currentVertex);

		// Affichage du chemin actuel si la profondeur est 3 (B0G, B0E, B0F)
		if (depth == 3) {
			ArrayList ar = new ArrayList(path);
			StringBuilder builder = new StringBuilder();
			path.forEach(p -> builder.append(p+" "));
			combinations.add(builder.toString());
			System.out.println(builder.toString().replace("_",""));
			visited.remove(currentVertex); // Pour permettre d'autres explorations
			return;
		}

		for (DefaultEdge edge : graph.edgesOf(currentVertex)) {
			String target = graph.getEdgeTarget(edge);
			if (target.equals(currentVertex)) {
				target = graph.getEdgeSource(edge);
			}

			if (!visited.contains(target) && isCorrectType(target, depth)) {
				path.push(target);
				dfs(graph, target, visited, path, depth + 1, combinations);
				path.pop();
			}
		}

		visited.remove(currentVertex);
	}

	// Vérifie si le sommet correspond au type attendu selon la profondeur
	private static boolean isCorrectType(String vertex, int depth) {
		if (depth == 1 && vertex.startsWith("B0E_")) return true;
		if (depth == 2 && vertex.startsWith("B0F_")) return true;
		return false;
	}

	public static List<String> getOrder(List<String> signature, Graph<String, DefaultEdge> graph, TreeMap<String, Integer> valueToIndex) {
		// Utiliser un TreeMap pour conserver l'ordre tout en triant par connectivité
		TreeMap<Integer, List<String>> connectivityMap = new TreeMap<>(Collections.reverseOrder());

		for (String carac : valueToIndex.keySet()) {
			int degree = graph.degreeOf(carac);
			connectivityMap.computeIfAbsent(degree, k -> new ArrayList<>()).add(carac);
		}

		LinkedList<String> orderedCaracs = new LinkedList<>();
		for (Map.Entry<Integer, List<String>> entry : connectivityMap.entrySet()) {
			orderedCaracs.addAll(entry.getValue());
		}


		return orderedCaracs;
	}


	public static List<String[][]> getTable1() {
		List<String[][]> table1 = new ArrayList<>();
		table1.add(new String[][]{
				{"B0F", "MK"},
				{"B0G", "01"},
				{"B0E", "0R"},
				{"B0H", "X1"}
		});
		table1.add(new String[][]{
				{"B0F", "MK"},
				{"B0G", "02"},
				{"B0E", "0L"},
				{"B0H", "X2"}
		});
		table1.add(new String[][]{
				{"B0F", "ZI"},
				{"B0G", "03"},
				{"B0E", "0J"},
				{"B0H", "X3"}
		});
		return table1;
	}

	public static List<String[][]> getTable2() {
		List<String[][]> table2 = new ArrayList<>();
		table2.add(new String[][]{
				{"B0F", "ZI"}
		});
		return table2;
	}

	public static List<String[][]> getTable3() {
		List<String[][]> table3 = new ArrayList<>();
		table3.add(new String[][]{
				{"B0F", "ZI"},
				{"B0G", "01"}
		});
		return table3;
	}


	// apply relationship in same table
	// we cannot have "bridge" between table to build a valid path §

	/*
	private static void addNodesAndEdges(Graph<String, DefaultEdge> graph, List<String[][]> table) {
		for (String[][] row : table) {
			String previousNode = null;
			for (String[] cv : row) {
				String node = cv[0] + "=" + cv[1];
				graph.addVertex(node);
				if (previousNode != null) {
					// Ajout de l'arête uniquement si elle n'existe pas déjà
					if (!graph.containsEdge(previousNode, node)) {
						graph.addEdge(previousNode, node);
					}
				}
				previousNode = node;
			}
		}
	}*/


	/* // with table in name
	private static void addNodesAndEdges(Graph<String, DefaultEdge> graph, List<String[][]> table, String tableName) {
		for (String[][] row : table) {
			String previousNode = null;
			for (String[] cv : row) {
				String node = tableName+"_"+cv[0] + "=" + cv[1];
				graph.addVertex(node);
				if (previousNode != null) {
					// Ajout de l'arête uniquement si elle n'existe pas déjà
					if (!graph.containsEdge(previousNode, node)) {
						graph.addEdge(previousNode, node);
					}
				}
				previousNode = node;
			}
		}
	} */


	private static void addNodesAndEdges(Graph<String, DefaultEdge> graph, List<String[][]> table) {
		Set<String> validConnections = new HashSet<>();

		for (String[][] row : table) {
			String previousNode = null;
			for (String[] cv : row) {
				String node =  cv[0] + "=" + cv[1];
				graph.addVertex(node);
				if (previousNode != null) {
					validConnections.add(previousNode + "->" + node);
				}
				previousNode = node;
			}
		}

		for (String connection : validConnections) {
			String[] nodes = connection.split("->");
			if (!graph.containsEdge(nodes[0], nodes[1])) {
				graph.addEdge(nodes[0], nodes[1]);
			}
		}
	}



	private static List<List<String>> generateCombinations(Graph<String, DefaultEdge> graph, Map<String, List<String>> signature) {
		List<List<String>> combinations = new ArrayList<>();
		List<String> targetNodes = new ArrayList<>();

		for (Map.Entry<String, List<String>> entry : signature.entrySet()) {
			String characteristic = entry.getKey();
			for (String value : entry.getValue()) {
				targetNodes.add(characteristic + "=" + value);
			}
		}

		for (String startNode : graph.vertexSet()) {
			List<String> path = new ArrayList<>();
			Set<String> visited = new HashSet<>();
			findPaths(graph, startNode, targetNodes, path, visited, combinations);
		}

		return combinations;
	}

	private static void findPaths(Graph<String, DefaultEdge> graph, String currentNode, List<String> targetNodes, List<String> path, Set<String> visited, List<List<String>> combinations) {
		path.add(currentNode);
		visited.add(currentNode);

		if (targetNodes.contains(currentNode) && allTargetsVisited(path, targetNodes)) {
			if (path.stream().anyMatch(node -> node.startsWith("B0F="))) {
				combinations.add(new ArrayList<>(path));
			}
		} else {
			for (DefaultEdge edge : graph.outgoingEdgesOf(currentNode)) {
				String nextNode = graph.getEdgeTarget(edge);
				if (!visited.contains(nextNode)) {
					findPaths(graph, nextNode, targetNodes, path, visited, combinations);
				}
			}
		}

		path.remove(path.size() - 1);
		visited.remove(currentNode);
	}

	private static boolean allTargetsVisited(List<String> path, List<String> targetNodes) {
		for (String target : targetNodes) {
			if (!path.contains(target)) {
				return false;
			}
		}
		return true;
	}


	public static LinkedList<String> getOrderEnumeration(Graph<String, DefaultEdge> graph, TreeMap<String, List<String>> signatureWithPossiblesValues) {

		Map<String, Integer> connectivity = new HashMap<>();
		for (String vertex : graph.vertexSet()) {
			int degree = graph.inDegreeOf(vertex) + graph.outDegreeOf(vertex);
			connectivity.put(vertex, degree);
		}

		LinkedList<String> orderedNodes = new LinkedList<>(graph.vertexSet());
		// Ordonner les nœuds par connectivité décroissante
		orderedNodes.sort((node1, node2) -> {
			int degree1 = connectivity.getOrDefault(node1, 0);
			int degree2 = connectivity.getOrDefault(node2, 0);
			return Integer.compare(degree2, degree1);
		});

		// Placer les caractéristiques de la signature en premier
		orderedNodes.sort((node1, node2) -> {
			boolean isNode1InSignature = isNodeInSignature(node1, signatureWithPossiblesValues);
			boolean isNode2InSignature = isNodeInSignature(node2, signatureWithPossiblesValues);
			if (isNode1InSignature && !isNode2InSignature) {
				return -1;
			} else if (!isNode1InSignature && isNode2InSignature) {
				return 1;
			} else {
				return 0;
			}
		});

		return orderedNodes;
	}

	private static boolean isNodeInSignature(String node, Map<String, List<String>> signature) {
		for (String characteristic : signature.keySet()) {
			if (node.startsWith(characteristic + "=")) {
				return true;
			}
		}
		return false;
	}




	private static void generateCombination(Graph<String, DefaultEdge> graph, LinkedHashSet<String> orderedNodes, TreeMap<String, List<String>> signatureWithPossiblesValues) {
		// Liste pour stocker les combinaisons valides
		List<Set<String>> validCombinations = new ArrayList<>();

		// Keep order node only for signature
		LinkedList<String> filterOrderNodes = orderedNodes.stream()
				.filter(o -> signatureWithPossiblesValues.containsKey(o.split("=")[0]))
				.collect(Collectors.toCollection(LinkedList::new));

		for (String orderNode : filterOrderNodes) {
			if (orderNode.startsWith("B0F")) {  // Change condition as needed
				System.out.println("Visit paths for " + orderNode);

				List<String> possiblesValueForOrderNode = signatureWithPossiblesValues.get(orderNode.split("=")[0]);
				System.out.println("--> " + possiblesValueForOrderNode);

				for (String possibleValue : possiblesValueForOrderNode) {
					String startNode = orderNode.split("=")[0] + "=" + possibleValue;
					System.out.println("-----> Explore path for " + startNode);
					HashSet<String> visited = new HashSet<>();
					Map<String, String> nodeValues = new HashMap<>();
					explorePath(graph, startNode, visited, nodeValues, validCombinations, filterOrderNodes, signatureWithPossiblesValues);
					System.out.println();
				}
			}
		}

		// Afficher les combinaisons valides
		System.out.println("Valid Combinations:");
		for (Set<String> combination : validCombinations) {
			System.out.println(combination);
		}
	}



	private static void explorePath(Graph<String, DefaultEdge> graph, String currentNode, HashSet<String> visited, Map<String, String> nodeValues, List<Set<String>> validCombinations, LinkedList<String> filterOrderNodes, TreeMap<String, List<String>> signatureWithPossiblesValues) {
		visited.add(currentNode);
		String currentKey = currentNode.split("=")[0];
		String currentValue = currentNode.split("=")[1];
		nodeValues.put(currentKey, currentValue);

		System.out.println("Visiting node: " + currentNode + ", visited: " + visited);

		// Vérifier si toutes les caractéristiques de la signature sont visitées
		if (nodeValues.keySet().containsAll(signatureWithPossiblesValues.keySet())) {
			// Filtrer les nœuds visités pour ne garder que ceux correspondant à la signature
			Set<String> validCombination = new HashSet<>();
			for (String node : visited) {
				String key = node.split("=")[0];
				if (signatureWithPossiblesValues.containsKey(key)) {
					validCombination.add(node);
				}
			}
			validCombinations.add(validCombination);
			return; // Arrêter l'exploration après avoir trouvé une combinaison valide
		}

		for (DefaultEdge edge : graph.edgesOf(currentNode)) {
			String target = graph.getEdgeTarget(edge);
			String source = graph.getEdgeSource(edge);

			String nextNode = source.equals(currentNode) ? target : source;
			String nextKey = nextNode.split("=")[0];
			String nextValue = nextNode.split("=")[1];

			if (!visited.contains(nextNode) && (!nodeValues.containsKey(nextKey) || nodeValues.get(nextKey).equals(nextValue))) {
				explorePath(graph, nextNode, new HashSet<>(visited), new HashMap<>(nodeValues), validCombinations, filterOrderNodes, signatureWithPossiblesValues);
			}
		}
	}




	/*
	private static void generateCombination(Graph<String, DefaultEdge> graph, LinkedHashSet<String> orderedNodes, TreeMap<String, List<String>> signatureWithPossiblesValues) {
		// Liste pour stocker les combinaisons valides
		List<Set<String>> validCombinations = new ArrayList<>();

		// Keep order node only for signature
		LinkedList<String> filterOrderNodes = orderedNodes.stream()
				.filter(o -> signatureWithPossiblesValues.containsKey(o.split("=")[0].split("_")[1]))
				.collect(Collectors.toCollection(LinkedList::new));

		for (String orderNode : filterOrderNodes) {
			if (orderNode.contains("B0F")) {  // Change condition as needed
				System.out.println("Visit paths for " + orderNode);

				List<String> possiblesValueForOrderNode = signatureWithPossiblesValues.get(orderNode.split("=")[0].split("_")[1]);
				System.out.println("--> " + possiblesValueForOrderNode);

				for (String possibleValue : possiblesValueForOrderNode) {
					String startNode = orderNode.split("=")[0].split("_")[0] + "_" + orderNode.split("=")[0].split("_")[1] + "=" + possibleValue;
					System.out.println("-----> Explore path for " + startNode);
					HashSet<String> visited = new HashSet<>();
					Map<String, String> nodeValues = new HashMap<>();
					explorePath(graph, startNode, visited, nodeValues, validCombinations, filterOrderNodes, signatureWithPossiblesValues);
					System.out.println();
				}
			}
		}

		// Afficher les combinaisons valides
		System.out.println("Valid Combinations:");
		for (Set<String> combination : validCombinations) {
			System.out.println(combination);
		}
	}*/

	/*
	private static void explorePath(Graph<String, DefaultEdge> graph, String currentNode, HashSet<String> visited, Map<String, String> nodeValues, List<Set<String>> validCombinations, LinkedList<String> filterOrderNodes, TreeMap<String, List<String>> signatureWithPossiblesValues) {
		visited.add(currentNode);
		String currentKey = currentNode.split("=")[0].split("_")[1];
		String currentValue = currentNode.split("=")[1];
		nodeValues.put(currentKey, currentValue);

		System.out.println("Visiting node: " + currentNode + ", visited: " + visited);

		// Vérifier si toutes les caractéristiques de la signature sont visitées
		if (nodeValues.keySet().containsAll(signatureWithPossiblesValues.keySet())) {
			// Filtrer les nœuds visités pour ne garder que ceux correspondant à la signature
			Set<String> validCombination = new HashSet<>();
			for (String node : visited) {
				String key = node.split("=")[0].split("_")[1];
				if (signatureWithPossiblesValues.containsKey(key)) {
					validCombination.add(node);
				}
			}
			validCombinations.add(validCombination);
			return; // Arrêter l'exploration après avoir trouvé une combinaison valide
		}

		for (DefaultEdge edge : graph.edgesOf(currentNode)) {
			String target = graph.getEdgeTarget(edge);
			String source = graph.getEdgeSource(edge);

			String nextNode = source.equals(currentNode) ? target : source;
			String nextKey = nextNode.split("=")[0].split("_")[1];
			String nextValue = nextNode.split("=")[1];

			if (!visited.contains(nextNode) && (!nodeValues.containsKey(nextKey) || nodeValues.get(nextKey).equals(nextValue))) {
				explorePath(graph, nextNode, new HashSet<>(visited), new HashMap<>(nodeValues), validCombinations, filterOrderNodes, signatureWithPossiblesValues);
			}
		}
	}


*/



	public static void main(String[] args) {
		SpringApplication.run(AsapApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {

		// build graph following relationship in table

		// Obtenir les tables mock
		List<String[][]> table1 = getTable1();
		List<String[][]> table2 = getTable2();
		List<String[][]> table3 = getTable3();

		// Initialiser le graphe
		Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

		// Ajouter les nœuds et les arêtes pour chaque table
		Set<String> validConnections = new HashSet<>();
		addNodesAndEdges(graph, table1);
		addNodesAndEdges(graph, table2);
		addNodesAndEdges(graph, table3);

		// Afficher le graphe
		System.out.println("Graph:");
		for (String vertex : graph.vertexSet()) {
			System.out.print(vertex + " -> ");
			for (DefaultEdge edge : graph.outgoingEdgesOf(vertex)) {
				System.out.print(graph.getEdgeTarget(edge) + " ");
			}
			System.out.println();
		}



		/*
			B0F	B0G	B0E	B0H
			MK	01	0R	X1
			MK	02	0L	X2
			ZI	03	0J	X3
			Table 2 :

			B0F
			ZI
			Table 3 :

			B0F	B0G
			ZI	01
		 */

		// Parcourir le graphe pour générer les combinaisons valides
		TreeMap<String, List<String>> signatureWithPossiblesValues = new TreeMap<>();
		signatureWithPossiblesValues.put("B0F", Arrays.asList("MK", "ZI"));
		signatureWithPossiblesValues.put("B0H", Arrays.asList("X1", "X2", "X3"));
		signatureWithPossiblesValues.put("B0E", Arrays.asList("0R", "0L", "0J"));

		System.out.println("Possible values (for signature) : ");
		System.out.println(signatureWithPossiblesValues);



		System.out.println("Order enumeration : ");
		LinkedList<String> orderedNode = getOrderEnumeration(graph, signatureWithPossiblesValues);
		LinkedHashSet<String> orderedNodeSimplified = orderedNode.stream().map(o -> o.split("=")[0]).collect(Collectors.toCollection(LinkedHashSet::new));
		System.out.println(orderedNodeSimplified);
		System.out.println("Detail : " + orderedNode);


		System.out.println("Combination generation : ");
		generateCombination(graph, orderedNodeSimplified, signatureWithPossiblesValues);

		/*
		System.out.println("Combination generation : ");
		var combinations = generateCombination(graph, orderedNodeSimplified, signatureWithPossiblesValues);
		System.out.println("Valid Combinations:");
		for (Set<String> combination : combinations) {
			System.out.println(combination);
		}*/


		/*
		List<List<String>> combinations = generateCombinations(graph, signature);

		System.out.println("Combinations for signature " + signature + ":");
		for (List<String> combination : combinations) {
			System.out.println(combination);
		}*/

		/*
		B0F :

		MK
				ZI
		B0G :

		01
		02
		03
		B0E :

		0R
		0L
		0J
		B0H :

		X1
				X2
		X3
		*/

	}
}
