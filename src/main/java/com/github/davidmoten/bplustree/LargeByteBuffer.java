package com.github.davidmoten.bplustree;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.github.davidmoten.guavamini.Preconditions;

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
     * <p>
     * Algorithm used is from ProtocolBuffers.
     */

    /**
     * Stores an integer in a variable number of bytes (up to 4). A varint is an
     * alternative storage method for a non-negative integer <= Long.MAX_VALUE/8.
     * For small values it may use only one byte.
     * 
     * <p>
     * Algorithm used is from ProtocolBuffers.
     * 
     * @param value must be between 0 and Integer.MAX_VALUE/8 inclusive.
     */

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

    default int getVarint() {
        // Adapated from ProtocolBuffers CodedInputStream
        int x;
        if ((x = get()) >= 0) {
            return x;
        } else if ((x ^= (get() << 7)) < 0) {
            x ^= (~0 << 7);
        } else if ((x ^= (get() << 14)) >= 0) {
            x ^= (~0 << 7) ^ (~0 << 14);
        } else if ((x ^= (get() << 21)) < 0) {
            x ^= (~0 << 7) ^ (~0 << 14) ^ (~0 << 21);
        } else {
            throw new IllegalStateException("malformed varint");
        }
        return x;
    }

    default void putVarint(int value) {
        Preconditions.checkArgument(value >= 0 && value <= Integer.MAX_VALUE / 8);
        // Adapated from ProtocolBuffers CodedOutputStream
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

    default void putVarlong(long value) {
        Preconditions.checkArgument(value >= 0);
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