package it.unipi.dii.aide.mircv.query;

import java.util.*;

import it.unipi.dii.aide.mircv.models.CollectionStatistics;
import it.unipi.dii.aide.mircv.models.Configuration;
import it.unipi.dii.aide.mircv.models.Posting;
import it.unipi.dii.aide.mircv.models.PostingList;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.IOException;

public class utils {

    // get the min docID from the posting list
    public static int getMinDocID() {
        // get the first docID
        int minDocID = queryHandler.orderedPostingList.get(0).getCurrentPostingList().getDocID();
        int docIDcmp;


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
    




    // conjunctive query
    public static PriorityQueue<scoreDoc> conjunctiveQuery(int k) throws IOException {

        // initialize the priority queue with score in descending order
        PriorityQueue<scoreDoc> scoreDocsDecreasing = new PriorityQueue<>(k, new scoreDocComparator());

        // initialize the priority queue with score in increasing order
        PriorityQueue<scoreDoc> scoreDocsIncreasing = new PriorityQueue<>(k, new scoreDocComparatorIncreasing());

        // initialize the score

        // initialize the score
        double score = 0;

        // used to check if already processed
        boolean firstIteration = true;

        // current docID
        int currentDocID;

        // order the posting list
        queryHandler.hashMapLength = (HashMap<Integer, Integer>) sortByValue(queryHandler.hashMapLength);

        // create a lisr of ordered posting list
        for (Map.Entry<Integer, Integer> entry : queryHandler.hashMapLength.entrySet()) {
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            queryHandler.orderedPostingList.add(queryHandler.postingListQuery.get(entry.getKey()));
        }

        // process all the docIDs in the first posting list
        while (true){
            // Retrieving the next docID among those not yet processed in all posting lists
            do {
                // check if it is the first iteration
                if (!firstIteration)
                    // get the next
                    queryHandler.orderedPostingList.get(0).nextPosting();

                // update variable
                firstIteration = false;

                if (queryHandler.orderedPostingList.get(0).getCurrentPostingList() == null) {
                    // no more docID to process
                    return scoreDocsDecreasing;
                }

                // candidate docID
                currentDocID = queryHandler.orderedPostingList.get(0).getCurrentPostingList().getDocID();

            } while (!controlPostingLists(currentDocID));

            // reset the score
            score = 0;

            for (PostingList postingList : queryHandler.orderedPostingList) {

                // update the score
                if (Configuration.isScoreON()) {
                    // BM25
                    score += BM25(postingList.getTerm(), postingList.getCurrentPostingList(), 1.2, 0.75);
                } else {
                    // TF-IDF
                    score += TFIDF(postingList.getTerm(), postingList.getCurrentPostingList());
                }
            }

            // update the priority queue
            if (scoreDocsDecreasing.size() < k) {
                // add the scoreDoc to the priority queue
                scoreDocsDecreasing.add(new scoreDoc(currentDocID, score));
                scoreDocsIncreasing.add(new scoreDoc(currentDocID, score));
            } else {
                // check if the score is greater than the minimum score in the priority queue
                if (scoreDocsIncreasing.peek().getScore() < score) {
                    // remove the minimum score
                    scoreDocsIncreasing.poll();
                    scoreDocsDecreasing.poll();

                    // add the scoreDoc to the priority queue
                    scoreDocsDecreasing.add(new scoreDoc(currentDocID, score));
                    scoreDocsIncreasing.add(new scoreDoc(currentDocID, score));
                }
            }

            // check if no more docID to process
            if (queryHandler.orderedPostingList.get(0).getCurrentPostingList() == null) {
                return scoreDocsDecreasing;
            }
        }

    }

    private static boolean controlPostingLists(int currentDocID) throws IOException {
        // check if the current docID is present in all the posting lists
        for (PostingList postingList : queryHandler.orderedPostingList) {

            // check if the current docID is present in the posting list
            postingList.nextGEQ(currentDocID);
            if (postingList.getCurrentPostingList() == null)
                return false;
        }

        // the current docID is present in all the posting lists
        return true;
    }
}
