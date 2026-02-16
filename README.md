# CO2 Calculator

[![Java](https://img.shields.io/badge/Java-21-blue?logo=java&logoColor=white)](https://www.oracle.com/java/)  
[![Gradle](https://img.shields.io/badge/Gradle-8.14-green?logo=gradle&logoColor=white)](https://gradle.org/)

A simple, robust CLI tool to calculate CO2-equivalent emissions for trips between two cities using various transportation methods.

---

## 🧰 Features

- Calculates **CO2-equivalent** (kg) for trips between any two cities.
- Supports small, medium, large cars, buses, and trains.
- Integrates with **OpenRouteService API** for real distances.
- Command-line interface with named arguments (`--start`, `--end`, `--transportation-method`).
- Robust **error handling** with structured exit codes.
- Retries HTTP calls with **exponential backoff** on failure.
- Fully **unit tested** and **integration tested** (with mocked ORS API responses).
- Cross-platform execution (Linux, macOS, Windows) via shell script.

---

## ⚙️ Design Decisions

- **Why not Spring Boot?**
    - This is a **CLI utility**, not a web service. Spring Boot would introduce unnecessary complexity (dependency injection, web server, auto-configuration) for a small, self-contained tool.
    - Using **plain Java 21 with Gradle** keeps the project lightweight, fast to build, and easy to run on any machine.

- **HTTP Service**
    - `HttpService` handles retries, logging, and error codes.
    - Makes the code **testable**, avoids duplicating HTTP logic, and allows mocking for integration tests.

- **Argument parsing**
    - Custom parser ensures strict validation for required arguments, supports `--key=value` or `--key value` syntax, and throws meaningful errors for missing or duplicate arguments.

- **Environment variables**
    - ORS API token is read via `ORS_TOKEN` environment variable, keeping secrets out of source code or config files.

---

## 📦 Tech Stack

- **Java 21** – modern JVM improvements.
- **Gradle 8.14** – build tool, dependency management, test execution.
- **JUnit 5 + Mockito** – unit and integration testing.
- **OpenRouteService API** – geocoding and distance calculation.
- Shell script wrapper – cross-platform execution.

---

## ⚡ Getting Started

#### If unzipped the project
### 1. Set your ORS API token
```bash
export ORS_TOKEN=<your_api_key>  # Linux / macOS
setx ORS_TOKEN "<your_api_key>"   # Windows (cmd)
```
### 2. Run the CLI
```bash
# Linux / macOS /  Windows
./co2-calculator.sh --start=Hamburg --end=Berlin --transportation-method=diesel-car-medium
```

### Example output:
```
Your trip caused 49.2kg of CO2-equivalent.
```

#### OR build on your own 

### 1. Clone the repository
```bash
git clone https://github.com/yelpalekshitij/co2-calculator.git
cd co2-calculator
```
### 2. Set your ORS API token
```bash
export ORS_TOKEN=<your_api_key>  # Linux / macOS
setx ORS_TOKEN "<your_api_key>"   # Windows (cmd)
```
### 3. Build the project
```bash
./gradlew clean installDist
```
### 4. Run the CLI
```bash
# Linux / MacOS /  Windows
./co2-calculator.sh --start=Hamburg --end=Berlin --transportation-method=diesel-car-medium
```

---

## 🧪 Testing

Unit tests: validate argument parsing, emission calculations, service exceptions.

Integration tests: ORS API is mocked, returns static JSON to test full flow without hitting real endpoints.

Run tests:
```shell
./gradlew test
```

---

###  AI is used for following tasks:

- Generation of shell script (co2-calculator.sh).
- Resolving build.gradle.kts issues to ensure all dependencies are correctly included in the JAR during Gradle build execution.
- Referring some syntaxes in tests.
