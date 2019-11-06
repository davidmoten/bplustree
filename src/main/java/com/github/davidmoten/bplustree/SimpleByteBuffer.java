package com.github.davidmoten.bplustree;

public interface SimpleByteBuffer {

    long position();

    void position(long newPosition);

    byte get();

    void put(byte b);

    void get(byte[] dst);

    void put(byte[] src);

    int getInt();

    void putInt(int value);

    long getLong();

    void putLong(long value);

}