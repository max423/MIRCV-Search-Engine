package it.unipi.dii.aide.mircv.query;

import it.unipi.dii.aide.mircv.models.PostingList;

import java.io.IOException;
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
            PostingList postingList = new PostingList();
            postingList.getPostingList().clear();
            postingList.setTerm(token);

            // obtain the posting list for the token
            postingList = PostingList.getPostingList(token);


        }
    }

}
