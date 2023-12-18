package it.unipi.dii.aide.mircv.indexer;

import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.PriorityQueue;

import static it.unipi.dii.aide.mircv.utils.FileUtils.CreateFinalStructure;
import static it.unipi.dii.aide.mircv.utils.FileUtils.GetCorrectChannel;

public class Merger {
    MappedByteBuffer mappedByteBuffer;
    FileChannel channelVocabulary;

    public void startMerger(Integer blockNumber) throws IOException {
        System.out.println("> Start Merging ...");

        //  we have partial_docid + partial_termfreq + partial_vocabulary

        // create an heap structure with the first element of each partial_vocabulary
        PriorityQueue<String> heap = new PriorityQueue<>();

        int i = 0;
        do {
            // read the first element of each partial_vocabulary
            // add it to the heap

            FileChannel channelVocabulary = GetCorrectChannel(i, 0);
            mappedByteBuffer = channelVocabulary.map(FileChannel.MapMode.READ_ONLY, 0, 20);
            String[] term = StandardCharsets.UTF_8.decode(mappedByteBuffer).toString().split("\0");
            //System.out.println(term[0]);
            heap.add(term[0]);

            i++;

        } while (i < blockNumber);

        // create the final_vocabulary
        CreateFinalStructure();







    }
}
