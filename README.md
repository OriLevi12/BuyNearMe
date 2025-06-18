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
BuyNearMe/
├── lib/                           # External dependencies
│   ├── gson-2.10.1.jar           # Google's JSON library
│   └── ShortestPathAlgo.jar      # Pathfinding algorithms library
├── src/
│   └── main/
│       ├── java/
│       │   └── com/om/
│       │       ├── client/        # Client-side components
│       │       │   ├── SimpleClient.java    # User interface and input handling
│       │       │   └── NetworkClient.java   # Network communication layer
│       │       ├── server/        # Server-side components
│       │       │   ├── HandleRequest.java   # Request processing
│       │       │   ├── Server.java          # Main server class
│       │       │   ├── Request.java         # Request data model
│       │       │   └── Response.java        # Response data model
│       │       ├── controller/    # Request handling and routing
│       │       │   └── StoreController.java # Business logic controller
│       │       ├── dm/            # Data models
│       │       │   ├── Store.java           # Store entity
│       │       │   └── Product.java         # Product entity
│       │       ├── dao/           # Data access layer
│       │       │   ├── IDao.java            # Data access interface
│       │       │   └── DaoFileImpl.java     # File-based DAO implementation
│       │       ├── service/       # Business logic and graph management
│       │       │   └── StoreService.java    # Core business logic service
│       │       └── Main.java      # Application entry point
│       ├── resources/             # Application resources
│       │   ├── datasource.txt     # Store and product data storage
│       │   └── graph.dat          # Graph nodes and edges data storage
│       └── test/                  # Test cases
│           └── com/om/service/
│               └── StoreServiceTest.java    # Service layer tests
├── pom.xml                        # Maven project configuration
└── README.md                      # Project documentation
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