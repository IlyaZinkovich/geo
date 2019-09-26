package io.github.ilyazinkovich.geo;

import static java.util.Collections.newSetFromMap;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.stream.Stream;

public class NavigableStorage<T> {

  private static final boolean INCLUSIVE = true;
  private final ConcurrentNavigableMap<Long, Set<T>> map;

  public NavigableStorage(final ConcurrentNavigableMap<Long, Set<T>> map) {
    this.map = map;
  }

  public void add(final Long key, final T value) {
    map.computeIfAbsent(key, k -> newSetFromMap(new ConcurrentHashMap<>()))
        .add(value);
  }

  public Stream<T> search(final Long fromInclusive, final Long toInclusive) {
    return map.subMap(fromInclusive, INCLUSIVE, toInclusive, INCLUSIVE)
        .values().stream().flatMap(Collection::stream);
  }
}
