package logss.btree;

import java.io.InputStream;
import java.io.OutputStream;

public interface Serializer<T> {

    T read(InputStream in, long position);

    void write(OutputStream out, T t);

    /**
     * Returns the maximum size in bytes of a serialized item. Returns 0 when there
     * is no maximum.
     * 
     * @return the maximum size in bytes of a serialized item. Returns 0 when there
     *         is no maximum.
     */
    int maxSize();
}
