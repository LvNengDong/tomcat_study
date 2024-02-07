package server;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * @Author lnd
 * @Description static
 * @Date 2024/1/17 15:43
 */
@Slf4j
public class StaticResourceProcessor {

    /**
     * 返回静态资源数据
     * 处理过程很简单，先将响应头写入输出流，然后从文件中读取内容写入输出流
     * */
    public void process(Request request, Response response) throws IOException {
        byte[] bytes = new byte[Constants.BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            OutputStream outputStream = response.getOutputStream();
            File file = new File(Constants.WEB_ROOT, request.getUri());
            if (file.exists()) { // 正常返回
                // 组装响应头
                String head = composeResponseHead(file);
                // 先把响应头写入输出流
                outputStream.write(head.getBytes(StandardCharsets.UTF_8));

                // 读取服务器上的静态资源，写入输出流
                fis = new FileInputStream(file);
                int ch = fis.read(bytes, 0, Constants.BUFFER_SIZE);
                while (ch != -1) {
                    outputStream.write(bytes, 0, Constants.BUFFER_SIZE);
                    ch = fis.read(bytes, 0, Constants.BUFFER_SIZE);
                }
                outputStream.flush();
            } else { // 异常返回
                outputStream.write(Constants.fileNotFoundMessage.getBytes());
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

    private String composeResponseHead(File file) {
        long fileLength = file.length();
        HashMap<String, Object> responseHeaderMap = Maps.newHashMap();
        responseHeaderMap.put("StatusCode", "200");
        responseHeaderMap.put("StatusName", "OK");
        responseHeaderMap.put("ContentType", "text/html;charset=utf-8");
        responseHeaderMap.put("ContentLength", fileLength);
        responseHeaderMap.put("ZonedDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.now()));
        StrSubstitutor sub = new StrSubstitutor(responseHeaderMap);
         //commons.lang3 工具包提供的方法，作用是：将给定字符串中的占位符替换为对应的值，详见Test类
        String responseHead = sub.replace(Constants.OKMessage);
        log.info("标准响应头 respHead:{}", responseHead);
        return responseHead;
    }
}
