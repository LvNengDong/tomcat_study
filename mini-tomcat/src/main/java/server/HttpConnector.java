package server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * @Author lnd
 * @Description Connector 负责接收、响应客户端请求
 *  HttpConnector 实现了了 Runnable 接口，支持并发处理，是为了提高整个服务器的吞吐量
 * @Date 2024/1/26 17:34
 */
@Slf4j
public class HttpConnector implements Runnable {
    int minProcessors = 3;
    int maxProcessors = 10;
    int curProcessors = 0;

    Deque<HttpProcessor> processors = new ArrayDeque<>(); // ArrayDeque，双端队列
    @Override
    public void run() {
        try {
            log.info("mini-tomcat服务启动 start");
            ServerSocket serverSocket = new ServerSocket(Constants.PORT, 1, InetAddress.getByName(Constants.HOST));
            log.info("mini-tomcat服务启动 end，服务器地址:{}", Constants.HOST + ":" + Constants.PORT);

            log.info("初始化服务器的处理器 start");
            for (int i = 0; i < minProcessors; i++) { //这里的处理有点类似于线程池的核心线程数，在未达到核心线程数之前，每次处理新任务都是新建一个处理器
                HttpProcessor initProcessor = new HttpProcessor(this);
                initProcessor.start(); // 在初始化服务器中的HttpConnector时，将HttpProcessor的个数初始化到minProcessors，并让每个HttpProcessor处理器处于待命(阻塞)状态
                processors.push(initProcessor);
            }
            curProcessors = minProcessors;
            log.info("初始化服务器的处理器 end 当前HttpProcessor处理器数量 curProcessors:{}", curProcessors);

            // 持续监听请求
            while (true) {
                try {
                    log.info("监听到客户端请求建立Socket连接 start");
                    Socket socket = serverSocket.accept(); // accept方法会为每一个连接都生成一个socket对象，如果同时有多个并发请求请求建立连接，就会生成多个socket对象
                    log.info("客户端请求建立Socket连接 end =======obj:{}", socket);

                    log.info("获取HttpProcessor处理器 start");
                    HttpProcessor processor = createProcessor(); //线程安全，锁对象为处理器队列【processors】，即同一时间只能有一个线程从processors中获取处理器
                    if (Objects.isNull(processor)) {
                        log.info("获取HttpProcessor处理器 error 未获取到处理器，丢弃当前Socket：{}", socket);
                        socket.close();
                        continue;
                    }
                    log.info("获取HttpProcessor处理器 end");

                    // 把Socket分配给HttpProcessor处理器，并唤醒HttpProcessor所在的线程
                    processor.assign(socket);

                    //log.info("Servlet处理 start");
                    //processor.process(socket);
                    //log.info("Servlet处理 end");
                    //
                    //log.info("释放Servlet处理器 start");
                    //this.processors.push(processor);
                    //log.info("释放Servlet处理器 end");
                    //
                    //log.info("关闭连接 start");
                    //socket.close();
                    //log.info("关闭连接 end");
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

    /**
     * 从池子中获取一个processor，如果池子为空且小于最大限制，则新建一个
     *
     * 注意并发处理
     * @return
     */
    private HttpProcessor createProcessor() {
        synchronized (this.processors) { //monitor对象锁
            if (!this.processors.isEmpty()) {
                return processors.pop();
            }
            if (this.curProcessors < this.maxProcessors) {
                return newProcessor();
            } else {
                log.info("Servlet处理器达到上限 curSize:{} maxSize:{}", this.curProcessors, this.maxProcessors);
                return null;
            }
        }
    }

    private HttpProcessor newProcessor() {
        HttpProcessor processor = new HttpProcessor(this);
        processors.push(processor);
        this.curProcessors++;
        return processors.pop();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    boolean available = true; // Processor会把这个标志置为false
    Socket socket;
    synchronized void assign(Socket socket) {
        while (available) {
            try {
                wait(); // monitor对象锁
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // 到了某个时候，Processor 线程把available标志设置为 false，Connector 线程就跳出死等的循环，然后把接收到的 Socket 交给 Processor。
        this.socket = socket;
        // 然后要立刻重新把 available 标志设置为 true，再调用 notifyAll() 通知其他线程。
        available = true;
        notifyAll();
    }

    public void recycle(HttpProcessor httpProcessor) {
        processors.push(httpProcessor);
    }
}
