package it.restaurant.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonDataStore implements DataStore {

    private final ObjectMapper mapper;
    private final Path dataDir;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public JsonDataStore(Path dataDir) {
        this.dataDir = dataDir;
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT);
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            throw new DataStoreException("Cannot create data directory: " + dataDir, e);
        }
    }

    @Override
    public <T> List<T> loadList(String key, Class<T> type) {
        lock.readLock().lock();
        try {
            Path file = fileFor(key);
            if (!Files.exists(file)) {
                return new ArrayList<>();
            }
            CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, type);
            try {
                return mapper.readValue(file.toFile(), listType);
            } catch (IOException e) {
                throw new DataStoreException("Cannot read " + file, e);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <T> void saveList(String key, List<T> items) {
        lock.writeLock().lock();
        try {
            write(fileFor(key), items);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public <T> Optional<T> load(String key, Class<T> type) {
        lock.readLock().lock();
        try {
            Path file = fileFor(key);
            if (!Files.exists(file)) {
                return Optional.empty();
            }
            try {
                return Optional.of(mapper.readValue(file.toFile(), type));
            } catch (IOException e) {
                throw new DataStoreException("Cannot read " + file, e);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public <T> void save(String key, T item) {
        lock.writeLock().lock();
        try {
            write(fileFor(key), item);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Path fileFor(String key) { return dataDir.resolve(key + ".json"); }

    private void write(Path file, Object value) {
        try {
            mapper.writeValue(file.toFile(), value);
        } catch (IOException e) {
            throw new DataStoreException("Cannot write " + file, e);
        }
    }
}
