package com.github.davidmoten.bplustree;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
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
        long num = segmentNumber(position);
        Segment segment = map.get(num);
        if (segment == null) {
            File file = new File(directory, "data-" + num);
            try {
                try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                    raf.setLength(segmentSizeBytes);
                }
                FileChannel channel = (FileChannel) Files.newByteChannel(file.toPath(), StandardOpenOption.CREATE,
                        StandardOpenOption.READ, StandardOpenOption.WRITE);
                MappedByteBuffer bb = channel.map(MapMode.READ_WRITE, 0, 1024 * 1024);

                map.put(num, new Segment(channel, bb));
                return bb;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            return segment.bb;
        }
    }

    private long segmentNumber(long position) {
        return position % segmentSizeBytes;
    }

    public void position(long newPosition) {
        this.position = newPosition;
    }

    public byte get() {
        return bb(position++).get();
    }

    public void put(byte b) {
        bb(position++).put(b);
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
        // TODO handle crossing multiple bb
        long p = position;
        position += Integer.SIZE;
        bb(p).putInt(value);
    }

    public int getInt(long index) {
        // TODO handle crossing multiple bb
        return bb(index).getInt();
    }

    public void putInt(long index, int value) {
        // TODO handle crossing multiple bb
        bb(index).putInt(value);
    }

    public long getLong() {
        // TODO handle crossing multiple bb
        long p = position;
        position += Long.SIZE;
        return bb(p).getLong();
    }

    public void putLong(long value) {
        // TODO handle crossing multiple bb
        long p = position;
        position += Long.SIZE;
        bb(p).putLong(value);
    }

    public long getLong(long index) {
        // TODO handle crossing multiple bb
        return bb(index).getLong();
    }

    public void putLong(long index, long value) {
        // TODO handle crossing multiple bb
        bb(index).putLong(value);
    }

}
