import structs.DictData;
import structs.MapData;
import structs.PostData;

public class InvertedFileReader {
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
     * Instantiates a new Inverted file writer from the config files. Used when the outfiles are already present, and
     * you want to read them.
     *
     */
    public InvertedFileReader() {
        rafMap = new RafTable(FILENAME_CONFIG_MAP, FILENAME_MAP, RafTable.RafStatus.READ);
        rafPost = new RafTable(FILENAME_CONFIG_POST, FILENAME_POST, RafTable.RafStatus.READ);
        rafDict = new RafTable(FILENAME_CONFIG_DICT, FILENAME_DICT, RafTable.RafStatus.READ);
    }

    /**
     * Close after reading.
     */
    public void closeAfterReading() {
        rafMap.closeFile();
        rafPost.closeFile();
        rafDict.closeFile();
    }

    /**
     * Read map record.
     *
     * @param recordNum the record num
     */
    public void readMapRecord(int recordNum) {
        MapData temp = new MapData();
        if (rafMap.read(recordNum, temp)) {
            temp.print();
        } else {
            System.out.println("miss");
        }
    }

    /**
     * Read post record.
     *
     * @param recordNum the record num
     */
    public void readPostRecord(int recordNum) {
        PostData temp = new PostData();
        if (rafPost.read(recordNum, temp)) {
            temp.print();
        } else {
            System.out.println("miss");
        }
    }

    /**
     * Read dict record.
     *
     * @param recordNum the record num
     */
    public void readDictRecord(int recordNum) {
        DictData temp = new DictData();
        if (rafDict.read(recordNum, temp) && Integer.parseInt(temp.numDocs) != -1) {
            temp.print();
        } else {
            System.out.println("miss");
        }
    }
}
