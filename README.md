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

## 🔄 Algorithm Switching

The system supports two pathfinding algorithms that can be switched at runtime:

### Dijkstra's Algorithm (Default)
- **Best for**: Abstract graphs without geographic coordinates
- **Guarantees**: Shortest path
- **Use case**: When you have a graph with arbitrary weights

### A* Algorithm
- **Best for**: Geographic coordinates with heuristics
- **Advantage**: Faster pathfinding using coordinate-based heuristics
- **Use case**: When you have real-world locations with coordinates

### How to Switch Algorithms

#### Using the Simple Client:
1. Start the server: `java -cp "lib/*;src/main/java" com.om.Main`
2. Start the client: `java -cp "lib/*;src/main/java" com.om.client.SimpleClient`
3. Choose option **20** to switch to A* algorithm
4. Choose option **21** to switch to Dijkstra algorithm
5. Choose option **22** to see the current algorithm


## 🧪 Testing

This project includes comprehensive test coverage with JUnit 5. The tests are organized into logical modules:

### Test Files
- `GraphOperationsTest.java` - Tests for graph/node/edge operations
- `StoreManagementTest.java` - Tests for store CRUD operations  
- `ProductManagementTest.java` - Tests for product management
- `PathfindingTest.java` - Tests for pathfinding algorithms

### Running Tests

#### Prerequisites
- JUnit JARs must be in the `lib` directory:
  - `junit-platform-console-standalone-1.10.0.jar`

#### Compile Tests
```bash
javac -cp "lib/*;src/main/java" -d src/main/java src/main/test/com/om/service/*.java
```

#### Run All Tests
```bash
java -cp "lib/*;src/main/java" org.junit.platform.console.ConsoleLauncher --class-path src/main/java --scan-class-path
```

#### Run Specific Test Files
```bash
# Run a single test file
java -cp "lib/*;src/main/java" org.junit.platform.console.ConsoleLauncher --class-path src/main/java --select-class com.om.service.GraphOperationsTest

# Run multiple specific test files
java -cp "lib/*;src/main/java" org.junit.platform.console.ConsoleLauncher --class-path src/main/java --select-class com.om.service.GraphOperationsTest --select-class com.om.service.StoreManagementTest --select-class com.om.service.ProductManagementTest --select-class com.om.service.PathfindingTest
```

#### Using IntelliJ IDEA
1. Right-click on any test file in the Project Explorer
2. Select "Run 'TestClassName'"
3. Or right-click on the `src/main/test` folder and select "Run 'All Tests'"

### Test Coverage
The test suite covers:
- ✅ Graph operations (add/remove nodes and edges)
- ✅ Store management (CRUD operations)
- ✅ Product management (add/remove/update products)
- ✅ Pathfinding algorithms (Dijkstra vs A*)
- ✅ Error handling and validation
- ✅ Integration tests
- ✅ Algorithm switching functionality

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
│   ├── junit-platform-console-standalone-1.10.0.jar  # JUnit 5 testing framework
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
│               ├── GraphOperationsTest.java    # Graph/node/edge operations tests
│               ├── StoreManagementTest.java    # Store CRUD operations tests
│               ├── ProductManagementTest.java  # Product management tests
│               └── PathfindingTest.java        # Pathfinding algorithms tests
├── BuyNearMe.iml                  # IntelliJ IDEA project configuration
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
5. Open the project in IntelliJ IDEA
6. Add both JAR files to your project's build path:
   - Right-click on each JAR in the `lib` directory
   - Select "Add as Library"
   - Choose "Project Library" level
7. Run the tests to verify the setup