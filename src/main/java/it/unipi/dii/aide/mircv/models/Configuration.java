package it.unipi.dii.aide.mircv.models;

public class Configuration {
    // compression
    private static boolean compressionON = true;
    // stemming
    private static boolean stemming_stopwordON = false;

    public Configuration(boolean compressionON, boolean stemming_stopwordON) {
        this.compressionON = compressionON;
        this.stemming_stopwordON = stemming_stopwordON;
    }

    public static boolean isCompressionON() {
        return compressionON;
    }

    public static boolean isStemming_stopwordON() {
        return stemming_stopwordON;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "compressionON=" + compressionON +
                ", stemming_stopwordON=" + stemming_stopwordON +
                '}';
    }
}
