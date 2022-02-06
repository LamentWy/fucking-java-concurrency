package com.lament.z.solution.SynchronizationOnMutableFieldDemo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.oldratlee.fucking.concurrency.SynchronizationOnMutableFieldDemo;

/**
 * 感觉这个 Demo 也有点随意...毕竟符合直觉的写法是直接 listeners.add(new Listener());
 * Demo 想表示的是 不要在 "MutableField" 上加锁。 所谓 MutableField 指的其实是引用会发生改变的域。
 * 比如 listeners 是 SyncOnMutableFieldSolution 类的 field，它类型是 CopyOnWriteArrayList。
 * 但是到了 addListener() 方法里面，synchronized (listeners) 这里对 CopyOnWriteArrayList 类型的 listeners 加了锁，
 * 但是 执行到 listeners = results; 时， listeners 的类型很快就变成了 ArrayList ，listeners 的引用发生了改变，
 * 由于 results = new ArrayList<>(listeners); 的存在，之后每一次对 addListener() 的调用都会改变一次 listeners 的引用。
 *
 * 而我们知道 synchronized 锁的实际是引用，也就是在这个 DEMO 中每次 synchronized(listeners) 都锁了个寂寞。
 */
public class SyncOnMutableFieldSolution {
    static final int ADD_COUNT = 10000;

    static class Listener {
        // stub class
    }

//    @SuppressWarnings("SynchronizeOnNonFinalField")
//    public void addListener(SynchronizationOnMutableFieldDemo.Listener listener) {
//        synchronized (listeners) {
//            List<SynchronizationOnMutableFieldDemo.Listener> results = new ArrayList<>(listeners);
//            results.add(listener);
//            listeners = results;
//        }
//    }

    private volatile List<Listener> listeners = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws Exception {
        SyncOnMutableFieldSolution demo = new SyncOnMutableFieldSolution();

        Thread thread1 = new Thread(demo.getConcurrencyCheckTask());
        thread1.start();
        Thread thread2 = new Thread(demo.getConcurrencyCheckTask());
        thread2.start();

        thread1.join();
        thread2.join();

        int actualSize = demo.listeners.size();
        int expectedSize = ADD_COUNT * 2;
        if (actualSize != expectedSize) {
            // On my development machine, it's almost must occur!
            // Simple and safe solution:
            //   final List field and use concurrency-safe List, such as CopyOnWriteArrayList
            System.err.printf("Fuck! Lost update on mutable field! actual %s expected %s.\n", actualSize, expectedSize);
        } else {
            System.out.println("Emm... Got right answer!!");
        }
    }


    ConcurrencyCheckTask getConcurrencyCheckTask() {
        return new ConcurrencyCheckTask();
    }

    private class ConcurrencyCheckTask implements Runnable {
        @Override
        public void run() {
            System.out.println("ConcurrencyCheckTask started!");
            for (int i = 0; i < ADD_COUNT; ++i) {
                listeners.add(new Listener());
              //  addListener(new Listener());
            }
            System.out.println("ConcurrencyCheckTask stopped!");
        }
    }
}
