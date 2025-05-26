package com.om.dm;

import java.util.List;

public class StoreSearchResult {
    private Store store;
    private List<String> path;
    private double totalDistance;

    public StoreSearchResult(Store store, List<String> path, double totalDistance) {
        this.store = store;
        this.path = path;
        this.totalDistance = totalDistance;
    }

    public Store getStore() {
        return store;
    }

    public List<String> getPath() {
        return path;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    @Override
    public String toString() {
        return "StoreSearchResult{" +
                "\n  store=" + store +
                "\n  path=" + String.join(" -> ", path) +
                "\n  totalDistance=" + totalDistance +
                "\n}";
    }
} 