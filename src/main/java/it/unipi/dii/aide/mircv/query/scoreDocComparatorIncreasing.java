package it.unipi.dii.aide.mircv.query;
import java.util.Comparator;


public class scoreDocComparatorIncreasing implements Comparator<scoreDoc> {
    @Override
    public int compare(scoreDoc o1, scoreDoc o2) {
        // ascending order of score and ascending order of docID
        if (o1.score < o2.score) {
            return -1;
        } else if (o1.score > o2.score) {
            return 1;
        } else {
            if (o1.docID < o2.docID) {
                return -1;
            } else {
                return 1;
            }
        }
    }


}
