package io.rivrs.libbl.utils;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.LongFunction;

public class ThreadSafeLong2ObjectMap<V> {
    private final Long2ObjectMap<V> map;
    private final ReentrantLock[] locks;

    public ThreadSafeLong2ObjectMap() {
        this(16); // default: 16 stripes
    }

    public ThreadSafeLong2ObjectMap(int stripes) {
        if (Integer.bitCount(stripes) != 1)
            throw new IllegalArgumentException("Stripes must be a power of two for hashing efficiency");
        this.map = new Long2ObjectOpenHashMap<>();
        this.locks = new ReentrantLock[stripes];
        for (int i = 0; i < stripes; i++) locks[i] = new ReentrantLock();
    }

    private ReentrantLock lockFor(long key) {
        int hash = Long.hashCode(key);
        return locks[hash & (locks.length - 1)];
    }

    /** Thread-safe put */
    public void put(long key, V value) {
        ReentrantLock lock = lockFor(key);
        lock.lock();
        try {
            map.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    /** Thread-safe get */
    public V get(long key) {
        ReentrantLock lock = lockFor(key);
        lock.lock();
        try {
            return map.get(key);
        } finally {
            lock.unlock();
        }
    }

    /** Thread-safe remove */
    public V remove(long key) {
        ReentrantLock lock = lockFor(key);
        lock.lock();
        try {
            return map.remove(key);
        } finally {
            lock.unlock();
        }
    }

    /** Thread-safe computeIfAbsent */
    public V computeIfAbsent(long key, LongFunction<? extends V> mappingFunction) {
        ReentrantLock lock = lockFor(key);
        lock.lock();
        try {
            V existing = map.get(key);
            if (existing == null) {
                existing = mappingFunction.apply(key);
                map.put(key, existing);
            }
            return existing;
        } finally {
            lock.unlock();
        }
    }

    /** Safe iteration with snapshot copy */
    public void forEach(BiConsumer<? super Long, ? super V> action) {
        // snapshot iteration to avoid locking entire map
        Long2ObjectMap<V> snapshot;
        synchronized (this) { // snapshot lock
            snapshot = new Long2ObjectOpenHashMap<>(map);
        }
        snapshot.forEach(action);
    }

    /** Number of entries (maybe slightly stale under heavy concurrency) */
    public int size() {
        synchronized (this) {
            return map.size();
        }
    }

    /** Clears all entries */
    public void clear() {
        synchronized (this) {
            map.clear();
        }
    }
}
