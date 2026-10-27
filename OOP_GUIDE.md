# Object-Oriented Programming (OOP) Guide & Viva Companion

**Project:** Expense Tracker System — Java OOP Application  
**Target Audience:** B.Tech Computer Science Students & College Viva Examiners  
**Course Context:** Object-Oriented Programming in Java (KTU CSE Syllabus)  
**Presented by:** Pavithra C, Sreenanda M, Sree Lekshmi.S, Sona Benny, Sanusha S Kumar, Sayoojya Das AK  
**Institution:** Department of Computer Science & Engineering, LBS Institute of Technology for Women (LBSITW)  

---

## 1. Architectural Overview & OOP Mapping

The application follows a standard **Tiered / Layered Enterprise Architecture** with strict Separation of Concerns:

```
[ Frontend: HTML5 / CSS3 / Vanilla JavaScript ]
                      │
                      ▼ (HTTP REST / JSON / Session Cookies)
[ Controller / Servlet Layer: Auth, Transaction, Budget, Alert, Report ]
                      │ (Composition: Controller has-a Service)
                      ▼
[ Service Layer: UserService, TransactionService, BudgetService, AlertService, ReportGenerator ]
                      │ (Composition: Service has-a DAO)
                      ▼
[ Data Access Object (DAO) Layer: UserDao, TransactionDao, BudgetDao, AlertDao ]
                      │ (JDBC: PreparedStatements, Connection, ResultSet)
                      ▼
[ Persistence Layer: MySQL Relational Database (InnoDB) ]
```

---

## 2. Mandatory OOP Concepts Demonstrated in Code

### A. Encapsulation (Data Hiding & Validation)

#### Definition
Encapsulation is the bundling of data (attributes) and methods that operate on that data into a single unit (class), while restricting direct access to internal state using private access modifiers.

#### Implementation in our Project
1. **Private Member Variables:** Every field in our domain models (`User`, `Transaction`, `Income`, `Expense`, `Budget`, `Alert`) is declared `private`. No outside class can manipulate raw memory values directly.
2. **Defensive Validation in Setters:** Mutators (`setter` methods) enforce domain integrity rules and throw custom `ValidationException` when violated:
   - **Amounts:** Must be strictly positive numbers (`amount > 0`). In `Transaction.java`:
     ```java
     public void setAmount(double amount) throws ValidationException {
         if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0.0) {
             throw new ValidationException("Transaction amount must be a positive number greater than 0.");
         }
         this.amount = Math.round(amount * 100.0) / 100.0;
     }
     ```
   - **Dates:** Dates cannot be null and cannot be set in the future. In `Transaction.java`:
     ```java
     public void setTransactionDate(LocalDate transactionDate) throws ValidationException {
         if (transactionDate == null) {
             throw new ValidationException("Transaction date is required.");
         }
         if (transactionDate.isAfter(LocalDate.now().plusDays(1))) {
             throw new ValidationException("Transaction date cannot be in the future.");
         }
         this.transactionDate = transactionDate;
     }
     ```
   - **Allowed Categories / Sources:** Enforced against predefined constants (`VALID_SOURCES` in `Income.java` and `VALID_CATEGORIES` in `Expense.java`). Any unknown category is rejected before reaching the database.
   - **Email and Password:** In `User.java`, emails are validated using regular expressions (`Pattern.compile("^[A-Za-z0-9+_.-]+@...")`), and passwords require a minimum of 6 characters.

---

### B. Inheritance (Code Reuse & Specialization)

#### Definition
Inheritance is the mechanism by which a subclass inherits fields and methods from a superclass, enabling code reuse and establishing an *“is-a”* hierarchical relationship.

#### Implementation in our Project
1. **Abstract Base Class:** `Transaction.java` encapsulates common attributes shared by all financial records:
   - `id` (int)
   - `userId` (int)
   - `amount` (double)
   - `transactionDate` (LocalDate)
   - `description` (String)
   - `createdAt` (Timestamp)
2. **Specialized Subclass 1: `Income extends Transaction`**
   - Establishes that *“Income is-a Transaction”*.
   - Reuses base fields through `super(userId, amount, transactionDate, description)`.
   - Introduces specialized attribute: `private String source` (e.g., Salary, Scholarship, Freelance, Gift).
