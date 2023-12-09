package it.unipi.dii.aide.mircv.models;

public class Document {
    private int docId;
    private String docno;
    private int length;

    public Document(int docId, String docno, int length) {
        this.docId = docId;
        this.docno = docno;
        this.length = length;
    }

    public int getDocId() {
        return docId;
    }

    public String getDocno() {
        return docno;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "Document{" +
                "docId=" + docId +
                ", docno='" + docno + '\'' +
                ", length=" + length +
                '}';
    }


}
