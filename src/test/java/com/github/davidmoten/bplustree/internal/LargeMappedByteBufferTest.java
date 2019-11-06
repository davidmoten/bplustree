package com.github.davidmoten.bplustree.internal;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class LargeMappedByteBufferTest {

    @Test
    public void testWriteAndReadIntValuesAcrossSegments() throws IOException {
        for (int size = 1; size <= 3 * Integer.BYTES + 1; size++) {
            try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(new File("target"), size)) {
                b.putInt(10);
                b.putInt(11);
                b.putInt(12);
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
        try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(new File("target"), 2)) {
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
    public void testWriteAndReadArrayWithinSegment() throws IOException {
        try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(new File("target"), 100)) {
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
    public void testWriteAndReadLongValuesAcrossSegments() throws IOException {
        for (int size = 1; size <= 3 * Long.BYTES + 1; size++) {
            try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(new File("target"), size)) {
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

    
}
