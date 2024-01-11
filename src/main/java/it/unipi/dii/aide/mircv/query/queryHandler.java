package it.unipi.dii.aide.mircv.query;

import it.unipi.dii.aide.mircv.models.CollectionStatistics;
import it.unipi.dii.aide.mircv.models.Configuration;
import it.unipi.dii.aide.mircv.models.PostingList;

import java.io.IOException;
import java.util.*;

import static it.unipi.dii.aide.mircv.text_processing.TextProcessing.DocumentProcessing;
import static it.unipi.dii.aide.mircv.utils.FileUtils.documentIndex;

// receive a query and return the top k  results
public class queryHandler {

    //public static ArrayList<PostingList> orderedPostingList = new ArrayList<>();
    public static ArrayList<PostingList> postingListQuery = new ArrayList<>();
    public static HashMap<Integer, Double> hashMapScore = new HashMap<>();
    public static HashMap<Integer, Integer> hashMapLength = new HashMap<>();
    //public static ArrayList<Double> maxScoreOrder = new ArrayList<>();

    // receive a query and return the top k (10 or 20) results
    public static void executeQuery(ArrayList<String> tokens, int k) throws IOException {

        int position = 0;

        // ArrayList of tokens removing duplicates
        ArrayList<String> tokensNoDuplicates = new ArrayList<>();
        for (String token : tokens) {
            if (!tokensNoDuplicates.contains(token)) {
                tokensNoDuplicates.add(token);
            }
        }

        // process each token of the query
        for (String token : tokensNoDuplicates) {

            // posting list initialization
            PostingList postingList = new PostingList(token);


            long startTime = System.currentTimeMillis();
            System.out.println("prima della decompressione " + System.currentTimeMillis());

            // obtain the posting list for the token
            postingList.getPostingList(token);

            long endTime = System.currentTimeMillis();
            System.out.println("dopo la decompressione " + System.currentTimeMillis());

            System.out.println("interval " + (endTime - startTime));




            //System.out.println(postingList);

            // check if the posting list is empty (the token is not in the vocabulary)
            if (postingList.getPostingList().size() == 0) {
                System.out.println("The token " + token + " is not in the vocabulary");
                if (Configuration.isConjunctiveON()) {
                    // print the results
                    System.out.println("Results for query: ");
                    System.out.println("--------------------------------------------------");
                    System.out.println("No results found");
                    System.out.println("--------------------------------------------------");
                    resetDataStructures();
                    return;
                }
                continue;
            }

            // add the posting list to the posting list with the tokens in the query
            postingListQuery.add(postingList);


            // add the size of the posting list to the hashmap
            hashMapLength.put(position, postingList.getPostingList().size());

            // increment the position
            position++;

        }

        // initialize a priority queue of scoreDoc objects
        PriorityQueue<scoreDoc> priorityQueue;

        // check if the posting list with the tokens in the query is empty
        if (postingListQuery.size() == 0) {
            System.out.println("All the tokens are not in the vocabulary");
            resetDataStructures();
            return;
        }

        // check the type of configuration
        if (Configuration.isConjunctiveON()) {
            // conjunctive
            priorityQueue = utils.conjunctive(k);
        } else {
            // disjunctive
            priorityQueue = utils.disjunctive(k);
        }

        //System.out.println("Priority queue size: " + priorityQueue.size());

        // print the results
        printResults(priorityQueue, k);

        // reset the data structures
        resetDataStructures();
    }

    // process the query, do text processing, check if the # of token > 0 and return the tokens
    public static ArrayList<String> QueryPreProcessing(String query) throws IOException {

        if (query == "") {
            System.out.println("The query is empty! \n");
            return null;
        }

        // do text processing on query
        String[] tokens = DocumentProcessing(query);

        // Transform tokens into an ArrayList<String>
        return new ArrayList<>(Arrays.asList(tokens));
    }


    private static void printResults(PriorityQueue<scoreDoc> priorityQueue, int k) {

        // print the results
        System.out.println("Results for query: ");
        System.out.println("--------------------------------------------------");

        // check if the priority queue is empty
        if (priorityQueue.isEmpty()) {
            System.out.println("No results found");
            return;
        }

        // print the results
        while (!priorityQueue.isEmpty() && k > 0) {
            scoreDoc scoreDoc = priorityQueue.poll();
            k--;
            //System.out.println("DocID: " + scoreDoc.getDocID() + " Score: " + scoreDoc.getScore());
            String pid = documentIndex.get(scoreDoc.getDocID()).getDocno(); // <- take docNo from documentIndex
            System.out.println("DocNo: " + pid + " Score: " + scoreDoc.getScore());

        }

        System.out.println("--------------------------------------------------");
    }



