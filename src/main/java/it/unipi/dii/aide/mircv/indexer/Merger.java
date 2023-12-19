package it.unipi.dii.aide.mircv.indexer;

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

import static it.unipi.dii.aide.mircv.utils.FileUtils.CreateFinalStructure;
import static it.unipi.dii.aide.mircv.utils.FileUtils.GetCorrectChannel;



public class Merger {
    MappedByteBuffer mappedByteBuffer;
    FileChannel channelVocabulary;

    long vocabularyEntrySize= 56; // check



    public void startMerger(Integer blockNumber) throws IOException {
        System.out.println("> Start Merging ...");

        // ogni posizione contiene l'offest del prossimo termine da prendere per ogni vocabolario parziale
        long[] currentOffsetVocabulary = new long[blockNumber+1 ];

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

        while (!heap.isEmpty()) {
            // Extract the entry <term,blocknum> from the heap heap
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
            VocabularyElem vocabularyElem = readVocabularyFromPartialFile( entry.getKey(),currentOffsetVocabulary[blockNumCorrent], GetCorrectChannel(blockNumCorrent, 0));

            // update the offset of the current partial_vocabulary
            currentOffsetVocabulary[blockNumCorrent] += vocabularyEntrySize;

            // controllo se c'è almeno un altro termine uguale nell heap
            // se si -> devo fare il merge
            // se no -> scrivo il termine nel final_vocabulary
            AbstractMap.SimpleEntry<String, Integer> head = heap.peek();
            String headTerm = head.getKey();

            if (headTerm.equals(entry.getKey())) {
                // merge
                // TODO
            } else {
                // write the term in the final_vocabulary
                // TODO
            }

        }
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
            System.out.println("VocabularyElem :" + vocabularyElem);

            return vocabularyElem;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // populate the heap with the first term of each partial_vocabulary and blockNumber
    private void populateHeap(Integer blockNumber, PriorityQueue<AbstractMap.SimpleEntry<String, Integer>> heap)
            throws IOException {
        int i = 0;
        do {
            // read the first element of each partial_vocabulary
            // add it to the heap
            FileChannel channelVocabulary = GetCorrectChannel(i, 0);
            mappedByteBuffer = channelVocabulary.map(FileChannel.MapMode.READ_ONLY, 0, 20);
            String[] term = StandardCharsets.UTF_8.decode(mappedByteBuffer).toString().split("\0");
            //System.out.println(term[0], i);

            heap.add(new AbstractMap.SimpleEntry<>(term[0], i));
            i++;

        } while (i < blockNumber);
    }
}
