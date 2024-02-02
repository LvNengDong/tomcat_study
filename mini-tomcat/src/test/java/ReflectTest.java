import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @Author lnd
 * @Description 测试反射部分的知识
 * @Date 2024/1/21 21:38
 */
public class ReflectTest {

    public static void main(String[] args) throws Exception {
        ReflectTest test = new ReflectTest();
        test.test01();
    }

    /**
     * 使用URLClassLoader加载本地的 .class 文件
     * */
    public void test01() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // 1、创建URLClassLoader对象，并指定 .class 文件所在文件夹的路径（这里的路径可以是互联网的任一资源，file指的是协议，表示本地文件）
        URLClassLoader loader = new URLClassLoader(new URL[]{new URL("file:/Users/workspace/tomcat-study/mini-tomcat/src/webroot/test/")});
        // 2、加载类
        Class<?> clazz = loader.loadClass("HelloServlet");
        Object instance = clazz.newInstance();
        System.out.println(instance);
    }
}
