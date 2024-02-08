package server;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @Author lnd
 * @Description Processor负责调用Servlet
 * @Date 2024/1/26 17:34
 */
@Slf4j
@NoArgsConstructor
public class HttpProcessor implements Runnable {

    public void process(Socket socket) {
        InputStream input = null;
        OutputStream output = null;
        try {
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
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }

    }

    @Override
    public void run() {

    }
}
