package it.unipi.dii.aide.mircv.models;

public class CollectionStatistics {
    // number of documents
    private long docCount;
    // total number of terms
    private long totalLength;


    public CollectionStatistics() {
        this.docCount = 0;
        this.totalLength = 0;
    }

    public CollectionStatistics(long docCount, long totalLength) {
        this.docCount = docCount;
        this.totalLength = totalLength;
    }

    public long getDocCount() {
        return docCount;
    }

    public long getTotalLength() {
        return totalLength;
    }

    // incremento numero di termini
    public void incrementTotalLength() {
        this.totalLength += 1;
    }

    // incremento numero di documenti
    public void incrementDocCount(int docCount) {
        this.docCount += docCount;
    }

    @Override
    public String toString() {
        return "CollectionStatistics{" +
                "docCount=" + docCount +
                ", totalLength=" + totalLength +
                '}';
    }
}
