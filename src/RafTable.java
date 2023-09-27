import structs.MapData;
import structs.Writeable;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * The type Raf table.
 *
 * @param <T> the type parameter
 */
public class RafTable <T extends Writeable> implements AutoCloseable {
    private final int[] colSizes;
    private final int NUM_COLUMNS;
    private final String OUT_FILE_NAME;
    private final String CONFIG_FILE_NAME;
    private RandomAccessFile stream;


    /**
     * close will be used in later homeworks when I turn this class into an actual extension of
     * RandomAccessFile. Methods will throw exceptions that need to be caught instead of handling them inside
     * RafTable. RafTable will be used in a 'try-with-resources' instead of the testing state it is in now.
     */
    @Override
    public void close() throws IOException {
        closeFile();
    }

    /**
     * The enum of the Random Access File status.
     */
    public enum RafStatus{
        /**
         * Read Random Access File status.
         */
        READ,
        /**
         * Write Random Access File status.
         */
        WRITE,
        /**
         * Closed Random Access File status.
         */
        CLOSED;

        /**
         * Random Access File open option string.
         *
         * @return the string, 'r' for READ, 'rw' for WRITE
         */
        String fileOpenOption(){
            switch (this) {
                case READ: return "r";
                case WRITE: return "rw";
                case CLOSED: return "";
            }
            return "";
        }

        /**
         * Get the enum RafStatus associated with the string of Random Access File's options .
         *
         * @param option the option "r" or "rw"
         * @return the raf status READ, WRITE, or CLOSED
         */
        RafStatus statusFromOption(String option) {
            switch (option) {
                case "r": return READ;
                case "rw": return WRITE;
                default: return CLOSED;
            }
        }
    }
    private RafStatus status;
    private int numRecords;
    private final int RECORD_SIZE;

    /**
     * Builder constructor for RafTable. Creates a RAF based on a builder.
     * May not be used in final product. It might make more sense to just use the traditional constructor.
     * I wanted to have both options in case one made more sense in later homeworks.
     */
    private RafTable(Builder builder) {
        this.CONFIG_FILE_NAME = builder.CONFIG_FILE_NAME;
        this.OUT_FILE_NAME = builder.OUT_FILE_NAME;
        this.NUM_COLUMNS = builder.NUM_COLUMNS;
        this.colSizes = builder.colSizes;
        this.status = builder.status;
        this.stream = builder.stream;
        // The number of bytes in a row. Each value length + a space between each + a newline at the end
        this.RECORD_SIZE = builder.rowSize + NUM_COLUMNS + 1;
        this.numRecords = 0;
    }

