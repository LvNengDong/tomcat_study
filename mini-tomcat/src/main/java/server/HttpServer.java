package server;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author lnd
 * @Description 用于启动Connector线程，等待客户端请求
 * @Date 2024/1/10 22:04
 */
@Slf4j
public class HttpServer {

    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        connector.start();
    }

}
