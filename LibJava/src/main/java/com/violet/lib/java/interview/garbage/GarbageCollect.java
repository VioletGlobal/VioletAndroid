package com.violet.lib.java.interview.garbage;

/**
 * Created by kan212 on 2018/4/23.
 * Java内存回收
 * <p>
 * 引用计数法(Reference Counting Collector)
 * 该算法使用引用计数器来区分存活对象和不再使用的对象。一般来说，堆中的每个对象对应一个引用计数器。
 * 当每一次创建一个对象并赋给一个变量时，引用计数器置为1。当对象被赋给任意变量时，
 * 引用计数器每次加1当对象出了作用域后(该对象丢弃不再使用)，引用计数器减1，一旦引用计数器为0，
 * 对象就满足了垃圾收集的条件。
 * tracing算法(Tracing Collector)
 * tracing算法是为了解决引用计数法的问题而提出，它使用了根集的概念。基于tracing算法的垃圾收集器从根集开始扫描，
 * 识别出哪些对象可达，哪些对象不可达，并用某种方式标记可达对象，例如对每个可达对象设置一个或多个位。
 * 在扫描识别过程中，基于tracing算法的垃圾收集也称为标记和清除(mark-and-sweep)垃圾收集器.
 */

public class GarbageCollect {


    /**
     * 1、当应用程序空闲时,即没有应用线程在运行时,GC会被调用。因为GC在优先级最低的线程中进行,
     * 所以当应用忙时,GC线程就不会被调用,但以下条件除外。
     * 2、Java堆内存不足时,GC会被调用。当应用线程在运行,并在运行过程中创建新对象,若这时内存空间不足,
     * JVM就会强制地调用GC线程,以便回收内存用于新的分配。若GC一次之后仍不能满足内存分配的要求,
     * JVM会再进行两次GC作进一步的尝试,若仍无法满足要求,则 JVM将报“out of memory”的错误,Java应用将停止
     */
    public void systemStartGc() {
        gc();
    }

    /**
     * 调用System.gc()也仅仅是一个请求(建议)。JVM接受这个消息后，并不是立即做垃圾回收，
     * 而只是对几个垃圾回收算法做了加权，使垃圾回收操作容易发生，或提早发生，或回收较多而已。
     */
    public void gc() {
        System.gc();
        System.runFinalization();
    }

    /**
     * 使用finalize()，是存在着垃圾回收器不能处理的特殊情况
     * 1、由于在分配内存的时候可能采用了类似 C语言的做法，而非JAVA的通常new做法
     * 比如native method调用了C/C++方法malloc()函数系列来分配存储空间，但是除非调用free()函数，
     * 否则这些内存空间将不会得到释放，那么这个时候就可能造成内存泄漏。但是由于free()方法是在C/C++中的函数，
     * 所以finalize()中可以用本地方法来调用它。以释放这些“特殊”的内存空间
     * 2、打开的文件资源，这些资源不属于垃圾回收器的回收范围
     * <p>
     * 只有到下一次再进行垃圾回收动作的时候，才会真正释放这个对象所占用的内存空间。
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public static void garbage(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: /n" + "java Garbage before/n or:/n" + "java Garbage after");
            return;
        }
        while (!GarbageItem.f) {
            new GarbageItem();
            new String("To take up space");
        }
        System.out.println("After all Chairs have been created:/n" + "total created = " + GarbageItem.created +
                "， total finalized = " + GarbageItem.finalized);
        if (args[0].equals("before")) {
            System.out.println("gc():");
            System.gc();
            System.out.println("runFinalization():");
            System.runFinalization();
        }
        System.out.println("bye!");
        if (args[0].equals("after"))
            System.runFinalizersOnExit(true);
    }

}

