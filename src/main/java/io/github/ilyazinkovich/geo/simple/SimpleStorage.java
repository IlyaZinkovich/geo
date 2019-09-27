package io.github.ilyazinkovich.geo.simple;

import static java.util.Collections.emptySet;
import static java.util.Collections.newSetFromMap;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleStorage<T> {

  private final ConcurrentMap<Long, Set<T>> map;

  public SimpleStorage(final ConcurrentMap<Long, Set<T>> map) {
    this.map = map;
  }

  public void add(final Long key, final T value) {
    map.computeIfAbsent(key, k -> newSetFromMap(new ConcurrentHashMap<>()))
        .add(value);
  }

  public Set<T> search(final Long key) {
    return map.getOrDefault(key, emptySet());
  }
}