    /**
     * Instantiates a new Raf table from a config file.
     *
     * @param configFileName the config file name
     * @param outFileName    the out file name
     * @param status         the status to open the stream as
     */
    public RafTable(String configFileName, String outFileName, RafStatus status) {
        this.CONFIG_FILE_NAME = configFileName;
        this.OUT_FILE_NAME = outFileName;
        this.status = status;
        String data;
        try (RandomAccessFile configStream = new RandomAccessFile(configFileName, "r")){
            this.stream = new RandomAccessFile(OUT_FILE_NAME, status.fileOpenOption());
            data = configStream.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] configValues = data.split(" ");
        this.numRecords = Integer.parseInt(configValues[0]);
        this.NUM_COLUMNS = configValues.length - 1;
        this.colSizes = new int[NUM_COLUMNS];
        int sumOfSizes = 0;
        for(int i = 1; i < configValues.length; i++) {
            colSizes[i - 1] = Integer.parseInt(configValues[i]);
            sumOfSizes += colSizes[i - 1];
        }
        // The number of bytes in a row. Each value length + a space between each + a newline at the end
        this.RECORD_SIZE = sumOfSizes + NUM_COLUMNS + 1;

    }
    /**
     * Instantiates a new Raf table.
     *
     * @param CONFIG_FILE_NAME the config file name
     * @param OUT_FILE_NAME    the out file name
     * @param status           the status to open the RAF in, READ, CLOSED, WRITE
     * @param colSizes         the column sizes
     */
    public RafTable(String CONFIG_FILE_NAME, String OUT_FILE_NAME, RafStatus status, int... colSizes) {
        this.CONFIG_FILE_NAME = CONFIG_FILE_NAME;
        this.OUT_FILE_NAME = OUT_FILE_NAME;
        this.status = status;
        this.colSizes = colSizes;
        this.NUM_COLUMNS = colSizes.length;
        if (this.status != RafStatus.CLOSED) {
            try {
                this.stream = new RandomAccessFile(OUT_FILE_NAME, status.fileOpenOption());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        // FIXME make sure the stream works as I expect
        this.RECORD_SIZE = Arrays.stream(colSizes).sum() + NUM_COLUMNS + 1;
        this.numRecords = 0;
    }

    /**
     * The RafTable Builder.
     *
     * @param <T> the type parameter
     */
    public static class Builder<T extends Writeable> {
        private int[] colSizes;
        private int NUM_COLUMNS;
        private String OUT_FILE_NAME;
        private final String CONFIG_FILE_NAME;
        private RandomAccessFile stream;
        private RafTable.RafStatus status;
        private int columnCount = -1;
        private int rowSize;

        /**
         * Instantiates a new Builder.
         *
         * @param outFileName    the out file name
         * @param configFileName the config file name
         * @param status         the status
         * @param numColumns     the num columns
         */
        public Builder(String outFileName, String configFileName, RafStatus status, int numColumns) {
            this.NUM_COLUMNS = numColumns;
            this.colSizes = new int[NUM_COLUMNS];
            this.OUT_FILE_NAME = outFileName;
            this.CONFIG_FILE_NAME = configFileName;
            this.status = status;
            try {
                if (status == RafStatus.READ)
                    this.stream = new RandomAccessFile(OUT_FILE_NAME, "r");
                if (status == RafStatus.WRITE)
                    this.stream = new RandomAccessFile(OUT_FILE_NAME, "rw");
                if (status == RafStatus.CLOSED)
                    this.stream = null;
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }

        /**
         * Add a column to the builder.
         *
         * @param size the size of the column in bytes
         * @return the builder
         */
        public Builder addColumn(int size) {
            try {
                if (++columnCount == NUM_COLUMNS)
                    throw new ColumnBoundsException("Added more columns than RafTable was set for");
            } catch (ColumnBoundsException e) {
                throw new RuntimeException(e);
            }
            colSizes[columnCount] = size;
            rowSize += size;
            return this;
        }

        /**
         * Build raf table.
         *
         * @return the raf table
         */
        public RafTable build(){
            return new RafTable<T>(this);
        }
    }

    /**
     * Write to a specific row in the RAF.
     *
     * @param data the data to write, of type Writeable
     * @param row  the row to write on
     */
    public void write(T data, int row) {
        try {
            long currentPosition = stream.getFilePointer();
            stream.seek((long) row * RECORD_SIZE);
            write(data);
            stream.seek(currentPosition);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Write.
     *
     * @param data the data
     */
    public void write(T data) {
        try {
            for(int size: colSizes) {
                if (!data.hasNext()) {
                    throw new ColumnBoundsException("Number of columns for data type " + data.getClass().getName()
                            + " does not match number of columns for RafTable: " + NUM_COLUMNS);
                }
                String next = data.getNext();
                String docIDFormat = "%" + size + "s" + " ";

                stream.write(String.format(docIDFormat, next
                        .substring(0, Math.min(next.length(), size))).getBytes());
            }
            numRecords += 1;
            stream.write('\n');
        } catch (IOException | ColumnBoundsException e) {
            throw new RuntimeException(e);
        }
        data.resetNext();
    }

    /**
     * Read boolean.
     *
     * @param recordNum the record num
     * @param data      the data
     * @return the boolean
     */
    public boolean read(int recordNum, T data) {
        if ((recordNum < 0) || (recordNum >= numRecords)) {
            return false;
        }

        try {
            stream.seek((long) recordNum * RECORD_SIZE);
            String row = stream.readLine();
            int prevIndex = 0;
            for(int columnSize: colSizes) {
                if (!data.hasNext()) {
                    throw new ColumnBoundsException("Number of columns for data type " + data.getClass().getName()
                            + " does not match number of columns for RafTable: " + NUM_COLUMNS);
                }
                data.setNext(row.substring(prevIndex, columnSize+prevIndex).strip());
                // Next value will start from end of current value(columnSize + prevIndex) + 1 space.
                prevIndex = columnSize + prevIndex + 1;
            }
            data.resetNext();
        } catch (IOException | ColumnBoundsException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * Close file boolean.
     *
     * @return the boolean
     */
    public boolean closeFile() {
        numRecords = 0;
        if (status != RafStatus.CLOSED) {
            status = RafStatus.CLOSED;
            try {
                stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

    /**
     * Sets read mode.
     */
    public void setReadMode() {
        if (status == RafStatus.READ) {
            return;
        }
        setNewStream(RafStatus.READ);
    }

    /**
     * Sets write mode. Starts num records at 0.
     */
    public void setWriteModeNew() {
        numRecords = 0;
        closeFile();
        setNewStream(RafStatus.WRITE);
    }

    private void setUpdateMode() {
        // TODO if needed in future HW
        // Set method to public, add RafStatus ENUM 'UPDATE'
    }
    private void setNewStream(RafStatus streamOption) {
        closeFile();
        if (streamOption == RafStatus.CLOSED) {
            return;
        }
        status = streamOption;
        try {
            stream = new RandomAccessFile(OUT_FILE_NAME, streamOption.fileOpenOption());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write configs.
     */
    public void writeConfigs() {
        StringBuilder outString = new StringBuilder();
        outString.append(numRecords).append(' ');
        for(int colSize: colSizes) {
            outString.append(colSize).append(' ');
        }

        try(RandomAccessFile configStream = new RandomAccessFile(CONFIG_FILE_NAME, "rw")){
            configStream.write(outString.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
