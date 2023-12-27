package it.unipi.dii.aide.mircv.indexer;

import it.unipi.dii.aide.mircv.models.Posting;
import it.unipi.dii.aide.mircv.models.PostingList;
import it.unipi.dii.aide.mircv.models.VocabularyElem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class IndexerTest {

    // docs
//    0	The presence of communication amid scientific minds was equally important to the success of the Manhattan Project as scientific intellect was. The only cloud hanging over the impressive achievement of the atomic researchers and engineers is what their success truly meant; hundreds of thousands of innocent lives obliterated.
//    1	The Manhattan Project and its atomic bomb helped bring an end to World War II. Its legacy of peaceful uses of atomic energy continues to have an impact on history and science.
//    2	Essay on The Manhattan Project - The Manhattan Project The Manhattan Project was to see if making an atomic bomb possible. The success of this project would forever change the world forever making it known that something this powerful can be manmade.

    // Manhattan, df = 3, cf = 5
    // Manhattan, (1, 1), (2, 1), (3, 3)

    // Project, df = 3, cf = 6
    // Project, (1, 1), (2, 1), (3, 4)

    // atomic, df = 3, cf = 4
    // atomic, (1, 1), (2,2) , (3, 1)

    // bomb, df = 2, cf = 2
    // bomb, (2, 1), (3, 1)

    // World, df = 2, cf = 2
    // World, (2, 1), (3, 1)


    @BeforeAll
    static void Indexer() throws IOException {
        Indexer indexer = new Indexer();

        indexer.main(null);
        // indexer.PlotFinalStructure();
    }

    @Test
    void TestVocabulary() throws IOException {

        // array of test
        List<VocabularyElem> testVocabulary = Arrays.asList(
                new VocabularyElem("manhattan", 3, 5),
                new VocabularyElem("project", 3, 6),
                new VocabularyElem("atomic", 3, 4),
                new VocabularyElem("bomb", 2, 2),
                new VocabularyElem("world", 2, 2)
        );

        // retrive vocabulary
        for (VocabularyElem vocabularyElem : testVocabulary) {
            VocabularyElem vocabularyElem1 = Indexer.getTestVocabularyElem(vocabularyElem.getTerm());
            System.out.println("VocTest on term: " + vocabularyElem.getTerm());
            assertEquals(vocabularyElem.getTerm(), vocabularyElem1.getTerm());
            assertEquals(vocabularyElem.getDocFreq(), vocabularyElem1.getDocFreq());
            assertEquals(vocabularyElem.getCollFreq(), vocabularyElem1.getCollFreq());
        }
    }

    @Test
    void TestPostingList() throws IOException {
        // array of test
        List<PostingList> testPostingList = Arrays.asList(
                new PostingList("manhattan", new ArrayList<Posting>(Arrays.asList(new Posting(1, 1), new Posting(2, 1), new Posting(3, 3)))),
                new PostingList("project", new ArrayList<Posting>(Arrays.asList(new Posting(1, 1), new Posting(2, 1), new Posting(3, 4)))),
                new PostingList("atomic", new ArrayList<Posting>(Arrays.asList(new Posting(1, 1), new Posting(2, 2), new Posting(3, 1)))),
                new PostingList("bomb", new ArrayList<Posting>(Arrays.asList(new Posting(2, 1), new Posting(3, 1)))),
                new PostingList("world", new ArrayList<Posting>(Arrays.asList(new Posting(2, 1), new Posting(3, 1)))
                )
        );

        // retrive posting list
        for (PostingList postingList : testPostingList) {
            PostingList postingList1 = Indexer.getTestPosting(postingList.getTerm());
            System.out.println("PostTest on term: " + postingList.getTerm());
            assertEquals(postingList.getTerm(), postingList1.getTerm());

            for (int i = 0; i < postingList.getPostingList().size(); i++) {
                assertEquals(postingList.getPostingList().get(i).getDocID(), postingList1.getPostingList().get(i).getDocID());
                assertEquals(postingList.getPostingList().get(i).getTermFreq(), postingList1.getPostingList().get(i).getTermFreq());
            }

        }

    }
}