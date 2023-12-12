package it.unipi.dii.aide.mircv.indexer;
import it.unipi.dii.aide.mircv.models.Configuration;
import it.unipi.dii.aide.mircv.text_processing.TextProcessing;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import static it.unipi.dii.aide.mircv.utils.FileUtils.initBuffer;

public class Spimi {

    // lexicon
    HashMap<String, Integer> lexicon = new HashMap<>();
    // block size
    private int blockSize;
    // number of blocks
    private int blockNum;
    // number of documents
    private int docNum;
    // number of terms
    private int termNum;
    // number of tokens
    private int tokenNum;

    public Spimi(int blockSize) {
        this.blockSize = blockSize;
        this.blockNum = 0;
        this.docNum = 0;
        this.termNum = 0;
        this.tokenNum = 0;
    }

    public void index() throws IOException {
        // start timer
        long startTime = System.currentTimeMillis();

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
            String docid = line.substring(0, tab);
            String text = line.substring(tab + 1);

            // check empty doc
            if (text.isEmpty())
                continue;

            // process text
            String[] tokens = TextProcessing.DocumentProcessing(text);


            System.out.println("docid: " + docid);
            System.out.println("text: " + text);
            System.out.println("tokens: " + String.join(" ", tokens));
            System.out.println("tokens length: " + tokens.length);
        }




        // end timer
        long endTime = System.currentTimeMillis();
    }


}
