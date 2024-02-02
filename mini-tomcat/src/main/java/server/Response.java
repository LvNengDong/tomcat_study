package server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * @Author lnd
 * @Description HTTP协议响应
 * @Date 2024/1/10 22:04
 */
@Slf4j
public class Response implements ServletResponse {


    private OutputStream outputStream;
    private Request request;

    PrintWriter writer;
    String contentType = null;
    long contentLength = -1;
    String charset = null;
    String characterEncoding = null;
    public void setRequest(Request request) {
        this.request = request;
    }

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }
    @Override
    public void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
    }
    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return (ServletOutputStream) outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        // args2 这个值的含义为 autoFlush，当为 true 时，println、printf 等方法会自动刷新输出流的缓冲
        writer = new PrintWriter(new OutputStreamWriter(outputStream, getCharacterEncoding()), true);
        return writer;
    }

    @Override
    public void setContentLength(int len) {

    }
    @Override
    public void setContentLengthLong(long len) {

    }
    @Override
    public void setContentType(String type) {

    }
    @Override
    public void setBufferSize(int size) {

    }
    @Override
    public int getBufferSize() {
        return 0;
    }
    @Override
    public void flushBuffer() throws IOException {

    }
    @Override
    public void resetBuffer() {

    }
    @Override
    public boolean isCommitted() {
        return false;
    }
    @Override
    public void reset() {

    }
    @Override
    public void setLocale(Locale loc) {

    }
    @Override
    public Locale getLocale() {
        return null;
    }
}
