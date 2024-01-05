package it.unipi.dii.aide.mircv.query;

import it.unipi.dii.aide.mircv.models.CollectionStatistics;
import it.unipi.dii.aide.mircv.models.Configuration;
import it.unipi.dii.aide.mircv.models.Posting;
import it.unipi.dii.aide.mircv.models.PostingList;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.IOException;
import java.util.*;

import static it.unipi.dii.aide.mircv.utils.FileUtils.collectionStatistics;

public class utils {

    // get the min docID from the posting list
    public static int getMinDocID() {
        int minDocID = Integer.MAX_VALUE;
        int docIDcmp;

        for (PostingList postingList : queryHandler.postingListQuery) {
            docIDcmp = postingList.getPostingList().get(0).getDocID();
            if (docIDcmp < minDocID) {
                minDocID = docIDcmp;
            }
        }
        return minDocID;
    }

    // conjunctive query
    public static PriorityQueue<scoreDoc> conjunctive(int k) throws IOException {

        // initialize the priority queue with score in descending order
        PriorityQueue<scoreDoc> scoreDocsDecreasing = new PriorityQueue<>(k, new scoreDocComparator());

        // initialize the priority queue with score in increasing order
        PriorityQueue<scoreDoc> scoreDocsIncreasing = new PriorityQueue<>(k, new scoreDocComparatorIncreasing());

        // initialize the score
        double score = 0;

        // next docID
        int nextDocID;

        // current docID
        int currentDocID;

        // get the min docID
        currentDocID = getMinDocID();

        // variable used to check if the docID is present in all the posting lists
        int present;

        // check all the docIDs in the posting lists
        while (true) {

            // reset the score
            score = 0;
            // update the next docID
            //nextDocID = collectionStatistics.getDocCount()-1;
            nextDocID = collectionStatistics.getDocCount();
            // update the presence
            present = 0;

            // iterate over the posting lists of the query
            for (PostingList postingList : queryHandler.postingListQuery) {


                System.out.println("----------");
                System.out.println("current postinglist "+postingList);
                System.out.println("Current Docid "+ currentDocID);
                System.out.println("Next Docid "+ nextDocID);
                System.out.println("current PIT "+postingList.getCurrentPostingList());

                // check if the posting list is empty
                if (postingList.getCurrentPostingList() == null) {
                    continue;
                }

                // check if the docID is the same
                if (postingList.getCurrentPostingList().getDocID() == currentDocID) {
                    // update the flag
                    present+=1;
                    // update the score
                    if (Configuration.isScoreON()) {
                        // BM25
                        score += BM25(postingList.getTerm(), postingList.getCurrentPostingList(), 1.2, 0.75);
                    } else {
                        // TF-IDF
                        score += TFIDF(postingList.getTerm(), postingList.getCurrentPostingList());
                    }

                    // update the posting list
                    postingList.nextPosting(postingList.getTerm());
                }
                System.out.println("Present value "+ present);


                // check if the posting list is empty
                if (postingList.getCurrentPostingList() == null) {
                    System.out.println("posting list is empty");
                    continue;
                }


                // update the next docID
                if (postingList.getCurrentPostingList().getDocID() < nextDocID) {
                    nextDocID = postingList.getCurrentPostingList().getDocID();
                }
            }

            // check if the docID is present in all the posting lists
            if (present == queryHandler.postingListQuery.size()) {
                System.out.println("Update the score of Current Docid "+ currentDocID);
                // update the priority queue
                if (scoreDocsDecreasing.size() < k) {
                    // add the scoreDoc to the priority queue
                    scoreDocsDecreasing.add(new scoreDoc(currentDocID, score));
                    scoreDocsIncreasing.add(new scoreDoc(currentDocID, score));
                } else {
                    // check if the score is greater than the minimum score in the priority queue
                    if (scoreDocsIncreasing.peek().getScore() < score) {
                        // add the scoreDoc to the priority queue
                        scoreDocsDecreasing.add(new scoreDoc(currentDocID, score));
                        scoreDocsIncreasing.add(new scoreDoc(currentDocID, score));

                        // remove the minimum score
                        scoreDocsDecreasing.remove(scoreDocsIncreasing.poll());
                    }
                }
            }

            // check if no more docID to process
            //if (currentDocID == nextDocID || nextDocID == collectionStatistics.getDocCount()-1){
            if (currentDocID == nextDocID || nextDocID == collectionStatistics.getDocCount()){
                return scoreDocsDecreasing;
            }

            // update the current docID
            currentDocID = nextDocID;
        }

    }

