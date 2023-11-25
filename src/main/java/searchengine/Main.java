package searchengine;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        InvertedIndexSearch invertedIndexSearch = new InvertedIndexSearch();
        invertedIndexSearch.buildIndex();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the word you want to search: ");
        String queryTerm = scanner.nextLine();
        List<String> searchResult = invertedIndexSearch.search(queryTerm);

        int totalOccurrences = 0;

        // keep track of the processed docs
        Set<String> writtenDocuments = new HashSet<>();

        for (String document : searchResult) {
            totalOccurrences += invertedIndexSearch
                    .getTermOccurrences(queryTerm, document);
            writtenDocuments.add(document);
        }

        String newResultRow = String.format(
                "%-20s %-60s %-10d",
                queryTerm,
                String.join(", ", writtenDocuments),
                totalOccurrences);

        try {
            // check if file exists or
            // exists but it is empty
            Path file = Path.of("results.txt");
            boolean fileIsEmpty = Files.notExists(file)
                    || Files.size(file) == 0;

            List<String> rows = new ArrayList<>();

            if (!fileIsEmpty) {
                rows = Files.readAllLines(file);
            }

            List<String> dataRows = new ArrayList<>();
            String headerRow = "";

            if (rows.size() > 0) {
                headerRow = rows.get(0);
                if (rows.size() > 1) {
                    dataRows.addAll(rows.subList(1, rows.size()));
                }
            } else {
                headerRow = String.format("%-20s %-60s %-10s",
                        "Search Term", "File Names", "Hits Found");
            }

            // handle the case with no file names
            if (writtenDocuments.isEmpty()) {
                newResultRow = String.format("%-20s %-60s %-10d",
                        queryTerm, "-", totalOccurrences);
            }

            dataRows.add(newResultRow);

            dataRows = sortRowsByOccurrences(dataRows);

            // write the final results.txt
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter("results.txt"))
            ) {
                writer.write(headerRow);
                writer.newLine();
                for (String row : dataRows) {
                    writer.write(row);
                    writer.newLine();
                }
            }
            System.out.println("Search result added to results.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> sortRowsByOccurrences(List<String> rows) {
        Collections.sort(rows, (row1, row2) -> {
            String[] parts1 = row1.split("\\s+");
            String[] parts2 = row2.split("\\s+");
            if (parts1.length >= 3 && parts2.length >= 3) {
                try {
                    int occurrences1 = Integer.parseInt(parts1[parts1.length - 1]);
                    int occurrences2 = Integer.parseInt(parts2[parts2.length - 1]);
                    return Integer.compare(occurrences2, occurrences1); // Descending order
                } catch (NumberFormatException e) {
                }
            }
            return 0;
        });
        return rows;
    }
}