package it.unipi.dii.aide.mircv.query;

import it.unipi.dii.aide.mircv.models.Configuration;
import it.unipi.dii.aide.mircv.models.Posting;
import it.unipi.dii.aide.mircv.models.PostingList;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static it.unipi.dii.aide.mircv.query.queryHandler.executeQuery;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class queryHandlerTest {

    @Test
    void executeQuery() throws IOException {

        // create a String as a query
        String query = "Test query for the search engine";

        ArrayList<String> tokens = queryHandler.QueryPreProcessing(query);

        System.out.println("Query tokens: " + tokens);

        // BM25 + Conjunctive + MaxScore
        Configuration configuration = new Configuration();
        configuration.setScoreON(true); // BM25
        configuration.setConjunctiveON(true); // Conjunctive
        configuration.setMaxScoreON(true); // MaxScore

        queryHandler.executeQuery(tokens, 5);

        // TFIDF + Conjunctive + MaxScore
        configuration.setScoreON(false); // TFIDF
        configuration.setConjunctiveON(true); // Conjunctive
        configuration.setMaxScoreON(true); // MaxScore

        queryHandler.executeQuery(tokens, 5);

        // BM25 + Disjunctive + MaxScore
        configuration.setScoreON(true); // BM25
        configuration.setConjunctiveON(false); // Disjunctive
        configuration.setMaxScoreON(true); // MaxScore

        queryHandler.executeQuery(tokens, 5);

        // TFIDF + Disjunctive + MaxScore
        configuration.setScoreON(false); // TFIDF
        configuration.setConjunctiveON(false); // Disjunctive
        configuration.setMaxScoreON(true); // MaxScore

        queryHandler.executeQuery(tokens, 5);

        // BM25 + Conjunctive + DAAT
        configuration.setScoreON(true); // BM25
        configuration.setConjunctiveON(true); // Conjunctive
        configuration.setMaxScoreON(false); // DAAT

        queryHandler.executeQuery(tokens, 5);

        // TFIDF + Conjunctive + DAAT
        configuration.setScoreON(false); // TFIDF
        configuration.setConjunctiveON(true); // Conjunctive
        configuration.setMaxScoreON(false); // DAAT

        queryHandler.executeQuery(tokens, 5);

        // BM25 + Disjunctive + DAAT
        configuration.setScoreON(true); // BM25
        configuration.setConjunctiveON(false); // Disjunctive
        configuration.setMaxScoreON(false); // DAAT

        queryHandler.executeQuery(tokens, 5);

        // TFIDF + Disjunctive + DAAT
        configuration.setScoreON(false); // TFIDF
        configuration.setConjunctiveON(false); // Disjunctive
        configuration.setMaxScoreON(false); // DAAT

        queryHandler.executeQuery(tokens, 5);

    }

}