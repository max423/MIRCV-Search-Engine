package it.unipi.dii.aide.mircv.text_processing;

import it.unipi.dii.aide.mircv.utils.FileUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextProcessingTest {



    @Test
    void documentProcessing() {



    }

    @Test
    void tokenize() {
        // test 1
        String doc = "The presence of communication amid scientific minds was equally important to the success of the Manhattan Project as scientific intellect was. The only cloud hanging over the impressive achievement of the atomic researchers and engineers is what their success truly meant; hundreds of thousands of innocent lives obliterated.";
        String[] tokens = TextProcessing.tokenize(doc);
        String[] testTokens = {"The", "presence", "of", "communication", "amid", "scientific", "minds", "was", "equally", "important", "to", "the", "success", "of", "the", "Manhattan", "Project", "as", "scientific", "intellect", "was.", "The", "only", "cloud", "hanging", "over", "the", "impressive", "achievement", "of", "the", "atomic", "researchers", "and", "engineers", "is", "what", "their", "success", "truly", "meant;", "hundreds", "of", "thousands", "of", "innocent", "lives", "obliterated."};
        assertArrayEquals(testTokens, tokens);

        // test 2
        doc = "The Manhattan Project and its atomic bomb helped bring an end to World War II. Its legacy of peaceful uses of atomic energy continues to have an impact on history and science.";
        tokens = TextProcessing.tokenize(doc);
        String[] testTokens2 = {"The", "Manhattan", "Project", "and", "its", "atomic", "bomb", "helped", "bring", "an", "end", "to", "World", "War", "II.", "Its", "legacy", "of", "peaceful", "uses", "of", "atomic", "energy", "continues", "to", "have", "an", "impact", "on", "history", "and", "science."};
        assertArrayEquals(testTokens2, tokens);

    }

    @Test
    void trouncateToken() {
        // MAX_TERM_LENGTH = 20;
        // test 1: lunghezza del token è inferiore a MAX_TERM_LENGTH
        String shortToken = "short";
        String resultShortToken = TextProcessing.trouncateToken(shortToken);
        assertEquals(shortToken, resultShortToken);

        // test 2: lunghezza del token è uguale a MAX_TERM_LENGTH
        String maxToken = "thisisamaxtoken12345";
        String resultMaxToken = TextProcessing.trouncateToken(maxToken);
        assertEquals(maxToken, resultMaxToken);
        System.out.println("maxToken: " + maxToken);
        System.out.println("resultMaxToken: " + resultMaxToken);

        // Caso in cui la lunghezza del token è superiore a MAX_TERM_LENGTH
        String longToken = "thisisaverylongtokenthisisaverylongtoken";
        String expectedResult = longToken.substring(0, FileUtils.MAX_TERM_LENGTH);
        String resultLongToken = TextProcessing.trouncateToken(longToken);
        assertEquals(expectedResult, resultLongToken);
        System.out.println("longToken: " + longToken);
        System.out.println("resultLongToken: " + resultLongToken);

    }

    @Test
    void stemming() {
        // test 1
        String[] inputTokens = {"running", "jumping", "swimming"};

        String[] resultTokens = TextProcessing.stemming(inputTokens);

        assertEquals("run", resultTokens[0]);
        assertEquals("jump", resultTokens[1]);
        assertEquals("swim", resultTokens[2]);
    }

    @Test
    void testRemoveStopWords() {
        // test
        String[] inputTokens = {"the", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog"};
        String[] resultTokens = TextProcessing.removeStopWords(inputTokens);
        String[] expectedTokens = {"quick", "brown", "fox", "jumps", "lazy", "dog"};
        assertArrayEquals(expectedTokens, resultTokens);
    }




}