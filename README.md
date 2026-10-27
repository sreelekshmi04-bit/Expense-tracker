# Expense Tracker System — Java OOP & MySQL Application

A personal finance management web application built with **Java (OOP), MySQL, JDBC, HTML5, CSS3, and Vanilla JavaScript**. Designed for academic project reviews, college viva demonstrations, and daily personal budgeting.

---

## Project Overview

- **Department:** Computer Science & Engineering
- **Institution:** LBS Institute of Technology for Women (LBSITW)
- **Project Team:** Pavithra C, Sreenanda M, Sree Lekshmi.S, Sona Benny, Sanusha S Kumar, Sayoojya Das AK
- **Syllabus / Context:** B.Tech Computer Science — Object-Oriented Programming in Java

---

## System Architecture (The Restaurant Analogy)

As presented in our project review, think of the system like a restaurant:

```
┌──────────────────────────────────────┐
│  FRONTEND (Dining Area)             │
│  • HTML5 Semantic Ledger Structure   │
│  • Editorial Journal CSS3 Palette   │
│  • Vanilla JavaScript (Fetch API)   │
└──────────────────┬───────────────────┘
                   │
                   ▼ (HTTP JSON & Cookies)
┌──────────────────────────────────────┐
│  JDBC (The Waiter)                   │
│  • java.sql.Connection               │
│  • java.sql.PreparedStatement        │
│  • java.sql.ResultSet                │
└──────────────────┬───────────────────┘
                   │
                   ▼
┌──────────────────────────────────────┐
│  BACKEND (The Kitchen)               │
│  • Controllers & Java Servlets       │
│  • Domain Models (Polymorphism)      │
│  • Business Services & Alert Engine  │
└──────────────────┬───────────────────┘
                   │
                   ▼
┌──────────────────────────────────────┐
│  DATABASE (The Storeroom)            │
│  • MySQL Relational Tables           │
│  • users, transactions, budgets,     │
│    alerts (Indexed & Isolated)       │
└──────────────────────────────────────┘
```

---

## Key Features

1. **User Authentication & Session Management:**
   - Registration and Login with salt-based SHA-256 password hashing.
   - Strict data isolation per user (`user_id`).
   - 1-click Demo credentials auto-filler for viva demonstrations.

2. **Income & Expense Journaling (Transactions):**
   - Add, edit, delete, and filter ledger items.
   - Income sources: `Salary`, `Scholarship`, `Freelance`, `Gift`, `Investment`, `Other`.
   - Expense categories: `Food`, `Transport`, `Shopping`, `Bills`, `Education`, `Entertainment`, `Healthcare`, `Other`.
   - Input validations and confirmation before deletion.

3. **Monthly Budget Envelopes:**
   - Set spending limits per expense category for any calendar month.
   - Live visual progress bars with Safe, Warning (80%+), and Exceeded indicators.

4. **Star Feature — Smart Spending Alerts:**
   - Evaluated immediately when any expense is added, edited, or deleted.
   - **Warning Threshold (80%):** Flags an alert when spending hits 80% of budget.
   - **Overspending Alert (>100%):** Calculates the exact overspend (e.g. *“You have exceeded your Food budget by ₹400.00 this month”*).
   - Duplicate prevention prevents notification spam unless the alert state changes.
   - Interactive notification bell and header badges.

5. **Financial Telemetry & CSV Audit:**
   - Live net ledger balance, total credits, total debits, and monthly savings rate.
   - 6-month comparative trend chart (pure SVG, zero external CDN dependencies).
   - Category allocation bar breakdowns.
   - Instant 1-click **Export to CSV** spreadsheet.

---

## Project Structure

