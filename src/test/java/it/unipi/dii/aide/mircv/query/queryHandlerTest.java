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
        String query1 = "First Test query for the search engine";
        String query2 = "Second Test query for the search engine";
        String query3 = "Third Test query for the search engine";

        ArrayList<String> tokens1 = queryHandler.QueryPreProcessing(query1);
        ArrayList<String> tokens2 = queryHandler.QueryPreProcessing(query2);
        ArrayList<String> tokens3 = queryHandler.QueryPreProcessing(query3);

        System.out.println("Query tokens: " + tokens1);
        System.out.println("Query tokens: " + tokens2);
        System.out.println("Query tokens: " + tokens3);

        // BM25 + Conjunctive
        Configuration configuration = new Configuration();
        configuration.setScoreON(true); // BM25
        configuration.setConjunctiveON(true); // Conjunctive

        queryHandler.executeQuery(tokens1, 5);
        queryHandler.executeQuery(tokens2, 5);
        queryHandler.executeQuery(tokens3, 5);

        // TFIDF + Conjunctive
        configuration.setScoreON(false); // TFIDF
        configuration.setConjunctiveON(true); // Conjunctive

        queryHandler.executeQuery(tokens1, 5);
        queryHandler.executeQuery(tokens2, 5);
        queryHandler.executeQuery(tokens3, 5);

        // BM25 + Disjunctive
        configuration.setScoreON(true); // BM25
        configuration.setConjunctiveON(false); // Disjunctive

        queryHandler.executeQuery(tokens1, 5);
        queryHandler.executeQuery(tokens2, 5);
        queryHandler.executeQuery(tokens3, 5);

        // TFIDF + Disjunctive
        configuration.setScoreON(false); // TFIDF
        configuration.setConjunctiveON(false); // Disjunctive

        queryHandler.executeQuery(tokens1, 5);
        queryHandler.executeQuery(tokens2, 5);
        queryHandler.executeQuery(tokens3, 5);

    }

}