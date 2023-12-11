package it.unipi.dii.aide.mircv.models;

public class Vocabulary {
    private String term;
    // number of documents in which the term appears
    private int DocFreq;
    // number of occurrences of the term in the collection
    private int CollFreq;

    public Vocabulary(String term, int docFreq, int collFreq) {
        this.term = term;
        DocFreq = docFreq;
        CollFreq = collFreq;
    }

    public String getTerm() {
        return term;
    }

    public int getDocFreq() {
        return DocFreq;
    }

    public int getCollFreq() {
        return CollFreq;
    }

    @Override
    public String toString() {
        return "Vocabulary{" +
                "term='" + term + '\'' +
                ", DocFreq=" + DocFreq +
                ", CollFreq=" + CollFreq +
                '}';
    }


}
