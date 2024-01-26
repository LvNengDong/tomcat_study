import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author lnd
 * @Description
 * @Date 2024/1/18 22:05
 */

public class CommonsLangTest {

    //public static void main(String[] args) {
    //    CommonsLangTest test = new CommonsLangTest();
    //    test.testStrReplace();
    //}

    /*
    * org.apache.commons.lang3.text.StrSubstitutor#StrSubstitutor(java.util.Map<java.lang.String,V>)
    * */
    public void testStrReplace() {
        String template = "Hello, ${name}! Today is ${dayOfWeek}.";

        // 设置替换的值
        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("name", "张飞");
        valueMap.put("dayOfWeek", "周天");
        // 执行替换
        StrSubstitutor substitutor = new StrSubstitutor(valueMap);
        String replace = substitutor.replace(template);

        System.out.println(replace);

    }


    public static void main(String[] args) {
        String dada = StringUtils.join("121", "dada");
        System.out.println(dada);
    }


}