3. **Specialized Subclass 2: `Expense extends Transaction`**
   - Establishes that *“Expense is-a Transaction”*.
   - Reuses base fields through `super(...)`.
   - Introduces specialized attribute: `private String category` (e.g., Food, Transport, Shopping, Bills, Education).

---

### C. Polymorphism (One Interface, Multiple Forms)

#### 1. Runtime Polymorphism (Dynamic Method Dispatch / Overriding)

##### Definition
Occurs when a subclass provides a specific implementation of a method declared in its superclass. The JVM decides at runtime which overridden method to execute based on the actual object instance referenced.

##### Implementation in our Project
The abstract class `Transaction` declares:
```java
public abstract double getSignedAmount();
public abstract String getTransactionType();
public abstract String getCategoryOrSource();
```

Both subclasses override these methods with distinct behavior:
- **`Income.java`:**
  ```java
  @Override
  public double getSignedAmount() {
      return getAmount(); // Inflow is positive (+amount)
  }

  @Override
  public String getTransactionType() {
      return "INCOME";
  }

  @Override
  public String getCategoryOrSource() {
      return this.source;
  }
  ```
- **`Expense.java`:**
  ```java
  @Override
  public double getSignedAmount() {
      return -getAmount(); // Outflow is negative (-amount)
  }

  @Override
  public String getTransactionType() {
      return "EXPENSE";
  }

  @Override
  public String getCategoryOrSource() {
      return this.category;
  }
  ```

##### Viva Demonstration in `TransactionServiceImpl.java`:
When calculating the net balance of a ledger folio:
```java
@Override
public double calculateNetBalance(List<Transaction> transactions) {
    double net = 0.0;
    for (Transaction tx : transactions) {
        // POLYMORPHIC CALL:
        // No "if (tx instanceof Income)" or "if (tx instanceof Expense)"!
        // JVM dynamically dispatches to Income.getSignedAmount() (+amount)
        // or Expense.getSignedAmount() (-amount) automatically.
        net += tx.getSignedAmount();
    }
    return Math.round(net * 100.0) / 100.0;
}
```

#### 2. Compile-Time Polymorphism (Method Overloading)

##### Definition
Occurs when two or more methods in the same class have the same name but different parameter lists (different number, type, or order of parameters). Resolved by the compiler.

##### Implementation in `TransactionService.java` & `TransactionServiceImpl.java`:
```java
// Overload 1: Retrieve all transactions
List<Transaction> getTransactions(int userId);

// Overload 2: Filter by transaction type only
List<Transaction> getTransactions(int userId, String type);

// Overload 3: Filter by date range
List<Transaction> getTransactions(int userId, LocalDate startDate, LocalDate endDate);

// Overload 4: Full multi-criteria filter
List<Transaction> getTransactions(int userId, String type, String categoryOrSource, 
                                  LocalDate startDate, LocalDate endDate);
```

##### Implementation in `BudgetService.java`:
```java
// Overload 1: Default to current month
List<BudgetProgress> getBudgetProgress(int userId);

// Overload 2: Target specific historical or future month
List<BudgetProgress> getBudgetProgress(int userId, String monthYear);
```

---

### D. Abstraction (Hiding Complexity)

#### Definition
Abstraction is the quality of dealing with ideas rather than events. In OOP, it means hiding implementation details and exposing only the essential interface to the user.

#### Implementation in our Project
1. **Abstract Class:** `Transaction` cannot be instantiated directly with `new Transaction()`. It forces callers to instantiate a concrete economic event (`Income` or `Expense`).
2. **DAO Interfaces:**
   - `UserDao`, `TransactionDao`, `BudgetDao`, `AlertDao`.
   - Business services interact purely with the interface without knowing whether storage is MySQL, PostgreSQL, or an in-memory cache.
3. **Service Interfaces:**
   - `AlertService` defines *what* smart budget evaluation does, while `AlertServiceImpl` hides *how* the 80% threshold calculation, SQL aggregations, and duplicate prevention algorithms operate.
4. **Strategy Pattern Abstraction (`ReportGenerator`):**
   - The interface `ReportGenerator` defines:
     ```java
     public interface ReportGenerator {
         String generateReport(List<Transaction> transactions);
         String getContentType();
         String getFileExtension();
     }
     ```
   - Two concrete strategies implement it:
     - `CsvReportGenerator`: Formats records into an RFC-4180 CSV spreadsheet.
     - `SummaryReportGenerator`: Formats records into an editorial plain-text ledger.
   - The `ReportController` can swap generators at runtime without altering report-processing logic.

