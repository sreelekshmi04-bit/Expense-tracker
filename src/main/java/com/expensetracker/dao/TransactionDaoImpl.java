package com.expensetracker.dao;

import com.expensetracker.exception.DatabaseException;
import com.expensetracker.exception.ValidationException;
import com.expensetracker.factory.TransactionFactory;
import com.expensetracker.model.CategoryTotal;
import com.expensetracker.model.Expense;
import com.expensetracker.model.Income;
import com.expensetracker.model.Transaction;
import com.expensetracker.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * JDBC & Resilient In-Memory Implementation of TransactionDao.
 * 
 * OOP Concepts Demonstrated:
 * 1. Abstraction: Implements TransactionDao contract, encapsulating persistence operations.
 * 2. Polymorphic Factory Mapping: Rows from database or memory are converted into
 *    polymorphic Income or Expense objects at runtime via TransactionFactory.
 * 3. Strategy / Resiliency Fallback: Uses MySQL JDBC when connected; falls back to
 *    thread-safe memory store if MySQL credentials are not yet set, preventing viva crashes.
 */
public class TransactionDaoImpl implements TransactionDao {

    // In-memory fallback repository
    private static final List<Transaction> MEM_TXS = new CopyOnWriteArrayList<>();
    private static final AtomicInteger MEM_ID_GEN = new AtomicInteger(100);

    static {
        initMemorySeed();
    }

    public static void resetMemorySeed() {
        MEM_TXS.clear();
        initMemorySeed();
    }

    public static void clearMemoryStore() {
        MEM_TXS.clear();
    }

