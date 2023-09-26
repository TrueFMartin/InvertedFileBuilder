package readwrite;

import structs.MapData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class MapFile implements ReadWriter<MapData> {

    private final static int DOC_ID_LENGTH = 4;
    private final static int FILE_NAME_LENGTH = 12;
    private final static int RECORD_SIZE = DOC_ID_LENGTH + FILE_NAME_LENGTH;
    private final static String OUT_FILE_NAME = "map.txt";
    private final RandomAccessFile stream;
    private int numRecords;

    MapFile() {
        this("r");
    }

    MapFile(String mode) {
        try {
            this.stream = new RandomAccessFile(OUT_FILE_NAME, mode);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    MapFile(RandomAccessFile stream) {
        this.stream = stream;
    }

    @Override
    public void write(MapData data) {
        String docIDFormat = "%" + DOC_ID_LENGTH + "s" + " ";
        String fileNameFormat = "%" + FILE_NAME_LENGTH + "s" + "\n";
        try {
            stream.write(String.format(docIDFormat, String.valueOf(data.docId)
                    .substring(0, Math.min(String.valueOf(data.docId).length(), DOC_ID_LENGTH))).getBytes());
            stream.write(String.format(fileNameFormat, data.fileName
                    .substring(0, Math.min(data.fileName.length(), FILE_NAME_LENGTH))).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        numRecords += 1;
    }

    @Override
    public structs.MapData read(int recordNum) {
        if ((recordNum < 0) && (recordNum >= numRecords)) {
            return null;
        }

        try {
            stream.seek(0); // return to the top of the fill
            stream.skipBytes(recordNum * RECORD_SIZE);
            String line = stream.readLine();
            return new MapData(line, DOC_ID_LENGTH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getConfig() {
        return this.numRecords;
    }
}
