package es.uam.eps.ir.diversity.distance;

import java.util.List;

public abstract class DistanceModel {
    public abstract double distance (long item1, long item2);
    public abstract double maxDistance ();

    private final String name;

    public DistanceModel(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public double avgDistance (long item, List<Long> items) {
        return avgDistance (item, items, items.size());
    }

    public double avgDistance (long item, List<Long> items, int n) {
        n = Math.min(n, items.size());
        double distance = 0;
        for (int i = 0; i < n; i++) {
            distance += distance (item, items.get(i));
        }
        return n == 0? maxDistance() : distance / (double) n;
    }

    public double minDistance (long item, List<Long> items) {
        return minDistance (item, items, items.size());
    }

    public double minDistance (long item, List<Long> items, int n) {
        double distance = Double.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            distance = Math.min (distance, distance (item, items.get(i)));
        }
        return distance;
    }
}
