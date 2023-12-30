package it.unipi.dii.aide.mircv.indexer;

import it.unipi.dii.aide.mircv.models.CollectionStatistics;
import it.unipi.dii.aide.mircv.models.Configuration;
import it.unipi.dii.aide.mircv.models.PostingList;
import it.unipi.dii.aide.mircv.models.VocabularyElem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.PriorityQueue;

import static it.unipi.dii.aide.mircv.compression.unary.writeTermFreqCompressed;
import static it.unipi.dii.aide.mircv.compression.variableByte.writeDocIdCompressed;
import static it.unipi.dii.aide.mircv.indexer.Spimi.lastDocId;
import static it.unipi.dii.aide.mircv.utils.FileUtils.CreateFinalStructure;
import static it.unipi.dii.aide.mircv.utils.FileUtils.GetCorrectChannel;

public class Merger {
    MappedByteBuffer mappedByteBuffer;
    FileChannel channelVocabulary;

    long vocabularyEntrySize= 56; // check

    String headTerm;

    PostingList postingList ;

    VocabularyElem vocabularyElem ;

    VocabularyElem vocabularyElemApp;

    int termFreqNewLen;
    int docIdNewLen;

    CollectionStatistics collectionStatistics = new CollectionStatistics();

    public void startMerger(int blockNumber) throws IOException {

        System.out.println("> Start Merging ...");

        // ogni posizione contiene l'offest del prossimo termine da prendere per ogni vocabolario parziale
        long[] currentOffsetVocabulary = new long[blockNumber ];
        System.out.println(currentOffsetVocabulary.length);


        // create the final_vocabulary
        CreateFinalStructure();

        // create a custom comparator for the heap , chiave term valore blockNumber
        Comparator<AbstractMap.SimpleEntry<String, Integer>> customComparator =
                (entry1, entry2) -> {
                    // confronto sulla chiave (ordine alfabetico)
                    int keyComparison = entry1.getKey().compareTo(entry2.getKey());
                    // se le chiavi sono uguali, confronto sul valore (in ordine crescente)
                    return (keyComparison == 0) ? entry1.getValue().compareTo(entry2.getValue()) : keyComparison;
                };

        // heap structure with the first term of each partial_vocabulary with blockNumber
        PriorityQueue<AbstractMap.SimpleEntry<String, Integer>> heap = new PriorityQueue<>(customComparator);
        // populate the heap
        populateHeap(blockNumber, heap);
        System.out.println("Heap size: " + heap.size());
        System.out.println("Heap: " + heap);

        while (!heap.isEmpty()) {
            // Extract the entry <term,blocknum> from the heap
            AbstractMap.SimpleEntry<String, Integer> entry = heap.poll();

            int blockNumCorrent = entry.getValue(); // block num corrente

            // insert the next term in the heap from the same partial vocabolary
            if( currentOffsetVocabulary[blockNumCorrent] +vocabularyEntrySize < GetCorrectChannel(blockNumCorrent, 0).size()) {

                FileChannel channelVocabulary = GetCorrectChannel(blockNumCorrent, 0);
                mappedByteBuffer = channelVocabulary.map(FileChannel.MapMode.READ_ONLY, currentOffsetVocabulary[blockNumCorrent] + 56, 20);

                if(mappedByteBuffer != null){
                    String[] term = StandardCharsets.UTF_8.decode(mappedByteBuffer).toString().split("\0");
                    heap.add(new AbstractMap.SimpleEntry<>(term[0], blockNumCorrent));
                }
            }

            // entry contains the next term to insert in the final_vocabulary
            vocabularyElem = readVocabularyFromPartialFile( entry.getKey(),currentOffsetVocabulary[blockNumCorrent], GetCorrectChannel(blockNumCorrent, 0));

            // update the offset of the current partial_vocabulary
            currentOffsetVocabulary[blockNumCorrent] += vocabularyEntrySize;

            // controllo se c'è almeno un altro termine uguale nell heap
            // se si -> devo fare il merge
            // se no -> scrivo il termine nel final_vocabulary
            AbstractMap.SimpleEntry<String, Integer> head = heap.peek();

            try {
                headTerm = head.getKey();
            } catch (NullPointerException e){
                headTerm= " ";  // setto come ultimo termine
            }

            if(vocabularyElemApp == null){
                // inizializzo vocabolario di appoggio
                vocabularyElemApp = vocabularyElem;

                // e prendo la posting list
                postingList= new PostingList(vocabularyElem.getTerm());
                postingList.readFromDisk(GetCorrectChannel(blockNumCorrent, 1), GetCorrectChannel(blockNumCorrent, 2), vocabularyElem.getDocIdsOffset(), vocabularyElem.getTermFreqOffset(), vocabularyElem.getDocIdsLen(), vocabularyElem.getTermFreqLen());

            }
            else {


                // vocabularyElemApp contiene quello vecchio  e devo aggiungere  a postingList la posting list di vocabularyElem
                // leggere la posting di vocabularyElem (termine corrente) e aggiungere i soui posting alla posting list

                postingList.addPostingFromDisk(GetCorrectChannel(blockNumCorrent, 1), GetCorrectChannel(blockNumCorrent, 2), vocabularyElem.getDocIdsOffset(), vocabularyElem.getTermFreqOffset(), vocabularyElem.getDocIdsLen(), vocabularyElem.getTermFreqLen());


                // termine doppione aggiorno i dati del vocabolario
                vocabularyElemApp.incDocFreq(vocabularyElem.getDocFreq());
                vocabularyElemApp.incCollFreq(vocabularyElem.getCollFreq());
                vocabularyElemApp.incFreqLen(vocabularyElem.getTermFreqLen());
                vocabularyElemApp.incDocLen(vocabularyElem.getDocIdsLen());

                // aggiornare Max
                vocabularyElem = vocabularyElemApp;



            }

            if (!headTerm.equals(entry.getKey())) {
                // non ci sono altri termini uguali -> scrivo vocabolario e posting list nel final_vocabulary e final_posting_list

                // controllo flag compressione, index_compressionON
                if(Configuration.isIndex_compressionON()){
                    // compressione abilitata

                    // TermFreq -> Unary
                    termFreqNewLen = writeTermFreqCompressed(postingList.getPostingList(), GetCorrectChannel(-1, 2));

                    // DocId -> VByte
                    docIdNewLen = writeDocIdCompressed(postingList.getPostingList(), GetCorrectChannel(-1, 1));

                    // update the vocabularyElem with new len
                    vocabularyElem.setDocIdsLen(docIdNewLen);
                    vocabularyElem.setTermFreqLen(termFreqNewLen);

                    // update the vocabularyElem with new offset
                    vocabularyElem.setDocIdsOffset(GetCorrectChannel(-1, 1).size() - docIdNewLen);
                    vocabularyElem.setTermFreqOffset(GetCorrectChannel(-1, 2).size() - termFreqNewLen);

                }
                else {
                    // compressione disabilitata
                    // write the posting list in the final_posting_list
                    postingList.writeToDisk(GetCorrectChannel(-1, 1), GetCorrectChannel(-1, 2));

                    // update the vocabularyElem with new offset
                    vocabularyElem.setDocIdsOffset(GetCorrectChannel(-1, 1).size() - vocabularyElem.getDocIdsLen());
                    vocabularyElem.setTermFreqOffset(GetCorrectChannel(-1, 2).size() - vocabularyElem.getTermFreqLen());
                }

                // write the term in the final_vocabulary
                vocabularyElem.writeToDisk(GetCorrectChannel(-1, 0));

                vocabularyElemApp = null;
                postingList = null;

                // update collection statistics
                collectionStatistics.incrementTotalLength(); // +1 per il termine

                if (collectionStatistics.getTotalLength() % 50000 == 0) {
                    System.out.println("< current merged terms: " + collectionStatistics.getTotalLength() +" >");
                }

            }

        }

        collectionStatistics.setDocCount(lastDocId);
        // write the collectionStatistics on the disk
        collectionStatistics.writeToDisk(GetCorrectChannel(-1, 3));

        System.out.println("> Merging completed!");

    }

