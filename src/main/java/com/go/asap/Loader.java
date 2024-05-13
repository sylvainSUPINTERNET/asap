package com.go.asap;

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Loader {

    public AbstractMap.SimpleEntry<LinkedHashSet<String>, Integer> readTable(String folder, String scopeFilter) {
        LinkedHashSet<String> characteristic_value = new LinkedHashSet<>();
        int numberOfColumns = 0;

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

                    for (Path file : files) {
                        try (BufferedReader reader = Files.newBufferedReader(file)) {
                            // Read first line to get column names
                            String headerLine = reader.readLine();
                            if (headerLine == null) {
                                continue;
                            }
                            String[] headers = headerLine.split("\\s+");
                            // Update the number of columns, assuming all lines in a file have the same number of columns
                            numberOfColumns = headers.length;

                            String line;
                            while ((line = reader.readLine()) != null) {
                                String[] values = line.split("\\s+");
                                if (values.length != headers.length) {
                                    System.out.println("Mismatch between headers and values in file: " + file);
                                    continue;
                                }
                                for (int i = 0; i < headers.length; i++) {
                                    String combinedValue = headers[i] + "_" + values[i];
                                    characteristic_value.add(combinedValue);
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

        return new AbstractMap.SimpleEntry<>(characteristic_value, numberOfColumns);
    }


    public List<List<Row>> generateVirtualTables(String folder, String scopeFilter,
                                                 Map<String, Integer> characteristicValueIndex,
                                                 Map<Integer, String> characteristicValueIndexReversed,
                                                 List<List<Row>> allTables) {

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

                    for (Path file : files) {
                        List<Row> table = new ArrayList<>();
                        try (BufferedReader reader = Files.newBufferedReader(file)) {
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
                                Row row = new Row();
                                row.setActive(true);
                                BitSet b1 = new BitSet();

                                for (int i = 0; i < headers.length; i++) {
                                    String combinedValue = headers[i] + "_" + values[i];
                                    if (characteristicValueIndex.containsKey(combinedValue)) {
                                        b1.set(characteristicValueIndex.get(combinedValue));
                                    }
                                }

                                row.setBitset(b1);
                                table.add(row);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (!table.isEmpty()) {
                            allTables.add(table);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return allTables;
    }

}
