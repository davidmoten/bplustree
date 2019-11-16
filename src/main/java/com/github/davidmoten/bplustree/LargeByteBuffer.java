package com.github.davidmoten.bplustree;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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

    default String getString() {
        int length = getVarint();
        byte[] bytes = new byte[length];
        get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    default void putString(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        putVarint(bytes.length);
        put(bytes);
    }

    /**
     * Stores an integer in a variable number of bytes (up to 5). A varint is an
     * alternative storage method for an integer that may take up as little as one
     * byte for small values.
     * 
     * <p>
     * Algorithm used is from ProtocolBuffers.
     * 
     * @param value integer value to store
     */
    default void putVarint(int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                put((byte) value);
                break;
            } else {
                put((byte) ((value & 0x7F) | 0x80));
                value >>>= 7;
            }
        }
    }

    /**
     * Returns an integer that was stored in a variable number of bytes (up to 5). A
     * varint is an alternative storage method for an integer that may take up as
     * little as one byte for small values.
     * 
     * <p>
     * Algorithm used is from ProtocolBuffers.
     */
    default int getVarint() {
        // Adapated from ProtocolBuffers CodedInputStream
        int x;
        long pos = position();
        if ((x = get()) >= 0) {
            return x;
        } else if ((x ^= (get() << 7)) < 0) {
            x ^= (~0 << 7);
        } else if ((x ^= (get() << 14)) >= 0) {
            x ^= (~0 << 7) ^ (~0 << 14);
        } else if ((x ^= (get() << 21)) < 0) {
            x ^= (~0 << 7) ^ (~0 << 14) ^ (~0 << 21);
        } else {
            position(pos);
            // get the value the slow way but can handle when integer needed more than 4
            // bytes to represent
            return (int) getVarlong();
        }
        return x;
    }

    /**
     * Stores a long in a variable number of bytes (up to 9). A varlong is an
     * alternative storage method for a long that may take up as little as one byte
     * for small values.
     * 
     * <p>
     * Algorithm used is from ProtocolBuffers.
     * 
     * @param value long value to store
     */
    default void putVarlong(long value) {
        while (true) {
            if ((value & ~0x7FL) == 0) {
                put((byte) value);
                return;
            } else {
                put((byte) (((int) value & 0x7F) | 0x80));
                value >>>= 7;
            }
        }
    }

    /**
     * Returns a long that was stored in a variable number of bytes (up to 9). A
     * varlong is an alternative storage method for a long that may take up as
     * little as one byte for small values.
     * 
     * <p>
     * Algorithm used is from ProtocolBuffers.
     */
    default long getVarlong() {
        long result = 0;
        for (int shift = 0; shift < 64; shift += 7) {
            final byte b = get();
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
        }
        throw new IllegalStateException("malformed varlong");
    }

}