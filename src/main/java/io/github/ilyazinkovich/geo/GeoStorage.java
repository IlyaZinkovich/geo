package io.github.ilyazinkovich.geo;

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

public class GeoStorage<T> {

  private static final int EARTH_RADIUS_IN_METERS = 6371010;
  private final int storageLevel;
  private final NavigableStorage<T> storage;

  public GeoStorage(final int storageLevel, final NavigableStorage<T> storage) {
    this.storageLevel = storageLevel;
    this.storage = storage;
  }

  public void addPoint(final T value, final double lat, final double lng) {
    long geoHash = S2CellId.fromLatLng(S2LatLng.fromDegrees(lat, lng)).parent(storageLevel).id();
    storage.add(geoHash, value);
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
    coverer.setMinLevel(storageLevel);
    coverer.setMaxLevel(storageLevel);
    return coverer.getCovering(polygon);
  }

  public Set<T> search(final double lat, final double lng, final int radiusInMeters) {
    S2CellUnion covering = coverArea(lat, lng, radiusInMeters);
    Set<T> result = new HashSet<>();
    for (S2CellId cellId : covering) {
      if (cellId.level() < storageLevel) {
        S2CellId begin = cellId.childBegin(storageLevel);
        S2CellId end = cellId.childEnd(storageLevel);
        result.addAll(storage.search(begin.id(), end.id()));
      } else {
        result.addAll(storage.get(cellId.id()));
      }
    }
    return result;
  }

  private S2CellUnion coverArea(final double lat, final double lng, final double radiusInMeters) {
    S2Point axis = S2LatLng.fromDegrees(lat, lng).toPoint();
    S1Angle axisAngle = S1Angle.radians(radiusInMeters / EARTH_RADIUS_IN_METERS);
    S2Cap cap = S2Cap.fromAxisAngle(axis, axisAngle);
    S2RegionCoverer coverer = new S2RegionCoverer();
    coverer.setMaxLevel(storageLevel);
    return coverer.getCovering(cap);
  }
}
