package com.expensetracker.dao;

import com.expensetracker.exception.DatabaseException;
import com.expensetracker.model.Alert;
import com.expensetracker.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * JDBC & In-Memory Fallback Implementation of AlertDao.
 */
public class AlertDaoImpl implements AlertDao {

    private static final List<Alert> MEM_ALERTS = new CopyOnWriteArrayList<>();
    private static final AtomicInteger MEM_ID_GEN = new AtomicInteger(100);

    static {
        initMemorySeed();
    }

    public static void resetMemorySeed() {
        MEM_ALERTS.clear();
        initMemorySeed();
    }

    public static void clearMemoryStore() {
        MEM_ALERTS.clear();
    }

    private static void initMemorySeed() {
        String ym = com.expensetracker.util.DateUtil.getCurrentMonthYear();
        MEM_ALERTS.add(new Alert(
            1, 1, "Food", ym, "OVERSPENT",
            113.33, 3400.00, 3000.00, 400.00,
            "Food Budget Exceeded! You have spent ₹3,400.00 out of your ₹3,000.00 budget. You are ₹400.00 over budget. Consider reducing your remaining Food expenses this month.",
            false, new Timestamp(System.currentTimeMillis())
        ));
        MEM_ALERTS.add(new Alert(
            2, 1, "Transport", ym, "WARNING",
            83.33, 1250.00, 1500.00, -250.00,
            "Transport Budget Warning! You have spent ₹1,250.00 out of your ₹1,500.00 budget (83.3% used). You have ₹250.00 remaining for this month.",
            false, new Timestamp(System.currentTimeMillis())
        ));
    }

    private static final String SQL_INSERT = 
        "INSERT INTO alerts (category, month_year, alert_type, threshold_percent, spent_amount, budget_limit, difference_amount, message, is_read) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE = 
        "UPDATE alerts SET alert_type = ?, threshold_percent = ?, spent_amount = ?, budget_limit = ?, difference_amount = ?, message = ?, is_read = ? " +
        "WHERE id = ?";

    private static final String SQL_MARK_READ = 
        "UPDATE alerts SET is_read = TRUE WHERE id = ?";

    private static final String SQL_MARK_ALL_READ = 
        "UPDATE alerts SET is_read = TRUE";

    private static final String SQL_FIND_ALL = 
        "SELECT id, category, month_year, alert_type, threshold_percent, spent_amount, budget_limit, difference_amount, message, is_read, created_at " +
        "FROM alerts ORDER BY id DESC LIMIT 50";

    private static final String SQL_FIND_UNREAD = 
        "SELECT id, category, month_year, alert_type, threshold_percent, spent_amount, budget_limit, difference_amount, message, is_read, created_at " +
        "FROM alerts WHERE is_read = FALSE ORDER BY id DESC";

    private static final String SQL_FIND_LATEST_BY_CAT_MONTH = 
        "SELECT id, category, month_year, alert_type, threshold_percent, spent_amount, budget_limit, difference_amount, message, is_read, created_at " +
        "FROM alerts WHERE category = ? AND month_year = ? ORDER BY id DESC LIMIT 1";

    private static final String SQL_DELETE = 
        "DELETE FROM alerts WHERE id = ?";

    private static final String SQL_DELETE_BY_CAT_MONTH = 
        "DELETE FROM alerts WHERE category = ? AND month_year = ?";

