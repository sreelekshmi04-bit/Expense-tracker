package com.expensetracker.util;

import com.expensetracker.model.*;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

/**
 * Lightweight, zero-dependency JSON Serializer and Parser.
 * Ensures the application is fully portable and runs on any standard JDK
 * without requiring external third-party JARs like Jackson or Gson.
 */
public class JsonUtil {

    /**
     * Serializes any standard object, collection, or map into a valid JSON string.
     */
    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            return "\"" + escape((String) obj) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof LocalDate || obj instanceof java.sql.Date) {
            return "\"" + obj.toString() + "\"";
        }
        if (obj instanceof java.sql.Timestamp) {
            return "\"" + obj.toString() + "\"";
        }
        if (obj instanceof Collection) {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : (Collection<?>) obj) {
                if (!first) sb.append(",");
                sb.append(toJson(item));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }
        if (obj instanceof double[]) {
            double[] arr = (double[]) obj;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < arr.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(arr[i]);
            }
            sb.append("]");
            return sb.toString();
        }
        if (obj instanceof int[]) {
            int[] arr = (int[]) obj;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < arr.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(arr[i]);
            }
            sb.append("]");
            return sb.toString();
        }
        if (obj instanceof Object[]) {
            Object[] arr = (Object[]) obj;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < arr.length; i++) {
                if (i > 0) sb.append(",");
                sb.append(toJson(arr[i]));
            }
            sb.append("]");
            return sb.toString();
        }
        if (obj instanceof Map) {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(escape(String.valueOf(entry.getKey()))).append("\":");
                sb.append(toJson(entry.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }

        // Domain Model Serializers
        if (obj instanceof Transaction) {
            Transaction tx = (Transaction) obj;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", tx.getId());
            map.put("userId", tx.getUserId());
            map.put("type", tx.getTransactionType());
            map.put("amount", tx.getAmount());
            map.put("signedAmount", tx.getSignedAmount());
            map.put("transactionDate", tx.getTransactionDate() != null ? tx.getTransactionDate().toString() : "");
            map.put("friendlyDate", DateUtil.formatFriendly(tx.getTransactionDate()));
            map.put("description", tx.getDescription());
            map.put("categoryOrSource", tx.getCategoryOrSource());
            map.put("paymentMethod", (tx instanceof Expense) ? ((Expense) tx).getPaymentMethod() : "Bank Transfer");
            map.put("createdAt", tx.getCreatedAt() != null ? tx.getCreatedAt().toString() : "");
            return toJson(map);
        }

        if (obj instanceof User) {
            User u = (User) obj;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("email", u.getEmail());
            map.put("currency", u.getCurrency());
            return toJson(map);
        }

        if (obj instanceof Budget) {
            Budget b = (Budget) obj;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", b.getId());
            map.put("userId", b.getUserId());
            map.put("category", b.getCategory());
            map.put("amountLimit", b.getAmountLimit());
            map.put("monthYear", b.getMonthYear());
            map.put("friendlyMonth", DateUtil.formatMonthFriendly(b.getMonthYear()));
            return toJson(map);
        }

        if (obj instanceof BudgetProgress) {
            BudgetProgress bp = (BudgetProgress) obj;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("budgetId", bp.getBudgetId());
            map.put("category", bp.getCategory());
            map.put("amountLimit", bp.getAmountLimit());
            map.put("spentAmount", bp.getSpentAmount());
            map.put("remainingAmount", bp.getRemainingAmount());
            map.put("percentageUsed", bp.getPercentageUsed());
            map.put("status", bp.getStatus());
            map.put("monthYear", bp.getMonthYear());
            return toJson(map);
        }

        if (obj instanceof Alert) {
            Alert a = (Alert) obj;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", a.getId());
            map.put("userId", a.getUserId());
            map.put("category", a.getCategory());
            map.put("monthYear", a.getMonthYear());
            map.put("alertType", a.getAlertType());
            map.put("thresholdPercent", a.getThresholdPercent());
            map.put("spentAmount", a.getSpentAmount());
            map.put("budgetLimit", a.getBudgetLimit());
            map.put("differenceAmount", a.getDifferenceAmount());
            map.put("message", a.getMessage());
            map.put("isRead", a.isRead());
            map.put("createdAt", a.getCreatedAt() != null ? a.getCreatedAt().toString() : "");
            return toJson(map);
        }

        if (obj instanceof SpendingSummary) {
            SpendingSummary s = (SpendingSummary) obj;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("totalIncome", s.getTotalIncome());
            map.put("totalExpense", s.getTotalExpense());
            map.put("currentBalance", s.getCurrentBalance());
            map.put("monthlyIncome", s.getMonthlyIncome());
            map.put("monthlyExpense", s.getMonthlyExpense());
            map.put("monthlySavings", s.getMonthlySavings());
            map.put("savingsRate", s.getSavingsRate());
            return toJson(map);
        }

        if (obj instanceof CategoryTotal) {
            CategoryTotal ct = (CategoryTotal) obj;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("category", ct.getCategory());
            map.put("totalAmount", ct.getTotalAmount());
            map.put("transactionCount", ct.getTransactionCount());
            map.put("percentageOfTotal", ct.getPercentageOfTotal());
            return toJson(map);
        }

        // Generic Reflection Fallback for other DTOs
        try {
            Map<String, Object> map = new LinkedHashMap<>();
            for (Method method : obj.getClass().getMethods()) {
                if (method.getName().startsWith("get") && method.getName().length() > 3 
                    && method.getParameterCount() == 0 && !method.getName().equals("getClass")) {
                    String propName = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4);
                    Object val = method.invoke(obj);
                    map.put(propName, val);
                }
            }
            return toJson(map);
        } catch (Exception e) {
            return "\"" + escape(obj.toString()) + "\"";
        }
    }

    /**
     * Parses simple JSON object string (key-value pairs) into a Map<String, String>.
     */
    public static Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) {
            return map;
        }

        String trimmed = json.trim();
        if (trimmed.startsWith("{")) trimmed = trimmed.substring(1);
        if (trimmed.endsWith("}")) trimmed = trimmed.substring(0, trimmed.length() - 1);

        // Simple tokenizer parsing JSON key-value pairs
        boolean inQuotes = false;
        StringBuilder currentKey = new StringBuilder();
        StringBuilder currentValue = new StringBuilder();
        boolean readingKey = true;

        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);

            if (c == '\"' && (i == 0 || trimmed.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
                continue;
            }

            if (!inQuotes) {
                if (c == ':') {
                    readingKey = false;
                    continue;
                }
                if (c == ',') {
                    String k = currentKey.toString().trim();
                    String v = currentValue.toString().trim();
                    if (!k.isEmpty()) {
                        map.put(k, unescape(v));
                    }
                    currentKey.setLength(0);
                    currentValue.setLength(0);
                    readingKey = true;
                    continue;
                }
            }

            if (readingKey) {
                currentKey.append(c);
            } else {
                currentValue.append(c);
            }
        }

        String k = currentKey.toString().trim();
        String v = currentValue.toString().trim();
        if (!k.isEmpty()) {
            map.put(k, unescape(v));
        }

        return map;
    }

    public static Map<String, Object> parseJsonMap(String json) {
        Map<String, String> simple = parseSimpleJson(json);
        Map<String, Object> res = new HashMap<>();
        for (Map.Entry<String, String> e : simple.entrySet()) {
            res.put(e.getKey(), e.getValue());
        }
        return res;
    }

    private static String escape(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private static String unescape(String raw) {
        if (raw == null) return "";
        String s = raw.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
        }
        return s.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }
}
