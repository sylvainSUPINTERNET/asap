package com.go.asap.m;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import java.util.*;

public class ExtendedBitSetGraphBuilder  {

    private TreeMap<String, BitSet> map = new TreeMap<>();
    private Graph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

    private String signature = "";

    public Graph<String, DefaultEdge> getGraph() {
        return this.graph;
    }

    public ExtendedBitSetGraphBuilder (TreeMap<String, BitSet> map, String signature) {
        this.map = map;

        this.signature = signature;

        for (String charValue : map.keySet()) {
            graph.addVertex(charValue);
        }

        buildGraph();
    }

    private void buildGraph() {
        List<String> keys = new ArrayList<>(map.keySet());

        // Génération de toutes les combinaisons de clés (taille 3 à N)
        for (int r = this.signature.length() ; r <= keys.size(); r++) {
            combine(keys, r);
        }
    }

    private void combine(List<String> keys, int r) {
        int[] combination = new int[r];

        // Initialisation avec la première combinaison
        for (int i = 0; i < r; i++) {
            combination[i] = i;
        }

        while (combination[r - 1] < keys.size()) {
            processCombination(keys, combination);

            // Génération de la prochaine combinaison
            int t = r - 1;
            while (t != 0 && combination[t] == keys.size() - r + t) {
                t--;
            }
            combination[t]++;
            for (int i = t + 1; i < r; i++) {
                combination[i] = combination[i - 1] + 1;
            }
        }
    }

    private void processCombination(List<String> keys, int[] combination) {
        BitSet intersection = (BitSet) map.get(keys.get(combination[0])).clone();
        for (int i = 1; i < combination.length; i++) {
            intersection.and(map.get(keys.get(combination[i])));
        }
        if (!intersection.isEmpty()) {
            // Si l'intersection n'est pas vide, ajouter les relations au graphe
            for (int i = 0; i < combination.length - 1; i++) {
                for (int j = i + 1; j < combination.length; j++) {
                    graph.addEdge(keys.get(combination[i]), keys.get(combination[j]));
                }
            }
        }
    }
}


