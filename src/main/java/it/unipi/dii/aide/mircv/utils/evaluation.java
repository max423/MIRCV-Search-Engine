package it.unipi.dii.aide.mircv.utils;

import it.unipi.dii.aide.mircv.models.Configuration;
import it.unipi.dii.aide.mircv.query.queryHandler;
import it.unipi.dii.aide.mircv.query.scoreDoc;

import javax.swing.plaf.ProgressBarUI;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static it.unipi.dii.aide.mircv.Main.configureSearchEngine;
import static it.unipi.dii.aide.mircv.utils.FileUtils.loadFinalStructure;
import static it.unipi.dii.aide.mircv.utils.FileUtils.takeFinalRAF;

public class evaluation {

    //private static Boolean queryType;
    //private static Boolean scoringFunction;

    static int k = 10; // # doc to retrieve

    public static void main(String[] args) throws IOException {
        takeFinalRAF();
        loadFinalStructure();

        System.out.println("Welcome to the MIRCV search engine!");

        while (true){
            ArrayList<String> tokens;
            long startTime, stopTime;
            Scanner scanner = new Scanner(System.in);
            String query;
            configureSearchEngine(scanner);

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

                tokens = queryHandler.QueryPreProcessing(finalQuery);

                // execute query
                PriorityQueue<scoreDoc> result = queryHandler.returnPriorityQueue(tokens, k);

                // create a qrel file
                writeQrelFile(queryTokens[0], result);

                query = reader.readLine();
                tokens.clear();
            }

        }

    }

    private static void writeQrelFile(String queryToken, PriorityQueue<scoreDoc> returnPriorityQueue) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/it/unipi/dii/aide/mircv/resources/qrel.txt", true)))
        {
            // write qrel file
            int i = 1;
            int k = 10;
            // query_id Q0 doc_id rank STANDARD
            while (!returnPriorityQueue.isEmpty() && k > 0){
                scoreDoc doc = returnPriorityQueue.poll();
                // query_id Q0 doc_id rank STANDARD
                writer.write(queryToken + " Q0 " + doc.getDocID() + " " + i + " " + doc.getScore() + " STANDARD\n");
                i++;
                k--;
            }
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