    @Override
    public int create(Alert alert) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            int newId = MEM_ID_GEN.incrementAndGet();
            alert.setId(newId);
            alert.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            MEM_ALERTS.add(0, alert);
            return newId;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, alert.getCategory());
            ps.setString(2, alert.getMonthYear());
            ps.setString(3, alert.getAlertType());
            ps.setDouble(4, alert.getThresholdPercent());
            ps.setDouble(5, alert.getSpentAmount());
            ps.setDouble(6, alert.getBudgetLimit());
            ps.setDouble(7, alert.getDifferenceAmount());
            ps.setString(8, alert.getMessage());
            ps.setBoolean(9, alert.isRead());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Creating alert failed, no rows affected.");
            }

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                alert.setId(id);
                return id;
            } else {
                throw new DatabaseException("Creating alert failed, no ID obtained.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error creating alert: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public boolean update(Alert alert) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            for (int i = 0; i < MEM_ALERTS.size(); i++) {
                if (MEM_ALERTS.get(i).getId() == alert.getId()) {
                    MEM_ALERTS.set(i, alert);
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
            ps.setString(1, alert.getAlertType());
            ps.setDouble(2, alert.getThresholdPercent());
            ps.setDouble(3, alert.getSpentAmount());
            ps.setDouble(4, alert.getBudgetLimit());
            ps.setDouble(5, alert.getDifferenceAmount());
            ps.setString(6, alert.getMessage());
            ps.setBoolean(7, alert.isRead());
            ps.setInt(8, alert.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error updating alert: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean markAsRead(int alertId) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            for (Alert a : MEM_ALERTS) {
                if (a.getId() == alertId) {
                    a.setRead(true);
                    return true;
                }
            }
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_MARK_READ);
            ps.setInt(1, alertId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error marking alert as read: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean markAsRead(int alertId, int userId) throws DatabaseException {
        return markAsRead(alertId);
    }

    @Override
    public boolean markAllAsRead() throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            for (Alert a : MEM_ALERTS) {
                a.setRead(true);
            }
            return true;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_MARK_ALL_READ);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DatabaseException("Database error marking all alerts read: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean markAllAsRead(int userId) throws DatabaseException {
        return markAllAsRead();
    }

    @Override
    public List<Alert> findAll(boolean unreadOnly) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            if (unreadOnly) {
                return MEM_ALERTS.stream().filter(a -> !a.isRead()).collect(Collectors.toList());
            }
            return new ArrayList<>(MEM_ALERTS);
        }

        List<Alert> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            String sql = unreadOnly ? SQL_FIND_UNREAD : SQL_FIND_ALL;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRowToAlert(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Database error finding alerts: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public List<Alert> findByUser(int userId, boolean unreadOnly) throws DatabaseException {
        return findAll(unreadOnly);
    }

    @Override
    public Optional<Alert> findLatestByCategoryAndMonth(String category, String monthYear) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_ALERTS.stream()
                .filter(a -> a.getCategory().equalsIgnoreCase(category) && a.getMonthYear().equals(monthYear))
                .findFirst();
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_FIND_LATEST_BY_CAT_MONTH);
            ps.setString(1, category);
            ps.setString(2, monthYear);
            rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToAlert(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Database error finding alert by category & month: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public Optional<Alert> findLatestByCategoryAndMonth(int userId, String category, String monthYear) throws DatabaseException {
        return findLatestByCategoryAndMonth(category, monthYear);
    }

    @Override
    public boolean delete(int alertId) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_ALERTS.removeIf(a -> a.getId() == alertId);
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_DELETE);
            ps.setInt(1, alertId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error deleting alert: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean delete(int alertId, int userId) throws DatabaseException {
        return delete(alertId);
    }

    @Override
    public boolean deleteByCategoryAndMonth(String category, String monthYear) throws DatabaseException {
        if (!DatabaseUtil.isMySQLAvailable()) {
            return MEM_ALERTS.removeIf(a -> a.getCategory().equalsIgnoreCase(category) && a.getMonthYear().equals(monthYear));
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_DELETE_BY_CAT_MONTH);
            ps.setString(1, category);
            ps.setString(2, monthYear);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error deleting alerts for category/month: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(ps, conn);
        }
    }

    @Override
    public boolean deleteByCategoryAndMonth(int userId, String category, String monthYear) throws DatabaseException {
        return deleteByCategoryAndMonth(category, monthYear);
    }

    private Alert mapRowToAlert(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String category = rs.getString("category");
        String monthYear = rs.getString("month_year");
        String alertType = rs.getString("alert_type");
        double thresholdPercent = rs.getDouble("threshold_percent");
        double spentAmount = rs.getDouble("spent_amount");
        double budgetLimit = rs.getDouble("budget_limit");
        double differenceAmount = rs.getDouble("difference_amount");
        String message = rs.getString("message");
        boolean isRead = rs.getBoolean("is_read");
        Timestamp createdAt = rs.getTimestamp("created_at");

        return new Alert(
            id, 1, category, monthYear, alertType,
            thresholdPercent, spentAmount, budgetLimit, differenceAmount,
            message, isRead, createdAt
        );
    }
}
