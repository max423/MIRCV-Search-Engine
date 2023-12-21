package it.unipi.dii.aide.mircv.indexer;


import it.unipi.dii.aide.mircv.models.*;
import it.unipi.dii.aide.mircv.utils.FileUtils;
import it.unipi.dii.aide.mircv.indexer.Spimi;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import static it.unipi.dii.aide.mircv.utils.FileUtils.GetCorrectChannel;


public class Indexer {
    static Integer blockNumber = 0;

    public static void main(String[] args) throws IOException {

        System.out.println("> Start Indexing ...");

        // delete old index files
        FileUtils.clearDataFolder();

        // start Spimi algorithm
        Spimi spimi = new Spimi();
        blockNumber= spimi.startIndexer();
        System.out.println("Number of blocks generated: " + blockNumber);

        Merger merger = new Merger();
        merger.startMerger(blockNumber);

        PlotFinalStructure();

    }

    private static void PlotFinalStructure() throws IOException {
        System.out.println("> Plotting final Structure ...");

        // offset corrente
        long currentOffset = 0;

        // take channel
        FileChannel channelVocabulary = FileUtils.GetCorrectChannel(-1, 0);
        FileChannel channelDocID = FileUtils.GetCorrectChannel(-1, 1);
        FileChannel channelTermFreq = FileUtils.GetCorrectChannel(-1, 2);

        // take the size of the file
        while (true) {
            try {
                if (!((currentOffset + 56) < channelVocabulary.size())) break;
            } catch (IOException e) {
                e.printStackTrace();
            }

            // read the vocabulary element
            VocabularyElem vocabularyElem = new VocabularyElem();
            vocabularyElem.readFromDisk(channelVocabulary, currentOffset);

            // print the vocabulary element
            System.out.println(vocabularyElem);
            // update the offset
            currentOffset += 56;

            // read the posting list
            PostingList postingList = new PostingList(vocabularyElem.getTerm());
            postingList.readFromDisk(channelDocID, channelTermFreq, vocabularyElem.getDocIdsOffset(), vocabularyElem.getTermFreqOffset(), vocabularyElem.getDocIdsLen(), vocabularyElem.getTermFreqLen());

            // print the posting list
            System.out.println(postingList);

        }

        // print the collection statistics
        CollectionStatistics collectionStatistics = new CollectionStatistics();
        collectionStatistics.readFromDisk(FileUtils.GetCorrectChannel(-1, 3), 0);
        System.out.println(collectionStatistics);
    }


    public static void printDocumentIndex() throws IOException {
        int position = 0;
        DocumentIndexElem doc_elem = new DocumentIndexElem();

        FileChannel DocIndexFC = new RandomAccessFile(FileUtils.Path_DocumentIndex, "rw").getChannel();

        while (position < (int)(DocIndexFC.size())) {
            System.out.println("Position: " + position);
            doc_elem = new DocumentIndexElem();
            doc_elem.readFromDisk(DocIndexFC, position);
            System.out.println(doc_elem.toString());
            System.out.printf("Docid: %d, DocNo: %s, Lenght: %d\n",doc_elem.getDocId(), doc_elem.getDocno(), doc_elem.getLength());

            position += 28;
        }
    }

}







