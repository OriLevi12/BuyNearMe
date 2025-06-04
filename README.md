# 🛒 Store Product Routing System – Backend Component

This project implements the backend logic and service layer of a store locator system based on a weighted graph. It enables managing stores and products, and finding the nearest store with a desired product using shortest path algorithm

## 💡 Features

- Add nodes and weighted edges to build a graph
- Add stores and assign them to locations in the graph
  - Stores are created with just name and location
  - Coordinates are automatically set based on node location
  - IDs are automatically generated
- Add products to stores
- Search for the nearest store with a specific product using:
    - Dijkstra's algorithm
    - A* algorithm (uses coordinates)
- See the full path and distance to the selected store
- File-based storage via `datasource.txt`
- Switchable algorithm at runtime
- Client-server architecture for remote access
- JSON-based communication

## 🧪 Demonstration

Run the `StoreTest.java` class for a complete set of test scenarios, including:
- Pathfinding
- Graph modification
- Algorithm comparison
- Error handling

## 📁 Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/om/
│   │       ├── client/     # Client-side components
│   │       ├── server/     # Server-side components
│   │       ├── controller/ # Request handling and routing
│   │       ├── dm/         # Data models (Store, Product, StoreSearchResult)
│   │       ├── dao/        # Data access layer (IDao, DaoFileImpl)
│   │       ├── service/    # Business logic and graph management
│   │       └── Main.java   # Application entry point
│   ├── resources/
│   │   └── datasource.txt  # Data storage
│   └── test/              # Test cases
lib/
├── ShortestPathAlgo.jar   # External pathfinding algorithms library
└── gson-2.10.1.jar       # Google's JSON library for Java
```

## 🔗 Dependencies

This project uses the [Pathfinding Library](https://github.com/OriLevi12/pathfinding-lib-java) as an external JAR dependency. The library provides:
- Multiple shortest path algorithms (Dijkstra, A*, Bellman-Ford)
- Unified interface for pathfinding operations
- Support for both directed and undirected graphs
- Coordinate-based node system
- Comprehensive error handling

## ⚠️ Note

1. Make sure the `datasource.txt` path matches what `DaoFileImpl` is configured to use.
2. The project requires the following JAR files to be present in the `lib` directory:
   - `ShortestPathAlgo.jar` - You can build it from the [Pathfinding Library repository](https://github.com/OriLevi12/pathfinding-lib-java)
   - `gson-2.10.1.jar` - Required for JSON serialization/deserialization
3. Ensure all dependencies are properly added to your project's build path

## 🛠️ Setup

1. Clone this repository
2. Build the pathfinding library from [OriLevi12/pathfinding-lib-java](https://github.com/OriLevi12/pathfinding-lib-java)
3. Copy the generated JAR to the `lib` directory
4. Download Gson library (version 2.10.1) and add it to the `lib` directory
5. Open the project in your IDE
6. Add both JAR files to your project's build path
7. Run the tests to verify the setup