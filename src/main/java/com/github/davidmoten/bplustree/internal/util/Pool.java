package com.github.davidmoten.bplustree.internal.util;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

import com.github.davidmoten.guavamini.Preconditions;

public final class Pool<T> {

    private final int maxSize;
    private final Supplier<T> factory;
    
    private final Queue<T> queue;
    private int checkedOut;

    public Pool(int maxSize, Supplier<T> factory) {
        Preconditions.checkArgument(maxSize > 0);
        this.factory = factory;
        this.queue = new ArrayDeque<>();
        this.checkedOut = 0;
        this.maxSize = maxSize;
    }

    public T get() {
        if (checkedOut == maxSize) {
            throw new IllegalStateException("cannot get item from pool because pool already at max size of " + maxSize);
        }
        T t = queue.poll();
        if (t == null) {
            t = factory.get();
        }
        checkedOut++;
        return t;
    }

    public void release(T t) {
        checkedOut--;
        queue.offer(t);
    }

}