    // take all the information from the partial_vocabulary for 1 vocabularyElem
    private VocabularyElem readVocabularyFromPartialFile(String t, long currentOffset, FileChannel channel) throws IOException {
        // 20 (term) + 4 (df) + 4 (cf) + 4(lastDocIdInserted) + 8 (docIdsOffset) + 8 (termFreqOffset) + 4 (docIdsLen) + 4(termFreqLen) = 56 bytes
        try {
            // creating ByteBuffer for reading term
            ByteBuffer buffer = ByteBuffer.allocate(20);
            channel.position(currentOffset + 20 );  // evito di leggere il term , tanto è gia nell'heap

            String term = t;

            // creating ByteBuffer for reading df, cf, lastDocIdInserted, docIdsOffset, termFreqOffset, docIdsLen, termFreqLen
            buffer = ByteBuffer.allocate(4 + 4 + 4 + 8 + 8 + 4 + 4);

            while (buffer.hasRemaining())
                channel.read(buffer);

            buffer.rewind(); // reset the buffer position to 0
            int df = buffer.getInt();                        // reading df from buffer
            int cf = buffer.getInt();                        // reading cf from buffer
            int lastDocIdInserted = buffer.getInt();         // reading lastDocIdInserted from buffe  // TODO SIMO
            long docIdsOffset = buffer.getLong();            // reading docIdsOffset from buffer
            long termFreqOffset = buffer.getLong();          // reading termFreqOffset from buffer
            int docIdsLen = buffer.getInt();                 // reading docIdsLen from buffer
            int termFreqLen = buffer.getInt();               // reading termFreqLen from buffer

            // creating the vocabularyElem
            VocabularyElem vocabularyElem = new VocabularyElem(term, df, cf, lastDocIdInserted, docIdsOffset, termFreqOffset, docIdsLen, termFreqLen);
            //System.out.println("VocabularyElem :" + vocabularyElem);

            return vocabularyElem;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // populate the heap with the first term of each partial_vocabulary and blockNumber
    private void populateHeap(int blockNumber, PriorityQueue<AbstractMap.SimpleEntry<String, Integer>> heap)
            throws IOException {
        int i = 0;
        do {
            // read the first element of each partial_vocabulary
            // add it to the heap
            FileChannel channelVocabulary = GetCorrectChannel(i, 0);

            ByteBuffer buffer = ByteBuffer.allocate(20);
            channelVocabulary.position(0);

            while (buffer.hasRemaining())
                channelVocabulary.read(buffer);

            String term = new String(buffer.array(), StandardCharsets.UTF_8).trim();

            heap.add(new AbstractMap.SimpleEntry<>(term, i));
            i++;

            System.out.println("Heap size: " + heap.size());

        } while (i < blockNumber);

    }
}
