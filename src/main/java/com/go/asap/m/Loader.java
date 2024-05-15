package com.go.asap.m;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Loader {
    public static void readTableAndBuildIndexMap(String folder, String scopeFilter, HashSet<String> characteristic_value,
                                    TreeMap<String, Integer> valueToIndex, TreeMap<Integer, String> indexToValue,
                                    AtomicInteger globalIdx) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        List<URL> resources;
        try {
            Enumeration<URL> urls = cl.getResources(folder);
            resources = Collections.list(urls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (URL url : resources) {
            try {
                URI uri = url.toURI();
                Path path = Paths.get(uri);
                try (Stream<Path> stream = Files.walk(path)) {
                    Pattern pattern = Pattern.compile(".*" + Pattern.quote(scopeFilter.replace("*", "")) + ".*\\.txt");
                    List<Path> files = stream.filter(Files::isRegularFile)
                            .filter(f -> pattern.matcher(f.getFileName().toString()).matches())
                            .collect(Collectors.toList());

                    System.out.println("Files matching pattern: " + files);

                    for (Path file : files) {
                        try (BufferedReader reader = Files.newBufferedReader(file)) {
                            // Read first line to get column names
                            String headerLine = reader.readLine();
                            if (headerLine == null) {
                                continue;
                            }
                            String[] headers = headerLine.split("\\s+");

                            String line;
                            while ((line = reader.readLine()) != null) {
                                String[] values = line.split("\\s+");
                                if (values.length != headers.length) {
                                    System.out.println("Mismatch between headers and values in file: " + file);
                                    continue;
                                }
                                for (int i = 0; i < headers.length; i++) {
                                    String combinedValue = headers[i] + "_" + values[i];
                                    System.out.println("Adding value: " + combinedValue);
                                    characteristic_value.add(combinedValue);

                                    // Add to index maps
                                    if (!valueToIndex.containsKey(combinedValue)) {
                                        valueToIndex.put(combinedValue, globalIdx.get());
                                        indexToValue.put(globalIdx.get(), combinedValue);
                                        globalIdx.getAndIncrement();
                                    }
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void readTableAndBuildTableAggregation(String folder, String scopeFilter,
                                                         TreeMap<String, Integer> valueToIndex, TreeMap<Integer, String> indexToValue,
                                                         String fileName, Table table, TreeMap<String, Row> valuesTable) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        List<URL> resources;
        try {
            Enumeration<URL> urls = cl.getResources(folder);
            resources = Collections.list(urls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (URL url : resources) {
            try {
                URI uri = url.toURI();
                Path path = Paths.get(uri);
                try (Stream<Path> stream = Files.walk(path)) {
                    Pattern pattern = Pattern.compile(".*" + Pattern.quote(scopeFilter.replace("*", "")) + ".*\\.txt");
                    List<Path> files = stream.filter(Files::isRegularFile)
                            .filter(f -> pattern.matcher(f.getFileName().toString()).matches())
                            .collect(Collectors.toList());

                    System.out.println("Files matching pattern: " + files);

                    for (Path file : files) {
                        try (BufferedReader reader = Files.newBufferedReader(file)) {
                            // Read first line to get column names
                            String headerLine = reader.readLine();
                            if (headerLine == null) {
                                continue;
                            }
                            String[] headers = headerLine.split("\\s+");

                            String line;
                            while ((line = reader.readLine()) != null) {
                                System.out.println("LINE + " + line);

                                String[] values = line.split("\\s+");

                                if (values.length != headers.length) {
                                    System.out.println("Mismatch between headers and values in file: " + file);
                                    continue;
                                }

                                for (int i = 0; i < headers.length; i++) {
                                    String combinedValue = headers[i] + "_" + values[i];
                                    BitSet bitSet = new BitSet(headers.length);

                                    // Set the bit for each column's value
                                    for (int j = 0; j < headers.length; j++) {
                                        String combined = headers[j] + "_" + values[j];
                                        bitSet.set(valueToIndex.get(combined));
                                    }

                                    if (valuesTable.containsKey(combinedValue)) {
                                        var existing = valuesTable.get(combinedValue);
                                        existing.getBitsetList().add(bitSet);
                                    } else {
                                        ArrayList<BitSet> bitSetList = new ArrayList<>();
                                        bitSetList.add(bitSet);
                                        valuesTable.put(combinedValue, new Row(Boolean.TRUE, bitSetList));
                                    }
                                }
                            }
                            table.setValue(valuesTable);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
