package com.github.davidmoten.bplustree;

import java.nio.ByteBuffer;

public class LargeByteBufferDelegating implements LargeByteBuffer {

    private final ByteBuffer bb = ByteBuffer.allocate(1024);
  
    
    @Override
    public long position() {
        return bb.position();
    }

    @Override
    public void position(long newPosition) {
        bb.position((int) newPosition);
    }

    @Override
    public byte get() {
        return bb.get();
    }

    @Override
    public void put(byte b) {
        bb.put(b);
    }

    @Override
    public void get(byte[] dst) {
        bb.get(dst);
    }

    @Override
    public void put(byte[] src) {
        bb.put(src);
    }

    @Override
    public int getInt() {
        return bb.getInt();
    }

    @Override
    public void putInt(int value) {
        bb.putInt(value);
    }

    @Override
    public short getShort() {
        return bb.getShort();
    }

    @Override
    public void putShort(short value) {
        bb.putShort(value);
    }

    @Override
    public long getLong() {
        return bb.getLong();
    }

    @Override
    public void putLong(long value) {
        bb.putLong(value);
    }

    @Override
    public double getDouble() {
        return bb.getDouble();
    }

    @Override
    public void putDouble(double value) {
        bb.putDouble(value);
    }

    @Override
    public double getFloat() {
        return bb.getFloat();
    }

    @Override
    public void putFloat(float value) {
        bb.putFloat(value);
    }

    @Override
    public void commit() {
        // do nothing
    }

}
