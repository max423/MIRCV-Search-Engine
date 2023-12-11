package it.unipi.dii.aide.mircv.text_processing;
import it.unipi.dii.aide.mircv.utils.FileUtils;
import it.unipi.dii.aide.mircv.models.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;

public class TextProcessing {
    // Regex for removing html tags
    private static final String HTML_REGEX = "<[^>]+>";

    // Regex for removing punctuation and strange characters
    private static final String CharREGEX = "[^a-zA-Z0-9\\s]";

    // Regex for split string into tokens
    private static final String SPLIT_REGEX = "\\s+";

    // Regex for removing 4 or more consecutive characters
    private static final String CONSECUTIVE_REGEX = "(.)\\1{3,}";

    private static final ste

    // we use a hashset because contains() is O(1)
    private static final HashSet<String> stopWords;


    static {
        try {
            stopWords = new HashSet<>(Files.readAllLines(Paths.get(FileUtils.Path_StopWords)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Process a document
     * @param doc document to process
     * @return array of tokens
     */
    public static ArrayList<String> DocumentProcessing(String doc) throws IOException{
        // 1) preprocessing document
        doc = cleanText(doc);

        // 2) tokenization
        ArrayList<String> tokens = tokenize(doc);

        // 3) stemming + stopword removal
        if (Configuration.isStemming_stopwordON()) {
            tokens = removeStopWords(tokens);
            tokens = stemming(tokens);
        }
        return tokens;
    }

    private static String cleanText(String doc) {
        // remove html tags
        doc = doc.replaceAll(HTML_REGEX, "");

        // remove punctuation and strange characters
        doc = doc.replaceAll(CharREGEX, " ");

        // remove multiple spaces
        doc = doc.replaceAll("\\s+", " ");

        // remove 3+ consecutive characters
        doc = doc.replaceAll(CONSECUTIVE_REGEX, "");

        // lowercase
        doc = doc.toLowerCase();

        return doc;
    }

    /**
     * Tokenize a string
     * @param Doc the string to tokenize
     * @return an array of tokens
     */
    public static ArrayList<String> tokenize(String Doc) {
        String[] tokens = Doc.split(SPLIT_REGEX);
        ArrayList<String> tokensList = new ArrayList<>();
        for (String token : tokens) {
            token = trouncateToken(token);
            if (!token.isEmpty()) { // CHECK
                tokensList.add(token);
            }
        }
        return tokensList;
    }

    // Trocare i token piu lunghi di MAX_TERM_LENGTH
    public static String trouncateToken(String token) {
        if (token.length() > FileUtils.MAX_TERM_LENGTH) {
            return token.substring(0,FileUtils.MAX_TERM_LENGTH );
        }
        return token;
    }

    private static ArrayList<String> stemming(ArrayList<String> tokens) {
    }

    private static ArrayList<String> removeStopWords(ArrayList<String> tokens) {
        ArrayList<String> tokensList = new ArrayList<>();
        for (String token : tokens) {
            if (!stopWords.contains(token)) {
                tokensList.add(token);
            }
        }
        return tokensList;
    }





}
