package it.unipi.dii.aide.mircv.models;

import it.unipi.dii.aide.mircv.indexer.Spimi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import static it.unipi.dii.aide.mircv.indexer.Spimi.lastDocId;

public class VocabularyElem {
    private String term;

    // number of documents in which the term appears
    private int DocFreq;

    // number of occurrences of the term in the collection
    private int CollFreq;

    // used only in the indexer
    private int lastDocIdInserted;

    // offset of the first docId in docIds posting list
    protected long docIdsOffset;

    // offset of the first Termfrequency in docIds posting list
    protected long termFreqOffset;

    // number of bytes of the docIds posting list
    protected int docIdsLen;

    // number of bytes of the termFreqs posting list
    protected int termFreqLen;

    // inverse document frequency
    private double idf;

//    // max BM25
//    private double maxBM25;
//
//    // max TFIDF
//    private double maxTFIDF;


//    public void setMaxBM25(double maxBM25) {
//        this.maxBM25 = maxBM25;
//    }



    public VocabularyElem(String term, int docFreq, int collFreq) {
        this.term = term;
        this.DocFreq = docFreq;
        this.CollFreq = collFreq;
    }

    public VocabularyElem() {}

    public VocabularyElem(String term) {
        this.term = term;
        this.DocFreq = 0;
        this.CollFreq = 0;
    }

    // full constructor
    public VocabularyElem(String term, int docFreq, int collFreq, int lastDocIdInserted, long docIdsOffset, long termFreqOffset, int docIdsLen, int termFreqLen) {
        this.term = term;
        this.DocFreq = docFreq;
        this.CollFreq = collFreq;
        this.lastDocIdInserted = lastDocIdInserted;
        this.docIdsOffset = docIdsOffset;
        this.termFreqOffset = termFreqOffset;
        this.docIdsLen = docIdsLen;
        this.termFreqLen = termFreqLen;
    }

