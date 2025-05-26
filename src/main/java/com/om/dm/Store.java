package com.om.dm;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a store that holds products and has a geographic location.
 */
public class Store implements Serializable {
    private int id;                    // Unique store ID
    private String name;              // Store name
    private double latitude;          // Store latitude (Y coordinate)
    private double longitude;         // Store longitude (X coordinate)
    private List<Product> products;   // List of products in this store
    private String locationId;        // Location ID for the store
    private List<String> pathToStore; // Path from user to this store
    private double distanceToStore;   // Total distance to this store

    /**
     * Default constructor - initializes an empty product list.
     */
    public Store() {
        products = new ArrayList<>();
        pathToStore = new ArrayList<>();
    }

    /**
     * Constructor with all store fields.
     */
    public Store(int id, String name, double latitude, double longitude, String locationId) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationId = locationId;
        this.products = new ArrayList<>();
        this.pathToStore = new ArrayList<>();
    }

    /**
     * Constructor with all fields, including product list.
     */
    public Store(int id, String name, double latitude, double longitude, List<Product> products, String locationId) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationId = locationId;
        this.products = products;
        this.pathToStore = new ArrayList<>();
    }

    // --- Getters and setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    /**
     * Adds a product to this store's product list.
     */
    public void addProduct(Product product) {
        products.add(product);
    }

    /**
     * Removes a product from the store by its ID.
     */
    public void removeProductById(int productId) {
        products.removeIf(p -> p.getId() == productId);
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public List<String> getPathToStore() {
        return pathToStore;
    }

    public void setPathToStore(List<String> pathToStore) {
        this.pathToStore = pathToStore;
    }

    public double getDistanceToStore() {
        return distanceToStore;
    }

    public void setDistanceToStore(double distanceToStore) {
        this.distanceToStore = distanceToStore;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Store{id=").append(id)
          .append(", name='").append(name).append('\'')
          .append(", location=(").append(latitude).append(",").append(longitude).append(")")
          .append(", locationId='").append(locationId).append('\'');
        
        if (!pathToStore.isEmpty()) {
            sb.append("\n  Path: ").append(String.join(" -> ", pathToStore));
            sb.append("\n  Total Distance: ").append(distanceToStore);
        }
        
        sb.append("\n  Products: ").append(products).append("}");
        return sb.toString();
    }
}