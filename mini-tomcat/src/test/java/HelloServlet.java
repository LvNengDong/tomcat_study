import server.Request;
import server.Response;
import server.Servlet;

import java.io.IOException;

/**
 * @Author lnd
 * @Description
 * @Date 2024/1/17 15:43
 */
public class HelloServlet implements Servlet {
    @Override
    public void service(Request req, Response resp) {
        String doc = "<!DOCTYPE html> \n" +
                "<html>\n" +
                "<head><meta charset=\"utf-8\"><title>Test</title></head>\n"+
                "<body bgcolor=\"#f0f0f0\">\n" +
                "<h1 align=\"center\">" + "Hello World 你好" + "</h1>\n";
        try {
            resp.getOutputStream().write(doc.getBytes("utf-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
        在编写完毕后，我们需要单独编译这个类，生成 HelloServlet.class，把编译后的文件放到 /webroot/test 目录下，
        原因在于我们的服务器需要从 webroot 目录下获取资源文件。
    */
}
