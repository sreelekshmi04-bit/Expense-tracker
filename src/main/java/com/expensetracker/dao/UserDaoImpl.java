package com.expensetracker.dao;

import com.expensetracker.exception.DatabaseException;
import com.expensetracker.model.User;
import com.expensetracker.util.DatabaseUtil;
import java.sql.*;
import java.util.Optional;

/**
 * JDBC Implementation of UserDao.
 * Uses PreparedStatements exclusively to prevent SQL Injection.
 */
public class UserDaoImpl implements UserDao {

    private static final String SQL_INSERT = 
        "INSERT INTO users (name, email, password_hash, salt, currency) VALUES (?, ?, ?, ?, ?)";
    
    private static final String SQL_FIND_BY_EMAIL = 
        "SELECT id, name, email, password_hash, salt, currency, created_at FROM users WHERE email = ?";
    
    private static final String SQL_FIND_BY_ID = 
        "SELECT id, name, email, password_hash, salt, currency, created_at FROM users WHERE id = ?";
    
    private static final String SQL_EXISTS_EMAIL = 
        "SELECT COUNT(*) FROM users WHERE email = ?";

    private static final String SQL_UPDATE = 
        "UPDATE users SET name = ?, currency = ? WHERE id = ?";

    @Override
    public int create(User user) throws DatabaseException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getSalt());
            ps.setString(5, user.getCurrency());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DatabaseException("Creating user failed, no rows affected.");
            }

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                user.setId(generatedId);
                return generatedId;
            } else {
                throw new DatabaseException("Creating user failed, no ID obtained.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error creating user: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) throws DatabaseException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_FIND_BY_EMAIL);
            ps.setString(1, email.trim().toLowerCase());
            rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Database error finding user by email: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public Optional<User> findById(int id) throws DatabaseException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_FIND_BY_ID);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Database error finding user by id: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public boolean existsByEmail(String email) throws DatabaseException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_EXISTS_EMAIL);
            ps.setString(1, email.trim().toLowerCase());
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Database error checking email existence: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(rs, ps, conn);
        }
    }

    @Override
    public boolean update(User user) throws DatabaseException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(SQL_UPDATE);
            ps.setString(1, user.getName());
            ps.setString(2, user.getCurrency());
            ps.setInt(3, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Database error updating user: " + e.getMessage(), e);
        } finally {
            DatabaseUtil.closeQuietly(ps, conn);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getString("salt"),
            rs.getString("currency"),
            rs.getTimestamp("created_at")
        );
    }
}
