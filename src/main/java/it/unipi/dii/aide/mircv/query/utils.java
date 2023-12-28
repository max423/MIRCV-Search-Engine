package it.unipi.dii.aide.mircv.query;

import java.util.*;

import it.unipi.dii.aide.mircv.models.CollectionStatistics;
import it.unipi.dii.aide.mircv.models.Posting;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.IOException;

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


    //todo
    /*public static double updatePartialScore(double score, int pos) {
        // todo fare classe per gestire i flag?
        // check if flag is set
        if (Flags.checkScore() == 1) {
            // BM25
            score += BM25(queryHandler.orderedPostingList.get(pos).getTerm(), queryHandler.orderedPostingList.get(pos).getCurrentPostingList(), 1.2, 0.75);
        } else {
            // TF-IDF
            score += TFIDF(queryHandler.orderedPostingList.get(pos).getTerm(), queryHandler.orderedPostingList.get(pos).getCurrentPostingList());
        }
    }*/

    private static double TFIDF(String term, Posting currentPostingList) {
        // term frequency weight
        double tf = currentPostingList.getTermFreq();
        double tfWeight = 1 + Math.log10(tf);

        // inverse document frequency
        double idf = FileUtils.vocabulary.get(term).getIdf();

        double TFIDF = tfWeight * idf;

        return TFIDF;
    }

    private static double BM25(String term, Posting currentPostingList, double k, double b) {
        // get term frequency of the posting list
        int tf = currentPostingList.getTermFreq();

        // get the IDF
        double idf = FileUtils.vocabulary.get(term).getIdf();

        // average document length
        double avgDocLen = CollectionStatistics.getAvgDocLen();

        double BM25 = idf * (tf / (k * ((1 - b) + (b * (FileUtils.documentIndex.get(currentPostingList.getDocID()).getLength() / avgDocLen))) + tf));

        return BM25;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {

        // create a list of the map entries
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());

        // sort the list
        list.sort(Map.Entry.comparingByValue());

        // create a new linked hashmap
        Map<K, V> result = new LinkedHashMap<>();

        // put the sorted list in the linked hashmap
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    // todo MAXSCORE algorithm
    /*public static PriorityQueue<scoreDoc> maxScore(int k) throws IOException {
        // initialize the priority queue with score in descending order
        PriorityQueue<scoreDoc> scoreDocs = new PriorityQueue<>(k, new scoreDocComparator());

    }*/
    


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

    // todo conjunctive query
    /*public static PriorityQueue<scoreDoc> conjunctiveQuery(int k) throws IOException {

        // initialize the priority queue with score in descending order
        PriorityQueue<scoreDoc> scoreDocs = new PriorityQueue<>(k, new scoreDocComparator());

        // initialize the priority queue with score in increasing order
        PriorityQueue<scoreDoc> scoreDocsIncreasing = new PriorityQueue<>(k, new scoreDocComparatorIncreasing());

        // initialize the score

        // initialize the score
        double score = 0;

        // position (current docID)
        int pos;



    }*/
}
