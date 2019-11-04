package com.github.davidmoten.bplustree;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.TreeMap;

public final class LargeMappedByteBuffer {

    private final int segmentSizeBytes;

    private final TreeMap<Long, Segment> map = new TreeMap<>();
    private final File directory;

    public LargeMappedByteBuffer(File directory, int segmentSizeBytes) {
        this.directory = directory;
        this.segmentSizeBytes = segmentSizeBytes;
    }

    private static final class Segment {
        final FileChannel channel;
        final MappedByteBuffer bb;

        Segment(FileChannel channel, MappedByteBuffer bb) {
            this.channel = channel;
            this.bb = bb;
        }

    }

    private long position;

    private MappedByteBuffer bb(long position) {
        long num = position % segmentSizeBytes;
        Segment segment = map.get(num);
        if (segment == null) {
            File file = new File(directory, "data-" + num);
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                raf.setLength(segmentSizeBytes);
            }
            FileChannel channel = (FileChannel) Files.newByteChannel(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
            MappedByteBuffer bb = channel.map(MapMode.READ_WRITE, 0, 1024*1024);
            map.put(num, new Segment(channel, bb));
            return bb;
        }
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
