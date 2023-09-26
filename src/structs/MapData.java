package structs;

public class MapData {
    public int docId;
    public String fileName;

    public MapData(int docId, String fileName) {
        this.docId = docId;
        this.fileName = fileName;
    }

    public MapData(String str, int offset) {
        this.docId = Integer.parseInt(str.substring(0, offset).trim());
        this.fileName = str.substring(offset).trim();
    }

    void print(){
        System.out.println("DocID: " + this.docId + ", FileName: " + this.fileName);
    }
}
