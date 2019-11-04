package com.github.davidmoten.bplustree;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

public final class LargeMappedByteBuffer {

    private long position;

    private MappedByteBuffer bb(long position) {
        return null;
    }

    public void position(long newPosition) {
        this.position = newPosition;
    }

    public byte get() {
        return bb(position++).get();
    }

    public ByteBuffer put(byte b) {
        return bb(position++).put(b);
    }

    public byte get(long index) {
        return bb(index).get();
    }

    public void put(long index, byte b) {
        bb(index).put(b);
    }

    public void get(byte[] dst) {
        // TODO handle crossing multiple bb
        long p = position;
        position += Integer.SIZE;
        bb(p).get(dst);
    }

    public void put(byte[] src) {
        // TODO handle crossing multiple bb
        long p = position;
        position += Integer.SIZE;
        bb(p).put(src);
    }

    public int getInt() {
        // TODO handlel crossing multiple bb
        long p = position;
        position += Integer.SIZE;
        return bb(p).getInt();
    }

    public void putInt(int value) {
        long p = position;
        position += Integer.SIZE;
        bb(p).putInt(value);
    }

    public int getInt(long index) {
        return bb(index).getInt();
    }

    public void putInt(long index, int value) {
        bb(index).putInt(value);
    }

    public long getLong() {
        long p = position;
        position += Long.SIZE;
        return bb(p).getLong();
    }

    public void putLong(long value) {
        long p = position;
        position += Long.SIZE;
        bb(p).putLong(value);
    }

    public long getLong(long index) {
        return bb(index).getLong();
    }

    public void putLong(long index, long value) {
        bb(index).putLong(value);
    }

}