    public VocabularyElem(String term, int df, int cf, long docIdsOffset, long termFreqOffset, int docIdsLen, int termFreqLen) {
        this.term = term;
        this.DocFreq = df;
        this.CollFreq = cf;
        this.docIdsOffset = docIdsOffset;
        this.termFreqOffset = termFreqOffset;
        this.docIdsLen = docIdsLen;
        this.termFreqLen = termFreqLen;
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

    public int getDocIdsLen() {
        return docIdsLen;
    }



    public void setDocIdsLen(int docIdsLen) {
        this.docIdsLen = docIdsLen;
    }

    public int getTermFreqLen() {
        return termFreqLen;
    }

    public void setTermFreqLen(int termFreqLen) {
        this.termFreqLen = termFreqLen;
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

    public long getDocIdsOffset() {
        return docIdsOffset;
    }

    public void setDocIdsOffset(long docIdsOffset) {
        this.docIdsOffset = docIdsOffset;
    }

    public long getTermFreqOffset() {
        return termFreqOffset;
    }

    public void setTermFreqOffset(long termFreqOffset) {
        this.termFreqOffset = termFreqOffset;
    }


    public void incFreqLen(int len) {
        this.termFreqLen += len;
    }


    public void incDocLen(int len) {
        this.docIdsLen += len;
    }

    public void incDocFreq( int df) {
        this.DocFreq += df;
    }

    public void incCollFreq( int cf) {
        this.CollFreq += cf;
    }




    @Override
    public String toString() {
        return "Vocabulary{" +
                "term='" + term + '\'' +
                ", DocFreq=" + DocFreq +
                ", CollFreq=" + CollFreq +
                ", lastDocIdInserted=" + lastDocIdInserted +
                ", docIdsOffset=" + docIdsOffset +
                ", termFreqOffset=" + termFreqOffset +
                ", docIdsLen=" + docIdsLen +
                ", termFreqLen=" + termFreqLen +
                ", idf=" + idf +
                '}';
    }

    public void writeToDisk(FileChannel channelVoc) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(20 + 4 + 4 + 8 + 8 + 4 + 4  + 8  ); // stringa paddata 20 caratteri

        channelVoc.position(channelVoc.size());
        CharBuffer charBuffer = CharBuffer.allocate(20);

        // writing into buffer
        for (int i = 0; i < this.term.length(); i++)
            charBuffer.put(i, this.term.charAt(i));

        buffer.put(StandardCharsets.UTF_8.encode(charBuffer));
        buffer.putInt(this.DocFreq);
        buffer.putInt(this.CollFreq);
        //buffer.putInt(this.lastDocIdInserted); // TODO Simo
        buffer.putLong(this.docIdsOffset);
        buffer.putLong(this.termFreqOffset);
        buffer.putInt(this.docIdsLen);
        buffer.putInt(this.termFreqLen);
        buffer.putDouble(this.idf);

        buffer = ByteBuffer.wrap(buffer.array());

        // writing into channel
        while (buffer.hasRemaining())
            channelVoc.write(buffer);

        // testing
        if(Configuration.isTesting()) {
            System.out.println("VocabularyElem written on disk: " + this);
        }


    }


    public void readFromDisk(FileChannel channel, long currentOffset) throws IOException {
        try {
            // creating ByteBuffer for reading term
            ByteBuffer buffer = ByteBuffer.allocate(20);
            channel.position(currentOffset);

            while (buffer.hasRemaining())
                channel.read(buffer);

            String term = new String(buffer.array(), StandardCharsets.UTF_8).trim();

            // creating ByteBuffer for reading df, cf, lastDocIdInserted, docIdsOffset, termFreqOffset, docIdsLen, termFreqLen
            buffer = ByteBuffer.allocate(4 + 4 + 8 + 8 + 4 + 4  + 8 );

//            int skipLen;
//            long skipOffset;
//            double idf;
//            double maxBM25;
//            double maxTFIDF;
//            8 + 8 + 8 = 24

            while (buffer.hasRemaining())
                channel.read(buffer);

            buffer.rewind(); // reset the buffer position to 0
            this.term = term;
            this.DocFreq = buffer.getInt();
            this.CollFreq = buffer.getInt();
//            this.lastDocIdInserted = buffer.getInt();       // TODO SIMO
            this.docIdsOffset = buffer.getLong();
            this.termFreqOffset = buffer.getLong();
            this.docIdsLen = buffer.getInt();
            this.termFreqLen =  buffer.getInt();
            this.idf = buffer.getDouble();
//            this.maxBM25 = buffer.getDouble();
//            this.maxTFIDF = buffer.getDouble();


        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getIdf() {
        return idf;
    }

    public void setIdf(double idf) {
        this.idf = idf;
    }


//    public void setMaxTFIDF(double maxTFIDF) {
//        this.maxTFIDF = maxTFIDF;
//    }
//
//    public double getMaxBM25() {
//        return maxBM25;
//    }
//
//    public double getMaxTFIDF() {
//        return maxTFIDF;
//    }

    public void computeIDF() {
        // lastDocId at the end is the number of documents in the collection
        System.out.println("lastDocId: " + lastDocId + " DocFreq: " + this.DocFreq);
        this.idf = Math.log10(lastDocId / (double)this.DocFreq);
    }

//    public void setMaxTFIDF(int maxTermFreq){
//        this.maxTFIDF = (1 + Math.log10(maxTermFreq)) * this.idf;
//    }


//    public void computeBM25andTFIDF(double avgDocLen, PostingList postingList) {
//
//        double b = 0.75;
//        double k1 = 1.2;
//
//        double current_BM25 = 0;
//        int dl = 0;
//        int maxTermFreq = 0;
//
//        for (Posting p : postingList.getPostingList()) {
//            dl = Spimi.docLen.get((int) (p.getDocID() - 1));         // doc len
//
//            current_BM25 = (p.getTermFreq() / ((k1 - b) + b * dl / avgDocLen) + p.getTermFreq()) * this.idf;
//
//            if (current_BM25 > this.getMaxBM25())
//                this.setMaxBM25(current_BM25);
//
//            // search for max term freq
//            if (p.getTermFreq() > maxTermFreq)
//                maxTermFreq = p.getTermFreq();
//        }
//
//        this.maxTFIDF = (1 + Math.log10(maxTermFreq)) * this.idf;
//        //System.out.println("Term: " + this.term + " maxTFIDF: " + this.maxTFIDF  + "AND "+ this.idf);
//    }
}
