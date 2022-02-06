package com.lament.z.solution.WrongCounterDemo;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 并发计数器，这个没啥好说的，最常见的例子。
 * ++counter; 这是典型的组合操作，不具备原子性，保证这个组合操作的原子性就可以了。
 */
public class WrongCounterSolution2 {
    private static final int INC_COUNT = 100000000;

    private volatile int counter = 0;

    Lock lock = new ReentrantLock();

    private void increase() {
        lock.lock();
        try {
            ++counter;
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws Exception {
        WrongCounterSolution2 demo = new WrongCounterSolution2();

        System.out.println("Start task thread!");
        Thread thread1 = new Thread(demo.getConcurrencyCheckTask());
        thread1.start();
        Thread thread2 = new Thread(demo.getConcurrencyCheckTask());
        thread2.start();

        thread1.join();
        thread2.join();

        int actualCounter = demo.counter;
        int expectedCount = INC_COUNT * 2;
        if (actualCounter != expectedCount) {
            // Even if volatile is added to the counter field,
            // On my dev machine, it's almost must occur!
            // Simple and safe solution:
            //   use AtomicInteger
            System.err.printf("Fuck! Got wrong count!! actual %s, expected: %s.", actualCounter, expectedCount);
        } else {
            System.out.println("Wow... Got right count!");
        }
    }

    ConcurrencyCheckTask getConcurrencyCheckTask() {
        return new ConcurrencyCheckTask();
    }

    private class ConcurrencyCheckTask implements Runnable {
        @Override
        @SuppressWarnings("NonAtomicOperationOnVolatileField")
        public void run() {
            for (int i = 0; i < INC_COUNT; ++i) {
                increase();
            }
        }
    }


}
