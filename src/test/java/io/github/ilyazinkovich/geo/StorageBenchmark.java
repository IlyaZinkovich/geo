package io.github.ilyazinkovich.geo;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class StorageBenchmark {

  @State(Scope.Benchmark)
  public static class EmptyNavigableStorageWithRandom {

    public NavigableStorage<Object> storage;
    public Random random;

    @Setup(Level.Iteration)
    public void setUp() {
      storage = new NavigableStorage<>(new ConcurrentSkipListMap<>());
      random = new Random();
    }
  }

  @Benchmark
  public void navigableStorageAdd(EmptyNavigableStorageWithRandom state) {
    state.storage.add(state.random.nextLong(), new Object());
  }

  @State(Scope.Benchmark)
  public static class FullNavigableStorageWithRandom {

    public NavigableStorage<Object> storage;
    public Random random;

    @Setup(Level.Iteration)
    public void setUp() {
      storage = new NavigableStorage<>(new ConcurrentSkipListMap<>());
      random = new Random();
      for (int i = 0; i < 10_000_000; i++) {
        storage.add(random.nextLong(), new Object());
      }
    }
  }

  @Benchmark
  public void navigableStorageSearch(FullNavigableStorageWithRandom state) {
    long one = state.random.nextLong();
    long two = state.random.nextLong();
    state.storage.search(Math.min(one, two), Math.max(one, two));
  }

  @State(Scope.Benchmark)
  public static class EmptySimpleStorageWithRandom {

    public SimpleStorage<Object> storage;
    public Random random;

    @Setup(Level.Iteration)
    public void setUp() {
      storage = new SimpleStorage<>(new ConcurrentHashMap<>());
      random = new Random();
    }
  }

  @Benchmark
  public void simpleStorageAdd(EmptySimpleStorageWithRandom state) {
    state.storage.add(state.random.nextLong(), new Object());
  }

  @State(Scope.Benchmark)
  public static class FullSimpleStorageWithRandom {

    public SimpleStorage<Object> storage;
    public Random random;

    @Setup(Level.Iteration)
    public void setUp() {
      storage = new SimpleStorage<>(new ConcurrentHashMap<>());
      random = new Random();
      for (int i = 0; i < 10_000_000; i++) {
        storage.add(random.nextLong(), new Object());
      }
    }
  }

  @Benchmark
  public void simpleStorageSearch(FullSimpleStorageWithRandom state) {
    state.storage.search(state.random.nextLong());
  }

  public static void main(String[] args) throws Exception {
    new Runner(new OptionsBuilder()
        .include(StorageBenchmark.class.getSimpleName())
        .mode(Mode.All)
        .threads(8)
        .forks(1)
        .warmupForks(1)
        .warmupIterations(2)
        .warmupTime(TimeValue.seconds(4))
        .measurementIterations(2)
        .measurementTime(TimeValue.seconds(4))
        .build())
        .run();
  }
}
