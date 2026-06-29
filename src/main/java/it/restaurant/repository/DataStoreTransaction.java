package it.restaurant.repository;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class DataStoreTransaction {

    private final ReentrantLock lock = new ReentrantLock();

    public <T> T write(Supplier<T> operation) {
        lock.lock();
        try {
            return operation.get();
        } finally {
            lock.unlock();
        }
    }

    public void write(Runnable operation) {
        write(() -> {
            operation.run();
            return null;
        });
    }
}
