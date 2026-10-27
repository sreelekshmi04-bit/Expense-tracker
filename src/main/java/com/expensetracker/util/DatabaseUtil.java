package com.expensetracker.util;

import com.expensetracker.exception.DatabaseException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Singleton / Utility class managing JDBC Database Connections.
 * 
 * OOP Concepts Demonstrated:
 * 1. Encapsulation: Database credentials and driver loading are encapsulated here.
 * 2. Static Factory Method: getConnection() provides centralized connection creation.
 * 3. Graceful Fallback: Detects MySQL availability and provides seamless fallback
 *    so the application remains 100% operational in viva demos even before MySQL credentials are set.
 * 4. Automatic Schema Bootstrapping: Auto-creates database and tables if not present.
 */
public class DatabaseUtil {

    private static String jdbcUrl;
    private static String dbUser;
    private static String dbPassword;
    private static String dbDriver;
    private static boolean initialized = false;
    private static Boolean mysqlAvailable = null;

    static {
        loadConfiguration();
    }

    public static synchronized void loadConfiguration() {
        Properties props = new Properties();
        boolean loaded = false;

        // 1. Try loading from file in current working directory
        File propFile = new File("db.properties");
        if (propFile.exists()) {
            try (InputStream in = new FileInputStream(propFile)) {
                props.load(in);
                loaded = true;
            } catch (Exception ignored) {}
        }

        // 2. Try loading from classpath resources
        if (!loaded) {
            try (InputStream in = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (in != null) {
                    props.load(in);
                    loaded = true;
                }
            } catch (Exception ignored) {}
        }

        jdbcUrl = props.getProperty("db.url", 
            "jdbc:mysql://localhost:3306/expense_tracker_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8");
        dbUser = props.getProperty("db.user", "root");
        dbPassword = props.getProperty("db.password", "root");
        dbDriver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

        try {
            Class.forName(dbDriver);
            initialized = true;
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseUtil] Warning: MySQL JDBC driver class not found: " + dbDriver);
        }

        // Test connectivity once
        testAndBootstrap();
    }

    private static synchronized void testAndBootstrap() {
        try (Connection testConn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
            mysqlAvailable = true;
            System.out.println("[DatabaseUtil] SUCCESS: Connected to MySQL database at: " + jdbcUrl);
            bootstrapTables(testConn);
        } catch (SQLException e) {
            // Check if failure is due to database not existing yet (error 1049)
            if (e.getErrorCode() == 1049 || e.getMessage().contains("Unknown database")) {
                try {
                    String baseUrl = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
                    try (Connection rootConn = DriverManager.getConnection(baseUrl, dbUser, dbPassword);
                         Statement st = rootConn.createStatement()) {
                        st.executeUpdate("CREATE DATABASE IF NOT EXISTS expense_tracker_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
                        System.out.println("[DatabaseUtil] Created database 'expense_tracker_db' automatically.");
                    }
                    try (Connection testConn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)) {
                        mysqlAvailable = true;
                        bootstrapTables(testConn);
                        return;
                    }
                } catch (Exception ignored) {}
            }

            mysqlAvailable = false;
            System.out.println("------------------------------------------------------------------");
            System.out.println("[DatabaseUtil] MySQL connection notice: " + e.getMessage());
            System.out.println("[DatabaseUtil] Mode: High-Fidelity In-Memory Active.");
            System.out.println("[DatabaseUtil] To persist to MySQL, update password in 'db.properties'.");
            System.out.println("------------------------------------------------------------------");
        }
    }

    private static void bootstrapTables(Connection conn) {
        String createTransactions = 
            "CREATE TABLE IF NOT EXISTS transactions (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  type ENUM('INCOME', 'EXPENSE') NOT NULL," +
            "  amount DECIMAL(12, 2) NOT NULL," +
            "  transaction_date DATE NOT NULL," +
            "  description VARCHAR(255) NOT NULL," +
            "  category_or_source VARCHAR(60) NOT NULL," +
            "  payment_method VARCHAR(30) DEFAULT 'Cash'," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "  INDEX idx_tx_date (transaction_date)," +
            "  INDEX idx_tx_type (type)," +
            "  INDEX idx_tx_cat (category_or_source)" +
            ") ENGINE=InnoDB;";

        String createBudgets = 
            "CREATE TABLE IF NOT EXISTS budgets (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  category VARCHAR(60) NOT NULL," +
            "  amount_limit DECIMAL(12, 2) NOT NULL," +
            "  month_year VARCHAR(7) NOT NULL," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
            "  CONSTRAINT uq_category_month UNIQUE (category, month_year)," +
            "  INDEX idx_budget_lookup (month_year, category)" +
            ") ENGINE=InnoDB;";

        String createAlerts = 
            "CREATE TABLE IF NOT EXISTS alerts (" +
            "  id INT AUTO_INCREMENT PRIMARY KEY," +
            "  category VARCHAR(60) NOT NULL," +
            "  month_year VARCHAR(7) NOT NULL," +
            "  alert_type ENUM('WARNING', 'OVERSPENT') NOT NULL," +
            "  threshold_percent DECIMAL(5, 2) NOT NULL," +
            "  spent_amount DECIMAL(12, 2) NOT NULL," +
            "  budget_limit DECIMAL(12, 2) NOT NULL," +
            "  difference_amount DECIMAL(12, 2) NOT NULL," +
            "  message TEXT NOT NULL," +
            "  is_read BOOLEAN DEFAULT FALSE," +
            "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "  INDEX idx_alerts_status (is_read, created_at)" +
            ") ENGINE=InnoDB;";

        try (Statement st = conn.createStatement()) {
            st.executeUpdate(createTransactions);
            st.executeUpdate(createBudgets);
            st.executeUpdate(createAlerts);
        } catch (SQLException e) {
            System.err.println("[DatabaseUtil] Warning checking/bootstrapping tables: " + e.getMessage());
        }
    }

    public static boolean isMySQLAvailable() {
        if (mysqlAvailable == null) {
            testAndBootstrap();
        }
        return Boolean.TRUE.equals(mysqlAvailable);
    }

    public static Connection getConnection() throws DatabaseException {
        if (!initialized || mysqlAvailable == null) {
            loadConfiguration();
        }
        try {
            return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            mysqlAvailable = false;
            throw new DatabaseException("Failed to connect to MySQL database at: " + jdbcUrl + 
                ". Error: " + e.getMessage(), e);
        }
    }

    public static void closeQuietly(AutoCloseable... closeables) {
        if (closeables == null) return;
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {}
            }
        }
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }

    public static String getDbUser() {
        return dbUser;
    }
}