    // disjunctive query
    public static PriorityQueue<scoreDoc> disjunctive(int k) throws IOException {

        // initialize the priority queue with score in descending order
        PriorityQueue<scoreDoc> scoreDocsDecreasing = new PriorityQueue<>(k, new scoreDocComparator());

        // initialize the priority queue with score in increasing order
        PriorityQueue<scoreDoc> scoreDocsIncreasing = new PriorityQueue<>(k, new scoreDocComparatorIncreasing());

        // initialize the score
        double score = 0;

        // next docID
        int nextDocID;

        // current docID
        int currentDocID;

        // get the min docID
        currentDocID = getMinDocID();

        // check all the docIDs in the posting lists
        while (true) {
            // reset the score
            score = 0;
            // update the next docID
            //nextDocID = collectionStatistics.getDocCount()-1;
            nextDocID = collectionStatistics.getDocCount();



            // iterate over the posting lists of the query
            for (PostingList postingList : queryHandler.postingListQuery) {

                System.out.println("----------");
                System.out.println("current postinglist "+postingList);
                System.out.println("Current Docid "+ currentDocID);
                System.out.println("Next Docid "+ nextDocID);
                System.out.println("current Posting "+postingList.getCurrentPostingList());

                // check if the posting list is empty
                if (postingList.getCurrentPostingList() == null) {
                    continue;
                }

                // check if the docID is the same
                if (postingList.getCurrentPostingList().getDocID() == currentDocID) {

                    // update the score
                    if (Configuration.isScoreON()) {
                        // BM25
                        score += BM25(postingList.getTerm(), postingList.getCurrentPostingList(), 1.2, 0.75);
                    } else {
                        // TF-IDF
                        score += TFIDF(postingList.getTerm(), postingList.getCurrentPostingList());
                    }

                    // todo capire come ottenere il prossimo posting
                    // next posting
                    postingList.nextPosting(postingList.getTerm());
                    // print current posting
                    System.out.println("current Posting after next "+postingList.getCurrentPostingList());

                }


                // check if the posting list is empty
                if (postingList.getCurrentPostingList() == null) {
                    System.out.println("posting list is empty");
                    continue;
                }

                // update the next docID
                if (postingList.getCurrentPostingList().getDocID() < nextDocID) {
                    nextDocID = postingList.getCurrentPostingList().getDocID();
                    System.out.println("Next Docid "+ nextDocID);
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
                    // add the scoreDoc to the priority queue
                    scoreDocsDecreasing.add(new scoreDoc(currentDocID, score));
                    scoreDocsIncreasing.add(new scoreDoc(currentDocID, score));

                    // remove the minimum score
                    scoreDocsDecreasing.remove(scoreDocsIncreasing.poll());
                }
            }

            // check if no more docID to process
            //if (currentDocID == nextDocID || nextDocID == collectionStatistics.getDocCount()-1) {
            if (currentDocID == nextDocID || nextDocID == collectionStatistics.getDocCount()) {
            //if (currentDocID == nextDocID) {
                return scoreDocsDecreasing;
            }

            // update the current docID
            currentDocID = nextDocID;
            System.out.println("Current Docid "+ currentDocID);

        }

    }



