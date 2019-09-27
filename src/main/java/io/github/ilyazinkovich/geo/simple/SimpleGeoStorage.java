package io.github.ilyazinkovich.geo.simple;

import com.google.common.geometry.S1Angle;
import com.google.common.geometry.S2Cap;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2CellUnion;
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Loop;
import com.google.common.geometry.S2Point;
import com.google.common.geometry.S2Polygon;
import com.google.common.geometry.S2RegionCoverer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleGeoStorage<T> {

  private static final int EARTH_RADIUS_IN_METERS = 6371010;
  private final int minStorageLevel;
  private final int maxStorageLevel;
  private final SimpleStorage<T> storage;

  public SimpleGeoStorage(final int minStorageLevel, final int maxStorageLevel,
      final SimpleStorage<T> storage) {
    this.minStorageLevel = minStorageLevel;
    this.maxStorageLevel = maxStorageLevel;
    this.storage = storage;
  }

  public void addPoint(final T value, final double lat, final double lng) {
    S2CellId cellId = S2CellId.fromLatLng(S2LatLng.fromDegrees(lat, lng));
    for (int level = minStorageLevel; level <= maxStorageLevel; level++) {
      long geoHash = cellId.parent(level).id();
      storage.add(geoHash, value);
    }
  }

  public void addPolygon(final T value, final double[][] latlng) {
    S2CellUnion covering = coverPolygon(latlng);
    for (S2CellId cellId : covering) {
      storage.add(cellId.id(), value);
    }
  }

  private S2CellUnion coverPolygon(final double[][] latlng) {
    List<S2Point> points = new ArrayList<>(latlng.length);
    for (final double[] coordinates : latlng) {
      points.add(S2LatLng.fromDegrees(coordinates[0], coordinates[1]).toPoint());
    }
    S2Loop loop = new S2Loop(points);
    loop.normalize();
    S2Polygon polygon = new S2Polygon(loop);
    S2RegionCoverer coverer = new S2RegionCoverer();
    coverer.setMinLevel(minStorageLevel);
    coverer.setMaxLevel(maxStorageLevel);
    return coverer.getCovering(polygon);
  }

  public Set<T> search(final double lat, final double lng, final int radiusInMeters) {
    S2CellUnion covering = coverArea(lat, lng, radiusInMeters);
    Set<T> result = new HashSet<>();
    for (S2CellId cellId : covering) {
      result.addAll(storage.search(cellId.id()));
    }
    return result;
  }

  private S2CellUnion coverArea(final double lat, final double lng, final double radiusInMeters) {
    S2Point axis = S2LatLng.fromDegrees(lat, lng).toPoint();
    S1Angle axisAngle = S1Angle.radians(radiusInMeters / EARTH_RADIUS_IN_METERS);
    S2Cap cap = S2Cap.fromAxisAngle(axis, axisAngle);
    S2RegionCoverer coverer = new S2RegionCoverer();
    coverer.setMinLevel(minStorageLevel);
    coverer.setMaxLevel(maxStorageLevel);
    return coverer.getCovering(cap);
  }
}
