package it.unipi.dii.aide.mircv.indexer;

import it.unipi.dii.aide.mircv.models.VocabularyElem;

import java.io.IOException;
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

                currentOffsetVocabulary[blockNumCorrent] += vocabularyEntrySize; // update the offset of the current partial_vocabulary

                if(mappedByteBuffer != null){
                    String[] term = StandardCharsets.UTF_8.decode(mappedByteBuffer).toString().split("\0");
                    heap.add(new AbstractMap.SimpleEntry<>(term[0], blockNumCorrent));
                    System.out.println("Heap :" + heap);
                }
            }

            // entry contains the next term to insert in the final_vocabulary
            VocabularyElem vocabularyElem = readVocabularyFromPartialFile(entry);


        }
        }

    private VocabularyElem readVocabularyFromPartialFile(AbstractMap.SimpleEntry<String, Integer> entry) {
        FileChannel channelDictionary = GetCorrectChannel(entry.getValue(), 0);
        long offset = 56 * entry.getValue(); // the size of each dictionary element is 56 bytes

        // read the dictionary element

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
