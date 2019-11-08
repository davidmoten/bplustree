package com.github.davidmoten.bplustree;

import java.nio.ByteBuffer;

/**
 * Similar to {@link ByteBuffer} but supports {@code long} positions instead of
 * {@code int} positions. Does not include those methods in ByteBuffer that
 * include the position as a parameter (for simplicity).
 * 
 * <p>
 * Also includes the notion of commit which forces flushing of memory buffers to
 * disk.
 *
 */
public interface LargeByteBuffer {

    long position();

    void position(long newPosition);

    byte get();

    void put(byte b);

    void get(byte[] dst);

    void put(byte[] src);

    int getInt();

    void putInt(int value);

    short getShort();

    void putShort(short value);

    long getLong();

    void putLong(long value);
    
    double getDouble();
    
    void putDouble(double value);
    
    double getFloat();
    
    void putFloat(float value);

    void commit();

}