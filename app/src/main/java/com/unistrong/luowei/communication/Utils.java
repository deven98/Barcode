package com.unistrong.luowei.communication;

public class Utils {
    public static String getHexString(byte[] data, int length) {
        StringBuilder sb = new StringBuilder(length * 2);
        char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (int i = 0; i < length; i++) {
            int value = data[i] & 255;
            sb.append(HEX[value / 16]).append(HEX[value % 16]);
        }
        return sb.toString();
    }
}
