package server;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;

import javax.servlet.Servlet;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * @Author lnd
 * @Description
 * @Date 2024/1/17 15:43
 */
@Slf4j
public class ServletProcessor {

    /**
     * 1、通过URI中的"/"定位到对应的Servlet名称，
     * 2、通过反射获取到对应的Servlet实现类并加载
     * 3、调用 service 方法获取动态资源返回体
     * 4、结合组装的响应头一起返回给客户端
     * @param request
     * @param response
     */
    public void process(Request request, Response response) {
        try {
            String uri = request.getUri();
            // 按照简单规则确定servlet名，认为uri最后一个 / 符号后面的字符串就是是 servlet 的名字
            String servletName = uri.substring(uri.lastIndexOf("/") + 1);
            log.info("uri:{} >> servlet:{}", uri, servletName);
            //URLClassLoader loader = null; //加载路径
            PrintWriter writer = null;
            URLClassLoader loader;
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(Constants.WEB_SERVLET_ROOT);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            log.info("URLClassLoaderSource:{}", repository);
            urls[0] = new URL(null, repository, streamHandler);
            log.info("urls:{}", JSON.toJSONString(urls));
            loader = new URLClassLoader(urls);

            // 获取PrintWriter
            response.setCharacterEncoding("UTF-8");
            writer = response.getWriter();
            // 加载 servlet
            Class<?> servletClass = null;
            log.info("加载servlet，servletName:{}", servletName);
            servletClass = loader.loadClass(servletName);
            // 写响应头
            String head = composeResponseHead();
            writer.println(head);
            Servlet servlet = null;
            // 创建servlet实例，并调用service方法，处理response响应体
            servlet = (Servlet) servletClass.newInstance();
            servlet.service(request, response);
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    private String composeResponseHead() {
        HashMap<String, Object> responseHeaderMap = Maps.newHashMap();
        responseHeaderMap.put("StatusCode", "200");
        responseHeaderMap.put("StatusName", "OK");
        responseHeaderMap.put("ContentType", "text/html;charset=utf-8");
        responseHeaderMap.put("ZonedDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.now()));
        StrSubstitutor sub = new StrSubstitutor(responseHeaderMap);
        //commons.lang3 工具包提供的方法，作用是：将给定字符串中的占位符替换为对应的值，详见Test类
        String responseHead = sub.replace(Constants.OKMessage);
        log.info("标准响应头 respHead:{}", responseHead);
        return responseHead;
    }

    /*
        看代码的细节，需要先创建一个 ClassLoader，就是这一句：
            loader = new URLClassLoader(urls);
        这是因为 Servlet 是由应用程序员编写的，我们写服务器的时候不知道路径，所以我们就规定一个目录，
        让程序员将 Servlet 放到这个目录下。为了将这些应用程序类和服务器自身的类分开，我们引入一个
        URLClassLoader 来进行加载。后面涉及到多应用的时候，会再详细介绍 Java 的类加载机制。
    */

    /*
        这个过程与实际的 Servlet 服务器规范大体一致，主要的区别在于单例模式。按照 Servlet 规范，
        一个 Servlet 应当是单对象多线程的（单个实例服务于多个线程）。而我们现在每次都是创建一个
        新的 Servlet 对象，后面需要进一步修正。
    */
}
