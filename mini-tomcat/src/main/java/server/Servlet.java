package server;

/**
 * @Author lnd
 * @Description
 * @Date 2024/1/17 15:43
 */
public interface Servlet {
    public void service(Request req, Response resp);
}
