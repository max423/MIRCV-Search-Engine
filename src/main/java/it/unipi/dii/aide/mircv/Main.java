package it.unipi.dii.aide.mircv;
import it.unipi.dii.aide.mircv.indexer.Indexer;
import it.unipi.dii.aide.mircv.indexer.Spimi;
import it.unipi.dii.aide.mircv.models.CollectionStatistics;
import it.unipi.dii.aide.mircv.models.Configuration;
import it.unipi.dii.aide.mircv.models.PostingList;
import it.unipi.dii.aide.mircv.models.VocabularyElem;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.Scanner;

import static it.unipi.dii.aide.mircv.utils.FileUtils.*;

public class Main {


    private static Boolean queryType;
    private static Boolean scoringFunction;
    private static Boolean searchStrategy;

    public static void main(String[] args) throws IOException {
        FileUtils.takeFinalRAF();
        // Indexer.PlotFinalStructure();

        //DemoInterface();





//        // retrive vocabulary and posting list of term
//        String term = "war";
//        VocabularyElem v = Indexer.getTestVocabularyElem(term);
//        PostingList p = Indexer.getTestPosting(term);
//        System.out.println(p);
//
//        // collection statistics
//        System.out.println("\nCollection Statistics");
//        CollectionStatistics collectionStatistics = new CollectionStatistics();
//        collectionStatistics.readFromDisk(FileUtils.GetCorrectChannel(-1, 3), 0);
//        System.out.println(collectionStatistics);

    }

    // Demo interface for the search engine
    public static void DemoInterface() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String query;
        System.out.println("Welcome to the MIRCV search engine!");

        configureSearchEngine(scanner);

        while (true) {
            System.out.println("Please enter your query (or 'exit' to quit): ");
            query = scanner.nextLine();

            if (query.equalsIgnoreCase("exit")) {
                System.out.println("Exiting the program. Goodbye!");
                break;
            }

            // Process the query using your DAAT or any other search logic
            // List<Integer> result = processQuery(query, iterators);

            // Print the results (assuming <pid> is the document ID)
            //System.out.println("Results for query '" + query + "': " + result);
        }

        // Close resources
        scanner.close();

    }

    // da tastiera prende le opzioni di configurazione ( query type, scoring function, search strategy)
    public static void configureSearchEngine(Scanner scanner) {

        // Configura la query
        System.out.println("Configure query type:");
        System.out.println("1: Conjunctive");
        System.out.println("2: Disjunctive");
        queryType = readIntInput(scanner, 1, 2);

        // Configura la funzione di scoring
        System.out.println("Configure scoring function:");
        System.out.println("1: BM25");
        System.out.println("2: TFIDF");
        scoringFunction = readIntInput(scanner, 1, 2);

        // Configura la strategia di ricerca // TODO SIMO search strategy?
        System.out.println("Configure search strategy:");
        System.out.println("1: MaxScore");
        System.out.println("2: DAAT");
        searchStrategy = readIntInput(scanner, 1, 2);

        // set configuration options and print them
        Configuration configuration = new Configuration();
        configuration.setConjunctiveON(queryType);
        configuration.setMaxScoreON(searchStrategy);
        configuration.setScoreON(scoringFunction);

        // Stampa le opzioni di configurazione
        System.out.println("Configured options:");
        System.out.println("Query type = " + (queryType == true ? "Conjunctive" : "Disjunctive"));
        System.out.println("Scoring function = " + (scoringFunction == true ? "BM25" : "TFIDF"));
        System.out.println("Search strategy = " + (searchStrategy == true ? "MaxScore" : "DAAT"));
        System.out.println("--------------------------------------------------");

        scanner.nextLine();
    }

    private static Boolean readIntInput(Scanner scanner, int minValue, int maxValue) {
        int input;
        do {
            System.out.print("Enter " + minValue + " or " + maxValue + ": ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
            input = scanner.nextInt();
        } while (input < minValue || input > maxValue);

        return input == 1; // 1: true, 2: false
    }


}

