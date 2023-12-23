package it.unipi.dii.aide.mircv.query;

import it.unipi.dii.aide.mircv.models.CollectionStatistics;

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
}
