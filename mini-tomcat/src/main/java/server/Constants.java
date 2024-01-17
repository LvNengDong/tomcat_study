package server;

import java.io.File;

/**
 * @Author lnd
 * @Description 分支重命名
 * @Date 2024/1/10 22:54
 */
public class Constants {
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "mini-tomcat/src/main/webroot";
    public static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        String currentDir = System.getProperty("user.dir");
        System.out.println("当前工作目录：" + currentDir);
        System.out.println(WEB_ROOT);
    }
}
