package com.violet.lib.algor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Created by kan212 on 2018/7/10.
 * 田忌赛马问题
 * #link https://www.cnblogs.com/airycode/p/5192028.html
 */

public class ScriptsScribes {


    public static void main(String args[]) {
        int n, m;
        List<Integer> list1 = new ArrayList<Integer>();
        List<Integer> list2 = new ArrayList<Integer>();
        Scanner in = new Scanner(System.in);


        while (true) {
            n = in.nextInt();
            if (n == 0) break;

            //输入数据
            for (int i = 0; i < n; ++i) {
                list1.add(in.nextInt());
            }
            for (int i = 0; i < n; ++i) {
                list2.add(in.nextInt());
            }
            //处理数据
            Collections.sort(list1);
            Collections.sort(list2);

            int i = 0, j = 0, x = n - 1, y = n - 1, count = 0;
            boolean bLast = true;

            while (bLast) {
                //是否是最后一匹马
                if (x == i)
                    bLast = false;

                if (list1.get(x) > list2.get(y)) {//如果田忌当前最好的马可以胜齐王最好的马，那么比一场
                    x--;
                    y--;
                    count += 200;
                } else if (list1.get(i) > list2.get(j)) {//如果田忌当前最差的马可以胜齐王最差的马，那么比一场
                    i++;
                    j++;
                    count += 200;
                } else {//否则，让田忌最差的马和齐王最好的好比一场
                    if (list1.get(i) < list2.get(y))
                        count -= 200;
                    i++;
                    y--;
                }
            }
            System.out.println(count);
            list1.clear();
            list2.clear();
        }

    }
}
