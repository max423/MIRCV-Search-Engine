package it.unipi.dii.aide.mircv.models;
import it.unipi.dii.aide.mircv.compression.unary;
import it.unipi.dii.aide.mircv.compression.variableByte;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;

public class PostingList {
    private String term;
    private final ArrayList<Posting> postingList;

    //private static SkipElem skipBlock;

    private Posting currentPostingList;

    // max BM25 score
    private double BM25;

    // max TF-IDF score
    private double TFIDF;

    //public static Iterator<SkipElem> skipElemIterator = null;
    public static Iterator<Posting> postingListIterator = null;

    private RandomAccessFile docId_RAF;
    private RandomAccessFile termFreq_RAF;

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

            ArrayList<Posting> postingListApp = new ArrayList<>();
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

    //public void readPostingListFromDisk(FileChannel channelDocID, FileChannel channelTermFreq, SkipElem skip) throws IOException {
    public void readPostingListFromDisk(FileChannel channelDocID, FileChannel channelTermFreq, long offsetDocId, long offsetTermFreq, int docIdsLen, int termFreqLen) throws IOException {
        // intialize one buffer for docIds and one for termFreqs
        //ByteBuffer bufferDocId = ByteBuffer.allocate(skip.getBlockDocIDLen());
        //ByteBuffer bufferTermFreq = ByteBuffer.allocate(skip.getBlockFreqLen());
        ByteBuffer bufferDocId = ByteBuffer.allocate(docIdsLen);
        ByteBuffer bufferTermFreq = ByteBuffer.allocate(termFreqLen);

        // get the position of the docIds and termFreqs
        //channelDocID.position(skip.getOffsetDocID());
        //channelTermFreq.position(skip.getOffsetFreq());
        channelDocID.position(offsetDocId);
        channelTermFreq.position(offsetTermFreq);

        // read the docIds and termFreqs
        while (bufferDocId.hasRemaining())
            channelDocID.read(bufferDocId);

        while (bufferTermFreq.hasRemaining())
            channelTermFreq.read(bufferTermFreq);

        // reset the buffer position to 0
        bufferDocId.rewind();
        bufferTermFreq.rewind();

        // create the posting list
        for (int i = 0; i < docIdsLen / 4; i++) {
            int docId = bufferDocId.getInt();
            int termFreq = bufferTermFreq.getInt();

            this.postingList.add(new Posting(docId, termFreq));
        }

    }

    //public void readCompressedPostingListFromDisk(FileChannel channelDocID, FileChannel channelTermFreq, SkipElem skip) throws IOException {
    public void readCompressedPostingListFromDisk(FileChannel channelDocID, FileChannel channelTermFreq, long offsetDocId, long offsetTermFreq, int docIdsLen, int termFreqLen) throws IOException {
        // intialize one buffer for docIds and one for termFreqs
        //ByteBuffer bufferDocId = ByteBuffer.allocate(skip.getBlockDocIDLen());
        //ByteBuffer bufferTermFreq = ByteBuffer.allocate(skip.getBlockFreqLen());
        ByteBuffer bufferDocId = ByteBuffer.allocate(docIdsLen);
        ByteBuffer bufferTermFreq = ByteBuffer.allocate(termFreqLen);

        // get the position of the docIds and termFreqs
        //channelDocID.position(skip.getOffsetDocID());
        //channelTermFreq.position(skip.getOffsetFreq());
        channelDocID.position(offsetDocId);
        channelTermFreq.position(offsetTermFreq);

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
        for (int i = 0; i < docIds.size(); i++) {   // modificato da docIdsLen/4 a docIdsLen -> docIds.size()
            int docId = docIds.get(i);
            int termFreq = termFreqs.get(i);

            this.postingList.add(new Posting(docId, termFreq));
        }

    }


