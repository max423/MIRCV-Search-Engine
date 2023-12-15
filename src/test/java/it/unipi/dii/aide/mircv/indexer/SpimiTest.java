package it.unipi.dii.aide.mircv.indexer;

import it.unipi.dii.aide.mircv.utils.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class SpimiTest {

    @Test
    void startIndexer() throws IOException {
        Spimi spimi = new Spimi();
        boolean run = spimi.startIndexer();

        public void testStartIndexer() {
            try {
                // Create a sample input for testing
                String sampleInput = "doc1\tThis is the content of document 1\n" +
                        "doc2\tAnother document with some text\n" +
                        "doc3\tAnd a third document for testing\n";

                // Use a StringReader to simulate reading from a file
                BufferedReader bufferedReader = new BufferedReader(new StringReader(sampleInput));

                // Set the docIndex_RAF to a mock instance or mock the relevant methods
                FileUtils.docIndex_RAF = new MockRandomAccessFile();

                // Set the MEMORYFree_THRESHOLD to a small value for testing purposes

                spimiInstance.MEMORYFree_THRESHOLD = 1;

                // Call the startIndexer method
                boolean result = spimiInstance.startIndexer(bufferedReader);

                // Assert that the method returned true (assuming it returns a boolean)
                assertTrue(result);


            } catch (IOException e) {
                e.printStackTrace();
                fail("IOException occurred during testing.");
            }
        }






    }
}