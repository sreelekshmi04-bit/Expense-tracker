package jakarta.servlet.http;

import java.io.IOException;
import java.io.PrintWriter;

public interface HttpServletResponse {
    void setContentType(String type);
    void setHeader(String name, String value);
    void setStatus(int sc);
    void sendError(int sc, String msg) throws IOException;
    PrintWriter getWriter() throws IOException;
}
