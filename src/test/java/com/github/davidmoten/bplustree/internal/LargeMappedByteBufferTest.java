package com.github.davidmoten.bplustree.internal;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.github.davidmoten.bplustree.Testing;

public class LargeMappedByteBufferTest {

    @Test
    public void testWriteAndReadIntValuesAcrossSegments() throws IOException {
        for (int size = 1; size <= 3 * Integer.BYTES + 1; size++) {
            try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), size,
                    "index-")) {
                b.putInt(10);
                assertEquals(4, b.position());
                b.putInt(11);
                assertEquals(8, b.position());
                b.putInt(12);
                assertEquals(12, b.position());
                // now read what we've just written
                b.position(0);
                assertEquals(10, b.getInt());
                assertEquals(11, b.getInt());
                assertEquals(12, b.getInt());
            }
        }
    }

    @Test
    public void testWriteAndReadBytes() throws IOException {
        try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), 2,
                "index-")) {
            b.put((byte) 1);
            b.put((byte) 2);
            b.put((byte) 3);
            b.position(0);
            assertEquals(1, b.get());
            assertEquals(2, b.get());
            assertEquals(3, b.get());
        }
    }

    @Test
    public void testReadAndWriteVarints() throws IOException {
        try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), 2,
                "index-")) {
            b.putVarint(1234567);
            for (int i = 0; i < 10000; i++) {
                b.putVarint(i);
            }
            for (int i = 1; i <= 16; i++) {
                b.putVarint(123 << i);
            }
            b.position(0);
            assertEquals(1234567, b.getVarint());
            for (int i = 0; i < 10000; i++) {
                assertEquals(i, b.getVarint());
            }
            for (int i = 1; i <= 16; i++) {
                assertEquals(123 << i, b.getVarint());
            }
        }
    }

    @Test
    public void testReadAndWriteVarlongs() throws IOException {
        try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), 2,
                "index-")) {
            b.putVarlong(1234567890123L);
            System.out.println(b.position());
            long maxLong = Long.MAX_VALUE;
            b.putVarlong(maxLong);
            for (int i = 0; i < 10000; i++) {
                b.putVarlong(i * 123);
            }
            for (int i = 0; i <= 62; i++) {
                System.out.println(maxLong >> i);
                b.putVarlong(maxLong >>i);
            }
            b.position(0);
            assertEquals(1234567890123L, b.getVarlong());
            assertEquals(maxLong, b.getVarlong());
            for (int i = 0; i < 10000; i++) {
                assertEquals(i * 123, b.getVarlong());
            }
            for (int i = 0; i <= 62; i++) {
                assertEquals(maxLong >> i, b.getVarlong());
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotWriteNegativeVarint() throws IOException {
        try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), 2,
                "index-")) {
            b.putVarint(-1);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCannotWriteVeryBigVarint() throws IOException {
        try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), 2,
                "index-")) {
            b.putVarint(Integer.MAX_VALUE + 1);
        }
    }

    @Test
    public void testWriteAndReadArrayWithinSegment() throws IOException {
        try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), 100,
                "index-")) {
            byte[] bytes = new byte[] { 1, 2, 3, 4, 5, 6 };
            b.put(bytes);
            b.put(bytes);

            // read
            b.position(0);
            byte[] a = new byte[bytes.length];
            b.get(a);
            assertArrayEquals(bytes, a);
            a = new byte[bytes.length];
            b.get(a);
            assertArrayEquals(bytes, a);
        }
    }

    @Test
    public void testWriteAndReadShortValuesAcrossSegments() throws IOException {
        for (int size = 1; size <= 3 * Short.BYTES + 1; size++) {
            try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), size,
                    "index-")) {
                b.putShort((short) 10);
                b.putShort((short) 11);
                b.putShort((short) 12);
                // now read what we've just written
                b.position(0);
                assertEquals(10, b.getShort());
                assertEquals(11, b.getShort());
                assertEquals(12, b.getShort());
            }
        }
    }

    @Test
    public void testWriteAndReadLongValuesAcrossSegments() throws IOException {
        for (int size = 1; size <= 3 * Long.BYTES + 1; size++) {
            try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), size,
                    "index-")) {
                b.putLong(10);
                b.putLong(11);
                b.putLong(12);
                // now read what we've just written
                b.position(0);
                assertEquals(10, b.getLong());
                assertEquals(11, b.getLong());
                assertEquals(12, b.getLong());
            }
        }
    }

    @Test
    public void testWriteAndReadDoubleValuesAcrossSegments() throws IOException {
        for (int size = 1; size <= 3 * Double.BYTES + 1; size++) {
            try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), size,
                    "index-")) {
                b.putDouble(10.01);
                b.putDouble(11.01);
                b.putDouble(12.01);
                // now read what we've just written
                b.position(0);
                assertEquals(10.01, b.getDouble(), 0.00001);
                assertEquals(11.01, b.getDouble(), 0.00001);
                assertEquals(12.01, b.getDouble(), 0.00001);
            }
        }
    }

    @Test
    public void testWriteAndReadFloatValuesAcrossSegments() throws IOException {
        for (int size = 1; size <= 3 * Float.BYTES + 1; size++) {
            try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), size,
                    "index-")) {
                b.putFloat(10.01f);
                b.putFloat(11.01f);
                b.putFloat(12.01f);
                // now read what we've just written
                b.position(0);
                assertEquals(10.01f, b.getFloat(), 0.00001);
                assertEquals(11.01f, b.getFloat(), 0.00001);
                assertEquals(12.01f, b.getFloat(), 0.00001);
            }
        }
    }
}
