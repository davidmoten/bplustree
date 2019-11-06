package com.github.davidmoten.bplustree.internal;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class LargeMappedByteBufferTest {

    @Test
    public void testWriteAndReadAcrossSegments() throws IOException {
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

}
