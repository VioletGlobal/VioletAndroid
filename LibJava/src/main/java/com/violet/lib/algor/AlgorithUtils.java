package com.violet.lib.algor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kan212 on 2018/5/15.
 */

public class AlgorithUtils {

    public static void Symmetric(String str) {
        Symmetric1(str);
        Symmetric2(str);
        Symmetric3(str);
    }

    /**
     * 看不懂
     * @param str
     */
    private static void Symmetric3(String str) {
        int maxlenth = 1;
        int lenth = str.length();
        int[] record = new int[lenth];
        int i = 1;
        for (; i < lenth; i++) {
            int max = 1;
            if ((i - record[i - 1]) >= 0 && str.indexOf(i) == str.indexOf(i - record[i - 1] - 1)) {
                max = max > (record[i - 1] + 2) ? max : (record[i - 1] + 2);
            }
            int k = 1;
            while (str.indexOf(i) == str.indexOf(i - k)) {
                k++;
            }
            max = max > k ? max : k;
            record[i] = max;
            if (record[i] > maxlenth) maxlenth = record[i];
        }
    }

    private static String Symmetric2(String str) {
        String revertStr = new StringBuilder(str).reverse().toString();
        List<String> revertList = new ArrayList<>();
        int max = 0;
        int start, end;
        for (int i = 0; i < str.length(); i++) {
            start = i;
            for (end = str.length(); ; end--) {
                if (start >= end) break;
                String buffer = str.substring(start, end);
                int temp = end - start;
                if (temp > max && revertStr.contains(buffer)) {
                    revertList.add(buffer);
                    max = temp;
                }
            }
        }
        int index = revertList.size() - 1;
        return revertList.get(index);
    }

    private static String Symmetric1(String str) {
        String subStr = "";
        for (int i = str.length(); i >= 2; i--) {
            for (int j = 0; j < i - 1; j++) {
                String temp = str.substring(i, j);
                if (duichen(temp) && subStr.length() < temp.length()) {
                    subStr = temp;
                }
            }
        }
        return subStr;
    }

    private static boolean duichen(String temp) {
        char[] chars = temp.toCharArray();
        int min = 0;
        int max = chars.length - 1;
        while (min < max) {
            if (chars[min++] != chars[max--]) {
                return false;
            }
        }
        return true;
    }


    public static int getPalindromeLength(String str) {
        StringBuilder newStr = new StringBuilder();
        //获取新的字符串
        newStr.append("#");
        for (int i = 0; i < str.length(); i++) {
            newStr.append(str.charAt(i));
            newStr.append("#");
        }

        int[] rad = new int[newStr.length()];
        int right = -1;     //右边界
        int id = -1;        //中心坐标
        for (int i = 0; i < newStr.length(); i++) {
            int r = 1;
            //确定一个最小半径
            if (i <= right) {
                r = Math.min(rad[id] - i + id, rad[2 * id - i]);
            }
            //寻找更大半径
            //三个条件 1:中心-半径>=0 2:中心+半径<字符串长度(中心+半径<=length-1) 3:字符串关于中心对称点相同
            //符合条件的话，半径自加
            while (i - r >= 0 && i + r < newStr.length() && newStr.charAt(i - r) == newStr.charAt(i + r)) {
                r++;
            }
            //更新边界 和 中心坐标
            if (i + r - 1 > right) {
                right = i + r - 1;
                id = i;
            }
            rad[i] = r;
        }
        int maxLength = 0;
        for (int r : rad) {
            if (r > maxLength) {
                maxLength = r;
            }
        }
        return maxLength - 1;
    }

}
