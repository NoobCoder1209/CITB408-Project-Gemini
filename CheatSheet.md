# Presentation & Analysis: Java Printing House Simulator

## 1\. Project Overview: The "Elevator Pitch"

  * **What is this project?**
    This is a "digital twin" of a real-world printing house, built in Java. It's not just a database for storing information; it's a **business operations simulator**. It models the interconnected processes of production, finance, inventory, and human resources.

  * **What is the core purpose?**
    The goal is to model and execute the *business logic* and *financial calculations* of the company. It's designed to answer questions like:

      * What is our total revenue, and what are our total expenses?
      * If revenue hits $10,000, how does that automatically affect our payroll calculations for managers?
      * Can "Machine-01" handle a print job for 5,000 copies of an A4 book, given its current paper load and color capabilities?
      * What is the correct sale price for a bulk order of 1,500 posters, applying our company's volume discount?

  * **What is the technology stack?**
    This is a focused, **core Java 17** project.

      * **Build Tool:** Apache **Maven** is used to manage the project structure, dependencies, and build lifecycle.
      * **Core Language:** Java 17, using modern features like `record` types (for `Paper`) and `Map.of()` helpers.
      * **Dependencies:** The *only* external dependency is **JUnit 5**. This was a deliberate choice to focus purely on object-oriented (OOP) principles and core Java capabilities without frameworks like Spring.

-----

## 2\. Core Architecture: The "Why" Behind the Structure

The project's design is built on **Separation of Concerns (SoC)**. Each package has a distinct and clear responsibility.

  * `model/` (The "Nouns" or "Data")

      * **Purpose:** These are the data blueprints of our business. They are "POJOs" (Plain Old Java Objects) or `record` types.
      * **Key Principle:** These classes hold *state*, not complex *logic*. A `Publication` object knows its title and page count, but it *doesn't* know how to calculate its own sale price. This is a crucial design choice.
      * **Sub-packages:** `employee/`, `publication/`, `paper/`, `machine/`.

  * `service/` (The "Verbs" or "Brains")

      * **Purpose:** This is where all the complex business logic lives. These classes are typically stateless and *act upon* the model objects.
      * **`PricingService`:** This is the "single source of truth" for paper costs. It's **decoupled** from the `Paper` model. If the CEO wants to change the pricing formula, we change it in *one place* here, without ever touching the `Paper` class.
      * **`FileService`:** This encapsulates all I/O logic. The main `PrintingHouse` class doesn't know *how* a report is saved; it just tells the `FileService` *what* to save.

  * `exception/` (The "Rulebook")

      * **Purpose:** We defined **custom, checked exceptions** to represent specific business-rule violations.
      * **Why?** Using `NotEnoughPaperException` is infinitely more descriptive and robust than returning `null` or `false`. It forces the calling code (the `PrintingMachine`) to acknowledge and handle a specific, predictable business error.

  * `Main.java` (The "Simulator")

      * **Purpose:** This is the "driver" or "runner." It's not part of the core library. It acts as a *consumer* of the `PrintingHouse` class to simulate a day-in-the-life, showing how all the pieces work together.

-----

## 3\. Key Component Deep Dive: The "How"

This project demonstrates several critical OOP concepts.

### A. The Employee Hierarchy (Polymorphism)

