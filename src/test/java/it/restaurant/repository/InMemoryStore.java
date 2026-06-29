package it.restaurant.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryStore implements DataStore {

    private final Map<String, Object> storage = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> loadList(String key, Class<T> type) {
        List<T> list = (List<T>) storage.get(key);
        return list != null ? list : List.of();
    }

    @Override
    public <T> void saveList(String key, List<T> items) {
        storage.put(key, new ArrayList<>(items));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> load(String key, Class<T> type) {
        return Optional.ofNullable((T) storage.get(key));
    }

    @Override
    public <T> void save(String key, T item) {
        storage.put(key, item);
    }
}
