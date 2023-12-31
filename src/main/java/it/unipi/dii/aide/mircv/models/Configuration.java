package it.unipi.dii.aide.mircv.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unipi.dii.aide.mircv.utils.FileUtils;

import java.io.FileReader;
import java.io.IOException;

public class Configuration {
    // compression
    private static boolean compressionON;
    // stemming
    private static boolean stemming_stopwordON;

    // compressione index
    private static boolean index_compressionON;

    // testing
    private static boolean testing_ON ;

    // score function TODO SIMO ? true = BM25 vs false= TFIDF
    private static boolean scoreON;

    // max score TODO SIMO ? MaxScore vs DAAT
    private static boolean maxScoreON;

    // true = conjunctive -  false= disjunctive
    private static boolean conjunctiveON;

    static {

        try {
            System.out.println("Reading configuration");
            System.out.println("--------------------------------------------------");
            // read json configuration file
            JsonObject jsonObject = JsonParser.parseReader(new FileReader("src/main/java/it/unipi/dii/aide/mircv/resources/configuration.json")).getAsJsonObject();
            //JsonObject jsonObject = JsonParser.parseReader(new FileReader("/Users/massimo/Desktop/MIRCV-Project/MIRCV-Project/src/main/java/it/unipi/dii/aide/mircv/resources/configuration.json")).getAsJsonObject();
            compressionON = jsonObject.get("read_compressionON").getAsBoolean();
            stemming_stopwordON = jsonObject.get("stemming_stopwordON").getAsBoolean();
            index_compressionON = jsonObject.get("index_compressionON").getAsBoolean();
            testing_ON = jsonObject.get("testing_ON").getAsBoolean();
            // print configuration
            System.out.println("Compressed Reading = " + compressionON + "\n" + "Stemming & Stopword = " + stemming_stopwordON + "\n" + "Index Compression = " + index_compressionON + "\n" + "Testing = " + testing_ON);
            System.out.println("--------------------------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Configuration() {
    }
    public Configuration(boolean compressionON, boolean stemming_stopwordON, boolean index_compressionON, boolean testing_ON) {
        this.compressionON = compressionON;
        this.stemming_stopwordON = stemming_stopwordON;
        this.index_compressionON = index_compressionON;
        this.testing_ON = testing_ON;
    }


    public static boolean isCompressionON() {
        return compressionON;
    }

    public static boolean isStemming_stopwordON() {
        return stemming_stopwordON;
    }

    public static boolean isIndex_compressionON() {return index_compressionON;}

    public static boolean isTesting() { return testing_ON;}
    public static boolean isScoreON() {
        return scoreON;
    }

    public static boolean isMaxScoreON() {
        return maxScoreON;
    }

    public static boolean isConjunctiveON() {
        return conjunctiveON;
    }

    public void setConjunctiveON(boolean compressionON) {
        this.conjunctiveON = compressionON;
    }

    public void setMaxScoreON(boolean maxScoreON) {
        this.maxScoreON = maxScoreON;
    }

    public void setScoreON(boolean scoreON) {
        this.scoreON = scoreON;
    }






    @Override
    public String toString() {
        return "Configuration{" +
                "read_compressionON=" + compressionON +
                ", stemming_stopwordON=" + stemming_stopwordON +
                ", index_compressionON=" + index_compressionON +
                ", testing_ON=" + testing_ON +
                ", scoreON=" + scoreON +
                ", maxScoreON=" + maxScoreON +
                ", conjunctiveON=" + conjunctiveON +
                '}';
    }
}
