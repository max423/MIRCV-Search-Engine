package it.unipi.dii.aide.mircv;
import it.unipi.dii.aide.mircv.indexer.Indexer;
import it.unipi.dii.aide.mircv.indexer.Spimi;
import it.unipi.dii.aide.mircv.models.Configuration;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import static it.unipi.dii.aide.mircv.utils.FileUtils.clearDataFolder;

public class Main {
    public static void main(String[] args) throws IOException {
        Configuration configuration = new Configuration();

        clearDataFolder();

        Spimi spimi = new Spimi();
        spimi.startIndexer();

        Indexer.printDocumentIndex();

    }
}