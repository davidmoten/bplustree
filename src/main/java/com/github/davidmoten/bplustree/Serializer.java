package com.github.davidmoten.bplustree;

import java.nio.ByteBuffer;

public interface Serializer<T> {

    T read(ByteBuffer bb);

    void write(ByteBuffer bb, T t);

    /**
     * Returns the maximum size in bytes of a serialized item. Returns 0 when there
     * is no maximum.
     * 
     * @return the maximum size in bytes of a serialized item. Returns 0 when there
     *         is no maximum.
     */
    int maxSize();
}
