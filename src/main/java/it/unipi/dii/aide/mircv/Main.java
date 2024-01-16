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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static it.unipi.dii.aide.mircv.query.queryHandler.*;
import static it.unipi.dii.aide.mircv.utils.FileUtils.loadFinalStructure;


public class Main {

    private static Boolean queryType;
    private static Boolean scoringFunction;
    static int k ;

    public static void main(String[] args) throws IOException {
        FileUtils.takeFinalRAF();
        loadFinalStructure();
        DemoInterface();
    }

    // Demo interface for the search engine
    public static void DemoInterface() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String query;
        System.out.println("\nWelcome to the MIRCV search engine!");

        configureSearchEngine(scanner);

        // setta k da tastiera
        System.out.println("Set k:");
        while (true) {
            try {
                k = scanner.nextInt();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input.");
                scanner.nextLine();
            }
        }

        System.out.println("To exit the search engine, type 'exit'.");
        System.out.println("To reconfigure the search engine, type 'config'.\n");
        scanner.nextLine();

        while (true) {
            System.out.println("Please enter your query ");
            System.out.print("> ");
            query = scanner.nextLine();

            if (query.equals("exit")) {
                // exit the search engine
                System.out.println("Exiting the Search Engine. Goodbye!");
                break;
            } else if (query.equals("config")) {

                // reconfigure the search engine
                System.out.println("Reconfiguring the search engine...");
                configureSearchEngine(scanner);

                System.out.println("Set k:");
                while (true) {
                    try {
                        k = scanner.nextInt();
                        break;
                    } catch (java.util.InputMismatchException e) {
                        System.out.println("Invalid input.");
                        scanner.nextLine();
                    }
                }
                scanner.nextLine();
                continue;
            }

//          long startTime = System.currentTimeMillis();
            // process the query and tokenize it
            ArrayList<String> Qtokens = QueryPreProcessing(query);
            if ( Qtokens==null ) {
                continue;
            }

            System.out.println("Query tokens: " + Qtokens);

            executeQuery(Qtokens, k);
//            long stopTime = System.currentTimeMillis();
//            System.out.println("Query time: " + (stopTime - startTime) + " ms");
        }

        scanner.close();
    }


    // da tastiera prende le opzioni di configurazione ( query type, scoring function, search strategy, k )
    public static void configureSearchEngine(Scanner scanner) {
        System.out.println("\nConfigure the search engine:");

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


        // set configuration options and print them
        Configuration configuration = new Configuration();
        configuration.setConjunctiveON(queryType);
        configuration.setScoreON(scoringFunction);

        // Stampa le opzioni di configurazione
        System.out.println("Configured options:");
        System.out.println("Query type = " + (queryType == true ? "Conjunctive" : "Disjunctive"));
        System.out.println("Scoring function = " + (scoringFunction == true ? "BM25" : "TFIDF"));
        System.out.println("--------------------------------------------------");

        if ( cacheFlag == true ) {
            // queryCache inizializza
            queryCache.invalidateAll();


        }

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

