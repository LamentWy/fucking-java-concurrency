package com.lament.z.solution.InvalidCombinationStateDemo;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 版本
 */
@SuppressWarnings("InfiniteLoopStatement")
public class InvalidCombinationStateSolutionLight {
    public static void main(String[] args) {
        CombinationStatTask task = new CombinationStatTask();
        Thread thread = new Thread(task);
        thread.start();

        Random random = new Random();
        while (true) {
            int rand = random.nextInt(1000);
            task.changeState(rand);
        }
    }

    private static class CombinationStatTask implements Runnable {
        // For combined state, adding volatile does not solve the problem
        volatile int state1;
        volatile int state2;

        Lock lock = new ReentrantLock();

        @Override
        public void run() {
            int c = 0;
            for (long i = 0; ; i++) {
                c = checkState(c, i);
            }
        }

        private int checkState(int c, long i) {
            lock.lock();
            try {
                int i1 = state1;
                int i2 = state2;
                if (i1 * 2 != i2) {
                    c++;
                    System.err.printf("Fuck! Got invalid CombinationStat!! check time=%s, happen time=%s(%s%%), count value=%s|%s\n",
                            i + 1, c, (float) c / (i + 1) * 100, i1, i2);
                  }
//                else {
//                    // if remove blew output,
//                    // the probability of invalid combination on my dev machine goes from ~5% to ~0.1%
//                    System.out.printf("Emm... %s|%s\n", i1, i2);
//                }

            }finally {
               lock.unlock();
            }
            return c;
        }

        void changeState(int rand) {
            lock.lock();
            try {
                this.state1 = rand;
                this.state2 = rand * 2;
            }finally {
                lock.unlock();
            }

        }
    }

}
