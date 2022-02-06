package com.lament.z.solution.InvalidLongDemo;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 这 Demo 是真的随意，属性又从task里出来了。
 * 这个的原因主要是 long double 这些 64 位长度的数据类型，在32位JVM上就需要两个32位拼接起来。
 * JLS中说了：
 * 对于64位的long和double，如果没有被volatile修饰，那么对其操作可以不是原子的。在操作的时候，可以分成两步，每次对32位操作。
 * 如果使用volatile修饰long和double，那么其读写都是原子操作
 * 对于64位的引用地址的读写，都是原子操作
 * 在实现JVM时，可以自由选择是否把读写long和double作为原子操作
 * 推荐JVM实现为原子操作
 *
 * 所以解决方案可选： 64位支持原子操作的JVM、加 volatile 修饰、同步块包裹
 */
public class InvalidLongSolution {
    long count = 0;

    synchronized long getCount() {
        return count;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        // LoadMaker.makeLoad();


        InvalidLongSolution demo = new InvalidLongSolution();

        Thread thread = new Thread(demo.getConcurrencyCheckTask());
        thread.start();

        for (int i = 0; ; i++) {
            @SuppressWarnings("UnnecessaryLocalVariable")
            final long l = i;
            demo.count = getNew(l);
        }
    }

    private synchronized static long getNew(long l) {
        return l << 32 | l;
    }

    ConcurrencyCheckTask getConcurrencyCheckTask() {
        return new ConcurrencyCheckTask();
    }

    private class ConcurrencyCheckTask implements Runnable {
        @Override
        @SuppressWarnings("InfiniteLoopStatement")
        public void run() {
            int c = 0;
            for (int i = 0; ; i++) {
                c = check(c, i);
            }
        }
    }

    private synchronized int check(int c, int i) {
        long l = getCount();
        long high = l >>> 32;
        long low = l & 0xFFFFFFFFL;
        if (high != low) {
            c++;
            System.err.printf("Fuck! Got invalid long!! check time=%s, happen time=%s(%s%%), count value=%s|%s\n",
                    i + 1, c, (float) c / (i + 1) * 100, high, low);
        } else {
            // If remove this output, invalid long is not observed on my dev machine
            // System.out.printf("Emm... %s|%s\n", high, low);
        }
        return c;
    }

}
