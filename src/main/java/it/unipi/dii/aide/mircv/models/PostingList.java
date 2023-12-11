package it.unipi.dii.aide.mircv.models;
import java.util.ArrayList;

public class PostingList {
    private String term;
    private final ArrayList<Posting> postingList;

    public PostingList(String term, ArrayList<Posting> postingList) {
        this.term = term;
        this.postingList = postingList;
    }

    public String getTerm() {
        return term;
    }

    public ArrayList<Posting> getPostingList() {
        return postingList;
    }

    @Override
    public String toString() {
        return "PostingList{" +
                "term='" + term + '\'' +
                ", postingList=" + postingList +
                '}';
    }


}
