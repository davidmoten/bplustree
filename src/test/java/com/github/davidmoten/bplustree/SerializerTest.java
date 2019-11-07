package com.github.davidmoten.bplustree;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.github.davidmoten.bplustree.internal.LargeMappedByteBuffer;

public class SerializerTest {

    @Test
    public void testLong() throws IOException {
        assertEquals(8, Serializer.LONG.maxSize());
        try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), 100, "test-")) {
            b.position(0);
            Serializer.LONG.write(b, Long.MAX_VALUE);
            b.position(0);
            assertEquals(Long.MAX_VALUE, (long) Serializer.LONG.read(b));
        }
    }

    @Test
    public void testShort() throws IOException {
        assertEquals(2, Serializer.SHORT.maxSize());
        try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), 100, "test-")) {
            b.position(0);
            Serializer.SHORT.write(b, Short.MAX_VALUE);
            b.position(0);
            assertEquals(Short.MAX_VALUE, (long) Serializer.SHORT.read(b));
        }
    }

    @Test
    public void testInteger() throws IOException {
        assertEquals(4, Serializer.INTEGER.maxSize());
        try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), 100, "test-")) {
            b.position(0);
            Serializer.INTEGER.write(b, Integer.MAX_VALUE);
            b.position(0);
            assertEquals(Integer.MAX_VALUE, (long) Serializer.INTEGER.read(b));
        }
    }

    @Test
    public void testUtf8() throws IOException {
        Serializer<String> ser = Serializer.utf8(16);
        assertEquals(16, ser.maxSize());
        try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), 100, "test-")) {
            b.position(0);
            ser.write(b, "hello");
            b.position(0);
            assertEquals("hello", ser.read(b));
        }
    }

    @Test
    public void testBytes() throws IOException {
        Serializer<byte[]> ser = Serializer.bytes(16);
        assertEquals(16, ser.maxSize());
        try (LargeMappedByteBuffer b = new LargeMappedByteBuffer(Testing.newDirectory(), 100, "test-")) {
            b.position(0);
            ser.write(b, "hello".getBytes(StandardCharsets.UTF_8));
            b.position(0);
            assertEquals("hello", new String(ser.read(b), StandardCharsets.UTF_8));
        }
    }

}
