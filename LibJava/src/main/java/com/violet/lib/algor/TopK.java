package com.violet.lib.algor;

import java.util.List;

/**
 * Created by kan212 on 2018/6/13.
 * java经典n个数字获取最大的m个数字的问题
 * #link https://blog.csdn.net/will130/article/details/49635429
 */

public class TopK {


    class TopKMerge {
        /**
         * 已知几个递减有序的m个数组，求这几个数据前k大的数
         * 适合采用Merge的方法,时间复杂度(O(k*m);
         *
         * @param input
         * @param k
         * @return
         */
        public int[] getTopK(List<List<Integer>> input, int k) {
            int index[] = new int[input.size()];//保存每个数组下标扫描的位置;
            int result[] = new int[k];
            for (int i = 0; i < k; i++) {
                int max = Integer.MIN_VALUE;
                int maxIndex = 0;
                for (int j = 0; j < input.size(); j++) {
                    if (index[j] < input.get(j).size()) {
                        if (max < input.get(j).get(index[j])) {
                            max = input.get(j).get(index[j]);
                            maxIndex = j;
                        }
                    }
                }
                if (max == Integer.MIN_VALUE) {
                    return result;
                }
                result[i] = max;
                index[maxIndex] += 1;

            }
            return result;
        }

    }

    class TopKQuickSort {
        /**
         * 利用快速排序的过程来求最小的k个数
         *
         * @param a
         * @param first
         * @param end
         * @return
         */
        int partion(int a[], int first, int end) {
            int i = first;
            int main = a[end];
            for (int j = first; j < end; j++) {
                if (a[j] < main) {
                    int temp = a[j];
                    a[j] = a[i];
                    a[i] = temp;
                    i++;
                }
            }
            a[end] = a[i];
            a[i] = main;
            return i;
        }

        void getTopKMinBySort(int a[], int first, int end, int k) {
            if (first < end) {
                int partionIndex = partion(a, first, end);
                if (partionIndex == k - 1) return;
                else if (partionIndex > k - 1) getTopKMinBySort(a, first, partionIndex - 1, k);
                else getTopKMinBySort(a, partionIndex + 1, end, k);
            }
        }

    }

    /**
     * 求最大K个采用小根堆，而求最小K个采用大根堆。
     * 求最大K个的步奏：
     * 根据数据前K个建立K个节点的小根堆。
     * 在后面的N-K的数据的扫描中，
     * 如果数据大于小根堆的根节点，则根节点的值覆为该数据，并调节节点至小根堆。
     * 如果数据小于或等于小根堆的根节点，小根堆无变化。
     * 求最小K个跟这求最大K个类似。时间复杂度O(nlogK)(n:数据的长度),特别适用于大数据的求Top K。
     */
    class TopKHeap {
        int[] createHeap(int a[], int k) {
            int[] result = new int[k];
            for (int i = 0; i < k; i++) {
                result[i] = a[i];
            }
            for (int i = 1; i < k; i++) {
                int child = i;
                int parent = (i - 1) / 2;
                int temp = a[i];
                while (parent >= 0 && child != 0 && result[parent] > temp) {
                    result[child] = result[parent];
                    child = parent;
                    parent = (parent - 1) / 2;
                }
                result[child] = temp;
            }
            return result;

        }

        void insert(int a[], int value) {
            a[0] = value;
            int parent = 0;

            while (parent < a.length) {
                int lchild = 2 * parent + 1;
                int rchild = 2 * parent + 2;
                int minIndex = parent;
                if (lchild < a.length && a[parent] > a[lchild]) {
                    minIndex = lchild;
                }
                if (rchild < a.length && a[minIndex] > a[rchild]) {
                    minIndex = rchild;
                }
                if (minIndex == parent) {
                    break;
                } else {
                    int temp = a[parent];
                    a[parent] = a[minIndex];
                    a[minIndex] = temp;
                    parent = minIndex;
                }
            }

        }

        int[] getTopKByHeap(int input[], int k) {
            int heap[] = this.createHeap(input, k);
            for (int i = k; i < input.length; i++) {
                if (input[i] > heap[0]) {
                    this.insert(heap, input[i]);
                }


            }
            return heap;

        }
    }
}