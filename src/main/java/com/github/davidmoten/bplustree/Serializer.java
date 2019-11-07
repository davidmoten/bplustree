package com.github.davidmoten.bplustree;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface Serializer<T> {

    T read(LargeByteBuffer bb);

    void write(LargeByteBuffer bb, T t);

    /**
     * Returns the maximum size in bytes of a serialized item. Returns 0 when there
     * is no maximum.
     * 
     * @return the maximum size in bytes of a serialized item. Returns 0 when there
     *         is no maximum.
     */
    int maxSize();

    public static Serializer<Integer> INTEGER = new Serializer<Integer>() {

        @Override
        public Integer read(LargeByteBuffer bb) {
            return bb.getInt();
        }

        @Override
        public void write(LargeByteBuffer bb, Integer t) {
            bb.putInt(t);
        }

        @Override
        public int maxSize() {
            return Integer.BYTES;
        }
    };

    public static Serializer<Long> LONG = new Serializer<Long>() {

        @Override
        public Long read(LargeByteBuffer bb) {
            return bb.getLong();
        }

        @Override
        public void write(LargeByteBuffer bb, Long t) {
            bb.putLong(t);
        }

        @Override
        public int maxSize() {
            return Long.BYTES;
        }
    };
    
    public static Serializer<Short> SHORT = new Serializer<Short>() {

        @Override
        public Short read(LargeByteBuffer bb) {
            return bb.getShort();
        }

        @Override
        public void write(LargeByteBuffer bb, Short t) {
            bb.putShort(t);
        }

        @Override
        public int maxSize() {
            return Short.BYTES;
        }
    };

    public static Serializer<String> utf8(int maxSize) {
        return string(StandardCharsets.UTF_8, maxSize);
    }

    public static Serializer<String> string(Charset charset, int maxSize) {
        return new Serializer<String>() {

            @Override
            public String read(LargeByteBuffer bb) {
                int size = bb.getInt();
                byte[] bytes = new byte[size];
                bb.get(bytes);
                return new String(bytes, charset);
            }

            @Override
            public void write(LargeByteBuffer bb, String s) {
                byte[] bytes = s.getBytes(charset);
                bb.putInt(bytes.length);
                bb.put(bytes);
            }

            @Override
            public int maxSize() {
                return maxSize;
            }
        };
    }

    public static Serializer<byte[]> bytes(int maxSize) {
        return new Serializer<byte[]>() {

            @Override
            public byte[] read(LargeByteBuffer bb) {
                int size = bb.getInt();
                byte[] bytes = new byte[size];
                bb.get(bytes);
                return bytes;
            }

            @Override
            public void write(LargeByteBuffer bb, byte[] bytes) {
                bb.putInt(bytes.length);
                bb.put(bytes);
            }

            @Override
            public int maxSize() {
                return maxSize;
            }

        };
    }
}
