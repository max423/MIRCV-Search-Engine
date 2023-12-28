package it.unipi.dii.aide.mircv.query;

public class scoreDoc {
    int docID;
    double score;

    public scoreDoc(int currentDocID, double score) {
        this.docID = currentDocID;
        this.score = score;
    }

    public int getDocID() {
        return docID;
    }

    public double getScore() {
        return score;
    }
}
