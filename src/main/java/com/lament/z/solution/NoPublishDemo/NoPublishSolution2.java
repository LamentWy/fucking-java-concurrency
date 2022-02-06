package com.lament.z.solution.NoPublishDemo;

/**
 * 解决方案2：将 stop 属性视为共享资源，做阻塞同步处理 <br/>
 * PS: 仅对于 NoPublishDemo 而言，我们只需要为 stop 属性提供可见性即可，其实并不需要完整的实现同步
 */
public class NoPublishSolution2 {
    private boolean stop = false;

    synchronized boolean isStop() {
        return stop;
    }

    synchronized void setStop(boolean stop) {
        this.stop = stop;
    }

    public static void main(String[] args) throws Exception {

        NoPublishSolution2 demo = new NoPublishSolution2();

        Thread thread = new Thread(demo.getConcurrencyCheckTask());
        thread.start();

        Thread.sleep(1000);
        System.out.println("Set stop to true in main!");
        demo.setStop(true);
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
            while (!isStop()) {
            }
            System.out.println("ConcurrencyCheckTask stopped!");
        }
    }
}
