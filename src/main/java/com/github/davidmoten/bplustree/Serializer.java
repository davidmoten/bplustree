package com.github.davidmoten.bplustree;

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

    public static Serializer<String> utf8(int maxSize) {
        return new Serializer<String>() {

            @Override
            public String read(LargeByteBuffer bb) {
                int size = bb.getInt();
                byte[] bytes = new byte[size];
                bb.get(bytes);
                return new String(bytes, StandardCharsets.UTF_8);
            }

            @Override
            public void write(LargeByteBuffer bb, String s) {
                byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
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
