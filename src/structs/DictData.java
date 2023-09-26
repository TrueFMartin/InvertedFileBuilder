package structs;

public class DictData {
    public final String term;
    public final int numDocs;
    public final int start;

    DictData(String term, int numDocs, int start) {
        this.term = term;
        this.numDocs = numDocs;
        this.start = start;
    }

    void print(){
        System.out.println("Term: " + this.term + ", NumDocs: " + this.numDocs + ", Start: " + this.start);
    }
}

