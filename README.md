# Monster's Den - Team 3

**Team Members:**
- Khan, Shahmeer 
- Singh, Simarjot 
- Zhou, Raymond
- Leon, Campos Maria 

Monster's Den is a 2D grid-based dungeon game built with Java 11, Maven, and JavaFX. 

---

## Requirements
- JDK 11 or higher
- Maven 3.8 or higher

Check your versions:
```bash
java -version
mvn -version
```

---

## Getting Started

### Clone the Repository
```bash
git clone https://github.sfu.ca/jminns/ProjectTeam3.git
cd ProjectTeam3
```

### Build the Project
```bash
mvn clean package
```

This creates:
- `target/monsters-den-1.0-SNAPSHOT-shaded.jar` - Executable JAR with all dependencies
- `target/lib/` - JavaFX native libraries (required to run the JAR)
- `target/site/apidocs/` - Javadoc documentation
- `artifacts/` - Pre-built artifacts (JAR + Javadocs pushed to repository)

---

## Running the Game

### Option 1: Using Maven (Recommended)
```bash
mvn javafx:run
```

### Option 2: Using the Pre-built JAR
We've included pre-built artifacts in the `artifacts/` directory. To run:

```bash
cd artifacts
java --module-path lib --add-modules javafx.controls,javafx.fxml -jar monsters-den-1.0-SNAPSHOT-shaded.jar
```

### Option 3: Using Your Own Built JAR
After running `mvn clean package`:

```bash
cd target
java --module-path lib --add-modules javafx.controls,javafx.fxml -jar monsters-den-1.0-SNAPSHOT-shaded.jar
```

**Note:** The JAR requires the `lib` folder (containing JavaFX native libraries) to be in the same directory.

---

## Testing

### Run All Tests
```bash
mvn test
```

### View Test Coverage
After running tests, open the JaCoCo coverage report:
```bash
open target/site/jacoco/index.html
```
Or navigate to `target/site/jacoco/index.html` in your browser.

---

## Documentation

### Generate Javadocs
Javadocs are automatically generated during `mvn package`, or generate manually:
```bash
mvn javadoc:javadoc
```

### View Javadocs
Open `target/site/apidocs/index.html` in your browser, or use the pre-generated docs in `artifacts/javadocs/`.

---

## Project Structure
```
ProjectTeam3/
├── src/
│   ├── main/
│   │   ├── java/com/team3/monstersden/
│   │   │   ├── Main.java              # Console entry point
│   │   │   └── MainFX.java            # JavaFX entry point
│   │   └── resources/                 # Images, maps, etc.
│   └── test/
│       └── java/com/team3/monstersden/ # Unit & integration tests
├── target/                             # Build output (generated)
│   ├── monsters-den-1.0-SNAPSHOT-shaded.jar
│   ├── lib/                            # JavaFX native libraries
│   └── site/
│       ├── jacoco/                     # Test coverage reports
│       └── apidocs/                    # Javadoc documentation
├── artifacts/                          # Pre-built artifacts (committed to repo)
│   ├── monsters-den-1.0-SNAPSHOT-shaded.jar
│   ├── lib/                            # JavaFX libraries
│   └── javadocs/                       # Pre-generated documentation
├── Documents/
│   └── Phase4Report.pdf                # Final project report
└── pom.xml                             # Maven configuration
```

---

## Quick Reference

| Task | Command |
|------|---------|
| Build everything | `mvn clean package` |
| Run game (easiest) | `mvn javafx:run` |
| Run tests | `mvn test` |
| Generate Javadocs | `mvn javadoc:javadoc` |
| View test coverage | Open `target/site/jacoco/index.html` |
| View Javadocs | Open `target/site/apidocs/index.html` |

---

## Troubleshooting

**"JavaFX runtime components are missing" error:**
- Ensure the `lib` folder is in the same directory as the JAR file
- Use the `--module-path lib --add-modules javafx.controls,javafx.fxml` flags when running

**"Could not find or load main class" error:**
- Run `mvn clean package` first to build the project
- Make sure you're using the `-shaded.jar` file, not the regular JAR

---

## Additional Information

For detailed game features, design decisions, and development process, please refer to `Documents/Phase4Report.pdf`.
