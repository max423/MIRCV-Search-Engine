package it.unipi.dii.aide.mircv.index;

import java.io.BufferedReader;
import java.io.IOException;

public class SPIMI {

    /**
     * initializes the buffer from which the entries are read
     * @param compressed  flag for compressed reading
     * @return buffer reader
     * */
    public static BufferedReader initBuffer(boolean compressed) throws IOException {

        if(compressed) { //read from compressed collection
            TarArchiveInputStream tarInput = null;
            try {
                tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(PATH_COMPRESSED_COLLECTION)));
                tarInput.getNextTarEntry();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            if (tarInput == null) {
                logger.fatal("Cannot access to the collection.");
                System.exit(-1);
            }
            return new BufferedReader(new InputStreamReader(tarInput, StandardCharsets.UTF_8));
        }

        return Files.newBufferedReader(Paths.get(PATH_TO_COLLECTION), StandardCharsets.UTF_8);
    }

    /**
     * deletes directories containing partial data structures and document Index file
     */
    private static void rollback(){

        FileUtils.deleteDirectory(ConfigurationParameters.getDocidsDir());
        FileUtils.deleteDirectory(ConfigurationParameters.getFrequencyDir());
        FileUtils.deleteDirectory(ConfigurationParameters.getPartialVocabularyDir());
        FileUtils.removeFile(ConfigurationParameters.getDocumentIndexPath());
    }

    /**
     * Function that searched for a given docid in a posting list.
     * If the document is already present it updates the term frequency for that
     * specific document, if that's not the case creates a new pair (docid,freq)
     * in which frequency is set to 1 and adds this pair to the posting list
     *
     * @param docid:       docid of a certain document
     * @param postingList: posting list of a given term
     **/
    protected static void updateOrAddPosting(int docid, PostingList postingList) {
        if (postingList.getPostings().size() > 0) {
            // last document inserted:
            Posting posting = postingList.getPostings().get(postingList.getPostings().size() - 1);
            //If the docId is the same I update the posting
            if (docid == posting.getDocid()) {
                posting.setFrequency(posting.getFrequency() + 1);
                return;
            }
        }
        // the document has not been processed (docIds are incremental):
        // create new pair and add it to the posting list
        postingList.getPostings().add(new Posting(docid, 1));

        //increment the number of postings
        numPostings++;

    }

    /**
     * Performs spimi algorithm
     *
     * @return the number of partial indexes created
     * @param compressedReadingEnable flag enabling reading from compressed file and stemming if true
     * @param debug flag enabling debug mode
     */
    public static int executeSPIMI(boolean compressedReadingEnable,boolean debug) {
        numIndex = 0;
        DocumentIndexEntry.resetOffset();
        try (
                BufferedReader br = initBuffer(compressedReadingEnable)
        ) {
            boolean allDocumentsProcessed = false; //is set to true when all documents are read

            int docid = 1; //assign docid in a incremental manner
            int docsLen = 0; // total sum of lengths of documents
            boolean writeSuccess; //checks whether the writing of the partial data structures was successful or not

            long MEMORY_THRESHOLD = Runtime.getRuntime().totalMemory() * 20 / 100; // leave 20% of memory free
            String[] split;
            while (!allDocumentsProcessed ) {
                HashMap<String, PostingList> index = new HashMap<>(); //hashmap containing partial index
                while (Runtime.getRuntime().freeMemory() > MEMORY_THRESHOLD ) { //build index until 80% of total memory is used

                    String line;
                    // if we reach the end of file (br.readline() -> null)
                    if ((line = br.readLine()) == null) {
                        // we've processed all the documents
                        System.out.println("all documents processed");
                        allDocumentsProcessed = true;
                        break;
                    }
                    // if the line is empty we process the next line
                    if (line.isBlank())
                        continue;

                    // split of the line in the format <pid>\t<text>
                    split = line.split("\t");

                    // Creation of the text document for the line
                    TextDocument document = new TextDocument(split[0], split[1].replaceAll("[^\\x00-\\x7F]", ""));
                    // Perform text preprocessing on the document

                    ProcessedDocument processedDocument = Preprocesser.processDocument(document);

                    if (processedDocument.getTokens().isEmpty())
                        continue;


                    int documentLength = processedDocument.getTokens().size();
                    //create new document index entry and add it to file
                    DocumentIndexEntry docIndexEntry = new DocumentIndexEntry(
                            processedDocument.getPid(),
                            docid,
                            documentLength
                    );

                    //update with length of new documents
                    docsLen += docIndexEntry.getDocLen();

                    // write the docIndex entry to disk
                    docIndexEntry.writeToDisk();

                    if(debug){
                        docIndexEntry.debugWriteToDisk("debugDOCINDEX.txt");
                    }

                    for (String term : processedDocument.getTokens()) {

                        if(term.isBlank())
                            continue;

                        PostingList posting; //posting list of a given term
                        if (!index.containsKey(term)) {
                            // create new posting list if term wasn't present yet
                            posting = new PostingList(term);
                            index.put(term, posting); //add new entry (term, posting list) to entry
                        } else {
                            //term is present, we can get its posting list
                            posting = index.get(term);
                        }

                        //insert or update new posting
                        updateOrAddPosting(docid, posting);
                        posting.updateBM25Parameters(documentLength, posting.getPostings().size());

                    }
                    docid++;
                    if((docid%1000000)==0){
                        System.out.println("at docid: "+docid);
                    }
                }

                //either if there is no  memory available or all documents were read, flush partial index onto disk
                writeSuccess = saveIndexToDisk(index, debug);

                //error during data structures creation. Rollback previous operations and end algorithm
                if(!writeSuccess){
                    System.out.println("Couldn't write index to disk.");
                    rollback();
                    return -1;
                }
                index.clear();

            }
            // update the size of the document index and save it to disk
            if(!CollectionSize.updateCollectionSize(docid-1) || !CollectionSize.updateDocumentsLenght(docsLen)){
                System.out.println("Couldn't update collection statistics.");
                return 0;
            }


            return numIndex;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }


    }



}
