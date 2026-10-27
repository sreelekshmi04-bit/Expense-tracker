package com.expensetracker.server;

import com.expensetracker.controller.*;
import com.expensetracker.util.DatabaseUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Built-in Zero-Dependency HTTP Server Runner.
 * 
 * Runs directly on any standard Java 17/21/25 JDK without requiring an external Tomcat installation.
 * Provides instant 1-click execution for college viva demos while sharing the exact same
 * Controllers, Services, DAOs, and Models as the Tomcat Servlet deployment.
 * 
 * Designed for Single-User Direct Access: opens straight to the dashboard with zero auth friction.
 */
public class StandaloneServer {

    private static final int PORT = 8080;
    // Allow WEB_APP_DIR to be set via environment variable for Docker/Render deployments
    private static final String WEB_APP_DIR = System.getenv("WEBAPP_DIR") != null
            ? System.getenv("WEBAPP_DIR")
            : "src/main/webapp";

    private static final TransactionController transactionController = new TransactionController();
    private static final BudgetController budgetController = new BudgetController();
    private static final AlertController alertController = new AlertController();
    private static final DashboardController dashboardController = new DashboardController();
    private static final ReportController reportController = new ReportController();

    public static void main(String[] args) throws IOException {
        int port = PORT;
        // Check PORT environment variable first (set by Render and other cloud platforms)
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.isEmpty()) {
            try { port = Integer.parseInt(portEnv); } catch (NumberFormatException ignored) {}
        } else if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(10));

        // API Endpoint Handlers
        server.createContext("/api/transactions", new TransactionHandler());
        server.createContext("/api/budgets", new BudgetHandler());
        server.createContext("/api/alerts", new AlertHandler());
        server.createContext("/api/dashboard", new DashboardHandler());
        server.createContext("/api/reports", new ReportHandler());
        server.createContext("/api/auth", new SingleUserAuthHandler());

        // Static Web Asset Handler (HTML, CSS, JS)
        server.createContext("/", new StaticFileHandler());

        System.out.println("==================================================================");
        System.out.println("   EXPENSE TRACKER SYSTEM — JAVA OOP APPLICATION                 ");
        System.out.println("==================================================================");
        System.out.println(" Server running at: http://localhost:" + port + "/");
        System.out.println(" Database status: " + (DatabaseUtil.isMySQLAvailable() ? "🟢 Connected to MySQL" : "🟡 In-Memory Active (Auto-Fallback)"));
        System.out.println(" Open your browser to access the Financial Journal.");
        System.out.println(" Press Ctrl+C in this terminal window to stop the server.");
        System.out.println("==================================================================");

        server.start();
    }

    // =========================================================================
    // HTTP API HANDLERS
    // =========================================================================

    private static class SingleUserAuthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            // Always returns current user info without requiring login
            String responseJson = "{\"success\":true,\"user\":{\"id\":1,\"fullName\":\"Personal Finance Journal\",\"email\":\"single-user@journal.local\"}}";
            sendJsonResponse(exchange, 200, responseJson);
        }
    }

    private static class TransactionHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String method = exchange.getRequestMethod();
            Map<String, String> params = getRequestParameters(exchange);
            String action = params.getOrDefault("action", "list");
            String responseJson;

            if ("POST".equalsIgnoreCase(method)) {
                if ("add".equalsIgnoreCase(action)) {
                    responseJson = transactionController.handleAdd(params);
                } else if ("update".equalsIgnoreCase(action)) {
                    responseJson = transactionController.handleUpdate(params);
                } else if ("delete".equalsIgnoreCase(action)) {
                    responseJson = transactionController.handleDelete(params);
                } else {
                    responseJson = transactionController.handleAdd(params);
                }
            } else {
                if ("get".equalsIgnoreCase(action)) {
                    responseJson = transactionController.handleGetById(params);
                } else {
                    responseJson = transactionController.handleList(params);
                }
            }

            sendJsonResponse(exchange, 200, responseJson);
        }
    }

    private static class BudgetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String method = exchange.getRequestMethod();
            Map<String, String> params = getRequestParameters(exchange);
            String action = params.getOrDefault("action", "list");
            String responseJson;

            if ("POST".equalsIgnoreCase(method)) {
                if ("delete".equalsIgnoreCase(action)) {
                    responseJson = budgetController.handleDeleteBudget(params);
                } else {
                    responseJson = budgetController.handleSetBudget(params);
                }
            } else {
                responseJson = budgetController.handleListProgress(params);
            }

            sendJsonResponse(exchange, 200, responseJson);
        }
    }

    private static class AlertHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String method = exchange.getRequestMethod();
            Map<String, String> params = getRequestParameters(exchange);
            String action = params.getOrDefault("action", "list");
            String responseJson;

            if ("POST".equalsIgnoreCase(method)) {
                if ("read".equalsIgnoreCase(action)) {
                    responseJson = alertController.handleMarkRead(params);
                } else if ("readAll".equalsIgnoreCase(action)) {
                    responseJson = alertController.handleMarkAllRead();
                } else if ("delete".equalsIgnoreCase(action)) {
                    responseJson = alertController.handleDelete(params);
                } else {
                    responseJson = alertController.handleListAlerts(params);
                }
            } else {
                responseJson = alertController.handleListAlerts(params);
            }

            sendJsonResponse(exchange, 200, responseJson);
        }
    }

    private static class DashboardHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            Map<String, String> params = getRequestParameters(exchange);
            String action = params.getOrDefault("action", "");
            String responseJson;
            if ("resetSample".equalsIgnoreCase(action)) {
                responseJson = dashboardController.handleResetSampleData();
            } else if ("clearAll".equalsIgnoreCase(action)) {
                responseJson = dashboardController.handleClearAllData();
            } else {
                responseJson = dashboardController.handleGetDashboard();
            }
            sendJsonResponse(exchange, 200, responseJson);
        }
    }

    private static class ReportHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            enableCors(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            Map<String, String> params = getRequestParameters(exchange);
            String format = params.get("format");

            if ("csv".equalsIgnoreCase(format) || "txt".equalsIgnoreCase(format)) {
                ReportController.ExportResult result = reportController.handleExport(params, format);
                byte[] bytes = result.content.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", result.contentType);
                exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"" + result.filename + "\"");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                String responseJson = reportController.handleGetFilteredReport(params);
                sendJsonResponse(exchange, 200, responseJson);
            }
        }
    }

    private static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            Path filePath = Paths.get(WEB_APP_DIR, path).normalize();
            File file = filePath.toFile();

            if (!file.exists() || file.isDirectory()) {
                File htmlFile = new File(WEB_APP_DIR + path + ".html");
                if (htmlFile.exists()) {
                    file = htmlFile;
                } else {
                    String notFound = "<h1>404 Not Found</h1><p>Requested file not found: " + path + "</p>";
                    byte[] bytes = notFound.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(404, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                    return;
                }
            }

            String mimeType = getMimeType(file.getName());
            exchange.getResponseHeaders().set("Content-Type", mimeType);
            exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
            exchange.getResponseHeaders().set("Pragma", "no-cache");
            exchange.getResponseHeaders().set("Expires", "0");
            exchange.sendResponseHeaders(200, file.length());
            try (OutputStream os = exchange.getResponseBody(); FileInputStream fis = new FileInputStream(file)) {
                fis.transferTo(os);
            }
        }
    }

    // =========================================================================
    // HELPER UTILITIES
    // =========================================================================

    private static void enableCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
        byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static Map<String, String> getRequestParameters(HttpExchange exchange) throws IOException {
        Map<String, String> params = new HashMap<>();

        // 1. Query parameters
        String query = exchange.getRequestURI().getRawQuery();
        if (query != null && !query.isEmpty()) {
            parseQueryString(query, params);
        }

        // 2. Request body parameters (POST/PUT)
        String method = exchange.getRequestMethod();
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            if (contentType != null && contentType.contains("application/json")) {
                if (!body.isBlank()) {
                    Map<String, Object> jsonMap = com.expensetracker.util.JsonUtil.parseJsonMap(body);
                    for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
                        params.put(entry.getKey(), entry.getValue() != null ? String.valueOf(entry.getValue()) : "");
                    }
                }
            } else if (!body.isEmpty()) {
                parseQueryString(body, params);
            }
        }

        return params;
    }

    private static void parseQueryString(String query, Map<String, String> target) {
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                if (idx > 0) {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    target.put(key, value);
                } else if (!pair.isEmpty()) {
                    String key = URLDecoder.decode(pair, StandardCharsets.UTF_8);
                    target.put(key, "");
                }
            } catch (Exception ignored) {}
        }
    }

    private static String getMimeType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".html") || lower.endsWith(".htm")) return "text/html; charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
        if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".ico")) return "image/x-icon";
        if (lower.endsWith(".csv")) return "text/csv; charset=UTF-8";
        return "application/octet-stream";
    }
}
