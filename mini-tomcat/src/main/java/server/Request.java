package server;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * @Author lnd
 * @Description HTTP协议请求
 * @Date 2024/1/10 22:04
 */
@Slf4j
public class Request implements ServletRequest {


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
        StringBuilder request = new StringBuilder();
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

    public Object getAttribute(String name) {
        return null;
    }

    public Enumeration<String> getAttributeNames() {
        return null;
    }

    public String getCharacterEncoding() {
        return null;
    }

    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    public int getContentLength() {
        return 0;
    }

    public long getContentLengthLong() {
        return 0;
    }

    public String getContentType() {
        return null;
    }

    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    public String getParameter(String name) {
        return null;
    }

    public Enumeration<String> getParameterNames() {
        return null;
    }

    public String[] getParameterValues(String name) {
        return new String[0];
    }

    public Map<String, String[]> getParameterMap() {
        return null;
    }

    public String getProtocol() {
        return null;
    }

    public String getScheme() {
        return null;
    }

    public String getServerName() {
        return null;
    }

    public int getServerPort() {
        return 0;
    }

    public BufferedReader getReader() throws IOException {
        return null;
    }

    public String getRemoteAddr() {
        return null;
    }

    public String getRemoteHost() {
        return null;
    }

    public void setAttribute(String name, Object o) {

    }

    public void removeAttribute(String name) {

    }

    public Locale getLocale() {
        return null;
    }

    public Enumeration<Locale> getLocales() {
        return null;
    }

    public boolean isSecure() {
        return false;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    public String getRealPath(String path) {
        return null;
    }

    public int getRemotePort() {
        return 0;
    }

    public String getLocalName() {
        return null;
    }

    public String getLocalAddr() {
        return null;
    }

    public int getLocalPort() {
        return 0;
    }

    public ServletContext getServletContext() {
        return null;
    }

    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    public boolean isAsyncStarted() {
        return false;
    }

    public boolean isAsyncSupported() {
        return false;
    }

    public AsyncContext getAsyncContext() {
        return null;
    }

    public DispatcherType getDispatcherType() {
        return null;
    }
}
