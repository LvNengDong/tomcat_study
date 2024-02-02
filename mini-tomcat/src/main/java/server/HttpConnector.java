package server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author lnd
 * @Description Connector 负责接收、响应客户端请求
 *  HttpConnector 实现了了 Runnable 接口，支持并发处理，是为了提高整个服务器的吞吐量
 * @Date 2024/1/26 17:34
 */
@Slf4j
public class HttpConnector implements Runnable {
    @Override
    public void run() {
        try {
            log.info("mini-tomcat服务启动 start");
            ServerSocket serverSocket = new ServerSocket(Constants.PORT, 1, InetAddress.getByName(Constants.HOST));
            log.info("mini-tomcat服务启动 end");
            // 持续监听请求
            while (true) {
                try {
                    log.info("建立Socket连接 ready");
                    Socket socket = serverSocket.accept(); // accept方法会为每一个连接都生成一个socket对象，如果同时有多个连接，就会生成多个socket对象
                    log.info("建立Socket连接 end =======obj:{}", socket);
                    log.info("Servlet处理 start");
                    HttpProcessor processor = new HttpProcessor();
                    processor.process(socket);
                    log.info("Servlet处理 end");
                    log.info("关闭连接 start");
                    socket.close();
                    log.info("关闭连接 end");
                } catch (IOException e) {
                    log.error("Socket服务数据传输异常 {}", this.getClass(), e);
                }
            }
        } catch (IOException e) {
            log.error("mini-tomcat服务启动失败，kill JVM进程", e);
            /*
                System.exit(1) 是 Java 中的一个方法，用于终止当前正在运行的 Java 虚拟机（JVM）。
                当调用 System.exit(1) 时，JVM 将会立即停止运行，并返回一个指定的退出状态码。
                在这里，参数 1 表示非正常退出，通常用于表示程序发生了错误或异常情况。
                System.exit(1) 的调用会导致程序立即终止，不会执行后续的代码。它可以用于在程序发生严重错误或异常时，强制终止程序的执行
             */
            System.exit(1);
        }
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }
}
