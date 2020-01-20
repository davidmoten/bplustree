package com.github.davidmoten.bplustree.internal.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class LazyList<T> {

    private final int size;
    private final Supplier<T> factory;
    private final List<T> list = new ArrayList<T>();

    public LazyList(int size, Supplier<T> factory) {
        this.size = size;
        this.factory = factory;
    }

    public T get(int index) {
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException();
        } else {
            if (index >= list.size()) {
                for (int j = list.size(); j <= index; j++) {
                    list.add(null);
                }
            }
            T v = list.get(index);
            if (v == null) {
                v = factory.get();
                list.set(index, v);
            }
            return v;
        }
    }

    public int size() {
        return size;
    }

}
