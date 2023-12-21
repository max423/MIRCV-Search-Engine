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

    // get the vocabulary element with the given term
    public VocabularyElem getVocabularyElem(String term) {
        // check if the element is in the cache
        if (elements.containsKey(term)) {
            return elements.get(term);
        }

        // get the element from the disk
        VocabularyElem elem = VocabularyElem.loadVocabularyElem(term);

        // add the element to the cache
        if (elem != null)
            elements.put(term, elem);

        return elem;
    }

    // load the vocabulary element from disk
    public static VocabularyElem loadVocabularyElem(String term) {

    }
}
