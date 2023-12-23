package it.unipi.dii.aide.mircv.models;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PostingList {
    private String term;
    private final ArrayList<Posting> postingList;

    private static SkipElem skipBlock;

    private Posting currentPostingList;

    public static Iterator<SkipElem> skipElemIterator = null;

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

    public void addPosting(Posting posting) {
        postingList.add(posting);
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
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

    public void readFromDisk( FileChannel channelDocID, FileChannel channelTermFreq, long offsetDocId , long offsetTermFreq, int docIdsLen, int termFreqLen) throws IOException {
        try {
            channelDocID.position(offsetDocId);
            channelTermFreq.position(offsetTermFreq);

            // creating ByteBuffer for reading docIds and termFreqs
            ByteBuffer bufferDocId = ByteBuffer.allocate(docIdsLen);
            ByteBuffer bufferTermFreq = ByteBuffer.allocate(termFreqLen);

            while (bufferDocId.hasRemaining())
                channelDocID.read(bufferDocId);

            while (bufferTermFreq.hasRemaining())
                channelTermFreq.read(bufferTermFreq);

            bufferDocId.rewind(); // reset the buffer position to 0
            bufferTermFreq.rewind(); // reset the buffer position to 0

            // reading docIds and termFreqs from buffer
            for (int i = 0; i < docIdsLen / 4; i++) {
                int docId = bufferDocId.getInt();
                int termFreq = bufferTermFreq.getInt();

                this.postingList.add(new Posting(docId, termFreq));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addPostingFromDisk(FileChannel channelDocID, FileChannel channelTermFreq, long docIdsOffset, long termFreqOffset, int docIdsLen, int termFreqLen) throws IOException{
        // devo aggiungere un posting alla posting list, usata nel merger per aggiungere i posting delle posting list parziali

        try {
            channelDocID.position(docIdsOffset);
            channelTermFreq.position(termFreqOffset);

            // creating ByteBuffer for reading docIds and termFreqs
            ByteBuffer bufferDocId = ByteBuffer.allocate(docIdsLen);
            ByteBuffer bufferTermFreq = ByteBuffer.allocate(termFreqLen);

            while (bufferDocId.hasRemaining())
                channelDocID.read(bufferDocId);

            while (bufferTermFreq.hasRemaining())
                channelTermFreq.read(bufferTermFreq);

            bufferDocId.rewind(); // reset the buffer position to 0
            bufferTermFreq.rewind(); // reset the buffer position to 0

            // reading docIds and termFreqs from buffer
            for (int i = 0; i < docIdsLen / 4; i++) {
                int docId = bufferDocId.getInt();
                int termFreq = bufferTermFreq.getInt();

                this.postingList.add(new Posting(docId, termFreq));     // mantengo i posting vecchi e aggiungo quelli nuovi
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
            // il termine ha una lista di posting più piccola di 1024, e quindi viene creato un unico blocco di skipping contenente l'intera lista di posting

            // initialize a new ArrayList of blocks
            blocks = new ArrayList<>();

            skipBlock= new SkipElem(0, vocabularyElem.getDocIdsOffset(), vocabularyElem.getDocIdsLen(), vocabularyElem.getTermFreqOffset(), vocabularyElem.getTermFreqLen());
            blocks.add(skipBlock);

            skipElemIterator = blocks.iterator();
            skipElemIterator.next();

            // todo fare lettura con e senza compressione
            //readPostingListFromDiskSkip(blocks.get(0), docId_RAF.getChannel(), termFreq_RAF.getChannel());

            skipBlock.setDocID(this.getPostingList().get(this.getPostingList().size()-1).getDocID());

        }
        else{
            //vengono recuperate le informazioni di skipping dal file
        }

    }

    public Posting getCurrentPostingList() {
        return currentPostingList;
    }
}
