package it.restaurant.repository;

import java.util.List;
import java.util.Optional;

public interface DataStore {
    <T> List<T> loadList(String key, Class<T> type);
    <T> void saveList(String key, List<T> items);
    <T> Optional<T> load(String key, Class<T> type);
    <T> void save(String key, T item);
}
