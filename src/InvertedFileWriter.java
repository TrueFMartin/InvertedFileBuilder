import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

public class InvertedFileWriter {
    static final int TOKEN_SIZE = 14;
    static final int FREQ_SIZE = 4;
    static final int RECORD_SIZE_MAP = TOKEN_SIZE + FREQ_SIZE + 2;
    static final int RECORD_SIZE_DICT = TOKEN_SIZE + FREQ_SIZE + 2;
    static final int RECORD_SIZE_POST = TOKEN_SIZE + FREQ_SIZE + 2;
    static int numRecordsMap = 0;
    static int numRecordsDict = 0;
    static int numRecordsPost = 0;

    static String FILENAME_MAP = "map.txt";
    static String FILENAME_DICT = "dict.txt";
    static String FILENAME_POST = "post.txt";
    static String FILENAME_CONFIG = "config.txt";

    static RandomAccessFile rafMap;
    static RandomAccessFile rafDict;
    static RandomAccessFile rafPost;
    static String Record;
    static int recordNum = 0;

    class MapData {
        int docId;
        String fileName;
        MapData(int docId, String fileName) {
            this.docId = docId;
            this.fileName = fileName;
        }
    }

    public InvertedFileWriter(){
        try {
            rafMap = new RandomAccessFile(FILENAME_MAP, "rw");
            rafDict = new RandomAccessFile(FILENAME_DICT, "rw");
            rafPost = new RandomAccessFile(FILENAME_POST, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void create(int dictFileSize) {
        try {
            numRecordsDict = dictFileSize;

            Files.deleteIfExists(Path.of(FILENAME_MAP));
            Files.deleteIfExists(Path.of(FILENAME_POST));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
