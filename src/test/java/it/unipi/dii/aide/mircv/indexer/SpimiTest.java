package it.unipi.dii.aide.mircv.indexer;

import it.unipi.dii.aide.mircv.models.Posting;
import it.unipi.dii.aide.mircv.models.PostingList;
import it.unipi.dii.aide.mircv.models.VocabularyElem;
import it.unipi.dii.aide.mircv.utils.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

// create a test for the method startIndexer() of the class Spimi
class SpimiTest {

    @Test
    void startIndexer() throws IOException {
        // create a Spimi object
        Spimi spimi = new Spimi();

        // create a BufferedReader object
        BufferedReader bufferedReader = new BufferedReader(new StringReader("doc1\tterm1 term2 term3\ndoc2\tterm1 term2\ndoc3\tterm1"));

        // create a MappedByteBuffer object
        MappedByteBuffer mappedByteBuffer = FileUtils.docIndex_RAF. ()
        (FileChannel.MapMode.READ_WRITE, 0, FileUtils.docIndex_RAF.size());

        // create a HashMap object
        HashMap<String, VocabularyElem> vocabulary = new HashMap<>();

        // create an ArrayList object
        ArrayList<String> termList = new ArrayList<>();

        // create a HashMap object
        HashMap<String, PostingList> postingListElem = new HashMap<>();

        // create a String object
        String line;

        // create an int object
        int tab;

        // create a String object
        String docno;

        // create a String object
        String text;

        // create an int object
        int documentLength;

        // create a long object
        long MEMORYFree_THRESHOLD = Runtime.getRuntime().totalMemory() * 20 / 100;

        // create a String object
        String[] terms;

        // create a String object
        String term;

        // create a PostingList object
        PostingList postingList;

        // create a Posting object
        Posting posting;

        // create a VocabularyElem object
        VocabularyElem vocabularyElem;

        // create a String object
        String[] docnos = {"doc1", "doc2", "doc3"};

        // create a String object
        String[] texts = {"term1 term2 term3", "term1 term2", "term1"};

        // create an int object
        int[] documentLengths = {3, 2, 1};

        // create a String object
        String[] terms1 = {"term1", "term2", "term3"};

        // create a String object
        String[] terms2 = {"term1", "term2"};

        // create a String object
        String[] terms3 = {"term1"};

        // create a String object
        String[] terms4 = {"term1", "term2", "term3"};

        // create a String object
        String[] terms5 = {"term1", "term2"};

        // create a String object
        String[] terms6 = {"term1"};

        // do the test
        


    }

    public static ArrayList<VocabularyElem> vocabularyElem(HashMap<String, PostingList> index) throws IOException {
        // Given inverted index generate vocabulary
        int numPostings = 0;
        for (PostingList pl : index.values())
            numPostings += pl.getPostingList().size();

        ArrayList<VocabularyElem> voc = new ArrayList<>(index.size());

        // instantiation of MappedByteBuffer for integer list of docids
        MappedByteBuffer docsBuffer = FileUtils.skeleton_RAF.get(0).get(1).getChannel().map(FileChannel.MapMode.READ_WRITE, 0, numPostings * 4L);

        // instantiation of MappedByteBuffer for integer list of freqs
        MappedByteBuffer freqsBuffer = FileUtils.skeleton_RAF.get(0).get(2).getChannel().map(FileChannel.MapMode.READ_WRITE, 0, numPostings * 4L);

        // check if MappedByteBuffers are correctly instantiated
        if (docsBuffer != null && freqsBuffer != null) {
            for (PostingList list : index.values()) {
                //create vocabulary entry
                VocabularyElem vocEntry = new VocabularyElem(list.getTerm());
                vocEntry.setDocIdsOffset(docsBuffer.position());
                vocEntry.setTermFreqOffset(docsBuffer.position());

                // write postings to file
                for (Posting posting : list.getPostingList()) {
                    // docid
                    docsBuffer.putInt(posting.getDocID());
                    // freq
                    freqsBuffer.putInt(posting.getTermFreq());
                }

                vocEntry.setDocIdsLen((numPostings * 4));
                vocEntry.setTermFreqLen((numPostings * 4));

                voc.add(vocEntry);
            }

        }
        return voc;
    }
}

