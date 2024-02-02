package server;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

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
        // 创建 ServerSocket
        ServerSocket serverSocket = initServerSocket();
        if (Objects.isNull(serverSocket)) {
            return;
        }
        // 持续监听请求
        while (true) {
            try {
                log.info("建立Socket连接 ready");
                Socket socket = serverSocket.accept(); // accept方法会为每一个连接都生成一个socket对象，如果同时有多个连接，就会生成多个socket对象
                log.info("建立Socket连接 end =======obj:{}", socket);

                log.info("解析input start");
                InputStream input = socket.getInputStream();
                Request request = new Request(input);
                request.parse();
                log.info("解析input end ========uri:{}", request.getUri());

                // 处理请求并响应
                log.info("处理input start");
                OutputStream output = socket.getOutputStream();
                Response response = new Response(output);
                response.setRequest(request);
                if (request.getUri().startsWith("/servlet")) {
                    log.info("处理Input，请求动态资源，servlet处理，start uri:{}", request.getUri());
                    ServletProcessor processor = new ServletProcessor();
                    processor.process(request, response);
                    log.info("处理Input，请求动态资源，servlet处理，end uri:{}", request.getUri());
                } else {
                    log.info("处理Input，请求静态资源，servlet处理，start uri:{}", request.getUri());
                    StatisResourceProcessor processor = new StatisResourceProcessor();
                    processor.process(request, response);
                    log.info("处理Input，请求静态资源，servlet处理，end uri:{}", request.getUri());
                }
                log.info("处理input end");
                log.info("关闭连接 start");
                socket.close();
                log.info("关闭连接 end");
            } catch (IOException e) {
                log.error("Socket服务数据传输异常 {}", this.getClass(), e);
            }
        }
    }

    private static ServerSocket initServerSocket() {
        try {
            log.info("mini-tomcat服务启动 start");
            ServerSocket serverSocket = new ServerSocket(Constants.PORT, 1, InetAddress.getByName(Constants.HOST));
            log.info("mini-tomcat服务启动 end");
            return serverSocket;
        } catch (IOException e) {
            log.error("mini-tomcat服务启动失败，kill JVM进程", e);
            /*
                System.exit(1) 是 Java 中的一个方法，用于终止当前正在运行的 Java 虚拟机（JVM）。
                当调用 System.exit(1) 时，JVM 将会立即停止运行，并返回一个指定的退出状态码。
                在这里，参数 1 表示非正常退出，通常用于表示程序发生了错误或异常情况。
                System.exit(1) 的调用会导致程序立即终止，不会执行后续的代码。它可以用于在程序发生严重错误或异常时，强制终止程序的执行
             */
            System.exit(1);
            return null;
        }
    }
}
