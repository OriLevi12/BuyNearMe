# 🛒 Store Product Routing System – Backend Component

This project implements the backend logic and service layer of a store locator system based on a weighted graph. It enables managing stores and products, and finding the nearest store with a desired product using shortest path algorithm

## 💡 Features

- Add nodes and weighted edges to build a graph
- Add stores and assign them to locations in the graph
- Add products to stores
- Search for the nearest store with a specific product using:
    - Dijkstra's algorithm
    - A* algorithm (uses coordinates)
- See the full path and distance to the selected store
- File-based storage via `datasource.txt`
- Switchable algorithm at runtime

## 🧪 Demonstration

Run the `StoreTest.java` class for a complete set of test scenarios, including:
- Pathfinding
- Graph modification
- Algorithm comparison
- Error handling

## 📁 Structure

- `src/com/om/dm` – Data model (`Store`, `Product`, `StoreSearchResult`)
- `src/com/om/dao` – Data access layer (`IDao`, `DaoFileImpl`)
- `src/com/om/service` – Business logic and graph management
- `lib/ShortestPathAlgo.jar` – External pathfinding algorithms library
- `resources/datasource.txt` – Data storage

## 🔗 Dependencies

This project uses the [Pathfinding Library](https://github.com/OriLevi12/pathfinding-lib-java) as an external JAR dependency. The library provides:
- Multiple shortest path algorithms (Dijkstra, A*, Bellman-Ford)
- Unified interface for pathfinding operations
- Support for both directed and undirected graphs
- Coordinate-based node system
- Comprehensive error handling

## ⚠️ Note

1. Make sure the `datasource.txt` path matches what `DaoFileImpl` is configured to use.
2. The project requires the `ShortestPathAlgo.jar` to be present in the `lib` directory. You can build it from the [Pathfinding Library repository](https://github.com/OriLevi12/pathfinding-lib-java).

## 🛠️ Setup

1. Clone this repository
2. Build the pathfinding library from [OriLevi12/pathfinding-lib-java](https://github.com/OriLevi12/pathfinding-lib-java)
3. Copy the generated JAR to the `lib` directory
4. Open the project in your IDE
5. Run the tests to verify the setup