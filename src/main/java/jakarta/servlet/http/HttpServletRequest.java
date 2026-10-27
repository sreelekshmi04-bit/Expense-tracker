package jakarta.servlet.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public interface HttpServletRequest {
    String getParameter(String name);
    Map<String, String[]> getParameterMap();
    String getMethod();
    String getRequestURI();
    String getHeader(String name);
    HttpSession getSession(boolean create);
    HttpSession getSession();
    BufferedReader getReader() throws IOException;
    Object getAttribute(String name);
    void setAttribute(String name, Object o);
}
