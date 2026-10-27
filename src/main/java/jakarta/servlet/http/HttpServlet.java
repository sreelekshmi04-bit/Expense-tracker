package jakarta.servlet.http;

import jakarta.servlet.ServletException;
import java.io.IOException;

public abstract class HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(405, "HTTP method GET is not supported by this URL");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(405, "HTTP method POST is not supported by this URL");
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(405, "HTTP method PUT is not supported by this URL");
    }

    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(405, "HTTP method DELETE is not supported by this URL");
    }
}
