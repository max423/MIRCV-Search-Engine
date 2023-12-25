package it.unipi.dii.aide.mircv.query;

import java.util.Scanner;
import it.unipi.dii.aide.mircv.models.CollectionStatistics;

import java.io.IOException;
import java.util.PriorityQueue;

public class utils {

    // get the min docID from the posting list
    public static int getMinDocID() {
        // get the first docID
        int minDocID = queryHandler.orderedPostingList.get(0).getCurrentPostingList().getDocID();
        int docIDcmp;

        // todo orderedPostingList
        for (int i = 0; i < queryHandler.orderedPostingList.size(); i++) {
            docIDcmp = queryHandler.orderedPostingList.get(i).getCurrentPostingList().getDocID();
            if (docIDcmp < minDocID) {
                minDocID = docIDcmp;
            }
        }

        return (int) minDocID;
    }

    // DAAT algorithm
    // todo
    public static PriorityQueue<scoreDoc> DAAT(int k) throws IOException {

        // initialize the priority queue with score in descending order
        PriorityQueue<scoreDoc> scoreDocs = new PriorityQueue<>(k, new scoreDocComparator());



        return scoreDocs;
    }


    // todo update partial score
    //public static double updatePartialScore(double score) {
        // todo
    //}



    // Demo interface for the search engine
    public void DemoInterface() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String query;

        System.out.println("Welcome to the MIRCV search engine!");

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
}
