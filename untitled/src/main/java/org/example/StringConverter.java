package org.example;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.List;

/**
 * @Author lnd
 * @Description
 * @Date 2024/3/11 15:35
 */
public class StringConverter {

    public static void main(String[] args) {
        String filePath = "/Users/workspace/tomcat-study/untitled/src/main/java/org/example/input.txt"; // 替换为您的文本文件路径
        String outFilePath = "/Users/workspace/tomcat-study/untitled/src/main/java/org/example/output.txt"; // 替换为您的文本文件路径
        String url = "http://hraven.corp.qunar.com/test/mapper?type=2&productId=";
        CharSource charSource = Files.asCharSource(new File(filePath), Charsets.UTF_8);
        //CharSource charSourceOut = Files.asCharSource(new File(outFilePath), Charsets.UTF_8);
        //count(charSourceOut);
        //CharSink charSink = Files.asCharSink(new File(outFilePath), Charsets.UTF_8);
        try {
            for (String line : charSource.readLines()) {
                String orderNo = CharMatcher.whitespace().removeFrom(line);
                CloseableHttpClient httpClient = HttpClients.createDefault();
                sendGetRequest(httpClient, url, orderNo);
                httpClient.close();
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("orderNo:" + orderNo);
            }
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    public static void count(CharSource charSourceOut) {
        try {
            String read = charSourceOut.read();
            List<String> toList = Splitter.on(",").splitToList(read);
            int size = toList.size();
            System.out.println(size);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static void sendGetRequest(CloseableHttpClient httpClient, String url, String params) throws IOException {
        HttpGet httpGet = new HttpGet(url + params);
        System.out.println(url + params);
        httpClient.execute(httpGet);
    }
}
