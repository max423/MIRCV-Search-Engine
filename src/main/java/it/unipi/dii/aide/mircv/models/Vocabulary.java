package it.unipi.dii.aide.mircv.models;

import java.util.LinkedHashMap;
import org.junit.platform.commons.util.LruCache;


public class Vocabulary extends LinkedHashMap<String, VocabularyElem> {

    private static Vocabulary instance = null;

    // cache last vocabulary element accessed
    private final static LruCache<String, VocabularyElem> elements= new LruCache<>(1000);

    // path of the vocabulary file NECESSARIO?
    private static final String VOCABULARY_PATH = "vocabulary.txt";

    private Vocabulary() {}

    // singleton
    public static Vocabulary getInstance() {
        if (instance == null) {
            instance = new Vocabulary();
        }
        return instance;
    }

}
