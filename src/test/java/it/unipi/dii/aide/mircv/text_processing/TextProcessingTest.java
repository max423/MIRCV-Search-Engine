package it.unipi.dii.aide.mircv.text_processing;
import it.unipi.dii.aide.mircv.utils.FileUtils;
import it.unipi.dii.aide.mircv.models.Configuration;
import it.unipi.dii.aide.mircv.text_processing.TextProcessing;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class TextProcessingTest {

    @org.junit.jupiter.api.Test
    void documentProcessing() throws IOException {
        BufferedReader bufferedReader = initBuffer(Configuration.isCompressionON()) ;
        String line;
        int tab;
        while((line = bufferedReader.readLine())!= null) {
            // split on tab
            tab = line.indexOf("\t");

            // check malformed line
            if(tab == -1) {  // tab not found
                continue;}

            // extract
            String docid = line.substring(0, tab);
            String text = line.substring(tab + 1);

            // check empty doc
            if (text.isEmpty())
                continue;

            // process text
            String[] tokens = TextProcessing.DocumentProcessing(text);



            System.out.println("docid: " + docid);
            System.out.println("text: " + text);
            System.out.println("tokens: " + String.join(" ", tokens));
            System.out.println("tokens length: " + tokens.length);




        }

    }



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

    @org.junit.jupiter.api.Test
    void tokenize() {
    }

    @org.junit.jupiter.api.Test
    void trouncateToken() {
    }
}