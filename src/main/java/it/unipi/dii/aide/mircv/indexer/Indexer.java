package it.unipi.dii.aide.mircv.indexer;


import it.unipi.dii.aide.mircv.models.DocumentIndexElem;
import it.unipi.dii.aide.mircv.utils.FileUtils;
import it.unipi.dii.aide.mircv.indexer.Spimi;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import it.unipi.dii.aide.mircv.utils.FileUtils;
import it.unipi.dii.aide.mircv.models.Configuration;


public class Indexer {

    public static void main(String[] args) throws IOException {

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







