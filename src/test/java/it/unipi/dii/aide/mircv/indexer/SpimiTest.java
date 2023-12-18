package it.unipi.dii.aide.mircv.indexer;

import it.unipi.dii.aide.mircv.models.*;
import it.unipi.dii.aide.mircv.utils.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import static it.unipi.dii.aide.mircv.utils.FileUtils.docIndex_RAF;
import static org.junit.jupiter.api.Assertions.*;


class SpimiTest {

    protected static final HashMap<String, VocabularyElem> vocabularyT = new HashMap<>();   // LinkedHashMap to preserve the order of insertion
    protected static final HashMap<String, VocabularyElem> vocabulary = new HashMap<>();    // LinkedHashMap to preserve the order of insertion

    // list of sorted term
    public static ArrayList<String> termList = new ArrayList<>();

    public static HashMap<String, PostingList> invIdexT = new HashMap<>();
    public static HashMap<String, PostingList> invIdex = new HashMap<>();

    static ArrayList<DocumentIndexElem> docIndex = new ArrayList<>();

    ArrayList<DocumentIndexElem> docIndexT = new ArrayList<>();

    private static BufferedReader bufferedReader;


    @BeforeAll
    public static void init() {
        // tree ,ocean, sunshine, whisper, tree, harmony
        // Sunshine, ball, house

        DocumentIndexElem doc1 = new DocumentIndexElem(1, "1", 6);
        DocumentIndexElem doc2 = new DocumentIndexElem(2, "2", 3);
        docIndex.add(doc1);
        docIndex.add(doc2);

        bufferedReader = new BufferedReader(new StringReader("1\ttree ocean sunshine whisper tree harmony\n" + "2\tsunshine ball house"));

        VocabularyElem VocElem1 = new VocabularyElem("tree", 1, 2);
        vocabularyT.put("tree", VocElem1);
        VocabularyElem VocElem2 = new VocabularyElem("ocean", 1, 1);
        vocabularyT.put("ocean", VocElem2);
        VocabularyElem VocElem3 = new VocabularyElem("sunshine", 2, 2);
        vocabularyT.put("sunshine", VocElem3);
        VocabularyElem VocElem4 = new VocabularyElem("whisper", 1, 1);
        vocabularyT.put("whisper", VocElem4);
        VocabularyElem VocElem5 = new VocabularyElem("harmony", 1, 1);
        vocabularyT.put("harmony", VocElem5);
        VocabularyElem VocElem6 = new VocabularyElem("ball", 1, 1);
        vocabularyT.put("ball", VocElem6);
        VocabularyElem VocElem7 = new VocabularyElem("house", 1, 1);
        vocabularyT.put("house", VocElem7);

        invIdex.put("harmony", new PostingList("harmony", new Posting(1, 1)));
        invIdex.put("house", new PostingList("house", new Posting(2, 1)));
        invIdex.put("ball", new PostingList("ball", new Posting(2, 1)));
        invIdex.put("ocean", new PostingList("ocean", new Posting(1, 1)));
        invIdex.put("sunshine", new PostingList("sunshine", new Posting(1, 1)));
        invIdex.get("sunshine").addPosting(new Posting(2, 1));
        invIdex.put("tree", new PostingList("tree", new Posting(1, 2)));
        invIdex.put("whisper", new PostingList("whisper", new Posting(1, 1)));

    }

    @Test
    public void Vocabulary_Equal() throws IOException {
        // build vocabulary
        HashMap<String, VocabularyElem> vocabularyT = buildVocaulary(bufferedReader);

        // check if the two vocabulary have the same size
        assertEquals(vocabularyT.size(), vocabulary.size());

        // check if the two vocabulary have the same terms
        for (String term : vocabulary.keySet()) {
            assertTrue(vocabulary.containsKey(term));
        }

        // check if the two vocabulary have the same docFreq
        for (String term : vocabulary.keySet()) {
            assertEquals(vocabulary.get(term).getDocFreq(),vocabularyT.get(term).getDocFreq());
        }

        // check if the two vocabulary have the same collFreq
        for (String term : vocabulary.keySet()) {
            assertEquals(vocabulary.get(term).getDocFreq(),vocabularyT.get(term).getDocFreq());
        }
    }

    @Test
    public void PostingList_Equal() throws IOException {
        // build posting
        HashMap<String, PostingList> invIdexT = buildPosting(bufferedReader);

        // check if the two posting have the same size
        assertEquals(invIdexT.size(), invIdex.size());

        // check if the two posting have the same terms
        for (String term : invIdex.keySet()) {
            assertEquals(invIdexT.containsKey(term), invIdex.containsKey(term));
        }

        // per ogni chiave del hashmap
        // controllo che il posting list sia uguale
        // controllo che il term sia uguale
        for (String term : invIdex.keySet()) {
            System.out.println("term: " + term);
            PostingList postList = invIdex.get(term);
            PostingList postListT = invIdexT.get(term);

            assertEquals(postList.getTerm(), postListT.getTerm());

            assertEquals(invIdexT.get(term).getTerm(), invIdex.get(term).getTerm());
        }
    }

