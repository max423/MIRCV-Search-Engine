package it.unipi.dii.aide.mircv.indexer;
import it.unipi.dii.aide.mircv.models.*;
import it.unipi.dii.aide.mircv.text_processing.TextProcessing;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static it.unipi.dii.aide.mircv.utils.FileUtils.docIndex_RAF;
import static it.unipi.dii.aide.mircv.utils.FileUtils.initBuffer;

public class Spimi {

    // docId counter
    protected int docid = 1;

    // vocabulary = hash map in memory
    protected final HashMap<String, VocabularyElem> vocabulary = new HashMap<>();

    // list of sorted term
    public static ArrayList<String> termList = new ArrayList<>();

    // posting list in memory
    public static HashMap<String, PostingList> postingListElem = new HashMap<>();



    public void startIndexer() throws IOException {
        System.out.println("Indexing...");

        // read collection according to the compression flag
        BufferedReader bufferedReader = initBuffer(Configuration.isCompressionON()) ;
        // init docIndex_RAF
        FileUtils.initDocIndex_RAF();

        String line;
        String docno;
        String text;
        int tab;
        int documnetLength;

        while((line = bufferedReader.readLine())!= null) {
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
            String[] tokens = TextProcessing.DocumentProcessing(text);

            documnetLength = tokens.length;

            // new document index elem
            DocumentIndexElem doc = new DocumentIndexElem(docid,docno,documnetLength);
            doc.writeToDisk(docIndex_RAF.getChannel());

            for (String token : tokens) {

                // compute term frequency in the document
                int tf = Collections.frequency(java.util.Arrays.asList(tokens), token);

                // check if token is already in the vocabulary
                if (vocabulary.containsKey(token)) {

                    // termine è presente nel dizionario
                    // se è nello stesso docuemnto -> fine
                    // altrimenti dobbiamo :
                    // 1) aggiungere alla posting list ?
                    // 2) aggiurnare lexicon -> docFreq + colFreq

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
                        postingListElem.get(token).addPosting(new Posting(docid, tf));
                    }

                } else {
                    // add new term in the vocabulary
                    VocabularyElem NewVocElem = new VocabularyElem(token, 1, tf);
                    NewVocElem.setLastDocIdInserted(docid);
                    vocabulary.put(token, NewVocElem);

                    // add new posting list
                    postingListElem.put(token, new PostingList(token, new Posting(docid, tf)));

                    // add new term in the list
                    termList.add(token);
                }
            }
            docid ++;
        }

    }

}
