package it.unipi.dii.aide.mircv.query;

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
}
