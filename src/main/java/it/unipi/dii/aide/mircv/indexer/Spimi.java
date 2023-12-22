package it.unipi.dii.aide.mircv.indexer;
import it.unipi.dii.aide.mircv.models.*;
import it.unipi.dii.aide.mircv.text_processing.TextProcessing;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static it.unipi.dii.aide.mircv.utils.FileUtils.docIndex_RAF;
import static it.unipi.dii.aide.mircv.utils.FileUtils.initBuffer;

public class Spimi {

    // block number
    protected int blockNum = 0;

    // docId counter
    protected int docid = 1;

    // vocabulary = hash map in memory
    protected final HashMap<String, VocabularyElem> vocabulary = new HashMap<>();

    // list of sorted term
    public static ArrayList<String> termList = new ArrayList<>();

    // posting list in memory
    public static HashMap<String, PostingList> postingListElem = new HashMap<>();
    private long MEMORYFree_THRESHOLD;

    public static int lastDocId = 0; // save last docId for CollectionStatistics


    public int startIndexer() throws IOException {
        System.out.println("Start Spimi Alg ...");

        // read collection according to the compression flag
        BufferedReader bufferedReader = initBuffer(Configuration.isCompressionON());
        // init docIndex_RAF
        FileUtils.initDocIndex_RAF();

        String line;
        String docno;
        String text;
        int tab;
        int documnetLength;

        MEMORYFree_THRESHOLD = Runtime.getRuntime().totalMemory() *35 / 100; // leave 20% of memory free
        System.out.println("MEMORYFree_THRESHOLD : " + MEMORYFree_THRESHOLD);

        while ((line = bufferedReader.readLine()) != null) {

            // checking if the used memory has reached the threshold
            checkMemory();
            
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
            DocumentIndexElem doc = new DocumentIndexElem(docid, docno, documnetLength);
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

            if (docid % 50000 == 0)
                System.out.println("< current docId: " + docid +" >");

            docid++;
        }

        // write the last block on the disk
        if (!WriteBlockOnDisk(blockNum, termList, vocabulary, postingListElem)) {
            System.out.println("Couldn't write block "+ blockNum + " to disk.");
            rollback();
            return -1;
        }
        System.out.println("Block " + blockNum + " written on disk.");

        lastDocId = docid; // save last docId for CollectionStatistics
        // close the docIndex_RAF
        docIndex_RAF.close();

        // clear data structure
        clearDataStructure();

        return blockNum;
    }

    private void checkMemory() {
        // check if the used memory has reached the threshold and write on disk
        if (Runtime.getRuntime().freeMemory() < MEMORYFree_THRESHOLD) {
            System.out.println("Memory full..");
            System.out.println("Writing block " + blockNum + " on disk..");

            if (!WriteBlockOnDisk(blockNum, termList, vocabulary, postingListElem)) {
                System.out.println("Couldn't write block "+ blockNum + " to disk.");
                rollback();
                System.exit(-1);
            }

            System.out.println("Block " + blockNum + " written on disk.");

            clearDataStructure();
            blockNum++;
            lastDocId = docid; // save last docId for CollectionStatistics

            // try to force garbage collection to free memory  // TODO SIMO
            while (Runtime.getRuntime().freeMemory() < MEMORYFree_THRESHOLD * 3) {
                // wait for memory to be freed
                System.gc();
            }
            System.out.println(Runtime.getRuntime().freeMemory() +  " <TH +" +MEMORYFree_THRESHOLD +"> Memory free again..");

        }
    }

    private void clearDataStructure() {
        vocabulary.clear();
        termList.clear();
        postingListElem.clear();
    }

    private void rollback() {
        FileUtils.clearDataFolder();
    }

    private boolean WriteBlockOnDisk(int blockNum, ArrayList<String> termList, HashMap<String, VocabularyElem> Pvocabulary, HashMap<String, PostingList> PpostingListElem) {

        // create RAF and temp file for the block
        FileUtils.createTempFile(blockNum);

        // sort the term list
        Collections.sort(termList);

        // write on the disk
         try {
             for (String term : termList) {

                 // get the posting list of the term
                 PostingList postList = PpostingListElem.get(term);

                 // get the vocabulary element of the term
                 VocabularyElem vocElem = Pvocabulary.get(term);

                 // set the offset of the DocId posting list in the vocabulary element
                 vocElem.setDocIdsOffset(FileUtils.skeleton_RAF.get(blockNum).get(1).getChannel().size());

                 // set the offset of the TermFreq posting list in the vocabulary element
                 vocElem.setTermFreqOffset(FileUtils.skeleton_RAF.get(blockNum).get(2).getChannel().size());

                 // write the posting list on the disk
                 postList.writeToDisk(FileUtils.skeleton_RAF.get(blockNum).get(1).getChannel(), FileUtils.skeleton_RAF.get(blockNum).get(2).getChannel());

                 // update the vocabulary element
                 vocElem.incFreqLen(postList.getPostingList().size()*4);
                 vocElem.incDocLen(postList.getPostingList().size()*4);

                 // TO DO  * update MAXTF

                 // write the vocabulary element on the disk
                 Pvocabulary.put(term, vocElem); // update the vocabulary
                 vocElem.writeToDisk(FileUtils.skeleton_RAF.get(blockNum).get(0).getChannel());

             }
             return true;
         }
            catch (IOException e) {
                System.out.println("I/O Error " + e);
                return false;
            }
    }

}
