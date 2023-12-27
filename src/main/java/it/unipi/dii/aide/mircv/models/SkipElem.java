package it.unipi.dii.aide.mircv.models;

public class SkipElem { 

    // max docID of the block
    private int docID;

    // length of block containig docIDs
    private int blockDocIDLen;

    // offset where postings start
    private long offsetDocID;

    // length of block containing freq
    private int blockFreqLen;

    // offset where freq start
    private long offsetFreq;

    public SkipElem(){
        docID = 0;
        blockDocIDLen = 0;
        offsetDocID = 0;
        blockFreqLen = 0;
        offsetFreq = 0;
    }

    public SkipElem(int docID, long offsetDocID, int blockDocIDLen, long offsetFreq, int blockFreqLen) {
        this.docID = docID;
        this.offsetDocID = offsetDocID;
        this.blockDocIDLen = blockDocIDLen;
        this.offsetFreq = offsetFreq;
        this.blockFreqLen = blockFreqLen;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }
}