    private static void resetDataStructures() {
        //priorityQueue.clear();

        if (postingListQuery != null)
            postingListQuery.clear();

        if (hashMapScore != null)
            hashMapScore.clear();

        if (hashMapLength != null)
            hashMapLength.clear();

    }

    // use for testing
    public static int returnTopDoc(ArrayList<String> tokens, int k) throws IOException {

        int position = 0;

        // ArrayList of tokens removing duplicates
        ArrayList<String> tokensNoDuplicates = new ArrayList<>();
        for (String token : tokens) {
            if (!tokensNoDuplicates.contains(token)) {
                tokensNoDuplicates.add(token);
            }
        }

        // process each token of the query
        for (String token : tokensNoDuplicates) {

            // posting list initialization
            PostingList postingList = new PostingList(token);

            // obtain the posting list for the token
            postingList.getPostingList(token);

            //System.out.println(postingList);

            // check if the posting list is empty (the token is not in the vocabulary)
            if (postingList.getPostingList().size() == 0) {
                if (Configuration.isConjunctiveON()) {
                    resetDataStructures();
                    return 0; // no results
                }
                continue;
            }

            // add the posting list to the posting list with the tokens in the query
            postingListQuery.add(postingList);


            // add the size of the posting list to the hashmap
            hashMapLength.put(position, postingList.getPostingList().size());

            // increment the position
            position++;

        }

        // initialize a priority queue of scoreDoc objects
        PriorityQueue<scoreDoc> priorityQueue;

        // check if the posting list with the tokens in the query is empty
        if (postingListQuery.size() == 0) {
            resetDataStructures();
            return 0; // no results
        }

        // check the type of configuration
        if (Configuration.isConjunctiveON()) {
            // conjunctive configuration
            priorityQueue = utils.conjunctive(k);
        } else {
            // disjunctive configuration
            priorityQueue = utils.disjunctive(k);
        }

        // reset the data structures
        resetDataStructures();

        scoreDoc scoreDoc = priorityQueue.poll();
        if (scoreDoc == null)
            return 0; // no results
        else
            return scoreDoc.getDocID();
    }

    // return PriorityQueue<scoreDoc> for testing
    public static PriorityQueue<scoreDoc> returnPriorityQueue(ArrayList<String> tokens, int k) throws IOException {
        int position = 0;

        // ArrayList of tokens removing duplicates
        ArrayList<String> tokensNoDuplicates = new ArrayList<>();
        for (String token : tokens) {
            if (!tokensNoDuplicates.contains(token)) {
                tokensNoDuplicates.add(token);
            }
        }

        // process each token of the query
        for (String token : tokensNoDuplicates) {

            // posting list initialization
            PostingList postingList = new PostingList(token);

            // obtain the posting list for the token
            postingList.getPostingList(token);

            //System.out.println(postingList);

            // check if the posting list is empty (the token is not in the vocabulary)
            if (postingList.getPostingList().size() == 0) {
                if (Configuration.isConjunctiveON()) {
                    resetDataStructures();
                    return null; // no results
                }
                continue;
            }

            // add the posting list to the posting list with the tokens in the query
            postingListQuery.add(postingList);

            // add the size of the posting list to the hashmap
            hashMapLength.put(position, postingList.getPostingList().size());

            // increment the position
            position++;

        }

        // initialize a priority queue of scoreDoc objects
        PriorityQueue<scoreDoc> priorityQueue;

        // check if the posting list with the tokens in the query is empty
        if (postingListQuery.size() == 0) {
            resetDataStructures();
            return null; // no results
        }

        // check the type of configuration
        if (Configuration.isConjunctiveON()) {
            // conjunctive configuration
            priorityQueue = utils.conjunctive(k);
        } else {
            // disjunctive configuration
            priorityQueue = utils.disjunctive(k);
        }

        // reset the data structures
        resetDataStructures();

        return priorityQueue;
    }
}