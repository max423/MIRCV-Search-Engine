package it.unipi.dii.aide.mircv.query;

import it.unipi.dii.aide.mircv.models.Configuration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static it.unipi.dii.aide.mircv.Main.configureSearchEngine;
import static it.unipi.dii.aide.mircv.utils.FileUtils.loadFinalStructure;
import static it.unipi.dii.aide.mircv.utils.FileUtils.takeFinalRAF;

public class queryPerformance {

    //private static Boolean queryType;
    //private static Boolean scoringFunction;

    static int k = 10; // # doc to retrieve

    public static void main(String[] arg) throws IOException {

        takeFinalRAF();
        loadFinalStructure();

        System.out.println("Welcome to the MIRCV search engine!");


        while (true){
            ArrayList<String> tokens;
            long startTime, stopTime;
            Scanner scanner = new Scanner(System.in);
            String query;
            configureSearchEngine(scanner);
            /*
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
            */
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("src/main/java/it/unipi/dii/aide/mircv/resources/msmarco-test2020-queries.tsv"), StandardCharsets.UTF_8));
            query = reader.readLine();

            ArrayList<Long> queryTime = new ArrayList<>();

            while (query != null){
                String[] queryTokens = query.split("\t");

                // print query
                System.out.println("Query ID: " + queryTokens[0]);
                System.out.println("Query: " + queryTokens[1]);


                List<String> queryList = new ArrayList<>(Arrays.asList(queryTokens));
                queryList.remove(0);

                // convert to document without id
                String finalQuery = String.join(",", queryList);

                // start timer
                startTime = System.currentTimeMillis();

                tokens = queryHandler.QueryPreProcessing(finalQuery);

                queryHandler.executeQuery(tokens, k);

                // stop timer
                stopTime = System.currentTimeMillis() - startTime;

                // print time
                System.out.println("Query time: " + stopTime + " ms");

                // add time to list
                queryTime.add(stopTime);

                query = reader.readLine();
                tokens.clear();
            }

            // average time in seconds
            long sum = 0;
            for (long time : queryTime){
                sum += time;
            }

            System.out.println("Average time: " + sum/queryTime.size()/1000 + " s"); // in secondi è troppo stampa sempre 0
            System.out.println("--------------------------------------------------");
            System.out.println("Average time: " + sum/queryTime.size() + " ms");
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