```
oops project/
├── pom.xml                               # Maven project configuration (WAR packaging)
├── database.sql                          # Complete MySQL schema and seed data
├── db.properties                         # Active database connection settings
├── db.properties.example                 # Template configuration
├── build.bat                             # Batch script to compile Java code
├── run.bat                               # 1-Click server launcher batch script
├── OOP_GUIDE.md                          # Detailed OOP concepts breakdown & viva Q&A
├── README.md                             # This setup and reference manual
├── lib/
│   └── mysql-connector-j.jar             # MySQL JDBC Connector driver
├── src/
│   └── main/
│       ├── java/
│       │   ├── com/expensetracker/
│       │   │   ├── model/                # Encapsulated entities & DTOs
│       │   │   │   ├── User.java
│       │   │   │   ├── Transaction.java  # Abstract base class
│       │   │   │   ├── Income.java       # Subclass (+amount)
│       │   │   │   ├── Expense.java      # Subclass (-amount)
│       │   │   │   ├── Budget.java
│       │   │   │   ├── Alert.java
│       │   │   │   ├── BudgetProgress.java
│       │   │   │   ├── SpendingSummary.java
│       │   │   │   └── CategoryTotal.java
│       │   │   ├── dao/                  # Abstraction: Data Access Objects
│       │   │   │   ├── UserDao.java & UserDaoImpl.java
│       │   │   │   ├── TransactionDao.java & TransactionDaoImpl.java
│       │   │   │   ├── BudgetDao.java & BudgetDaoImpl.java
│       │   │   │   └── AlertDao.java & AlertDaoImpl.java
│       │   │   ├── service/              # Business rules & polymorphic report generators
│       │   │   │   ├── UserService.java & UserServiceImpl.java
│       │   │   │   ├── TransactionService.java & TransactionServiceImpl.java
│       │   │   │   ├── BudgetService.java & BudgetServiceImpl.java
│       │   │   │   ├── AlertService.java & AlertServiceImpl.java
│       │   │   │   ├── ReportGenerator.java (Interface)
│       │   │   │   ├── CsvReportGenerator.java
│       │   │   │   └── SummaryReportGenerator.java
│       │   │   ├── controller/           # Controller handlers
│       │   │   │   ├── AuthController.java
│       │   │   │   ├── TransactionController.java
│       │   │   │   ├── BudgetController.java
│       │   │   │   ├── AlertController.java
│       │   │   │   ├── DashboardController.java
│       │   │   │   └── ReportController.java
│       │   │   ├── servlet/              # Jakarta Servlets for Tomcat
│       │   │   │   ├── AuthServlet.java
│       │   │   │   ├── TransactionServlet.java
│       │   │   │   ├── BudgetServlet.java
│       │   │   │   ├── AlertServlet.java
│       │   │   │   ├── DashboardServlet.java
│       │   │   │   └── ReportServlet.java
│       │   │   ├── factory/              # Factory Pattern
│       │   │   │   └── TransactionFactory.java
│       │   │   ├── util/                 # Helpers (DB, Hashing, Dates, JSON)
│       │   │   │   ├── DatabaseUtil.java
│       │   │   │   ├── PasswordUtil.java
│       │   │   │   ├── ValidationUtil.java
│       │   │   │   ├── DateUtil.java
│       │   │   │   └── JsonUtil.java
│       │   │   ├── exception/            # Custom Exception Hierarchy
│       │   │   │   ├── ExpenseTrackerException.java
│       │   │   │   ├── ValidationException.java
│       │   │   │   ├── DatabaseException.java
│       │   │   │   └── AuthenticationException.java
│       │   │   └── server/               # Built-in zero-dependency HTTP server
│       │   │       └── StandaloneServer.java
│       │   └── jakarta/servlet/          # Portable Servlet API stubs
│       ├── resources/
│       │   └── db.properties             # Classpath DB configuration
│       └── webapp/                       # Editorial Ledger Web UI
│           ├── WEB-INF/web.xml           # Servlet mapping descriptor
│           ├── index.html                # Project introduction & landing page
│           ├── login.html                # Auth & Registration page
│           ├── dashboard.html            # Primary Ledger Dashboard
│           ├── transactions.html         # Full Transaction Ledger Folio
│           ├── budgets.html              # Budget Envelopes page
│           ├── reports.html              # Audit & CSV Export page
│           ├── css/
│           │   ├── ledger-theme.css      # Editorial palette & masthead
│           │   ├── components.css        # Ledger tables, cards, modals
│           │   └── responsive.css        # Mobile adaptability
│           └── js/
│               ├── api.js                # Fetch client library
│               ├── auth.js               # Auth & session logic
│               ├── dashboard.js          # Dashboard telemetry & charts
│               ├── transactions.js       # Transaction CRUD & modals
│               ├── budgets.js            # Budget envelopes & gauges
│               ├── reports.js            # Filters & CSV download
│               └── alerts.js             # Smart spending alert triggers
```

---

## Quick Start Guide

### Step 1: Set Up MySQL Database

1. Open **MySQL Workbench** or the **MySQL Command Line Client**.
2. Run the script provided in `database.sql`:
   ```sql
   source /path/to/database.sql;
   ```
   *(Or copy and paste the contents of `database.sql` into MySQL Workbench and click Execute).*
3. This creates the database `expense_tracker_db`, tables (`users`, `transactions`, `budgets`, `alerts`), and seed data.

### Step 2: Configure Database Credentials

Open `db.properties` in the project root directory and update the password to match your MySQL server password:
```properties
db.url=jdbc:mysql://localhost:3306/expense_tracker_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```

---

### Step 3: Run the Application

You have two convenient ways to run the project:

#### Option A: 1-Click Launch (Recommended for Quick Demos)
Simply double-click:
```bat
run.bat
```
*(Or in PowerShell / Command Prompt, run `.\run.bat`)*

What this does:
1. Compiles all Java source files with `javac` into `target\classes`.
2. Starts the built-in HTTP server on port 8080.
3. Automatically opens your default web browser at `http://localhost:8080`.

#### Option B: Deploy to Apache Tomcat 10+
1. If using Maven:
   ```cmd
   mvn clean package
   ```
   This generates `target\expense-tracker.war`.
2. Copy `expense-tracker.war` into your Tomcat `webapps` directory:
   ```
   C:\Tomcat\webapps\expense-tracker.war
   ```
3. Start Tomcat via `catalina.bat start` or Tomcat Monitor.
4. Access the web app in your browser at:
   ```
   http://localhost:8080/expense-tracker/
   ```

#### Option C: In Eclipse / IntelliJ / VS Code
- **Eclipse:** File -> Import -> Existing Maven Projects -> Select project folder -> Run on Server (Apache Tomcat 10+).
- **IntelliJ:** Open folder -> Import as Maven Project -> Add Tomcat Run Configuration -> Run.
- **VS Code:** Open folder -> Install *Extension Pack for Java* -> Run `StandaloneServer.java`.

---

## Default Demo Credentials

For quick evaluation during college project reviews:
- **Email:** `demo@expensetracker.com`
- **Password:** `Password@123`
- *(Or click the **Auto Fill** button on the login screen to fill in credentials with 1 click).*

---

## Academic Reference

Please consult the companion document [`OOP_GUIDE.md`](OOP_GUIDE.md) for in-depth code line references, class diagrams, design pattern discussions, and **25 anticipated viva examination questions with complete textbook answers**.
