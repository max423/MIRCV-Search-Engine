package it.unipi.dii.aide.mircv.models;

public class VocabularyElem {
    private String term;
    // number of documents in which the term appears
    private int DocFreq;
    // number of occurrences of the term in the collection
    private int CollFreq;

    // used only in the indexer
    private int lastDocIdInserted;

    public VocabularyElem(String term, int docFreq, int collFreq) {
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

    public int getLastDocIdInserted() {
        return lastDocIdInserted;
    }

    public void setLastDocIdInserted(int lastDocIdInserted) {
        this.lastDocIdInserted = lastDocIdInserted;
    }

    public void incDocFreq() {
        this.DocFreq++;
    }
    public void updateCollFreq(int AnotherCollFreq) {
        this.CollFreq += AnotherCollFreq;
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
