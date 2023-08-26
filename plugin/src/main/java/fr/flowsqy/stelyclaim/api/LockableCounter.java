package fr.flowsqy.stelyclaim.api;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockableCounter {

    private final Lock lock;
    private int count;

    public LockableCounter() {
        lock = new ReentrantLock();
        count = 0;
    }

    public int get() {
        try {
            lock.lock();
            return count++;
        } finally {
            lock.unlock();
        }
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

}
