package com.github.davidmoten.bplustree.internal;

import java.io.File;

import org.junit.Test;

public class LargeMappedByteBufferTest {

    @Test
    public void test() {
        LargeMappedByteBuffer b = new LargeMappedByteBuffer(new File("target"), 10);
        b.putInt(10);
        b.putInt(11);
        b.putInt(12);
    }

}
