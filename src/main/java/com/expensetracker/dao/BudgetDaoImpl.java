package com.expensetracker.dao;

import com.expensetracker.exception.DatabaseException;
import com.expensetracker.exception.ValidationException;
import com.expensetracker.model.Budget;
import com.expensetracker.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * JDBC Implementation of BudgetDao with Resilient In-Memory Fallback.
 * Supports UPSERT for category + month budgets.
 */
public class BudgetDaoImpl implements BudgetDao {

    private static final List<Budget> MEM_BUDGETS = new CopyOnWriteArrayList<>();
    private static final AtomicInteger MEM_ID_GEN = new AtomicInteger(100);

    static {
        initMemorySeed();
    }

    public static void resetMemorySeed() {
        MEM_BUDGETS.clear();
        initMemorySeed();
    }

    public static void clearMemoryStore() {
        MEM_BUDGETS.clear();
    }

    private static void initMemorySeed() {
        try {
            String ym = com.expensetracker.util.DateUtil.getCurrentMonthYear();
            MEM_BUDGETS.add(new Budget(1, 1, "Food", 3000.00, ym, new Timestamp(System.currentTimeMillis())));
            MEM_BUDGETS.add(new Budget(2, 1, "Transport", 1500.00, ym, new Timestamp(System.currentTimeMillis())));
            MEM_BUDGETS.add(new Budget(3, 1, "Shopping", 2500.00, ym, new Timestamp(System.currentTimeMillis())));
            MEM_BUDGETS.add(new Budget(4, 1, "Bills", 4000.00, ym, new Timestamp(System.currentTimeMillis())));
            MEM_BUDGETS.add(new Budget(5, 1, "Education", 2000.00, ym, new Timestamp(System.currentTimeMillis())));
        } catch (Exception ignored) {}
    }

    private static final String SQL_UPSERT = 
        "INSERT INTO budgets (category, amount_limit, month_year) " +
        "VALUES (?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE amount_limit = VALUES(amount_limit)";

    private static final String SQL_DELETE = 
        "DELETE FROM budgets WHERE id = ?";

    private static final String SQL_FIND_BY_ID = 
        "SELECT id, category, amount_limit, month_year, created_at FROM budgets WHERE id = ?";

    private static final String SQL_FIND_BY_CAT_MONTH = 
        "SELECT id, category, amount_limit, month_year, created_at " +
        "FROM budgets WHERE category = ? AND month_year = ?";

    private static final String SQL_FIND_BY_MONTH = 
        "SELECT id, category, amount_limit, month_year, created_at " +
        "FROM budgets WHERE month_year = ? ORDER BY category ASC";

    private static final String SQL_FIND_ALL = 
        "SELECT id, category, amount_limit, month_year, created_at " +
        "FROM budgets ORDER BY month_year DESC, category ASC";

    @Override
    public int saveOrUpdate(Budget budget) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            for (Budget b : MEM_BUDGETS) {
                if (b.getCategory().equalsIgnoreCase(budget.getCategory()) && 
                    b.getMonthYear().equals(budget.getMonthYear())) {
                    try {
                        b.setAmountLimit(budget.getAmountLimit());
                    } catch (ValidationException ignored) {}
                    budget.setId(b.getId());
                    return b.getId();
                }
            }
            int newId = MEM_ID_GEN.incrementAndGet();
            budget.setId(newId);
            budget.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            MEM_BUDGETS.add(budget);
            return newId;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_UPSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, budget.getCategory());
            ps.setDouble(2, budget.getAmountLimit());
            ps.setString(3, budget.getMonthYear());

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                budget.setId(id);
                return id;
            } else {
                Optional<Budget> existing = findByCategoryAndMonth(budget.getCategory(), budget.getMonthYear());
                if (existing.isPresent()) {
                    int id = existing.get().getId();
                    budget.setId(id);
                    return id;
                }
                return budget.getId();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error saving budget: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public boolean delete(int id) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_BUDGETS.removeIf(b -> b.getId() == id);
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_DELETE);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error deleting budget: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean delete(int id, int userId) throws DatabaseException {
        return delete(id);
    }

    @Override
    public Optional<Budget> findById(int id) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_BUDGETS.stream().filter(b -> b.getId() == id).findFirst();
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
                return Optional.of(mapRowToBudget(rs));
            }
            return Optional.empty();
        } catch (SQLException | ValidationException e) {
            throw new DatabaseException("Error finding budget by id: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public Optional<Budget> findById(int id, int userId) throws DatabaseException {
        return findById(id);
    }

    @Override
    public Optional<Budget> findByCategoryAndMonth(String category, String monthYear) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_BUDGETS.stream()
                .filter(b -> b.getCategory().equalsIgnoreCase(category) && b.getMonthYear().equals(monthYear))
                .findFirst();
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_FIND_BY_CAT_MONTH);
            ps.setString(1, category);
            ps.setString(2, monthYear);
            rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToBudget(rs));
            }
            return Optional.empty();
        } catch (SQLException | ValidationException e) {
            throw new DatabaseException("Error finding budget for category & month: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public Optional<Budget> findByCategoryAndMonth(int userId, String category, String monthYear) throws DatabaseException {
        return findByCategoryAndMonth(category, monthYear);
    }

    @Override
    public List<Budget> findByMonth(String monthYear) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_BUDGETS.stream()
                .filter(b -> b.getMonthYear().equals(monthYear))
                .sorted((a, b) -> a.getCategory().compareToIgnoreCase(b.getCategory()))
                .collect(Collectors.toList());
        }

        List<Budget> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_FIND_BY_MONTH);
            ps.setString(1, monthYear);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRowToBudget(rs));
            }
            return list;
        } catch (SQLException | ValidationException e) {
            throw new DatabaseException("Error finding budgets by month: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public List<Budget> findByUserAndMonth(int userId, String monthYear) throws DatabaseException {
        return findByMonth(monthYear);
    }

    @Override
    public List<Budget> findAll() throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return new ArrayList<>(MEM_BUDGETS);
        }

        List<Budget> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_FIND_ALL);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRowToBudget(rs));
            }
            return list;
        } catch (SQLException | ValidationException e) {
            throw new DatabaseException("Error retrieving all budgets: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public List<Budget> findAllByUser(int userId) throws DatabaseException {
        return findAll();
    }

    private Budget mapRowToBudget(ResultSet rs) throws SQLException, ValidationException {
        int id = rs.getInt("id");
        String category = rs.getString("category");
        double amount = rs.getDouble("amount_limit");
        String monthYear = rs.getString("month_year");
        Timestamp createdAt = rs.getTimestamp("created_at");

        return new Budget(id, 1, category, amount, monthYear, createdAt);
    }
}
