# Printing House Simulator

This is a Java project that models the business operations of a printing house using object-oriented principles.

## Features

* **Core Entities**: Models publications (Books, Posters), employees (Operators, Managers), and printing machines.
* **Business Logic**:
    * Flexible paper pricing model based on type (PLAIN, GLOSSY) and size (A5-A1).
    * Sales calculations with volume-based discounts.
    * Expense tracking for employee salaries (including manager bonuses) and paper inventory costs.
* **Technical Features**:
    * Custom exceptions for business rule violations (e.g., `NotEnoughPaperException`).
    * Text file I/O for saving and loading human-readable financial reports.
    * Java Serialization for saving and loading employee data.
* **Testing**: Includes a comprehensive JUnit 5 test suite covering all major business logic.

## Project Structure

The project follows a standard Maven layout:

* `src/main/java`: Main application source code.
    * `com.printinghouse.model`: Core entities (POJOs, Records).
    * `com.printinghouse.service`: Business logic services (Pricing, File I/O).
    * `com.printinghouse.exception`: Custom exception classes.
    * `com.printinghouse.Main`: A runnable class to simulate the printing house.
* `src/test/java`: JUnit 5 unit tests.
* `pom.xml`: Maven project file with dependencies (only JUnit 5).

## How to Run

### Prerequisites

* Java 17 (or newer)
* Apache Maven

### Running the Application (Simulation)

1.  Navigate to the project's root directory (where `pom.xml` is located).
2.  Compile the project:
    ```bash
    mvn compile
    ```
3.  Run the `Main` class simulation:
    ```bash
    mvn exec:java -Dexec.mainClass="com.printinghouse.Main"
    ```
    This will run the simulation defined in `Main.java` and print the output to the console. It will also generate a `financial_report.txt` file in the project's root directory.

### Running the Tests

1.  From the project's root directory, run the Maven test command:
    ```bash
    mvn test
    ```
    Maven will compile the project, run all tests in the `src/test/java` directory, and provide a summary report.
