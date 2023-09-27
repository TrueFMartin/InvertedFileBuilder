public class InvFileTester {
    // Helper Methods
    InvertedFileWriter invertedFileWriter;
    InvertedFileReader invertedFileReader;


    public void writeTestRecords() {
        invertedFileWriter = new InvertedFileWriter();
        invertedFileWriter.openForWriteNew();
        writeMapRecords();
        writePostRecords();
        writeDictRecords();
        invertedFileWriter.closeAfterWriting();
    }

    public void readTestRecords(int...records) {
        invertedFileReader = new InvertedFileReader();
        readMapRecords(records);
        records[2] = 11;
        records[4] = 12;
        readPostRecords(records);
        records[2] = 10;
        records[4] = 11;
        readDictRecords(records);
        invertedFileReader.closeAfterReading();

        System.out.println("Testing reading from a closed inverted file");
        invertedFileReader.readDictRecord(2);
        invertedFileReader.readPostRecord(2);
    }

    private void writeMapRecords() {
        int[] docIds = new int[] {0, 1, 2, 3};
        String[] fileNames = new String[] {"Document0", "Document1", "Document2", "Document3"};
        for (int i = 0; i < 4; i++) {
            invertedFileWriter.writeMapRecord(docIds[i], fileNames[i]);
        }
    }

    private void writePostRecords() {
        int[] docIds = new int[] {0, 1, 2, 3, 2, 2, 0, 0, 1, 3, 2};
        int[] weights = new int[] {1, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1};
        for (int i = 0; i < docIds.length; i++) {
            invertedFileWriter.writePostRecord(docIds[i], weights[i]);
        }
    }

    private void writeDictRecords() {
        String[] terms = new String[] {"empty","ate","doctor","dry","empty","empty","quickly","dog","empty","cat", "empty", "empty", "duck"};
        int[] numDocs = new int[] {-1, 4, 1, 1, -1, -1, 1, 2, -1, 1, -1, -1, 1};
        int[] start = new int[] {-1, 0, 4, 5, -1, -1, 6, 7, -1, 9, -1, -1, 10};
        for (int i = 0; i < terms.length; i++) {
            invertedFileWriter.writeDictRecord(terms[i], numDocs[i], start[i]);
        }
    }

    private void readMapRecords(int[] records) {
        System.out.println("testing read of Map records");
        for(int i: records) {
            invertedFileReader.readMapRecord(i);
        }
    }

    private void readPostRecords(int[] records) {
        System.out.println("testing read of Post records");
        for(int i: records) {
            invertedFileReader.readPostRecord(i);
        }
    }

    private void readDictRecords(int[] records) {
        System.out.println("testing read of Dict records");
        for(int i: records) {
            invertedFileReader.readDictRecord(i);
        }
    }
    public static void main(String[] args) {
        InvFileTester tester = new InvFileTester();
        tester.writeTestRecords();
        tester.readTestRecords(2, 0, 3, -1, 4);

    }
}