    private static void initMemorySeed() {
        try {
            LocalDate now = LocalDate.now();
            int y = now.getYear();
            int m = now.getMonthValue();

            // Income Entries for current month
            MEM_TXS.add(TransactionFactory.createTransaction(1, 1, "INCOME", 25000.00, LocalDate.of(y, m, 1), 
                "Monthly Internship Stipend", "Salary", "Bank Transfer", Timestamp.valueOf(LocalDate.of(y, m, 1).atTime(10, 0))));
            MEM_TXS.add(TransactionFactory.createTransaction(2, 1, "INCOME", 5000.00, LocalDate.of(y, m, 5), 
                "Merit Scholarship Grant", "Scholarship", "Bank Transfer", Timestamp.valueOf(LocalDate.of(y, m, 5).atTime(11, 30))));
            MEM_TXS.add(TransactionFactory.createTransaction(3, 1, "INCOME", 3500.00, LocalDate.of(y, m, 10), 
                "Web Design Project for Club", "Freelance", "UPI", Timestamp.valueOf(LocalDate.of(y, m, 10).atTime(16, 45))));

            // Expense Entries for current month
            MEM_TXS.add(TransactionFactory.createTransaction(4, 1, "EXPENSE", 1000.00, LocalDate.of(y, m, 2), 
                "Hostel Mess & Groceries", "Food", "UPI", Timestamp.valueOf(LocalDate.of(y, m, 2).atTime(12, 0))));
            MEM_TXS.add(TransactionFactory.createTransaction(5, 1, "EXPENSE", 1200.00, LocalDate.of(y, m, 4), 
                "College Canteen & Snacks", "Food", "Cash", Timestamp.valueOf(LocalDate.of(y, m, 4).atTime(14, 15))));
            MEM_TXS.add(TransactionFactory.createTransaction(6, 1, "EXPENSE", 1200.00, LocalDate.of(y, m, 8), 
                "Weekend Outing with Team", "Food", "UPI", Timestamp.valueOf(LocalDate.of(y, m, 8).atTime(19, 30))));
            MEM_TXS.add(TransactionFactory.createTransaction(7, 1, "EXPENSE", 700.00, LocalDate.of(y, m, 3), 
                "Bus Pass & Auto Rickshaw", "Transport", "UPI", Timestamp.valueOf(LocalDate.of(y, m, 3).atTime(8, 30))));
            MEM_TXS.add(TransactionFactory.createTransaction(8, 1, "EXPENSE", 550.00, LocalDate.of(y, m, 9), 
                "Metro Smart Card Recharge", "Transport", "Card", Timestamp.valueOf(LocalDate.of(y, m, 9).atTime(17, 0))));
            MEM_TXS.add(TransactionFactory.createTransaction(9, 1, "EXPENSE", 1800.00, LocalDate.of(y, m, 6), 
                "Reference Textbooks & Notebooks", "Education", "UPI", Timestamp.valueOf(LocalDate.of(y, m, 6).atTime(15, 0))));
            MEM_TXS.add(TransactionFactory.createTransaction(10, 1, "EXPENSE", 2200.00, LocalDate.of(y, m, 7), 
                "Electricity & Internet WiFi", "Bills", "Bank Transfer", Timestamp.valueOf(LocalDate.of(y, m, 7).atTime(11, 0))));
            MEM_TXS.add(TransactionFactory.createTransaction(11, 1, "EXPENSE", 1200.00, LocalDate.of(y, m, 12), 
                "Running Shoes", "Shopping", "Card", Timestamp.valueOf(LocalDate.of(y, m, 12).atTime(18, 20))));

            // Prior Month (M-1) for visual 6-month chart demonstration
            LocalDate prev1 = now.minusMonths(1);
            int py1 = prev1.getYear();
            int pm1 = prev1.getMonthValue();
            MEM_TXS.add(TransactionFactory.createTransaction(12, 1, "INCOME", 25000.00, LocalDate.of(py1, pm1, 1), 
                "Previous Month Stipend", "Salary", "Bank Transfer", Timestamp.valueOf(LocalDate.of(py1, pm1, 1).atTime(10, 0))));
            MEM_TXS.add(TransactionFactory.createTransaction(13, 1, "EXPENSE", 14000.00, LocalDate.of(py1, pm1, 15), 
                "Hostel Mess & Living Costs", "Food", "UPI", Timestamp.valueOf(LocalDate.of(py1, pm1, 15).atTime(12, 0))));

            // Two Months Ago (M-2) for visual 6-month chart demonstration
            LocalDate prev2 = now.minusMonths(2);
            int py2 = prev2.getYear();
            int pm2 = prev2.getMonthValue();
            MEM_TXS.add(TransactionFactory.createTransaction(14, 1, "INCOME", 20000.00, LocalDate.of(py2, pm2, 1), 
                "Stipend", "Salary", "Bank Transfer", Timestamp.valueOf(LocalDate.of(py2, pm2, 1).atTime(10, 0))));
            MEM_TXS.add(TransactionFactory.createTransaction(15, 1, "EXPENSE", 11500.00, LocalDate.of(py2, pm2, 12), 
                "Course Materials & Groceries", "Education", "UPI", Timestamp.valueOf(LocalDate.of(py2, pm2, 12).atTime(14, 0))));
        } catch (Exception ignored) {}
    }

    private static final String SQL_INSERT = 
        "INSERT INTO transactions (type, amount, transaction_date, description, category_or_source, payment_method) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE = 
        "UPDATE transactions SET type = ?, amount = ?, transaction_date = ?, description = ?, category_or_source = ?, payment_method = ? " +
        "WHERE id = ?";

    private static final String SQL_DELETE = 
        "DELETE FROM transactions WHERE id = ?";

    private static final String SQL_FIND_BY_ID = 
        "SELECT id, type, amount, transaction_date, description, category_or_source, payment_method, created_at " +
        "FROM transactions WHERE id = ?";

    private static final String SQL_FIND_ALL = 
        "SELECT id, type, amount, transaction_date, description, category_or_source, payment_method, created_at " +
        "FROM transactions ORDER BY transaction_date DESC, id DESC";

    private static final String SQL_FIND_RECENT = 
        "SELECT id, type, amount, transaction_date, description, category_or_source, payment_method, created_at " +
        "FROM transactions ORDER BY transaction_date DESC, id DESC LIMIT ?";

