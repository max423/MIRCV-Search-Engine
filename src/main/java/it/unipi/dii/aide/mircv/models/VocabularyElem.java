package it.unipi.dii.aide.mircv.models;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

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

    // length of the skip information
    private int skipLen;


    public VocabularyElem(String term, int docFreq, int collFreq) {
        this.term = term;
        this.DocFreq = docFreq;
        this.CollFreq = collFreq;
    }

    public VocabularyElem() {

    }

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
        this.CollFreq += len;
    }

    public void incDocLen(int len) {
        this.docIdsLen += len;
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
                '}';
    }

    public void writeToDisk(FileChannel channelVoc) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(20 + 4 + 4 + 4 + 8 + 8 + 4 + 4); // stringa paddata 20 caratteri

        channelVoc.position(channelVoc.size());
        CharBuffer charBuffer = CharBuffer.allocate(20);

        // writing into buffer
        for (int i = 0; i < this.term.length(); i++)
            charBuffer.put(i, this.term.charAt(i));

        buffer.put(StandardCharsets.UTF_8.encode(charBuffer));
        buffer.putInt(this.DocFreq);
        buffer.putInt(this.CollFreq);
        buffer.putInt(this.lastDocIdInserted); // TODO Simo
        buffer.putLong(this.docIdsOffset);
        buffer.putLong(this.termFreqOffset);
        buffer.putInt(this.docIdsLen);
        buffer.putInt(this.termFreqLen);

        buffer = ByteBuffer.wrap(buffer.array());

        // writing into channel
        while (buffer.hasRemaining())
            channelVoc.write(buffer);

    }

    public int getSkipLen() {
        return skipLen;
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
            buffer = ByteBuffer.allocate(4 + 4 + 4 + 8 + 8 + 4 + 4);

            while (buffer.hasRemaining())
                channel.read(buffer);

            buffer.rewind(); // reset the buffer position to 0
            this.term = term;
            this.DocFreq = buffer.getInt();
            this.CollFreq = buffer.getInt();
            this.lastDocIdInserted = buffer.getInt();       // TODO SIMO
            this.docIdsOffset = buffer.getLong();
            this.termFreqOffset = buffer.getLong();
            this.docIdsLen = buffer.getInt();
            this.termFreqLen =  buffer.getInt();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
