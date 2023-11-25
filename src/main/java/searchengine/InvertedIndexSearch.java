package searchengine;

import java.io.*;
import java.util.*;

public class InvertedIndexSearch {
    private Map<String, List<String>> invertedIndex;
    private Map<String, Map<String, Integer>> termOccurrences;

    public InvertedIndexSearch() {
        invertedIndex = new HashMap<>();
        termOccurrences = new HashMap<>();
    }

    public void buildIndex() {
        // Load files from the resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            List<String> fileNames = Arrays.asList(
                    "document1.txt",
                    "document2.txt",
                    "document3.txt",
                    "document4.txt");

            for (String fileName : fileNames) {
                InputStream inputStream = classLoader.getResourceAsStream(fileName);
                if (inputStream != null) {
                    try (BufferedReader reader =
                                 new BufferedReader(
                                         new InputStreamReader(inputStream))) {
                        // Use the filename as the document ID
                        String documentId = fileName;
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] tokens = line.trim().split("\\s+");
                            for (String term : tokens) {
                                // Remove periods and commas from the term
                                term = term.replaceAll("[.,]", "").toLowerCase();
                                if (!invertedIndex.containsKey(term)) {
                                    invertedIndex.put(term, new ArrayList<>());
                                }
                                invertedIndex.get(term).add(documentId);

                                if (!termOccurrences.containsKey(term)) {
                                    termOccurrences.put(term, new HashMap<>());
                                }
                                // use the inner map of termOccurrences
                                // to keep count of the occurrences
                                Map<String, Integer> docOccurrences = termOccurrences.get(term);
                                docOccurrences.put(
                                        documentId,
                                        docOccurrences.getOrDefault(documentId, 0) + 1);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> search(String queryTerm) {
        List<String> searchResult = new ArrayList<>();

        for (String word : invertedIndex.keySet()) {
            if (word.toLowerCase().contains(queryTerm.toLowerCase())) {
                List<String> documents = invertedIndex.get(word);
                for (String document : documents) {
                    if (!searchResult.contains(document)) {
                        searchResult.add(document);
                    }
                }
            }
        }
        return searchResult;
    }

    public int getTermOccurrences(String term, String documentId) {
        if (termOccurrences.containsKey(term)) {
            Map<String, Integer> docOccurrences = termOccurrences.get(term);
            if (docOccurrences.containsKey(documentId)) {
                return docOccurrences.get(documentId);
            }
        }
        return 0;
    }
}