import structs.DictData;
import structs.MapData;
import structs.PostData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The type Inverted file writer.
 */
public class InvertedFileWriter {
    /**
     * The Default token size.
     */
    static final int DEFAULT_TOKEN_SIZE = 14;
    /**
     * The Default num docs size.
     */
    static final int DEFAULT_NUM_DOCS_SIZE = 4;
    /**
     * The Default file name size.
     */
    static final int DEFAULT_FILE_NAME_SIZE = 16;
    /**
     * The Default start size.
     */
    static final int DEFAULT_START_SIZE = 4;
    /**
     * The Default doc id size.
     */
    static final int DEFAULT_DOC_ID_SIZE = 4;
    /**
     * The Default freq size.
     */
    static final int DEFAULT_FREQ_SIZE = 4;

    /**
     * The Filename map.
     */
    static String FILENAME_MAP = "map.txt";
    /**
     * The Filename dict.
     */
    static String FILENAME_DICT = "dict.txt";
    /**
     * The Filename post.
     */
    static String FILENAME_POST = "post.txt";
    /**
     * The Filename config map.
     */
    static String FILENAME_CONFIG_MAP = "config_map.txt";
    /**
     * The Filename config dict.
     */
    static String FILENAME_CONFIG_DICT = "config_dict.txt";
    /**
     * The Filename config post.
     */
    static String FILENAME_CONFIG_POST = "config_post.txt";

    private RafTable rafMap;
    private RafTable rafDict;
    private RafTable rafPost;


    /**
     * Instantiates a new Inverted file writer with the default settings.
     */
    public InvertedFileWriter() {
        rafMap = new RafTable.Builder<MapData>(FILENAME_MAP,FILENAME_CONFIG_MAP, RafTable.RafStatus.WRITE, 2)
                .addColumn(DEFAULT_DOC_ID_SIZE)
                .addColumn(DEFAULT_FILE_NAME_SIZE)
                .build();

        rafPost = new RafTable.Builder<PostData>(FILENAME_POST, FILENAME_CONFIG_POST, RafTable.RafStatus.WRITE, 2)
                .addColumn(DEFAULT_DOC_ID_SIZE)
                .addColumn(DEFAULT_FREQ_SIZE)
                .build();

        rafDict = new RafTable.Builder<DictData>(FILENAME_DICT, FILENAME_CONFIG_DICT, RafTable.RafStatus.WRITE, 3)
                .addColumn(DEFAULT_TOKEN_SIZE)
                .addColumn(DEFAULT_NUM_DOCS_SIZE)
                .addColumn(DEFAULT_START_SIZE)
                .build();
    }

    /**
     * Instantiates a new Inverted file writer from the config files. Used when the outfiles are already present, and
     * you want to update them.
     *
     * @param openFromConfig are the files present and a config file exists?
     */
    public InvertedFileWriter(boolean openFromConfig) {
        if (openFromConfig) {
            rafMap = new RafTable(FILENAME_CONFIG_MAP, FILENAME_MAP, RafTable.RafStatus.WRITE);
            rafPost = new RafTable(FILENAME_CONFIG_POST, FILENAME_POST, RafTable.RafStatus.WRITE);
            rafDict = new RafTable(FILENAME_CONFIG_DICT, FILENAME_DICT, RafTable.RafStatus.WRITE);
        } else {
            rafMap = new RafTable.Builder<MapData>(FILENAME_MAP,FILENAME_CONFIG_MAP, RafTable.RafStatus.WRITE, 2)
                    .addColumn(DEFAULT_DOC_ID_SIZE)
                    .addColumn(DEFAULT_FILE_NAME_SIZE)
                    .build();

            rafPost = new RafTable.Builder<PostData>(FILENAME_POST, FILENAME_CONFIG_POST, RafTable.RafStatus.WRITE, 2)
                    .addColumn(DEFAULT_DOC_ID_SIZE)
                    .addColumn(DEFAULT_FREQ_SIZE)
                    .build();

            rafDict = new RafTable.Builder<DictData>(FILENAME_DICT, FILENAME_CONFIG_DICT, RafTable.RafStatus.WRITE, 3)
                    .addColumn(DEFAULT_TOKEN_SIZE)
                    .addColumn(DEFAULT_NUM_DOCS_SIZE)
                    .addColumn(DEFAULT_START_SIZE)
                    .build();
        }
    }
    /**
     * Open for write, overwriting the current files.
     */
    public void openForWriteNew() {
        try {
            Files.deleteIfExists(Path.of(FILENAME_MAP));
            Files.deleteIfExists(Path.of(FILENAME_POST));
            Files.deleteIfExists(Path.of(FILENAME_DICT));
            Files.deleteIfExists(Path.of(FILENAME_CONFIG_MAP));
            Files.deleteIfExists(Path.of(FILENAME_CONFIG_POST));
            Files.deleteIfExists(Path.of(FILENAME_CONFIG_DICT));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        rafMap.setWriteModeNew();
        rafPost.setWriteModeNew();
        rafDict.setWriteModeNew();
    }

    /**
     * Close after writing, writes config files when finished.
     */
    public void closeAfterWriting() {
        rafMap.writeConfigs();
        rafPost.writeConfigs();
        rafDict.writeConfigs();
        rafMap.closeFile();
        rafPost.closeFile();
        rafDict.closeFile();
    }

    /**
     * Write map record.
     *
     * @param docID    the doc id
     * @param fileName the file name
     */
    public void writeMapRecord(int docID, String fileName) {
        rafMap.write(new MapData(Integer.toString(docID), fileName));
    }

    /**
     * Write post record.
     *
     * @param docID  the doc id
     * @param weight the weight
     */
    public void writePostRecord(int docID, int weight) {
        rafPost.write(new PostData(Integer.toString(docID), Integer.toString(weight)));
    }

    /**
     * Write dict record to a particular row.
     *
     * @param row     the row
     * @param term    the term
     * @param numDocs the num docs
     * @param start   the start
     */
    public void writeDictRecordHashed(int row, String term, int numDocs, int start) {
        rafDict.write(new DictData(term, Integer.toString(numDocs), Integer.toString(start)), row);
    }

    /**
     * Write dict record.
     *
     * @param term    the term
     * @param numDocs the num docs
     * @param start   the start
     */
    public void writeDictRecord(String term, int numDocs, int start) {
        rafDict.write(new DictData(term, Integer.toString(numDocs), Integer.toString(start)));
    }
}
