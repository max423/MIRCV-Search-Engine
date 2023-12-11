package it.unipi.dii.aide.mircv.models;

public class CollectionStatistics {
    // number of documents
    private long docCount;
    // total number of terms
    private long totalLength;

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

    @Override
    public String toString() {
        return "CollectionStatistics{" +
                "docCount=" + docCount +
                ", totalLength=" + totalLength +
                '}';
    }
}
