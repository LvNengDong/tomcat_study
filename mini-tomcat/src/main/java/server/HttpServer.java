package server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author lnd
 * @Description
 * @Date 2024/1/10 22:04
 */
@Slf4j
public class HttpServer {

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.await();
    }

    /**
     * 启动服务器，持续监听请求
     * */
    public void await() {
        int port = 8080;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
            log.info("服务器启动成功");
        } catch (IOException e) {
            log.error("Socket服务启动失败 {}", this.getClass(), e);
            /*
                System.exit(1) 是 Java 中的一个方法，用于终止当前正在运行的 Java 虚拟机（JVM）。
                当调用 System.exit(1) 时，JVM 将会立即停止运行，并返回一个指定的退出状态码。
                在这里，参数 1 表示非正常退出，通常用于表示程序发生了错误或异常情况。
                System.exit(1) 的调用会导致程序立即终止，不会执行后续的代码。它可以用于在程序发生严重错误或异常时，强制终止程序的执行
             */
            System.exit(1);
        }

        while (true) {
            try {
                // 建立连接
                Socket socket = serverSocket.accept();
                log.info("accept方法会为每一个连接都生成一个socket对象，如果同时有多个连接，就会生成多个socket对象 {}", socket.hashCode());
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();

                // 解析input请求
                Request request = new Request(input);
                request.parse();

                // 处理请求并响应
                Response response = new Response(output);
                response.setRequest(request);
                if (request.getUri().startsWith("/servlet")) {
                    ServletProcessor processor = new ServletProcessor();
                    processor.process(request, response);
                } else {
                    StatisResourceProcessor processor = new StatisResourceProcessor();
                    processor.process(request, response);
                }
                log.info("一次请求处理结束");
                // 关闭连接
                socket.close();
            } catch (IOException e) {
                log.error("Socket服务数据传输异常 {}", this.getClass(), e);
            }

        }
    }
}