    // load the posting list given the term
    public void getPostingList(String term) throws IOException {

        // initialize the random access file
        docId_RAF = new RandomAccessFile(FileUtils.Path_FinalDocId, "r");
        termFreq_RAF = new RandomAccessFile(FileUtils.Path_FinalTermFreq, "r");

        VocabularyElem vocabularyElem = FileUtils.vocabulary.get(term);

        // read skipping info
        //RandomAccessFile skip_RAF = new RandomAccessFile(FileUtils.Path_Skipping, "r");

        // check if the term is in the vocabulary
        if (vocabularyElem == null) {
            System.out.println("Term "+ term + " not found in the vocabulary");
            return;
        }


        // get the number of blocks of term
        //ArrayList<SkipElem> blocks;
        //if (vocabularyElem.getSkipLen() == 0){
            // il termine ha una lista di posting più piccola di 1024, e quindi viene creato un unico blocco di skipping contenente l'intera lista di posting

            // initialize a new ArrayList of blocks
            //blocks = new ArrayList<>();

            //skipBlock= new SkipElem(0, vocabularyElem.getDocIdsOffset(), vocabularyElem.getDocIdsLen(), vocabularyElem.getTermFreqOffset(), vocabularyElem.getTermFreqLen());
            //blocks.add(skipBlock);

            //skipElemIterator = blocks.iterator();
            //skipElemIterator.next();

            //if(Configuration.isIndex_compressionON()){
                // compression On
                //readCompressedPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), blocks.get(0));
            //}
            //else {
                // compression Off
                //readPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), blocks.get(0));
            //}

            // set the last docID
            //skipBlock.setDocID(this.getPostingList().get(this.getPostingList().size()-1).getDocID());
        //}
        //else{
            //vengono recuperate le informazioni di skipping dal file

            // size of step
            //int step = 32;

            // address to read the skip info
            //long skipAddress = vocabularyElem.getSkipOffset();

            // number of blocks
            //int numBlocks = vocabularyElem.getSkipLen() / step;

            // initialize a new ArrayList of blocks
            //blocks = new ArrayList<>();

            // read the skip info
            //for (int i = 0; i < numBlocks; i++) {
                //SkipElem skipElem = new SkipElem();
                // read skipping element from disk
                //skipElem.readFromDisk(skip_RAF.getChannel(), skipAddress + i * step);
                // add the skip element to the list
                //blocks.add(skipElem);
            //}

            // current block
            //skipBlock = blocks.get(0);

            // initialize the iterator
            //skipElemIterator = blocks.iterator();
            //skipElemIterator.next();

            if(Configuration.isIndex_compressionON()) {
                // compressione index On
                //readCompressedPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), blocks.get(0));
                readCompressedPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), vocabularyElem.getDocIdsOffset(), vocabularyElem.getTermFreqOffset(), vocabularyElem.getDocIdsLen(), vocabularyElem.getTermFreqLen());
            }else{
                // compressione index Off
                //readPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), blocks.get(0));
                readPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), vocabularyElem.getDocIdsOffset(), vocabularyElem.getTermFreqOffset(), vocabularyElem.getDocIdsLen(), vocabularyElem.getTermFreqLen());
            }
        //}

        // set posting list iterator ! serve !
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
        //return this.currentPostingList;
        return currentPostingList;
    }

    public Double getMaxBM25() {
        return BM25;
    }

    public Double getMaxTFIDF() {
        return TFIDF;
    }

    // get the next posting of the posting list
    public void nextPosting(String term) throws IOException {
        // check if the posting list is empty
        /*if (!postingListIterator.hasNext()) {

            // get the next posting

            postingList.clear();

            VocabularyElem vocabularyElem = FileUtils.vocabulary.get(term);
            if (Configuration.isIndex_compressionON()) {
                // compressione index On
                readCompressedPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), vocabularyElem.getDocIdsOffset(), vocabularyElem.getTermFreqOffset(), vocabularyElem.getDocIdsLen(), vocabularyElem.getTermFreqLen());
            } else {
                // compressione index Off
                readPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), vocabularyElem.getDocIdsOffset(), vocabularyElem.getTermFreqOffset(), vocabularyElem.getDocIdsLen(), vocabularyElem.getTermFreqLen());
            }

            postingListIterator = postingList.iterator();
        }
        if (postingListIterator.hasNext()) {
            // print first posting
            System.out.println("primo next");
            currentPostingList = postingListIterator.next();
        }
        if (postingListIterator.hasNext()) {
            // print second posting
            System.out.println("secondo next");
            currentPostingList = postingListIterator.next();
        }*/
        if (postingListIterator.hasNext()) {
            System.out.println("next");
            currentPostingList = postingListIterator.next();
        }
    }
        /*else {
            // the posting list is empty and there are no more blocks

            //if (skipElemIterator == null || !skipElemIterator.hasNext()) {
                // the posting list is empty and there are no more blocks
                this.currentPostingList = null;
                return;
            //}


            // get the next block
            //SkipElem nextBlock = skipElemIterator.next();

            // clear the posting list
            //postingList.clear();

            // read the posting list from disk
            //try {
                //if(Configuration.isIndex_compressionON()) {
                    // compressione index On
                    //readCompressedPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), nextBlock);
                //}else{
                    // compressione index Off
                    //readPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), nextBlock);
                //}
            //} catch (IOException e) {
                //e.printStackTrace();
            //}

            // set the posting list iterator
            //postingListIterator = postingList.iterator();
        }
    }

    // skip to the next posting
    /*public void nextGEQ(int currentDocID) {

        boolean blockChanged = false;

        // check if the max docID of the block is greater than the current docID
        while (skipBlock.getDocID() < currentDocID) {

            // try to get the next block
            if (skipElemIterator.hasNext()) {
                skipBlock = skipElemIterator.next();
                blockChanged = true;
            } else {
                // there are no more blocks
                currentPostingList = null;
                return;
            }
        }

        // check if the block has changed
        if (blockChanged) {
            // clear the posting list
            postingList.clear();

            // read the posting list from disk
            try {
                if(Configuration.isIndex_compressionON()) {
                    // compressione index On
                    readCompressedPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), skipBlock);
                }else{
                    // compressione index Off
                    readPostingListFromDisk(docId_RAF.getChannel(), termFreq_RAF.getChannel(), skipBlock);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // set the posting list iterator
            postingListIterator = postingList.iterator();

            // set the current posting list
            currentPostingList = postingListIterator.next();
        }

        // get the next posting of the posting list until the docID is greater than the current docID or the posting list is empty
        while (currentPostingList.getDocID() < currentDocID && postingListIterator.hasNext()) {
            currentPostingList = postingListIterator.next();
        }
    }*/


    // TODO Possiamo eliminarla
    public int getMaxTermFreq() {
        int maxTermFreq = 0;

        for (Posting posting : postingList) {
            if (posting.getTermFreq() > maxTermFreq)
                maxTermFreq = posting.getTermFreq();
        }

        return maxTermFreq;
    }
}
