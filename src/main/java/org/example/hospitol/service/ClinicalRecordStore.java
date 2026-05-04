package org.example.hospitol.service;

import java.io.*;
import java.util.*;

public class ClinicalRecordStore {

    private static final ClinicalRecordStore INSTANCE = new ClinicalRecordStore();
    public static ClinicalRecordStore getInstance() { return INSTANCE; }
    private ClinicalRecordStore() { loadFromFile(); }

    private static final String FILE = "clinical_records.txt";
    private final Map<String, List<String>> records = new HashMap<>();

    public void addRecord(String patientName, String record) {
        records.computeIfAbsent(patientName, k -> new ArrayList<>()).add(record);
        saveToFile();
    }

    public List<String> getRecords(String patientName) {
        return records.getOrDefault(patientName, new ArrayList<>());
    }

    private void saveToFile() {
        try (FileWriter fw = new FileWriter(FILE, false)) {
            for (Map.Entry<String, List<String>> entry : records.entrySet()) {
                for (String rec : entry.getValue()) {
                    fw.write(entry.getKey() + "|" + rec.replace("|", "_") + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Could not save records: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    records.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load records: " + e.getMessage());
        }
    }
}