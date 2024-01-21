package server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;

/**
 * @Author lnd
 * @Description HTTP协议响应
 * @Date 2024/1/10 22:04
 */
@Slf4j
public class Response {

    @Getter
    private OutputStream outputStream;
    private Request request;
    public void setRequest(Request request) {
        this.request = request;
    }

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }



}
