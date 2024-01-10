package server;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author lnd
 * @Description HTTP协议请求
 * @Date 2024/1/10 22:04
 */
@Slf4j
public class Request {


    private InputStream inputStream;
    @Getter@Setter
    private String uri;

    public Request(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * 解析InputStream
     * */
    public void parse() {
        StringBuffer request = new StringBuffer();
        int i;
        byte[] buffer = new byte[2048];
        try {
            i = inputStream.read(buffer);
        } catch (IOException e) {
            log.error("解析请求参数出错_{}", this.getClass(), e);
            i = -1; // 异常标识
        }
        for (int j = 0; j < i; j++) {
            request.append((char) buffer[j]);
        }
        // 获取Request中携带的URI
        String uri = this.parseUri(request.toString());
        this.setUri(uri);
    }

    /*
     * HTTP请求示例：
     *       GET /hello.txt HTTP/1.1
     *       .....
     *
     * HTTP 协议规定，在请求格式第一行的内容中，包含请求方法、请求路径、使用的协议以及版本，用一个空格分开。
     * 如果格式稍微有点出入，这个解析就会失败。从这里也可以看出，遵守协议的重要性。
     * */
    public String parseUri(String requestString) {
        int index1;
        int index2;
        index1 = requestString.indexOf(" "); //该方法返回子字符串在原字符串中第一次出现的位置，如果找不到则返回 -1。
        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1); // 从指定位置开始查找子串在原字符串中出现的位置，如果找不到则返回 -1。
            if (index2 > index1) {
                return requestString.substring(index1 + 1, index2); // 截取URI部分
            }
        }
        return null;
    }
}
