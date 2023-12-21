package it.unipi.dii.aide.mircv.models;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class PostingList {
    private String term;
    private final ArrayList<Posting> postingList;

    public PostingList(String term) {
        this.term = term;
        this.postingList = new ArrayList<>();
    }

    public PostingList(String term, Posting posting) {
        this.term = term;
        this.postingList = new ArrayList<>();
        postingList.add(posting);
    }

    public PostingList(String term, ArrayList<Posting> postingList) {
        this.term = term;
        this.postingList = postingList;
    }

    // load the posting list given the term
    public void getPostingList(String term) throws IOException {

        // initialize the random access file
        RandomAccessFile docId_RAF = new RandomAccessFile(FileUtils.Path_FinalDocId, "r");
        RandomAccessFile termFreq_RAF = new RandomAccessFile(FileUtils.Path_FinalTermFreq, "r");

        // initialize a variable to store the vocabularyElem
        //HashMap<Integer, DocumentIndexElem> documentIndex = new HashMap<>();
        HashMap<String, VocabularyElem> vocabulary = new HashMap<>();
        VocabularyElem vocabularyElem = vocabulary.get(term);

        // check if the term is in the vocabulary
        if (vocabularyElem == null) {
            System.out.println("Term "+ term + " not found in the vocabulary");
            return;
        }

        // read skipping info
        RandomAccessFile skip_RAF = new RandomAccessFile(FileUtils.Path_Skipping, "r");

        // get the number of blocks of term
        ArrayList<SkipElem> blocks;
        if (vocabularyElem.getSkipLen() == 0){

            // initialize a new ArrayList of blocks
            blocks = new ArrayList<>();

        }



    }

    public void addPosting(Posting posting) {
        postingList.add(posting);
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


    public void writeToDisk(FileChannel channelDocID, FileChannel channelTermFreq) throws IOException {

        ByteBuffer docsByteBuffer;
        ByteBuffer freqsByteBuffer;

        channelDocID.position(channelDocID.size());
        channelTermFreq.position(channelTermFreq.size());

        docsByteBuffer = ByteBuffer.allocate(this.postingList.size() * 4);
        freqsByteBuffer = ByteBuffer.allocate(this.postingList.size() * 4);

        // write docIds and termFreqs to the buffers
        for (Posting posting : this.postingList) {
            docsByteBuffer.putInt(posting.getDocID());
            freqsByteBuffer.putInt(posting.getTermFreq());
        }

        docsByteBuffer = ByteBuffer.wrap(docsByteBuffer.array());
        freqsByteBuffer = ByteBuffer.wrap(freqsByteBuffer.array());

        // write buffers to the channels
        while (docsByteBuffer.hasRemaining())
            channelDocID.write(docsByteBuffer);

        while (freqsByteBuffer.hasRemaining())
            channelTermFreq.write(freqsByteBuffer);


        // TO DO : check if this is necessary
        // 1) skipElement

    }

    public void setTerm(String term) {
        this.term = term;
    }
}
