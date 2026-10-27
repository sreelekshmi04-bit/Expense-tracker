package com.expensetracker.controller;

import com.expensetracker.exception.AuthenticationException;
import com.expensetracker.exception.ValidationException;
import com.expensetracker.model.User;
import com.expensetracker.service.UserService;
import com.expensetracker.service.UserServiceImpl;
import com.expensetracker.util.JsonUtil;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller handling user registration, authentication, and session state.
 * Demonstrates Separation of Concerns: converts HTTP request data to domain calls.
 */
public class AuthController {

    private final UserService userService;

    public AuthController() {
        this(new UserServiceImpl());
    }

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles User Registration.
     */
    public String handleRegister(Map<String, String> params, Map<String, Object> session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String name = params.get("name");
            String email = params.get("email");
            String password = params.get("password");

            User user = userService.register(name, email, password);

            // Establish active session
            session.put("user", user);

            response.put("success", true);
            response.put("message", "Registration successful! Welcome to Expense Tracker, " + user.getName() + ".");
            response.put("user", user);
        } catch (ValidationException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    /**
     * Handles User Login.
     */
    public String handleLogin(Map<String, String> params, Map<String, Object> session) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = params.get("email");
            String password = params.get("password");

            User user = userService.login(email, password);

            // Establish active session
            session.put("user", user);

            response.put("success", true);
            response.put("message", "Welcome back, " + user.getName() + "!");
            response.put("user", user);
        } catch (AuthenticationException | ValidationException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
        }
        return JsonUtil.toJson(response);
    }

    /**
     * Handles User Logout.
     */
    public String handleLogout(Map<String, Object> session) {
        session.remove("user");
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "You have been logged out successfully.");
        return JsonUtil.toJson(response);
    }

    /**
     * Checks current session.
     */
    public String handleGetCurrentUser(Map<String, Object> session) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) session.get("user");
        if (user != null) {
            response.put("authenticated", true);
            response.put("user", user);
        } else {
            response.put("authenticated", false);
        }
        return JsonUtil.toJson(response);
    }
}
