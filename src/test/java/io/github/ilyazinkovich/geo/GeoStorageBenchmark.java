package io.github.ilyazinkovich.geo;

import io.github.ilyazinkovich.geo.navigable.NavigableGeoStorage;
import io.github.ilyazinkovich.geo.navigable.NavigableStorage;
import io.github.ilyazinkovich.geo.simple.SimpleGeoStorage;
import io.github.ilyazinkovich.geo.simple.SimpleStorage;
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

public class GeoStorageBenchmark {

  private static final int RADIUS_IN_METERS = 10000;
  private static final int MAX_POINTS_COUNT = 1_000_000;
  private static final int MIN_STORAGE_LEVEL = 10;
  private static final int MAX_STORAGE_LEVEL = 12;

  public static void main(String[] args) throws Exception {
    new Runner(new OptionsBuilder()
        .include(GeoStorageBenchmark.class.getSimpleName())
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

  @Benchmark
  public void navigableStorageAdd(EmptyNavigableGeoStorageWithRandom state) {
    double lat = state.random.nextDouble() % 90;
    double lng = state.random.nextDouble() % 180;
    state.storage.addPoint(new Object(), lat, lng);
  }

  @Benchmark
  public void navigableStorageSearch(FullNavigableGeoStorageWithRandom state) {
    double lat = state.random.nextDouble() % 90;
    double lng = state.random.nextDouble() % 180;
    state.storage.search(lat, lng, RADIUS_IN_METERS);
  }

  @Benchmark
  public void simpleStorageAdd(EmptySimpleGeoStorageWithRandom state) {
    double lat = state.random.nextDouble() % 90;
    double lng = state.random.nextDouble() % 180;
    state.storage.addPoint(new Object(), lat, lng);
  }

  @Benchmark
  public void simpleStorageSearch(FullSimpleGeoStorageWithRandom state) {
    double lat = state.random.nextDouble() % 90;
    double lng = state.random.nextDouble() % 180;
    state.storage.search(lat, lng, RADIUS_IN_METERS);
  }

  @State(Scope.Benchmark)
  public static class EmptyNavigableGeoStorageWithRandom {

    public NavigableGeoStorage<Object> storage;
    public Random random;

    @Setup(Level.Iteration)
    public void setUp() {
      NavigableStorage<Object> underlyingStorage =
          new NavigableStorage<>(new ConcurrentSkipListMap<>());
      storage = new NavigableGeoStorage<>(MAX_STORAGE_LEVEL, underlyingStorage);
      random = new Random();
    }
  }

  @State(Scope.Benchmark)
  public static class FullNavigableGeoStorageWithRandom {

    public NavigableGeoStorage<Object> storage;
    public Random random;

    @Setup(Level.Iteration)
    public void setUp() {
      NavigableStorage<Object> underlyingStorage =
          new NavigableStorage<>(new ConcurrentSkipListMap<>());
      storage = new NavigableGeoStorage<>(MAX_STORAGE_LEVEL, underlyingStorage);
      random = new Random();
      for (int i = 0; i < MAX_POINTS_COUNT; i++) {
        double lat = random.nextDouble() % 90;
        double lng = random.nextDouble() % 180;
        storage.addPoint(new Object(), lat, lng);
      }
    }
  }

  @State(Scope.Benchmark)
  public static class EmptySimpleGeoStorageWithRandom {

    public SimpleGeoStorage<Object> storage;
    public Random random;

    @Setup(Level.Iteration)
    public void setUp() {
      SimpleStorage<Object> underlyingStorage =
          new SimpleStorage<>(new ConcurrentHashMap<>());
      storage = new SimpleGeoStorage<>(MIN_STORAGE_LEVEL, MAX_STORAGE_LEVEL, underlyingStorage);
      random = new Random();
    }
  }

  @State(Scope.Benchmark)
  public static class FullSimpleGeoStorageWithRandom {

    public SimpleGeoStorage<Object> storage;
    public Random random;

    @Setup(Level.Iteration)
    public void setUp() {
      SimpleStorage<Object> underlyingStorage =
          new SimpleStorage<>(new ConcurrentHashMap<>());
      storage = new SimpleGeoStorage<>(MIN_STORAGE_LEVEL, MAX_STORAGE_LEVEL, underlyingStorage);
      random = new Random();
      for (int i = 0; i < MAX_POINTS_COUNT; i++) {
        double lat = random.nextDouble() % 90;
        double lng = random.nextDouble() % 180;
        storage.addPoint(new Object(), lat, lng);
      }
    }
  }
}