    private static double TFIDF(String term, Posting currentPostingList) {
        // term frequency weight
        double tf = currentPostingList.getTermFreq();
        double tfWeight = 1 + Math.log10(tf);

        // inverse document frequency
        double idf = FileUtils.vocabulary.get(term).getIdf();
        System.out.println("IDF for "+ term + " is "+idf);

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

    /*public static PriorityQueue<scoreDoc> maxScore(int k) throws IOException {

        // initialize the priority queue with score in descending order
        PriorityQueue<scoreDoc> scoreDocsDecreasing = new PriorityQueue<>(k, new scoreDocComparator());

        // initialize the priority queue with score in increasing order
        PriorityQueue<scoreDoc> scoreDocsIncreasing = new PriorityQueue<>(k, new scoreDocComparatorIncreasing());

        // values of an upper bound calculated based on the highest scores associated with the query terms
        ArrayList<Double> upperBound = new ArrayList<>();
        upperBound.add(queryHandler.maxScoreOrder.get(0));
        for (int i = 1; i < queryHandler.maxScoreOrder.size(); i++) {
            upperBound.add(upperBound.get(i - 1) + queryHandler.maxScoreOrder.get(i));
        }

        // initialize the score
        double score = 0;
        // next docID
        int nextDocID;
        // divide the query to principal and not principal list
        int listIndex = 0;
        // lower bound
        double lowerBound = 0;

        int currentDocID = getMinDocID();

        // there are still items in the list of the query to be processed and there are documents to be processed
        while (listIndex < queryHandler.orderedPostingList.size() && currentDocID >= 0) {

            // reset the score
            score = 0;

            // update the next docID
            nextDocID = CollectionStatistics.getDocCount();

            // principal list
            for (int i = listIndex; i < queryHandler.orderedPostingList.size(); i++) {

                // check if the posting list is empty
                if (queryHandler.orderedPostingList.get(i).getCurrentPostingList() == null) {
                    continue;
                }

                // check if the docID is the same
                if (queryHandler.orderedPostingList.get(i).getCurrentPostingList().getDocID() == currentDocID) {
                    // update the score
                    if (Configuration.isScoreON()) {
                        // BM25
                        score += BM25(queryHandler.orderedPostingList.get(i).getTerm(), queryHandler.orderedPostingList.get(i).getCurrentPostingList(), 1.2, 0.75);
                    } else {
                        // TF-IDF
                        score += TFIDF(queryHandler.orderedPostingList.get(i).getTerm(), queryHandler.orderedPostingList.get(i).getCurrentPostingList());
                    }

                    // update the posting list
                    queryHandler.orderedPostingList.get(i).nextPosting();

                    // check if the posting list is empty
                    if (queryHandler.orderedPostingList.get(i).getCurrentPostingList() == null) {
                        continue;
                    }
                }

                // update the next docID
                if (queryHandler.orderedPostingList.get(i).getCurrentPostingList().getDocID() < nextDocID) {
                    nextDocID = queryHandler.orderedPostingList.get(i).getCurrentPostingList().getDocID();
                }
            }

            // not principal list
            for (int i = listIndex - 1; i > 0; i--) {

                // check if the posting list is empty
                if (queryHandler.orderedPostingList.get(i).getCurrentPostingList() == null) {
                    continue;
                }

                // check lower bound
                if (score + upperBound.get(i) < lowerBound) {
                    break;
                }

                queryHandler.orderedPostingList.get(i).nextGEQ(currentDocID);

                // check if the posting list is empty
                if (queryHandler.orderedPostingList.get(i).getCurrentPostingList() == null) {
                    continue;
                }

                // check if the docID is the same
                if (queryHandler.orderedPostingList.get(i).getCurrentPostingList().getDocID() == currentDocID) {
                    // update the score
                    if (Configuration.isScoreON()) {
                        // BM25
                        score += BM25(queryHandler.orderedPostingList.get(i).getTerm(), queryHandler.orderedPostingList.get(i).getCurrentPostingList(), 1.2, 0.75);
                    } else {
                        // TF-IDF
                        score += TFIDF(queryHandler.orderedPostingList.get(i).getTerm(), queryHandler.orderedPostingList.get(i).getCurrentPostingList());
                    }
                }
            }


            // list index update
            if (scoreDocsDecreasing.size() < k) {
                // add the scoreDoc to the priority queue
                scoreDocsDecreasing.add(new scoreDoc(currentDocID, score));
                scoreDocsIncreasing.add(new scoreDoc(currentDocID, score));
            } else {
                // check if the score is greater than the minimum score in the priority queue
                if (scoreDocsIncreasing.peek().getScore() < score) {
                    // add the scoreDoc to the priority queue
                    scoreDocsDecreasing.add(new scoreDoc(currentDocID, score));
                    scoreDocsIncreasing.add(new scoreDoc(currentDocID, score));

                    // remove the minimum score
                    scoreDocsDecreasing.remove(scoreDocsIncreasing.poll());
                }

                // update the lower bound
                lowerBound = scoreDocsIncreasing.peek().getScore();

                // update the list index
                while (listIndex < queryHandler.orderedPostingList.size() && upperBound.get(listIndex) < lowerBound) {
                    listIndex++;
                }
            }


            // check if it is the last docID
            if (nextDocID == CollectionStatistics.getDocCount()) {
                break;
            }

            // update the current docID
            currentDocID = nextDocID;
        }

        return scoreDocsDecreasing;
    }*/

    // conjunctive query
    /*public static PriorityQueue<scoreDoc> conjunctive(int k) throws IOException {

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

        // order the posting list : <position, length>
        queryHandler.hashMapLength = (HashMap<Integer, Integer>) sortByValue(queryHandler.hashMapLength);

        // create a list of ordered posting list
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
                    // add the scoreDoc to the priority queue
                    scoreDocsDecreasing.add(new scoreDoc(currentDocID, score));
                    scoreDocsIncreasing.add(new scoreDoc(currentDocID, score));

                    // remove the minimum score
                    scoreDocsDecreasing.remove(scoreDocsIncreasing.poll());
                }
            }

            // check if no more docID to process
            if (queryHandler.orderedPostingList.get(0).getCurrentPostingList() == null) {
                return scoreDocsDecreasing;
            }
        }

    }*/


    /*private static boolean controlPostingLists(int currentDocID) throws IOException {
        // check if the current docID is present in all the posting lists
        for (PostingList postingList : queryHandler.orderedPostingList) {

            // check if the current docID is present in the posting list
            //postingList.nextGEQ(currentDocID);
            postingList.nextPosting();
            if (postingList.getCurrentPostingList() == null || postingList.getCurrentPostingList().getDocID() > currentDocID)
                return false;
            else if (postingList.getCurrentPostingList().getDocID() == currentDocID)
                continue;
        }

        // the current docID is present in all the posting lists
        return true;
    }*/
}