This is the most powerful OOP concept in the project.

  * **The Contract:** We have an `abstract class Employee`. It *guarantees* one method: `calculateSalary()`.
  * **The Implementations:**
      * `Operator` implements `calculateSalary()` with a simple rule: `return baseSalary;`.
      * `Manager` *also* implements `calculateSalary()`, but it has a second, overloaded method: `calculateSalary(revenue, threshold)`. This method contains complex bonus logic.
  * **The Payoff (Polymsorphism):**
    In `PrintingHouse.calculateTotalExpenses()`, we have **one simple loop**:
    ```java
    for (Employee emp : employees) {
        if (emp instanceof Manager manager) {
            totalSalaries = totalSalaries.add(manager.calculateSalary(...));
        } else {
            totalSalaries = totalSalaries.add(emp.calculateSalary());
        }
    }
    ```
    This loop treats all employees the same, but thanks to polymorphism, it *automatically* calls the correct `calculateSalary` logic for each object. If we add a new `Intern` class, this loop **does not need to change**. This is the **Open-Closed Principle**: our system is *open* to extension (new employee types) but *closed* for modification (we don't have to change the payroll code).

### B. The `PrintingMachine` (State Management & Error Handling)

This class is a "stateful agent." Its behavior today is dependent on what happened to it yesterday.

  * **State:** It holds `currentPaperLoad` and `loadedPaper`.
  * **Enforcing Rules:** The `printPublication()` method is a perfect example of robust error handling. It's a "gauntlet" of checks that must be passed *in order*:
    1.  **Capability Check:** `if (useColor && !isColor)` -\> Throws `InvalidPrintRequestException`. It fails fast *before* checking paper.
    2.  **Configuration Check:** `if (publication.getPageSize() != loadedPaper.pageSize())` -\> Throws `InvalidPrintRequestException`. It checks if the machine is set up for this job.
    3.  **Resource Check:** `if (sheetsNeeded > currentPaperLoad)` -\> Throws `NotEnoughPaperException`. Only *after* all other checks pass does it check if it has enough paper.
  * **Result:** This prevents corrupted data (like a negative paper count) and makes the system's behavior predictable.

### C. The `PrintingHouse` (The Facade Pattern)

The `PrintingHouse` class acts as a **Facade**.

  * **What it does:** It provides a simple, clean API for the "outside world" (like `Main.java` or a future web controller).
  * **Example:** The `Main` class just calls `house.calculateTotalExpenses()`. It *doesn't* need to know about `PricingService`, `Manager` bonuses, or `Operator` salaries. The `PrintingHouse` class *orchestrates* all that complexity internally.
  * **Benefit:** This simplifies the system. The "caller" doesn't need to manage 5 different objects; it just tells the `PrintingHouse` what it wants done.

-----

## 4\. Technical Features: I/O and Persistence

We implemented two different kinds of data persistence to meet different needs.

### A. Human-Readable Text Reports

  * **How:** `saveReport()` uses a `BufferedWriter` to manually format strings and write them to a `.txt` file. `loadReport()` uses a `BufferedReader` and string-splitting logic to parse it.
  * **Pros:** Easy to read, debug, and share. Any user can open `financial_report.txt` in a text editor.
  * **Cons:** Very brittle. If we change the text format (e.g., from "Total Revenue:" to "Revenue:"), the parser will break.
  * **Use Case:** Ideal for final financial reports, logs, and human-readable configuration.

### B. Java Object Serialization

  * **How:** `saveEmployees()` uses an `ObjectOutputStream`. The `Employee` class (and its subclasses) `implements Serializable`.
  * **Pros:** Extremely simple and powerful. With one line (`oos.writeObject(employees)`), we save the *entire list of objects*, preserving their types (a `Manager` is saved as a `Manager`) and all their data.
  * **Cons:** A binary, unreadable file. It's also brittle; if we add or remove a field from the `Employee` class, loading an old file will fail (unless we manage `serialVersionUIDs`).
  * **Use Case:** Ideal for "saving the state" of an application, like saving a game or a user's session, to be perfectly restored later.

-----

## 5\. Project Execution: The Maven Lifecycle

We use Maven to manage the entire project. Here's what the commands do:

1.  `mvn compile`

      * **What it does:** "Are there any syntax errors?"
      * It finds all `.java` files in `src/main/java`, compiles them into `.class` (bytecode) files, and puts them in the `target/classes` directory.

2.  `mvn test`

      * **What it does:** "Does the logic work as expected?"
      * This is our **quality gate**. It first compiles all the test code from `src/test/java`. Then, it runs *every single test method* (all the `@Test` annotations) using JUnit 5.
      * **Example:** It *proves* that the manager's bonus is calculated correctly, that the pricing for A3 paper is correct, and that the `PrintingMachine` *does* throw an exception when it's out of paper.
      * If any test fails, the build stops.

3.  `mvn exec:java -Dexec.mainClass="com.printinghouse.Main"`

      * **What it does:** "Run the simulation."
      * It executes the compiled Java code, starting at the `main` method in our `com.printinghouse.Main` class.
      * This is what runs the demo, prints the output to the console, and generates the `financial_report.txt` file.

4.  `(Future) mvn package`

      * **What it does:** "Create a distributable file."
      * It takes all the compiled code from `target/classes` and bundles it into a single `.jar` file (e.g., `printing-house-project-1.0.0.jar`). This one file is all we'd need to deploy and run this application on a server.
