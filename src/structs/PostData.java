package structs;

public class PostData {
    int docId;
    int weight;

    PostData(int docId, int weight) {
        this.docId = docId;
        this.weight = weight;
    }

    void print(){
        System.out.println("DocID: " + this.docId + ", Weight: " + this.weight);
    }
}
