package com.lament.z.solution.SymmetricLockDeadlockDemo;

/**
 * 俩线程互相等对方的锁，然后抱死。
 * Demo 的例子这种情况其实比较容易发现，一般也不这么写代码。下面的解决方案只是调整了加锁顺序。
 * 比较隐蔽的死锁是协作对象之间的死锁，比如 TODO JAVA并发编程实战 P174 中的出租车+调度室的例子。
 */
public class SymmetricLockDeadlockSolution {
    static final Object lock1 = new Object();
    static final Object lock2 = new Object();

    public static void main(String[] args) throws Exception {
        Thread thread1 = new Thread(new ConcurrencyCheckTask1());
        thread1.start();
        Thread thread2 = new Thread(new ConcurrencyCheckTask2());
        thread2.start();
    }


    private static class ConcurrencyCheckTask1 implements Runnable {
        @Override
        @SuppressWarnings("InfiniteLoopStatement")
        public void run() {
            System.out.println("ConcurrencyCheckTask1 started!");
            while (true) {
                synchronized (lock2) {
                    synchronized (lock1) {
                        System.out.println("Hello1");
                        break;
                    }
                }
            }
        }
    }

    private static class ConcurrencyCheckTask2 implements Runnable {
        @Override
        @SuppressWarnings("InfiniteLoopStatement")
        public void run() {
            System.out.println("ConcurrencyCheckTask2 started!");
            while (true) {
                synchronized (lock2) {
                    synchronized (lock1) {
                        System.out.println("Hello2");
                        break;
                    }
                }
            }
        }
    }
}
