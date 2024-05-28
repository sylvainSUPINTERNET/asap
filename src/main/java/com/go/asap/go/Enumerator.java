package com.go.asap.go;

import java.util.List;
import java.util.TreeMap;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;


public class Enumerator {

    private List<String> signature;
    private TreeMap<String, List<String>> possiblesValuesFilteredBySignature;
    private TreeMap<String, TreeMap<String, TableRow>> tablesLair;

    public Enumerator(List<String> signature, TreeMap<String, List<String>> possiblesValues, TreeMap<String, TreeMap<String, TableRow>> tablesLair) {
        this.signature = signature;
        this.tablesLair = tablesLair;
        this.possiblesValuesFilteredBySignature = new TreeMap<>();
        for (String charInSignature : signature) {
            possiblesValuesFilteredBySignature.put(charInSignature, possiblesValues.get(charInSignature));
        }

        System.out.println("Signature " + this.signature);
        System.out.println("Possibles values ");
        System.out.println(possiblesValuesFilteredBySignature);
    }

    public void trimTables() {


        TreeMap<String, TreeMap<String, TableRow>> deletedValues = new TreeMap<>();

        this.signature.forEach((signatureCharacteristic) -> {
            List<String> values = this.possiblesValuesFilteredBySignature.get(signatureCharacteristic);
            System.out.println();
            System.out.println("Checking for "+ signatureCharacteristic + " : " + values);


            this.tablesLair.entrySet().stream().forEach((entry)-> {
                String tableName = entry.getKey();
                TreeMap<String, TableRow> table = entry.getValue();

                String[] tableNameSplit = tableName.split("_");


                if (Arrays.asList(tableNameSplit).contains(signatureCharacteristic)
                        // TODO debug
                        && signatureCharacteristic.equalsIgnoreCase("B0C")
                ) {

                    System.out.println(signatureCharacteristic + " present in table : " + tableName + " for " + signatureCharacteristic);


                    table.keySet().forEach(tableKey-> {
                        System.out.println("-- " + tableKey);

                        var splitTableKey = Arrays.stream(tableKey.split("_")).findFirst();
                        if ( splitTableKey.isPresent() && splitTableKey.get().equalsIgnoreCase(signatureCharacteristic)) {
                            System.out.println("---> Must activate : " + tableKey);
                            table.get(tableKey).setIsValid(Boolean.TRUE);
                        }


                    });
                }


                // TODO debug
                if (Arrays.asList(tableNameSplit).contains(signatureCharacteristic)
                        // TODO debug
                        && signatureCharacteristic.equalsIgnoreCase("B0F")
                ) {

                }

            });




        });


     // carac donnée AA BB CC
        // je prend les valeur possible pour chaque carac
        // je commence par AA
        // j'active les ligne dans les table ou y'a AA
        // ensuite je fais BB si la table contiens AA et BB je dois verifier intersection
        // pas d'intersection => ligne n'est poas valide (u pdate invalide )
        // si j'ai CC et BB et que je n'ai pas d'intersection je dois desactiver BB aussi => update
        // Si j'ai BB CC et ça desactive des BB utilisé dans AA il faudra en cascade desactiver les ligne de AA plus valide => update impossible
    }

    public void generateCombinations() {

    }


}
