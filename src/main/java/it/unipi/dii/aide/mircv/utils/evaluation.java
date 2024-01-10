package it.unipi.dii.aide.mircv.utils;

import com.google.gson.FieldAttributes;
import it.unipi.dii.aide.mircv.models.Configuration;
import it.unipi.dii.aide.mircv.query.queryHandler;
import it.unipi.dii.aide.mircv.query.scoreDoc;

import javax.swing.plaf.ProgressBarUI;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static it.unipi.dii.aide.mircv.Main.configureSearchEngine;
import static it.unipi.dii.aide.mircv.utils.FileUtils.*;

public class evaluation {

    //private static Boolean queryType;
    //private static Boolean scoringFunction;

    static int k = 30; // # doc to retrieve
    static String TestQueryPath = "src/main/java/it/unipi/dii/aide/mircv/resources/msmarco-test2020-queries.tsv";
    static String ResultQueryPath ="src/main/java/it/unipi/dii/aide/mircv/resources/resultTrecQeury.txt";
    static String QrelPath = "src/main/java/it/unipi/dii/aide/mircv/resources/2020qrels-pass.txt";

    public static void main(String[] args) throws IOException {
        takeFinalRAF();
        loadFinalStructure();


        while (true){
            ArrayList<String> tokens;
            long startTime, stopTime;
            Scanner scanner = new Scanner(System.in);
            String query;
            configureSearchEngine(scanner);

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(TestQueryPath), StandardCharsets.UTF_8));
            query = reader.readLine();

            ArrayList<Long> queryTime = new ArrayList<>();

            while (query != null){

                String[] queryTokens = query.split("\t");

                // print query
                //System.out.println("Query ID: " + queryTokens[0]);
                //System.out.println("Query: " + queryTokens[1]);

                List<String> queryList = new ArrayList<>(Arrays.asList(queryTokens));
                queryList.remove(0);

                // convert to document without id
                String finalQuery = String.join(",", queryList);

                tokens = queryHandler.QueryPreProcessing(finalQuery);

                // execute query
                PriorityQueue<scoreDoc> result = queryHandler.returnPriorityQueue(tokens, k);

                // create a qrel file
                writeResultOnFile(queryTokens[0], result);

                query = reader.readLine();
                tokens.clear();
            }
            System.out.println("Query result written on file ");

            // run trec_eval
            runTrecEval(ResultQueryPath, QrelPath);

            // clean result file
//            PrintWriter writer = new PrintWriter(ResultQueryPath);
//            writer.print("");
//            writer.close();

        }



    }

    // create a Result TracEval file for all query in msmarco-test-queries.tsv file
    // according to the format : <query_id Q0 doc_id rank score STANDARD>
    private static void writeResultOnFile(String queryId, PriorityQueue<scoreDoc> returnPriorityQueue) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ResultQueryPath, true)))
        {
            int rank = 1;
            int k = 30;
            // <query_id Q0 doc_id rank score STANDARD>

            while (!returnPriorityQueue.isEmpty() && k > 0){
                // returnPriorityQueue size
                System.out.println(returnPriorityQueue.size());
                scoreDoc doc = returnPriorityQueue.poll();
                String pid = documentIndex.get(doc.getDocID()).getDocno();
                String resultLine = queryId + " Q0 " + pid + " " + rank + " " + doc.getScore() + " STANDARD\n";
                writer.write(resultLine);
                rank++;
                k--;

                //System.out.println(resultLine);
            }

        }
    }

    // compare ResultQuery of our system file with Qrel file
    // using trec_eval tool to evaluate the system performance
    // command : trec_eval -m all_trec qrels.dev.tsv resultTrecQeury.txt
    public static void runTrecEval(String resultsFile, String qrelsFile) {
        try {
            // get absolute path of the file
            File fileQrels = new File(qrelsFile);
            String AbsolutePathfileQrels = fileQrels.getAbsolutePath();
            File fileResult = new File(resultsFile);
            String AbsolutePathfileResult = fileResult.getAbsolutePath();

            // Costruisci il comando per eseguire trec_eval
            String command = "/usr/local/bin/trec_eval -m all_trec " + AbsolutePathfileQrels + " " + AbsolutePathfileResult;
            //String command = "/usr/local/bin/trec_eval -m ndcg_cut -m map -m recip_rank -m recall.10,100,1000 -M1000  " + AbsolutePathfileQrels + " " + AbsolutePathfileResult;

            // Esegui il comando
            Process process = Runtime.getRuntime().exec(command);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/it/unipi/dii/aide/mircv/resources/evaluation.txt", true))) {
                // scrivi il comafigurazione
                writer.write("--------------------------------------------------\n");
                writer.write("isConjunctive = " + Configuration.isConjunctiveON() + "\n" + "Stemming & Stopword = " + Configuration.isStemming_stopwordON()
                        + "\n" + "BM25ON ="+ Configuration.isScoreON() + "\n");

                // Leggi l'output del processo
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        // e salva il risultato in un file
                        writer.write(line + "\n");
                    }
                }

                // Leggi l'errore del processo
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
