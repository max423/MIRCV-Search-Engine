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

    static int k;

    // iterative process to evaluate the system performance by changing the configuration
    // create a Result TracEval file for all query in msmarco-test-queries.tsv file
    // and run trec_eval tool to evaluate the system performance
    public static void main(String[] args) throws IOException {
        takeFinalRAF();
        loadFinalStructure();


        while (true){
            ArrayList<String> tokens;
            long startTime, stopTime;
            Scanner scanner = new Scanner(System.in);
            String query;
            configureSearchEngine(scanner);

            // setta k da tastiera
            System.out.println("Set k:");
            while (true) {
                try {
                    k = scanner.nextInt();
                    break;
                } catch (java.util.InputMismatchException e) {
                    System.out.println("Invalid input.");
                    scanner.nextLine();
                }
            }

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
            PrintWriter writer = new PrintWriter(ResultQueryPath);
            writer.print("");
            writer.close();

        }
    }

    // create a Result TracEval file for all query in msmarco-test-queries.tsv file
    // according to the format : <query_id Q0 doc_id rank score STANDARD>
    private static void writeResultOnFile(String queryId, PriorityQueue<scoreDoc> returnPriorityQueue) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ResultQueryPath, true)))
        {
            int rank = 1;
            int g = k;

            // <query_id Q0 doc_id rank score STANDARD>
            while (!returnPriorityQueue.isEmpty() && g > 0){
                // returnPriorityQueue size
                System.out.println(returnPriorityQueue.size());
                scoreDoc doc = returnPriorityQueue.poll();
                String pid = documentIndex.get(doc.getDocID()).getDocno();
                String resultLine = queryId + " Q0 " + pid + " " + rank + " " + doc.getScore() + " STANDARD\n";
                writer.write(resultLine);
                rank++;
                g--;

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

            // Command to run trec_eval
            String command = "/usr/local/bin/trec_eval -m all_trec " + AbsolutePathfileQrels + " " + AbsolutePathfileResult;
            //String command = "/usr/local/bin/trec_eval -m ndcg_cut -m map -m recip_rank -m recall.10,100,1000 -M1000  " + AbsolutePathfileQrels + " " + AbsolutePathfileResult;

            // run the command
            Process process = Runtime.getRuntime().exec(command);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/it/unipi/dii/aide/mircv/resources/evaluation.txt", true))) {

                // scrivi il comafigurazione
                writer.write("--------------------------------------------------\n");
                writer.write("Stemming & Stopword = " + Configuration.isStemming_stopwordON()+ "\n" );

                if (Configuration.isConjunctiveON())
                    writer.write("Conjunctive = ON \n");
                else
                    writer.write("Disjunctive = ON \n");

                if (Configuration.isScoreON())
                    writer.write("BM25 = ON \n");
                else
                    writer.write("TFIDF = ON \n");

                // write the evaluation result pn file
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);

                        writer.write(line + "\n");
                    }
                }

                // check tutto ok
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