---

### E. Additional OOP Best Practices

#### 1. Composition (*“has-a”* relationship)
Rather than using inheritance for everything, our services use composition:
- `TransactionServiceImpl` **has-a** `TransactionDao` and **has-a** `AlertService`.
- `BudgetServiceImpl` **has-a** `BudgetDao` and **has-a** `TransactionDao`.
- `TransactionController` **has-a** `TransactionService`.
This promotes loose coupling and testability.

#### 2. Factory Pattern (`TransactionFactory.java`)
Decouples object instantiation from business services:
```java
Transaction tx = TransactionFactory.createTransaction(
    type, userId, amount, date, description, categoryOrSource
);
```
Based on the `type` parameter ("INCOME" or "EXPENSE"), the factory instantiates the correct subclass and returns an abstract `Transaction` reference.

#### 3. Singleton Pattern (`DatabaseUtil.java`)
Centralizes JDBC connection configuration and driver initialization. Properties are loaded once and connection creation is governed through static methods.

#### 4. Custom Exception Hierarchy
Demonstrates OOP exception inheritance:
```
java.lang.Throwable
   └── java.lang.Exception
         └── ExpenseTrackerException (Base)
               ├── ValidationException
               ├── DatabaseException
               └── AuthenticationException
```

---

## 3. Mapping Table: Concepts to Code

| OOP Concept | Class Name | Method / Implementation Reference |
|---|---|---|
| **Encapsulation** | `com.expensetracker.model.User` | `setName()`, `setEmail()`, private fields |
| **Encapsulation** | `com.expensetracker.model.Transaction` | `setAmount()`, `setTransactionDate()` |
| **Encapsulation** | `com.expensetracker.model.Expense` | `setCategory()` with `VALID_CATEGORIES` list |
| **Inheritance** | `com.expensetracker.model.Transaction` | Abstract base class with common fields |
| **Inheritance** | `com.expensetracker.model.Income` | `public class Income extends Transaction` |
| **Inheritance** | `com.expensetracker.model.Expense` | `public class Expense extends Transaction` |
| **Runtime Polymorphism** | `com.expensetracker.model.Income` | `@Override public double getSignedAmount()` (+amount) |
| **Runtime Polymorphism** | `com.expensetracker.model.Expense` | `@Override public double getSignedAmount()` (-amount) |
| **Runtime Polymorphism** | `com.expensetracker.service.TransactionServiceImpl` | `calculateNetBalance(List<Transaction>)` |
| **Compile-Time Polymorphism** | `com.expensetracker.service.TransactionService` | `getTransactions()` with 1, 2, 3, & 4 arguments |
| **Compile-Time Polymorphism** | `com.expensetracker.service.BudgetService` | `getBudgetProgress(int)` vs `getBudgetProgress(int, String)` |
| **Abstraction** | `com.expensetracker.service.AlertService` | Interface hiding spending alert logic |
| **Abstraction** | `com.expensetracker.service.ReportGenerator` | Strategy interface implemented by CSV & Text generators |
| **Abstraction** | `com.expensetracker.dao.TransactionDao` | Interface hiding JDBC / SQL PreparedStatements |
| **Composition** | `com.expensetracker.service.TransactionServiceImpl` | `private final TransactionDao transactionDao;` |
| **Factory Pattern** | `com.expensetracker.factory.TransactionFactory` | `createTransaction(type, userId, amount, ...)` |
| **Exception Hierarchy** | `com.expensetracker.exception.*` | `ValidationException extends ExpenseTrackerException` |

---

## 4. Star Feature: Smart Spending Alert OOP Logic

The Smart Spending Alert is designed using the **Observer / Event Trigger Principle**:

```
[ User Logs or Edits Expense ]
              │
              ▼
    TransactionService.addTransaction()
              │
              ▼ (triggers alert evaluation)
    AlertService.evaluateSpendingAlert(userId, category, monthYear)
              │
              ├── 1. Fetches Budget Limit (BudgetDao)
              ├── 2. Calculates Live Spent Sum (TransactionDao)
              ├── 3. Computes Percentage: (spent / limit) * 100
              │
              ├── If percentage >= 80% and <= 100%:
              │   └── State: WARNING (Threshold Alert)
              │
              ├── If percentage > 100%:
              │   └── State: OVERSPENT (Excess amount calculated: spent - limit)
              │
              └── If percentage < 80%:
                  └── State: SAFE (Prior unresolved warnings cleared)
```