    @Test
    public void DocumentIndex_Equal() throws IOException {
        // build document index
        ArrayList<DocumentIndexElem> docIndexT = buildDocumentIndexElem(bufferedReader);

        // check if the two document index have the same size
        assertEquals(docIndex.size(), docIndexT.size());

        // check if the two document index have the same docid
        for (int i = 0; i < docIndex.size(); i++) {
            assertEquals(docIndex.get(i).getDocId(), docIndexT.get(i).getDocId());
        }

        // check if the two document index have the same docno
        for (int i = 0; i < docIndex.size(); i++) {
            assertEquals(docIndex.get(i).getDocno(), docIndexT.get(i).getDocno());
        }

        // check if the two document index have the same documnetLength
        for (int i = 0; i < docIndex.size(); i++) {
            assertEquals(docIndex.get(i).getLength(), docIndexT.get(i).getLength());
        }
    }



    private ArrayList<DocumentIndexElem> buildDocumentIndexElem(BufferedReader bufferedReader) throws IOException {

        int docid = 1;
        String line;
        String docno;
        String text;
        int tab;
        int documnetLength;

        while ((line = bufferedReader.readLine()) != null) {

            // split on tab
            tab = line.indexOf("\t");

            // check malformed line
            if (tab == -1) {  // tab not found
                continue;
            }

            // extract
            docno = line.substring(0, tab);
            text = line.substring(tab + 1);

            // check empty doc
            if (text.isEmpty())
                continue;

            // process text
            String[] tokens = text.split(" ");

            documnetLength = tokens.length;

            // new document index elem
            DocumentIndexElem doc = new DocumentIndexElem(docid, docno, documnetLength);
            docIndexT.add(doc);

            docid++;
        }
        return docIndexT;
    }



    private HashMap<String, PostingList> buildPosting(BufferedReader bufferedReader) throws IOException {

        // init docIndex_RAF
        FileUtils.initDocIndex_RAF();

        int docid = 1;
        String line;
        String docno;
        String text;
        int tab;
        int documnetLength;

        while ((line = bufferedReader.readLine()) != null) {

            // split on tab
            tab = line.indexOf("\t");


            // extract
            docno = line.substring(0, tab);
            text = line.substring(tab + 1);

            String[] tokens = text.split(" ");

            documnetLength = tokens.length;

            for (String token : tokens) {

                // compute term frequency in the document
                int tf = Collections.frequency(java.util.Arrays.asList(tokens), token);

                // check if token is already in the vocabulary
                if (vocabulary.containsKey(token)) {

                    // get the vocabulary element
                    VocabularyElem vocElem = vocabulary.get(token);
                    // check if the term is in another document
                    if (vocElem.getLastDocIdInserted() != docid) {

                        // update the document frequency
                        vocElem.incDocFreq();
                        // update the collection frequency
                        vocElem.updateCollFreq(tf);
                        // update the last document id inserted
                        vocElem.setLastDocIdInserted(docid);

                        // add the posting list
                        invIdexT.get(token).addPosting(new Posting(docid, tf));
                    }

                } else {
                    // add new term in the vocabulary
                    VocabularyElem NewVocElem = new VocabularyElem(token, 1, tf);
                    NewVocElem.setLastDocIdInserted(docid);
                    vocabulary.put(token, NewVocElem);

                    // add new posting list
                    invIdexT.put(token, new PostingList(token, new Posting(docid, tf)));

                    // add new term in the list
                    termList.add(token);
                }
            }

            if (docid % 50000 == 0)
                System.out.println("< current docId: " + docid +" >");

            docid++;
        }

        return invIdexT;
    }




    private HashMap<String, VocabularyElem> buildVocaulary(BufferedReader bufferedReader) throws IOException {

        // init docIndex_RAF
        FileUtils.initDocIndex_RAF();
        int docid = 1;
        String line;
        String docno;
        String text;
        int tab;
        int documnetLength;

        while ((line = bufferedReader.readLine()) != null) {

            // split on tab
            tab = line.indexOf("\t");

            // check malformed line
            if (tab == -1) {  // tab not found
                continue;
            }

            // extract
            docno = line.substring(0, tab);
            text = line.substring(tab + 1);

            // check empty doc
            if (text.isEmpty())
                continue;

            String[] tokens = text.split(" ");

            documnetLength = tokens.length;

            // new document index elem
            DocumentIndexElem doc = new DocumentIndexElem(docid, docno, documnetLength);
            doc.writeToDisk(docIndex_RAF.getChannel());

            for (String token : tokens) {

                // compute term frequency in the document
                int tf = Collections.frequency(java.util.Arrays.asList(tokens), token);

                // check if token is already in the vocabulary
                if (vocabulary.containsKey(token)) {

                    // get the vocabulary element
                    VocabularyElem vocElem = vocabulary.get(token);
                    // check if the term is in another document
                    if (vocElem.getLastDocIdInserted() != docid) {

                        // update the document frequency
                        vocElem.incDocFreq();
                        // update the collection frequency
                        vocElem.updateCollFreq(tf);
                        // update the last document id inserted
                        vocElem.setLastDocIdInserted(docid);

                    }

                } else {
                    // add new term in the vocabulary
                    VocabularyElem NewVocElem = new VocabularyElem(token, 1, tf);
                    NewVocElem.setLastDocIdInserted(docid);
                    vocabulary.put(token, NewVocElem);

                    // add new term in the list
                    termList.add(token);
                }
            }

            docid++;
        }
        // sort the vocabulary
        return vocabulary;
    }

}