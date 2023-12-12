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

import static it.unipi.dii.aide.mircv.utils.FileUtils.initBuffer;

public class Spimi {

    // docId counter
    protected int docid = 1;

    // vocabulary = hash map to store the terms and their posting list
    protected final HashMap<String, VocabularyElem> vocabulary = new HashMap<>();

    // document index = linked hash map to preserve insertion order
    protected final LinkedHashMap<Integer, DocumentIndexElem> documentIndex = new LinkedHashMap<>();


    public void index() throws IOException {
        System.out.println("Indexing...");

        // read collection according to the compression flag
        BufferedReader bufferedReader = initBuffer(Configuration.isCompressionON()) ;
        String line;
        int tab;



        while((line = bufferedReader.readLine())!= null) {
            // split on tab
            tab = line.indexOf("\t");

            // check malformed line
            if (tab == -1) {  // tab not found
                continue;
            }

            // extract
            String docno = line.substring(0, tab);
            String text = line.substring(tab + 1);

            // check empty doc
            if (text.isEmpty())
                continue;

            // process text
            String[] tokens = TextProcessing.DocumentProcessing(text);

            int documnetLength = tokens.length;

            // update document index
            documentIndex.put(docid, new DocumentIndexElem(docid,docno,documnetLength));        // preserve insertion order



            // update vocabulary
            for (String token : tokens) {
                // check if the term is already in the vocabulary
                if (vocabulary.containsKey(token)) { // O(1)
                    // update vocabulary element

                } else {
                    // New element in the vocabulary
                    int df = 1;
                    int cf = 1;
                    vocabulary.put(token, new VocabularyElem(token, df,cf));
                }
            }



            docid ++;


        }




    }


}
