package server;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author lnd
 * @Description HTTP协议响应
 * @Date 2024/1/10 22:04
 */
@Slf4j
public class Response {

    private OutputStream outputStream;
    private Request request;
    public void setRequest(Request request) {
        this.request = request;
    }

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * 返回静态资源数据
     * */
    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[Constants.BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            File file = new File(Constants.WEB_ROOT, request.getUri());
            if (file.exists()) {
                // 正常返回
                fis = new FileInputStream(file);
                // 读取服务器上的静态资源
                int ch = fis.read(bytes, 0, Constants.BUFFER_SIZE);
                if (ch != -1) {
                    outputStream.write(bytes, 0, Constants.BUFFER_SIZE);
                    ch = fis.read(bytes, 0, Constants.BUFFER_SIZE);
                }
                outputStream.flush();
            } else {
                // 错误返回
                String errorMessage = "HTTP/1.1 404 FIle Not Found\r\n" + "Content-Type: text/html\r\n" + "Content-Length: 23\r\n" + "\r\n" + "File Not Found ";
                outputStream.write(errorMessage.getBytes());
            }
        } catch (Exception e) {
            log.error("服务器处理出错 {}", this.getClass(), e);
        } finally {
            // 释放资源
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("关闭Stream流失败 {}", this.getClass(), e);
                    throw e;
                }
            }
        }
    }
}
