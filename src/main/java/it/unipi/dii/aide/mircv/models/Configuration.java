package it.unipi.dii.aide.mircv.models;

public class Configuration {
    // compression
    private boolean compressionON;
    // stemming
    private boolean stemming_stopwordON;

    public Configuration(boolean compressionON, boolean stemming_stopwordON) {
        this.compressionON = compressionON;
        this.stemming_stopwordON = stemming_stopwordON;
    }

    public boolean isCompressionON() {
        return compressionON;
    }

    public boolean isStemming_stopwordON() {
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
