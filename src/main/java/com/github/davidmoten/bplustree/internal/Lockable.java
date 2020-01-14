package com.github.davidmoten.bplustree.internal;

public interface Lockable {
    
    void locked(boolean locked);

    boolean isLocked();

    default void lock() {
        locked(true);
    }

    default void release() {
        locked(false);
    }

}
