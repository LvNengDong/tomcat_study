package org.example;

import java.util.HashMap;

/**
 * @Author lnd
 * @Description
 * @Date 2024/3/25 10:45
 */
public class Person {
    public String name;

    public String getName() {
        return name;
    }

    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        String s = map.get("aa");
        System.out.println(s);
        System.out.println("1");
    }
}
