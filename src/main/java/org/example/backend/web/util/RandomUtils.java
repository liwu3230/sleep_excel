package org.example.backend.web.util;

import java.util.Random;
public class RandomUtils {

    private static final char[] codes = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    public static String randomStr(int len) {
        StringBuilder result = new StringBuilder(len);
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            result.append(codes[random.nextInt(codes.length)]);
        }
        return result.toString();
    }

}
