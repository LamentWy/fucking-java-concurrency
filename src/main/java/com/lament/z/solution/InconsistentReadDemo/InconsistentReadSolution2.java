package com.lament.z.solution.InconsistentReadDemo;

import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 读写锁
 */
public class InconsistentReadSolution2 {
    volatile int count = 1;

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        InconsistentReadSolution2 demo = new InconsistentReadSolution2();

        Thread thread = new Thread(demo.getConcurrencyCheckTask());
        thread.start();

        while (true) {

            demo.increase(demo);

        }
    }

    private  void increase(InconsistentReadSolution2 demo) {
        lock.writeLock().lock();
        try {
            demo.count++;
        }finally {
            lock.writeLock().unlock();
        }

    }

    private int checkConsecutive(int c, int i) {
        lock.readLock().lock();
        try {
            int c1 = count;
            int c2 = count;
            if (c1 != c2) {
                c++;
                // On my dev machine,
                // a batch of inconsistent reads can be observed when the process starts
                System.err.printf("Fuck! Got inconsistent read!! check time=%s, happen time=%s(%s%%), 1=%s, 2=%s\n",
                        i + 1, c, (float) c / (i + 1) * 100, c1, c2);
            }
        }finally {
            lock.readLock().unlock();
        }

        return c;
    }

    ConcurrencyCheckTask getConcurrencyCheckTask() {
        return new ConcurrencyCheckTask();
    }

    private class ConcurrencyCheckTask implements Runnable {
        @Override
        @SuppressWarnings({"InfiniteLoopStatement", "ConstantConditions"})
        public void run() {
            int c = 0;
            for (int i = 0; ; i++) {
                // 2 consecutive reads in the same thread
                c = checkConsecutive(c, i);
            }
        }
    }


}
