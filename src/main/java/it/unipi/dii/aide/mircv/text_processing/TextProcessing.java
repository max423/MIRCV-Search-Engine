package it.unipi.dii.aide.mircv.text_processing;
import it.unipi.dii.aide.mircv.utilities.FileUtills;

import static it.unipi.dii.aide.mircv.utilities.FileUtills.MAX_TERM_LENGTH;

public class TextProcessing {

    // Regex for removing punctuation and strange characters
    private static final String CharREGEX = "[^a-zA-Z0-9\\s]";

    // Regex for splitting a string into tokens
    private static final String SPLIT_REGEX = "\\s+";


    /**
     * Tokenize a string
     * @param Doc the string to tokenize
     * @return an array of tokens
     */
    public static String[] tokenize(String Doc) {
        // lowercase
        Doc = Doc.toLowerCase();
        // remove punctuation and strange characters
        Doc = Doc.replaceAll(CharREGEX, " ");
        // split into tokens
        return Doc.split(SPLIT_REGEX);
    }

    // Trocare i token piu lunghi di MAX_TERM_LENGTH
    public static String trouncateToken(String token) {
        if (token.length() > FileUtills.MAX_TERM_LENGTH) {
            return token.substring(0,FileUtills.MAX_TERM_LENGTH );
        }
        return token;
    }
}