### Duplicate Alert Prevention:
In `AlertServiceImpl.java`:
Before inserting a new alert, the system checks `alertDao.findLatestByCategoryAndMonth(...)`. If an alert already exists with the same status (e.g. `WARNING`), the system updates the live numbers in place rather than bombarding the user with duplicate records. If the alert transitions to `OVERSPENT`, a new high-priority notification is registered.

---

## 5. College Viva Questions & Answers (25 Anticipated Q&A)

### Q1: What is the main objective of your project?
**Answer:** The Expense Tracker System is a full-stack personal finance application built with Java, MySQL, and Servlets that enables users to record income and expenses, maintain monthly category budgets, analyze spending trends, and receive automated Smart Spending Alerts before overspending occurs.

### Q2: Why did you make `Transaction` an abstract class instead of an interface or normal class?
**Answer:** `Transaction` has concrete state that all transactions share (ID, user ID, amount, date, description) with concrete validation logic. Making it an abstract class allows us to share state and code via inheritance, while leaving `getSignedAmount()`, `getTransactionType()`, and `getCategoryOrSource()` abstract so `Income` and `Expense` provide their own financial semantics. It also prevents instantiating an ambiguous `Transaction` object directly.

### Q3: How did you implement Encapsulation in your project?
**Answer:** All class attributes across all models (`User`, `Transaction`, `Budget`, etc.) are declared `private`. Access is only possible through getters and setters. Setters contain strict defensive validations—for example, preventing negative or zero amounts, future transaction dates, empty categories, or malformed email addresses—throwing `ValidationException` when violated.

### Q4: Give a concrete example of Runtime Polymorphism in your code.
**Answer:** In `TransactionServiceImpl.java`, the method `calculateNetBalance(List<Transaction> transactions)` loops through a list of abstract `Transaction` references and calls `tx.getSignedAmount()`. Without using any `instanceof` check, the JVM at runtime invokes `Income.getSignedAmount()` (which adds the amount) or `Expense.getSignedAmount()` (which subtracts the amount).

### Q5: What is Method Overloading, and where did you use it?
**Answer:** Method Overloading (compile-time polymorphism) occurs when multiple methods in the same class share a name but differ in parameter count or types. We used it in `TransactionService.java` with four overloaded variants of `getTransactions(...)` to allow querying all transactions, filtering by type, filtering by date interval, or filtering by all criteria simultaneously.

### Q6: What is the difference between Abstraction and Encapsulation?
**Answer:** 
- **Encapsulation** is data hiding and bundling methods with data (e.g., keeping `private double amount` and validating it in `setAmount()`).
- **Abstraction** is hiding internal implementation details and exposing a clean interface (e.g., using `AlertService` or `TransactionDao` interfaces without revealing SQL queries or math calculations to callers).

### Q7: Why did you use the DAO (Data Access Object) pattern?
**Answer:** The DAO pattern isolates database operations from business logic. If we ever switch the persistence store from MySQL to PostgreSQL or MongoDB, we only change the DAO implementation (`TransactionDaoImpl`), leaving the Service and Controller layers untouched.

### Q8: How did you prevent SQL Injection in your database operations?
**Answer:** We used JDBC `PreparedStatement` with parameterized placeholders (`?`) for every query. Parameter values are escaped and typed by the JDBC driver, preventing malicious SQL strings from altering query structure.

### Q9: How are passwords stored securely?
**Answer:** Passwords are never stored in plain text. In `PasswordUtil.java`, we generate a cryptographically secure random 16-byte salt using `SecureRandom` and hash the combined password and salt using SHA-256 before storing them in MySQL.

### Q10: How does your Smart Spending Alert feature work?
**Answer:** Whenever an expense is added, edited, or deleted, or when a budget limit is updated, the `AlertService` compares the live month-to-date category expenditure against the allocated limit. If spending reaches 80% to 100%, it flags a `WARNING`. If it exceeds 100%, it flags an `OVERSPENT` alert detailing the exact amount overspent. If spending falls below 80% after an expense deletion, past warnings are automatically resolved.

