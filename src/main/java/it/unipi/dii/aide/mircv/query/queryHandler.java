package it.unipi.dii.aide.mircv.query;

import it.unipi.dii.aide.mircv.models.PostingList;
import it.unipi.dii.aide.mircv.models.Vocabulary;
import it.unipi.dii.aide.mircv.models.VocabularyElem;

import java.util.ArrayList;

// receive a query and return the top k (10 or 20) results
public class queryHandler {

    private static final Vocabulary vocabulary = Vocabulary.getInstance();
    // load the posting list of the query terms from disk
    /*public static ArrayList<PostingList> loadPostingLists(ArrayList<String> tokens, boolean conjunctive) {

        // ArrayList of tokens removing duplicates
        ArrayList<String> tokensNoDuplicates = new ArrayList<>();
        for (String token : tokens) {
            if (!tokensNoDuplicates.contains(token)) {
                tokensNoDuplicates.add(token);
            }
        }

        // for each token, load the posting list from disk
        ArrayList<PostingList> postingLists = new ArrayList<>();
        for (String token : tokensNoDuplicates) {
            postingLists.add(PostingList.loadPostingList(token));
        }


    }*/
}
