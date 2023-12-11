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

        
    }

    @org.junit.jupiter.api.Test
    void tokenize() {
    }

    @org.junit.jupiter.api.Test
    void trouncateToken() {
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


}