### Q11: How do you prevent duplicate alerts from spamming the user?
**Answer:** In `AlertServiceImpl.java`, before creating a record, we check the latest recorded alert for that category and month. If the alert level has not changed (e.g., still in `WARNING`), we update the existing record with refreshed numbers rather than inserting duplicate records.

### Q12: Why did you use Composition instead of multiple inheritance?
**Answer:** Java does not support multiple inheritance with classes to avoid the Diamond Problem. Composition (*“has-a”*) provides greater flexibility, lower coupling, and adheres to the principle: *“Favor object composition over class inheritance.”* For example, `TransactionServiceImpl` has-a `TransactionDao`.

### Q13: What is the Factory Pattern in your project?
**Answer:** `TransactionFactory.java` provides static factory methods to construct either `Income` or `Expense` objects based on a type string ("INCOME" or "EXPENSE"), decoupling caller code from direct subclass constructor calls.

### Q14: How does user data isolation work?
**Answer:** Every database table (`transactions`, `budgets`, `alerts`) contains a foreign key `user_id`. Every SQL query in our DAO layer explicitly filters by `user_id = ?`, ensuring that users can only view, edit, or delete their own financial records.

### Q15: What design pattern did you use for database connections?
**Answer:** The Singleton / Static Factory pattern in `DatabaseUtil.java`. It centralizes driver loading, connection pooling properties, and safe closing of `Connection`, `PreparedStatement`, and `ResultSet` objects.

### Q16: How does the CSV Export work in terms of OOP?
**Answer:** We defined an abstraction `ReportGenerator` interface. `CsvReportGenerator` implements this interface and generates RFC-4180 compliant CSV text by polymorphically reading each transaction's getters. Because it implements `ReportGenerator`, the controller interacts with it via interface abstraction.

### Q17: What happens if an expense is deleted?
**Answer:** When an expense is deleted, `TransactionServiceImpl.deleteTransaction()` removes the row and immediately notifies `AlertService.evaluateSpendingAlert()`. If category spending drops below 80%, existing unread warnings for that category and month are automatically removed.

### Q18: What is a custom exception, and why did you create one?
**Answer:** Standard Java exceptions like `IllegalArgumentException` are generic. We created `ExpenseTrackerException` as a checked base exception, along with subclasses `ValidationException`, `DatabaseException`, and `AuthenticationException`. This allows catching and handling domain-specific errors cleanly.

### Q19: What is the role of `web.xml` in this application?
**Answer:** `web.xml` is the standard Jakarta EE / Java Web Application Deployment Descriptor. It maps URL routes (such as `/api/transactions`, `/api/budgets`) to their corresponding Servlet classes for Apache Tomcat.

### Q20: How does your Standalone Server work without Tomcat?
**Answer:** It uses the standard Java SE `com.sun.net.httpserver.HttpServer` class built into the JDK. It listens on port 8080, routes API requests to the same controllers, and serves the static HTML, CSS, and JS files, enabling instant execution without any external server configuration.

### Q21: What is the difference between `super` and `this` in Java?
**Answer:** `this` refers to the current instance of the class, while `super` refers to the immediate parent class instance, used to invoke parent constructors (`super(...)`) or access parent methods.

### Q22: What is the difference between method overriding and method overloading?
**Answer:**
- **Overloading:** Same method name, different signatures, in the same class, resolved at compile-time.
- **Overriding:** Same method name and same signature, across parent and child classes, resolved at runtime via dynamic method dispatch.

### Q23: Why did you choose MySQL and JDBC over ORM frameworks like Hibernate?
**Answer:** For an academic project demonstrating core OOP, direct JDBC with PreparedStatements provides full transparency into SQL queries, connection lifecycles, and transactions without introducing heavy third-party framework magic that obscures learning.

### Q24: What is the purpose of the `salt` in password hashing?
**Answer:** A salt is a unique random string appended to the password before hashing. It ensures that even if two users choose the same password (e.g. `Password@123`), their resulting hashes in the database will be completely different, protecting against Rainbow Table attacks.

### Q25: How does the user interface reflect an editorial ledger rather than a generic SaaS theme?
**Answer:** Instead of purple gradients and generic cards, the UI uses warm paper / soft ivory tones (`#F9F7F2`), deep botanical forest-green headings (`#1B3B2B`), terracotta accents (`#C85A32`), elegant serif typography (`Newsreader`), tabular figures for alignment, and classic double bottom ruling on net ledger totals.
