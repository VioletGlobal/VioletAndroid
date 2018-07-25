package com.violet.lib.algor;

import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by kan212 on 2018/6/13.
 * #link https://blog.csdn.net/xsf50717/article/details/47375437
 * Java中链表的基本操作
 */

public class BaseLinkList extends LinkedList {

    /**
     * 定义链表
     */
    public class Node {
        int value;
        Node next;

        public Node(int n) {
            this.value = n;
            this.next = null;
        }

        public void addNode(Node node) {
            if (next == null) {
                next = node;
            } else {
                next.addNode(node);
            }
        }
    }

    public Node build() {
        Node first = new Node(124);
        for (int i = 0; i < 9; i++) {
            first.addNode(new Node(i));
        }
        return first;
    }

    /**
     * 获取链表的数量
     *
     * @param head
     * @return
     */
    public int getListLen(Node head) {
        int len = 0;
        while (head != null) {
            len++;
            head = head.next;
        }
        return len;
    }

    /**
     * 采用双指针，主要是4行代码，其中2,3俩行完成指针反转，1,4主要是保持head往下指
     *
     * @param head
     * @return
     */
    public Node reverseList(Node head) {
        // 安全性检查
        if (head == null || head.next == null)
            return head;
        Node pre = null;
        Node temp = null;
        while (head != null) {
            // 以下1234均指以下四行代码
            temp = head.next;// 与第4行对应完成头结点移动
            head.next = pre;// 与第3行对应完成反转
            pre = head;// 与第2行对应完成反转
            head = temp;// 与第1行对应完成头结点移动
        }
        return pre;
    }

    /**
     * 1 - 2 - 3 - 4 - 5 - 6
     * 2 - 1 - 4 - 3 - 6 - 5
     * @param head
     * @return
     */
    public static Node reverseBy2Rec(Node head){
        if (head == null || head.next == null)
            return head;
        Node pre = null;
        Node temp = null;

        while (null != head){
            temp = head.next;
            head.next = pre;
            pre = head;
            head = temp;
        }
        return  pre;
    }

    /**
     * 反转链表-递归
     *
     * @param head
     * @return
     */
    public static Node reverseListRec(Node head) {
        if (head == null || head.next == null) {
            return head;
        }
        Node reHead = reverseListRec(head.next);
        head.next.next = head;
        head.next = null;
        return reHead;
    }

    public Node reverseKGroup(Node head, int k) {
        Node curr = head;
        int count = 0;
        while (curr != null && count != k) { // find the k+1 node
            curr = curr.next;
            count++;
        }
        if (count == k) { // if k+1 node is found
            curr = reverseKGroup(curr, k); // reverse list with k+1 node as head
            // head - head-pointer to direct part,
            // curr - head-pointer to reversed part;
            while (count-- > 0) { // reverse current k-group:
                Node tmp = head.next; // tmp - next head in direct part
                head.next = curr; // preappending "direct" head to the reversed list
                curr = head; // move head of reversed part to a new node
                head = tmp; // move "direct" head to the next node in direct part
            }
            head = curr;
        }
        return head;

    }

    /**
     * 查找链表倒数第K个节点
     * @param head
     * @param k
     * @return
     */
    public Node reKNode(Node head, int k) {
        if (head == null)
            return head;
        int len = getListLen(head);
        if (k > len)
            return null;
        Node targetK = head;
        Node nextK = head;
        // 先走到K个位置
        for (int i = 0; i < k; i++) {
            nextK = nextK.next;
        }
        // 再和头结点一起走，nextk走到结尾，此时targetk为倒数第K个节点
        while (nextK != null) {
            nextK = nextK.next;
            targetK = targetK.next;
        }
        return targetK;
    }

    /**
     * 快慢指针，不多解释
     * @param head
     * @return
     */
    public Node getMid(Node head) {
        // 类似的快慢指针法
        // 安全性检查
        if (head == null || head.next == null)
            return head;
        Node target = head;
        Node temp = head;
        while (temp != null && temp.next != null) {
            target = target.next;
            temp = temp.next.next;
        }
        return target;
    }

    /**
     * 判断一个单链表中是否有环,快慢指针
     * @param head
     * @return
     */
    public boolean hasCycle(Node head) {
        boolean flag = false;
        Node p1 = head;
        Node p2 = head;
        while (p1 != null && p2 != null) {
            p1 = p1.next;
            p2 = p2.next.next;
            if (p2 == p1) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 从尾到头打印单链表（栈）
     * @param head
     */
    public static void reList2(Node head) {
        Stack<Node> s = new Stack<Node>();
        while (head != null) {
            s.push(head);
            head = head.next;
        }
        while (!s.isEmpty()) {
            System.out.println(s.pop().value);
        }
    }

    /**
     * 从头到尾打印单链表
     * @param head
     */
    public static void reList(Node head) {
        while (head != null) {
            System.out.println(head.value);
            head = head.next;
        }
    }

    /**
     * 由小到大合并俩个有序的单链表(循环)
     * @param head1
     * @param head2
     * @return
     */
    public Node mergeSort1(Node head1, Node head2) {
        // 安全性检查
        if (head1 == null)
            return head2;
        if (head2 == null)
            return head1;
        // 新建合并节点
        Node target = null;
        // 确定第一个元素的节点
        if (head1.value > head2.value) {
            target = head2;
            head2 = head2.next;
        } else {
            target = head1;
            head1 = head1.next;
        }
        target.next = null;
        // 开始合并
        Node mergeHead = target;
        while (head1 != null && head2 != null) {
            // 当两个链表都不为空
            if (head1.value > head2.value) {
                target.next = head2;
                head2 = head2.next;
            } else {
                target.next = head1;
                head1 = head1.next;
            }
            target = target.next;
            target.next = null;
        }
        if (head1 == null)
            target.next = head2;
        else
            target.next = head1;
        return mergeHead;

    }

    /**
     * 由小到大合并俩个有序的单链表(递归)
     * @param head1
     * @param head2
     * @return
     */
    public Node mergeSort2(Node head1, Node head2) {
        if (head1 == null)
            return head2;
        if (head2 == null)
            return head1;
        if (head1.value > head2.value) {
            head2.next = mergeSort2(head2.next, head1);
            return head2;
        } else {
            head1.next = mergeSort2(head1.next, head2);
            return head1;
        }
    }

}
