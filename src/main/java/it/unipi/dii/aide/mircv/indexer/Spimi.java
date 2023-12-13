package it.unipi.dii.aide.mircv.indexer;
import it.unipi.dii.aide.mircv.models.Configuration;
import it.unipi.dii.aide.mircv.models.DocumentIndexElem;
import it.unipi.dii.aide.mircv.models.VocabularyElem;
import it.unipi.dii.aide.mircv.text_processing.TextProcessing;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static it.unipi.dii.aide.mircv.utils.FileUtils.docIndex_RAF;
import static it.unipi.dii.aide.mircv.utils.FileUtils.initBuffer;

public class Spimi {

    // docId counter
    protected int docid = 1;

    // vocabulary = hash map to store the terms and their posting list
    protected final HashMap<String, VocabularyElem> vocabulary = new HashMap<>();

    // document index = linked hash map to preserve insertion order
    protected final LinkedHashMap<Integer, DocumentIndexElem> documentIndex = new LinkedHashMap<>();


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

            docid ++;
        }

    }

}
