package readwrite;


import java.io.RandomAccessFile;

public interface ReadWriter<T>{
    abstract void write(T data);
    abstract T read(int recordNum);
    abstract int getConfig();

}
