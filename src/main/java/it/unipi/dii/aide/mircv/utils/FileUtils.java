package it.unipi.dii.aide.mircv.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class FileUtils {
    public static int MAX_TERM_LENGTH = 20; // in bytes
    // path stop words
    public static String Path_StopWords = "src/main/java/it/unipi/dii/aide/mircv/resources/stopwords.txt"; // https://gist.github.com/larsyencken/1440509
    // path Uncompressed collection
    public static String Path_Uncompressed_Collection = "src/main/java/it/unipi/dii/aide/mircv/resources/collection_prova.tsv";
    // path Compressed collection
    public static String Path_Compressed_Collection = "/Users/massimo/Desktop/collection.tar.gz";
    // path to the configuration json file
    public static String Path_Configuration = "src/main/java/it/unipi/dii/aide/mircv/resources/configuration.json";




    // path to the document index
    public static String Path_DocumentIndex = "src/main/resources/document_index";

    // path to the Partial Vocabulary
    public static String Path_PartialVocabulary = "src/main/resources/partial_vocabulary";
    // path to the Partial Posting-DocId
    public static String Path_PartialDocId = "src/main/resources/partial_docid";
    // path to the Partial Postings-TermFreq
    public static String Path_PartialTermFreq = "src/main/resources/partial_termfreq";

    public static RandomAccessFile docIndex_RAF;

    public static final HashMap<Integer, ArrayList<RandomAccessFile>> skeleton_RAF = new HashMap<>();

    // clear data folder
    public static void clearDataFolder() {
        System.out.println("Clearing data folder...");
        File dataFolder = new File("src/main/resources");
        if (dataFolder.exists()) {
            for (File file : dataFolder.listFiles()) {
                file.delete();
            }
        }
    }


    // read the collection according to the compression flag
    public static BufferedReader initBuffer(boolean compressed) throws IOException {

        if(compressed) {
            //read from compressed collection
            TarArchiveInputStream tarInput = null;
            try {
                tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(FileUtils.Path_Compressed_Collection)));
                tarInput.getNextTarEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (tarInput == null) {
                System.out.println("Cannot access to the collection.");
                System.exit(-1);
            }
            return new BufferedReader(new InputStreamReader(tarInput, StandardCharsets.UTF_8));
        }
        //read from uncompressed collection
        return Files.newBufferedReader(Paths.get(FileUtils.Path_Uncompressed_Collection), StandardCharsets.UTF_8);
    }

    // inizialize the docIndex_RAF
    public static void initDocIndex_RAF() throws IOException {
        docIndex_RAF = new RandomAccessFile(new File(Path_DocumentIndex), "rw");
    }

    public static void createTempFile(int blockNum) {
        // temp file for data structure in spimi run : partial termlist, partial vocabulary, partial postings Per block

        ArrayList<RandomAccessFile> array_RAF = new ArrayList<>();
        try {
            array_RAF.add(new RandomAccessFile(new File(Path_PartialVocabulary + blockNum), "rw"));
            array_RAF.add(new RandomAccessFile(new File(Path_PartialDocId + blockNum), "rw"));
            array_RAF.add(new RandomAccessFile(new File(Path_PartialTermFreq + blockNum), "rw"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // add to the skeleton
        skeleton_RAF.put(blockNum, array_RAF);

    }
}