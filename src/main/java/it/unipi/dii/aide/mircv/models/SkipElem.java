package it.unipi.dii.aide.mircv.models;

import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

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

    // read skip element from disk
    public void readFromDisk(FileChannel skipRaf, long skipAddress) throws IOException {
        // allocate the size of the skip element
        ByteBuffer skipBuffer= ByteBuffer.allocate(4+8+4+8+4);

        // start position
        skipRaf.position(skipAddress);

        // read the skip element
        while (skipBuffer.hasRemaining()) {
            skipRaf.read(skipBuffer);
        }

        // reset the buffer
        skipBuffer.rewind();
        // get skip info
        this.setDocID(skipBuffer.getInt());
        this.setOffsetDocID(skipBuffer.getLong());
        this.setBlockDocIDLen(skipBuffer.getInt());
        this.setOffsetFreq(skipBuffer.getLong());
        this.setBlockFreqLen(skipBuffer.getInt());
    }

    private void setBlockFreqLen(int blockFreqLen) {
        this.blockFreqLen = blockFreqLen;
    }

    private void setOffsetFreq(long offsetFreq) {
        this.offsetFreq = offsetFreq;
    }

    private void setBlockDocIDLen(int blockDocIDLen) {
        this.blockDocIDLen = blockDocIDLen;
    }

    private void setOffsetDocID(long offsetDocID) {
        this.offsetDocID = offsetDocID;
    }
}