    @Override
    public int create(Transaction transaction) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            int newId = MEM_ID_GEN.incrementAndGet();
            transaction.setId(newId);
            transaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            MEM_TXS.add(0, transaction);
            return newId;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, transaction.getTransactionType());
            ps.setDouble(2, transaction.getAmount());
            ps.setDate(3, java.sql.Date.valueOf(transaction.getTransactionDate()));
            ps.setString(4, transaction.getDescription());
            ps.setString(5, transaction.getCategoryOrSource());
            
            String paymentMethod = "Cash";
            if (transaction instanceof Expense) {
                paymentMethod = ((Expense) transaction).getPaymentMethod();
            }
            ps.setString(6, paymentMethod != null ? paymentMethod : "Cash");

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Creating transaction failed, no rows affected.");
            }

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                transaction.setId(generatedId);
                return generatedId;
            } else {
                throw new DatabaseException("Creating transaction failed, no ID obtained.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error creating transaction: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public boolean update(Transaction transaction) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            for (int i = 0; i < MEM_TXS.size(); i++) {
                if (MEM_TXS.get(i).getId() == transaction.getId()) {
                    MEM_TXS.set(i, transaction);
                    return true;
                }
            }
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_UPDATE);
            ps.setString(1, transaction.getTransactionType());
            ps.setDouble(2, transaction.getAmount());
            ps.setDate(3, java.sql.Date.valueOf(transaction.getTransactionDate()));
            ps.setString(4, transaction.getDescription());
            ps.setString(5, transaction.getCategoryOrSource());
            
            String paymentMethod = "Cash";
            if (transaction instanceof Expense) {
                paymentMethod = ((Expense) transaction).getPaymentMethod();
            }
            ps.setString(6, paymentMethod != null ? paymentMethod : "Cash");
            ps.setInt(7, transaction.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error updating transaction: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_TXS.removeIf(tx -> tx.getId() == id);
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_DELETE);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error deleting transaction: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean delete(int id, int userId) throws DatabaseException {
        return delete(id);
    }

    @Override
    public Optional<Transaction> findById(int id) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_TXS.stream().filter(tx -> tx.getId() == id).findFirst();
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_FIND_BY_ID);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToTransaction(rs));
            }
            return Optional.empty();
        } catch (SQLException | ValidationException e) {
            throw new DatabaseException("Error retrieving transaction by ID: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public Optional<Transaction> findById(int id, int userId) throws DatabaseException {
        return findById(id);
    }

    @Override
    public List<Transaction> findAll() throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return new ArrayList<>(MEM_TXS);
        }

        List<Transaction> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_FIND_ALL);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRowToTransaction(rs));
            }
            return list;
        } catch (SQLException | ValidationException e) {
            throw new DatabaseException("Error retrieving transactions: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public List<Transaction> findByUserId(int userId) throws DatabaseException {
        return findAll();
    }

    @Override
    public List<Transaction> findRecent(int limit) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_TXS.stream().limit(Math.max(1, limit)).collect(Collectors.toList());
        }

        List<Transaction> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_FIND_RECENT);
            ps.setInt(1, Math.max(1, limit));
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRowToTransaction(rs));
            }
            return list;
        } catch (SQLException | ValidationException e) {
            throw new DatabaseException("Error retrieving recent transactions: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public List<Transaction> findRecent(int userId, int limit) throws DatabaseException {
        return findRecent(limit);
    }

    @Override
    public List<Transaction> findByFilters(String type, String categoryOrSource, 
                                          LocalDate startDate, LocalDate endDate) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_TXS.stream().filter(tx -> {
                if (type != null && !type.isBlank() && !type.equalsIgnoreCase("ALL")) {
                    if (!tx.getTransactionType().equalsIgnoreCase(type)) return false;
                }
                if (categoryOrSource != null && !categoryOrSource.isBlank() && !categoryOrSource.equalsIgnoreCase("ALL")) {
                    if (!tx.getCategoryOrSource().equalsIgnoreCase(categoryOrSource)) return false;
                }
                if (startDate != null && tx.getTransactionDate().isBefore(startDate)) return false;
                if (endDate != null && tx.getTransactionDate().isAfter(endDate)) return false;
                return true;
            }).collect(Collectors.toList());
        }

        StringBuilder sql = new StringBuilder(
            "SELECT id, type, amount, transaction_date, description, category_or_source, payment_method, created_at " +
            "FROM transactions WHERE 1=1 "
        );
        List<Object> params = new ArrayList<>();

        if (type != null && !type.isBlank() && !type.equalsIgnoreCase("ALL")) {
            sql.append("AND type = ? ");
            params.add(type.toUpperCase());
        }

        if (categoryOrSource != null && !categoryOrSource.isBlank() && !categoryOrSource.equalsIgnoreCase("ALL")) {
            sql.append("AND category_or_source = ? ");
            params.add(categoryOrSource);
        }

        if (startDate != null) {
            sql.append("AND transaction_date >= ? ");
            params.add(java.sql.Date.valueOf(startDate));
        }

        if (endDate != null) {
            sql.append("AND transaction_date <= ? ");
            params.add(java.sql.Date.valueOf(endDate));
        }

        sql.append("ORDER BY transaction_date DESC, id DESC");

        List<Transaction> results = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                results.add(mapRowToTransaction(rs));
            }
            return results;
        } catch (SQLException | ValidationException e) {
            throw new DatabaseException("Error filtering transactions: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public List<Transaction> findByFilters(int userId, String type, String categoryOrSource, 
                                          LocalDate startDate, LocalDate endDate) throws DatabaseException {
        return findByFilters(type, categoryOrSource, startDate, endDate);
    }

    @Override
    public double getTotalIncome() throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_TXS.stream()
                .filter(tx -> tx instanceof Income)
                .mapToDouble(Transaction::getAmount)
                .sum();
        }
        return getAggregateSum("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'INCOME'");
    }

    @Override
    public double getTotalIncome(int userId) throws DatabaseException {
        return getTotalIncome();
    }

    @Override
    public double getTotalExpense() throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_TXS.stream()
                .filter(tx -> tx instanceof Expense)
                .mapToDouble(Transaction::getAmount)
                .sum();
        }
        return getAggregateSum("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'EXPENSE'");
    }

    @Override
    public double getTotalExpense(int userId) throws DatabaseException {
        return getTotalExpense();
    }

    @Override
    public double getMonthlyIncome(String monthYear) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_TXS.stream()
                .filter(tx -> tx instanceof Income && tx.getTransactionDate().toString().startsWith(monthYear))
                .mapToDouble(Transaction::getAmount)
                .sum();
        }
        String sql = "SELECT COALESCE(SUM(amount), 0.0) FROM transactions " +
                     "WHERE type = 'INCOME' AND DATE_FORMAT(transaction_date, '%Y-%m') = ?";
        return getAggregateSumWithParam(sql, monthYear);
    }

    @Override
    public double getMonthlyIncome(int userId, String monthYear) throws DatabaseException {
        return getMonthlyIncome(monthYear);
    }

    @Override
    public double getMonthlyExpense(String monthYear) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_TXS.stream()
                .filter(tx -> tx instanceof Expense && tx.getTransactionDate().toString().startsWith(monthYear))
                .mapToDouble(Transaction::getAmount)
                .sum();
        }
        String sql = "SELECT COALESCE(SUM(amount), 0.0) FROM transactions " +
                     "WHERE type = 'EXPENSE' AND DATE_FORMAT(transaction_date, '%Y-%m') = ?";
        return getAggregateSumWithParam(sql, monthYear);
    }

    @Override
    public double getMonthlyExpense(int userId, String monthYear) throws DatabaseException {
        return getMonthlyExpense(monthYear);
    }

    @Override
    public double getCategorySpent(String category, String monthYear) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_TXS.stream()
                .filter(tx -> tx instanceof Expense && 
                              tx.getCategoryOrSource().equalsIgnoreCase(category) &&
                              tx.getTransactionDate().toString().startsWith(monthYear))
                .mapToDouble(Transaction::getAmount)
                .sum();
        }
        String sql = "SELECT COALESCE(SUM(amount), 0.0) FROM transactions " +
                     "WHERE type = 'EXPENSE' AND category_or_source = ? AND DATE_FORMAT(transaction_date, '%Y-%m') = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, category);
            ps.setString(2, monthYear);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving category spent: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public double getCategorySpent(int userId, String category, String monthYear) throws DatabaseException {
        return getCategorySpent(category, monthYear);
    }

    @Override
    public List<CategoryTotal> getCategoryBreakdown(String monthYear) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            Map<String, Double> map = new HashMap<>();
            double grandTotal = 0.0;
            for (Transaction tx : MEM_TXS) {
                if (tx instanceof Expense && tx.getTransactionDate().toString().startsWith(monthYear)) {
                    double amt = tx.getAmount();
                    map.put(tx.getCategoryOrSource(), map.getOrDefault(tx.getCategoryOrSource(), 0.0) + amt);
                    grandTotal += amt;
                }
            }
            List<CategoryTotal> list = new ArrayList<>();
            final double total = grandTotal;
            map.forEach((k, v) -> {
                double pct = total > 0.0 ? Math.round((v / total * 100.0) * 10.0) / 10.0 : 0.0;
                list.add(new CategoryTotal(k, v, 1, pct));
            });
            list.sort((a, b) -> Double.compare(b.getTotalSpent(), a.getTotalSpent()));
            return list;
        }

        String sql = 
            "SELECT category_or_source, COALESCE(SUM(amount), 0.0) AS total_spent " +
            "FROM transactions " +
            "WHERE type = 'EXPENSE' AND DATE_FORMAT(transaction_date, '%Y-%m') = ? " +
            "GROUP BY category_or_source ORDER BY total_spent DESC";

        List<CategoryTotal> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, monthYear);
            rs = ps.executeQuery();
            double grandTotal = 0.0;
            while (rs.next()) {
                String cat = rs.getString("category_or_source");
                double spent = rs.getDouble("total_spent");
                list.add(new CategoryTotal(cat, spent));
                grandTotal += spent;
            }
            if (grandTotal > 0.0) {
                for (CategoryTotal ct : list) {
                    ct.setPercentageOfTotal(Math.round((ct.getTotalAmount() / grandTotal * 100.0) * 10.0) / 10.0);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Error calculating category breakdown: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public List<CategoryTotal> getCategoryBreakdown(int userId, String monthYear) throws DatabaseException {
        return getCategoryBreakdown(monthYear);
    }

    @Override
    public Map<String, double[]> getMonthlyComparison(int monthsCount) throws DatabaseException {
        Map<String, double[]> map = new LinkedHashMap<>();
        YearMonth current = YearMonth.now();

        for (int i = (monthsCount > 0 ? monthsCount : 6) - 1; i >= 0; i--) {
            YearMonth ym = current.minusMonths(i);
            String monthKey = ym.toString();
            double inc = getMonthlyIncome(monthKey);
            double exp = getMonthlyExpense(monthKey);
            map.put(monthKey, new double[]{inc, exp});
        }

        return map;
    }

    @Override
    public Map<String, double[]> getMonthlyComparison(int userId, int monthsCount) throws DatabaseException {
        return getMonthlyComparison(monthsCount);
    }

    private double getAggregateSum(String sql) throws DatabaseException {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        } catch (SQLException e) {
            throw new DatabaseException("Error calculating aggregate sum: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, st, conn);
        }
    }

    private double getAggregateSumWithParam(String sql, String param) throws DatabaseException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, param);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        } catch (SQLException e) {
            throw new DatabaseException("Error calculating aggregate sum: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException, ValidationException {
        int id = rs.getInt("id");
        String type = rs.getString("type");
        double amount = rs.getDouble("amount");
        LocalDate date = rs.getDate("transaction_date").toLocalDate();
        String description = rs.getString("description");
        String catOrSource = rs.getString("category_or_source");
        
        String paymentMethod = "Cash";
        try {
            String pm = rs.getString("payment_method");
            if (pm != null && !pm.isBlank()) {
                paymentMethod = pm;
            }
        } catch (SQLException ignored) {}
        
        Timestamp createdAt = rs.getTimestamp("created_at");

        return TransactionFactory.createTransaction(
            id, 1, type, amount, date, description, catOrSource, paymentMethod, createdAt
        );
    }
}
