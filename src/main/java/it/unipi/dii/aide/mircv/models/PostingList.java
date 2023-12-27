package it.unipi.dii.aide.mircv.models;
import it.unipi.dii.aide.mircv.compression.unary;
import it.unipi.dii.aide.mircv.compression.variableByte;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static it.unipi.dii.aide.mircv.compression.unary.readTermFreqCompressed;
import static it.unipi.dii.aide.mircv.compression.variableByte.readDocIdsCompressed;

public class PostingList {
    private String term;
    private final ArrayList<Posting> postingList;

    private static SkipElem skipBlock;

    private Posting currentPostingList;

    // max BM25 score
    private double BM25;

    // max TF-IDF score
    private double TFIDF;

    public static Iterator<SkipElem> skipElemIterator = null;
    public static Iterator<Posting> postingListIterator = null;

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

    public void readPostingListFromDisk(FileChannel channelDocID, FileChannel channelTermFreq, SkipElem skip) throws IOException {
        // intialize one buffer for docIds and one for termFreqs
        ByteBuffer bufferDocId = ByteBuffer.allocate(skip.getBlockDocIDLen());
        ByteBuffer bufferTermFreq = ByteBuffer.allocate(skip.getBlockFreqLen());

        // get the position of the docIds and termFreqs
        channelDocID.position(skip.getOffsetDocID());
        channelTermFreq.position(skip.getOffsetFreq());

        // read the docIds and termFreqs
        while (bufferDocId.hasRemaining())
            channelDocID.read(bufferDocId);

        while (bufferTermFreq.hasRemaining())
            channelTermFreq.read(bufferTermFreq);

        // reset the buffer position to 0
        bufferDocId.rewind();
        bufferTermFreq.rewind();

        // create the posting list
        for (int i = 0; i < skip.getBlockDocIDLen() / 4; i++) {
            int docId = bufferDocId.getInt();
            int termFreq = bufferTermFreq.getInt();

            this.postingList.add(new Posting(docId, termFreq));
        }

    }

    public void readCompressedPostingListFromDisk(FileChannel channelDocID, FileChannel channelTermFreq, SkipElem skip) throws IOException {
        // intialize one buffer for docIds and one for termFreqs
        ByteBuffer bufferDocId = ByteBuffer.allocate(skip.getBlockDocIDLen());
        ByteBuffer bufferTermFreq = ByteBuffer.allocate(skip.getBlockFreqLen());

        // get the position of the docIds and termFreqs
        channelDocID.position(skip.getOffsetDocID());
        channelTermFreq.position(skip.getOffsetFreq());

        // read the docIds and termFreqs
        while (bufferDocId.hasRemaining())
            channelDocID.read(bufferDocId);

        while (bufferTermFreq.hasRemaining())
            channelTermFreq.read(bufferTermFreq);

        // reset the buffer position to 0
        bufferDocId.rewind();
        bufferTermFreq.rewind();

        // decompress
        ArrayList<Integer> docIds = variableByte.decompress(bufferDocId.array());
        ArrayList<Integer> termFreqs = unary.decompress(bufferTermFreq.array());

        // create the posting list
        for (int i = 0; i < skip.getBlockDocIDLen() / 4; i++) {
            int docId = bufferDocId.getInt();
            int termFreq = bufferTermFreq.getInt();

            this.postingList.add(new Posting(docId, termFreq));
        }

    }


    // load the posting list given the term
    public void getPostingList(String term) throws IOException {

        // initialize the random access file
        RandomAccessFile docId_RAF = new RandomAccessFile(FileUtils.Path_FinalDocId, "r");
        RandomAccessFile termFreq_RAF = new RandomAccessFile(FileUtils.Path_FinalTermFreq, "r");

        VocabularyElem vocabularyElem = FileUtils.vocabulary.get(term);

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

            if(Configuration.isIndex_compressionON()){
                // compression On
                readCompressedPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), blocks.get(0));
            }
            else {
                // compression Off
                readPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), blocks.get(0));
            }

            // set the last docID
            skipBlock.setDocID(this.getPostingList().get(this.getPostingList().size()-1).getDocID());
        }
        else{
            //vengono recuperate le informazioni di skipping dal file

            // size of step
            int step = 32;

            // address to read the skip info
            long skipAddress = vocabularyElem.getSkipOffset();

            // number of blocks
            int numBlocks = vocabularyElem.getSkipLen() / step;

            // initialize a new ArrayList of blocks
            blocks = new ArrayList<>();

            // read the skip info
            for (int i = 0; i < numBlocks; i++) {
                SkipElem skipElem = new SkipElem();
                // read skipping element from disk
                skipElem.readFromDisk(skip_RAF.getChannel(), skipAddress + i * step);
                // add the skip element to the list
                blocks.add(skipElem);
            }

            // current block
            skipBlock = blocks.get(0);

            // initialize the iterator
            skipElemIterator = blocks.iterator();
            skipElemIterator.next();

            if(Configuration.isIndex_compressionON()) {
                // compressione index On
                readCompressedPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), blocks.get(0));
            }else{
                // compressione index Off
                readPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), blocks.get(0));
            }
        }

        // set posting list iterator
        postingListIterator = postingList.iterator();
        currentPostingList = postingListIterator.next();

        if (Configuration.isScoreON()) {
            // BM25
            this.BM25 = vocabularyElem.getMaxBM25();
        } else {
            // TF-IDF
            this.TFIDF = vocabularyElem.getMaxTFIDF();
        }


    }

    public Posting getCurrentPostingList() {
        return currentPostingList;
    }
}
