package com.go.asap.m;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Enumerator {


    // Helper class to store the state of the traversal
    private static class TraversalState {
        int signatureCaracIndex;
        int characValueIndex;

        TraversalState(int signatureCaracIndex, int characValueIndex) {
            this.signatureCaracIndex = signatureCaracIndex;
            this.characValueIndex = characValueIndex;
        }
    }

    public static int traverseGroupsV3(TreeMap<String, PseudoTable> pseudoTables, String signature,
                                       TreeMap<String, Integer> valueToIndex,
                                       TreeMap<String, List<BitSet>> memoizationPossiblesValuesFiltered,
                                       Set<BitSet> invalidBitSets,
                                       TreeMap<Integer, String> indexToValue) {

        String[] splitedSignature = signature.split(" ");

        // init identified not possible values
        HashSet<String> characValueToIgnore = new HashSet<>();
        for ( BitSet invalidBitset : invalidBitSets ) {
            for (int i = invalidBitset.nextSetBit(0); i != -1; i = invalidBitset.nextSetBit(i + 1)) {
                characValueToIgnore.add(indexToValue.get(i));
            }
        }


        // Prepare groups
        HashMap<String, HashSet<String>> groups = new HashMap<>();
        for ( String charSignature: splitedSignature ) {

            if ( !groups.containsKey(charSignature) ) {
                groups.put(charSignature, new HashSet<>());
            }
        }

        memoizationPossiblesValuesFiltered.keySet().forEach( key -> {
            String[] splitedKey = key.split("_");
            groups.computeIfPresent(splitedKey[0], (k, v) -> {
                v.add(key);
                return v;
            });
        });


        for ( Map.Entry<String, HashSet<String>> entry : groups.entrySet() ) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }


        // Enumerator
        // Reminder :
        // We have to take each possibles values based on the signature
        // Everytime:
        // We temporary remove entry where we don't find in pseudo table the AA_01
        // then at the begining thats all
        // after next char for value 02, like BB_02
        // more complex, we have to do same thing, remove where we don't find BB_02 in pseudo table BUT
        // also when we found it, we have to remove "useless" bitset in the lsit ( means we didnt found index in the bitset = remove the bitset from the BB_02 bitset list )
        // And we repeat same thing for CC_21, but this time we have 2 index to check AA_02 and BB_02
        // at the end we have to check on the last char of the signature if we have more values ? if yes
        // we have "reset" line closed due to previous CC_21 only ( keep other ) and this time, manage C_22 in the same way.
        // at the end, we finished for this level, we have to go back to prev level, means BB and check if we have more values
        // if yes, then we reset CC_ line disable and also reset BB_ lines disabled
        // and repeat the process with the new BB value ( so check AA_02 and BB_03 new value ) intersection
        // then go further, and repeat 2 intersection AA and BB_03 and CC_21 then CC_22 and so we go back to end ( reset CC_ )
        // back to BB, no mroe values then reset
        // we back to AA, more values ? save "tmp" combinations found from previous AA_01, then repeat the full process for the new AA_02 value
        // No more value, then it's over
        //
        //


        return 0;
    }





}
