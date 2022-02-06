package com.lament.z.solution.InconsistentReadDemo;

/**
 *  主线程执行 count++ ，thread 线程执行顺序读。
 *  线程不安全的原因也很简单，main 线程自增时在不断的把新值写会主内存，
 *  thread 线程不停的从主内存读，C1读完，C2开始读之间一但刷新新值，自然读到不一样的数据。
 * 解决方案：对读写都做同步处理，或者读写锁
 */
public class InconsistentReadSolution {
    int count = 1;

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        InconsistentReadSolution demo = new InconsistentReadSolution();

        Thread thread = new Thread(demo.getConcurrencyCheckTask());
        thread.start();

        while (true) {
            synchronized (demo){
                demo.count++;
            }

        }
    }

    private synchronized int checkConsecutive(int c, int i) {
        int c1 = count;
        int c2 = count;
        if (c1 != c2) {
            c++;
            // On my dev machine,
            // a batch of inconsistent reads can be observed when the process starts
            System.err.printf("Fuck! Got inconsistent read!! check time=%s, happen time=%s(%s%%), 1=%s, 2=%s\n",
                    i + 1, c, (float) c / (i + 1) * 100, c1, c2);
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
