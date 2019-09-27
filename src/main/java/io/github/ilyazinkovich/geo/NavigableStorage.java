package io.github.ilyazinkovich.geo;

import static java.util.Collections.emptySet;
import static java.util.Collections.newSetFromMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;

public class NavigableStorage<T> {

  private static final boolean INCLUSIVE = true;
  private final ConcurrentNavigableMap<Long, Set<T>> map;

  public NavigableStorage(final ConcurrentNavigableMap<Long, Set<T>> map) {
    this.map = map;
  }

  public void add(final Long key, final T value) {
    map.computeIfAbsent(key, k -> newSetFromMap(new ConcurrentHashMap<>())).add(value);
  }

  public Set<T> get(final Long key) {
    return map.getOrDefault(key, emptySet());
  }

  public Set<T> search(final Long fromInclusive, final Long toInclusive) {
    Set<T> result = new HashSet<>();
    Collection<Set<T>> subsets =
        map.subMap(fromInclusive, INCLUSIVE, toInclusive, INCLUSIVE).values();
    for (Set<T> subset : subsets) {
      result.addAll(subset);
    }
    return result;
  }
}
