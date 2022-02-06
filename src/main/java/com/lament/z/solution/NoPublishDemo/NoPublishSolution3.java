package com.lament.z.solution.NoPublishDemo;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 解决方案3：使用 AtomicBoolean。<br/>
 * PS: 仅对于 NoPublishDemo 而言，我们不需要 AtomicBoolean 的提供的原子性，只是利用了 private volatile int value; 提供的可见性。
 */
public class NoPublishSolution3 {
    AtomicBoolean stop = new AtomicBoolean(false);

    public static void main(String[] args) throws Exception {
        // LoadMaker.makeLoad();

        NoPublishSolution3 demo = new NoPublishSolution3();

        Thread thread = new Thread(demo.getConcurrencyCheckTask());
        thread.start();

        Thread.sleep(1000);
        System.out.println("Set stop to true in main!");
        demo.stop.set(true);
        // 用CAS进行 set 也可以，但是没必要。 一方面我们只解决可见性就可以了，二来没有别的线程竞争
        // demo.stop.compareAndSet(false,true);
        System.out.println("Exit main.");
    }

    ConcurrencyCheckTask getConcurrencyCheckTask() {
        return new ConcurrencyCheckTask();
    }

    private class ConcurrencyCheckTask implements Runnable {
        @Override
        @SuppressWarnings({"WhileLoopSpinsOnField", "StatementWithEmptyBody"})
        public void run() {
            System.out.println("ConcurrencyCheckTask started!");
            // If the value of stop is visible in the main thread, the loop will exit.
            // On my dev machine, the loop almost never exits!
            // Simple and safe solution:
            //   add volatile to the `stop` field.
            while (!stop.get()) {
            }
            System.out.println("ConcurrencyCheckTask stopped!");
        }
    }
}
