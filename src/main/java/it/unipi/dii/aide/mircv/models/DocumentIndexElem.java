package it.unipi.dii.aide.mircv.models;

import java.nio.channels.FileChannel;

public class DocumentIndexElem {
    private int docId;
    private String docNo;   // dalla collection
    private int docLen;  // lunghezza del documento

    public DocumentIndexElem(int docId, String docno, int length) {
        this.docId = docId;
        this.docNo = docno;
        this.docLen = length;
    }

    public int getDocId() {
        return docId;
    }

    public String getDocno() {
        return docNo;
    }

    public int getLength() {
        return docLen;
    }

    @Override
    public String toString() {
        return "DocumentIndexElem{" +
                "docId=" + docId +
                ", docno='" + docNo + '\'' +
                ", length=" + docLen +
                '}';
    }


    public void writeToDisk(FileChannel channel) {
        // 30 (doc_no) + 4 (docId) + 4 (length) = 38 bytes


    }
}
