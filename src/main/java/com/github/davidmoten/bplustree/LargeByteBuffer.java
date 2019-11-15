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

    /**
     * Returns an integer that was stored in a variable number of bytes (up to 4). A
     * varint is an alternative storage method for a non-negative integer <=
     * Long.MAX_VALUE/8. For small values it may use only one byte.
     * 
     * <p>Algorithm used is from ProtocolBuffers.
     */
    int getVarint();

    /**
     * Stores an integer in a variable number of bytes (up to 4). A
     * varint is an alternative storage method for a non-negative integer <=
     * Long.MAX_VALUE/8. For small values it may use only one byte.
     * 
     * <p>Algorithm used is from ProtocolBuffers.
     * 
     * @param value must be between 0 and Integer.MAX_VALUE/8 inclusive.
     */
    void putVarint(int value);

    short getShort();

    void putShort(short value);

    long getLong();

    void putLong(long value);

    double getDouble();

    void putDouble(double value);

    double getFloat();

    void putFloat(float value);

    String getString();

    void putString(String value);

    void commit();

}