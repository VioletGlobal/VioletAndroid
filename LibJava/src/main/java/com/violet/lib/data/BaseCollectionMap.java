package com.violet.lib.data;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Created by kan212 on 2018/4/25.
 * 集合
 */

public class BaseCollectionMap {

    /**
     * 存储及随机访问一连串对象”的做法，array是最有效率的一种
     */
    Array array;
    String[] strings;


    /**
     * Collection类型者，每个位置只有一个元素。
     * Map类型者，持有 key-value pair，像个小型数据库。
     */
    private void differCollectOrMap() {
        Collection collection;
        Map map;

    }

    private void Collections() {
        //将以特定次序存储元素。所以取出来的顺序可能和放入顺序不同。
        ArrayList arrayList = arrayList();


        LinkedList linkedList = linkedList();
        Vector vector = vector();
        Stack stack = stack();
        // 不能含有重复的元素
        HashSet hashSet;
        TreeSet treeSet;
    }


    private void Maps() {
        HashMap hashMap = hashMap();
        Hashtable hashTable = hashTable();
        TreeMap treeMap;
    }


    /**
     * ArrayList实现了可变大小的数组。它允许所有元素，包括null。ArrayList没有同步。
     * size，isEmpty，get，set方法运行时间为常数。但是add方法开销为分摊的常数，添加n个元素需要O(n)的时间。
     * 其他的方法运行时间为线性。
     * <p>
     * 每个ArrayList实例都有一个容量（Capacity），即用于存储元素的数组的大小。
     * 这个容量可随着不断添加新元素而自动增加，但是增长算法并没有定义。当需要插入大量元素时，
     * 在插入前可以调用ensureCapacity方法来增加ArrayList的容量以提高插入效率。
     * <p>
     * 非同步的
     */
    private ArrayList arrayList() {
        return new ArrayList();
    }

    /**
     * LinkedList实现了List接口，允许null元素。此外LinkedList提供额外的get，remove，insert方法在
     * LinkedList的首部或尾部。这些操作使LinkedList可被用作堆栈（stack），队列（queue）或双向队列（deque）。
     * 　　注意LinkedList没有同步方法。如果多个线程同时访问一个List，则必须自己实现访问同步。
     * 一种解决方法是在创建List时构造一个同步的List：
     *
     * @return
     */
    private LinkedList linkedList() {
        Collections.synchronizedList(new LinkedList());
        return new LinkedList();
    }

    /**
     * Vector非常类似ArrayList，但是Vector是同步的。由Vector创建的Iterator，虽然和ArrayList创建的Iterator是同一接口
     * ，但是，因为Vector是同步的，当一个Iterator被创建而且正在被使用，另一个线程改变了Vector的状态
     * （例如，添加或删除了一些元素），这时调用Iterator的方法时将抛出ConcurrentModificationException，
     * 因此必须捕获该异常
     *
     * @return
     */
    private Vector vector() {
        return new Vector<>();
    }

    /**
     * Stack继承自Vector，实现一个后进先出的堆栈。Stack提供5个额外的方法使得Vector得以被当作堆栈使用。
     * 基本的push和pop方法，还有peek方法得到栈顶的元素，empty方法测试堆栈是否为空，search方法检测一个元素在堆栈中的位置。
     * Stack刚创建后是空栈
     *
     * @return
     */
    private Stack stack() {
        return new Stack();
    }

    /**
     * Hashtable继承Map接口，实现一个key-value映射的哈希表。任何非空（non-null）的对象都可作为key或者value。
     * 添加数据使用put(key, value)，取出数据使用get(key)，这两个基本操作的时间开销为常数。
     * Hashtable通过initial capacity和load factor两个参数调整性能。通常缺省的load factor 0.75较好地实现了时间和空间的均衡。
     * 增大load factor可以节省空间但相应的查找时间将增大，这会影响像get和put这样的操作。
     * <p>
     * Hashtable是同步的。
     *
     * @return
     */
    private Hashtable hashTable() {
        return new Hashtable();
    }

    /**
     * HashMap和Hashtable类似，不同之处在于HashMap是非同步的，并且允许null，即null value和null key。
     * 但是将HashMap视为Collection时（values()方法可返回Collection），其迭代子操作时间开销和HashMap的容量成比例。
     * 因此，如果迭代操作的性能相当重要的话，不要将HashMap的初始化容量设得过高，或者load factor过低
     *
     * @return
     */
    private HashMap hashMap() {
        return new HashMap();
    }


}
