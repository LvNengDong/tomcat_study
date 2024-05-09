package server;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

/**
 * @Author lnd
 * @Description Processor负责调用Servlet
 * @Date 2024/1/26 17:34
 */
@Slf4j
public class HttpProcessor implements Runnable {

    Socket socket;
    boolean available = false; //是否获取到了Socket
    HttpConnector connector;

    public HttpProcessor(HttpConnector connector) {
        this.connector = connector;
    }

    @Override
    public void run() {
        while (true) {
            // 每个HttpProcessor都是一个独立的线程，在无需处理Socket时进入阻塞状态，在分配到Socket时进行处理
            Socket socket = await();
            if (Objects.isNull(socket)) {
                log.info("Socket is empty");
                continue;
            }
            log.info("Processor处理Socket请求 processor:{} socket:{}", this, socket);
            process(socket);
            // 回收processor
            connector.recycle(this);
        }
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }


    public void process(Socket socket) {
        InputStream input = null;
        OutputStream output = null;
        try {
            Thread.sleep(3000);
            log.info("解析input start");
            input = socket.getInputStream();
            Request request = new Request(input);
            request.parse();
            log.info("解析input end ========uri:{}", request.getUri());

            // 处理请求并响应
            log.info("处理input start");
            output = socket.getOutputStream();
            Response response = new Response(output);
            response.setRequest(request);
            if (request.getUri().startsWith("/servlet")) {
                log.info("处理Input，请求动态资源，servlet处理，start uri:{}", request.getUri());
                ServletProcessor processor = new ServletProcessor();
                processor.process(request, response);
                log.info("处理Input，请求动态资源，servlet处理，end uri:{}", request.getUri());
            } else {
                log.info("处理Input，请求静态资源，servlet处理，start uri:{}", request.getUri());
                StaticResourceProcessor processor = new StaticResourceProcessor();
                processor.process(request, response);
                log.info("处理Input，请求静态资源，servlet处理，end uri:{}", request.getUri());
            }
            log.info("处理input end");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized void assign(Socket socket) {
        try {
            // 等待connector提供新的socket
            while (available) { // 循环：避免【虚假唤醒】
                log.info("当前线程没有获取到Socket连接，等待获取新的Socket连接");
                wait();
            }
            // 存储新可用的Socket并通知我们的线程
            log.info("HttpConnector已经收到Socket连接，分配Socket给Processor Socket:{}", socket);
            this.socket = socket;
            available = true; // 表示当前Processor已经收到了Socket连接
            notifyAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    private synchronized Socket await() {
        try {
            // 等待Connector提供一个新的Socket
            while (!available) {
                log.info("HttpProcessor: 没有要处理的Socket请求，进入阻塞状态 processor:{}", this);
                wait();
                // 阻塞点/恢复点
            }
            // 通知Connector我们已经收到这个Socket了
            log.info("HttpProcessor: 分配到了Socket连接，唤醒HttpProcessor并处理Socket processor：{} socket:{}", this, socket);
            Socket socket = this.socket;
            available = false; //重置标志位（恢复到初始状态）
            notifyAll();
            return socket;
        } catch (Exception e) {
            log.info("Error", e);
            return null;
        }
    }
}
