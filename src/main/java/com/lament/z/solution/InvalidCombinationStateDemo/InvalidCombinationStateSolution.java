package com.lament.z.solution.InvalidCombinationStateDemo;

import java.util.Random;

/**
 * Demo 中设定的是 CombinationStatTask 拥有两个状态 state1 和 state2，并且二者必须满足 state1 * 2 = state2 的关系。
 * 由于两个属性的改变需要同步变化，所以这个 Demo 中加不加 volatile 不重要，我们需要保证组合状态本身的原子性，
 *
 * 解决方案也很简单：只要保证修改状态和检查状态的代码同步即可。
 *
 */
@SuppressWarnings("InfiniteLoopStatement")
public class InvalidCombinationStateSolution {
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
        int state1;
        int state2;

        public synchronized void changeState(int newState){
            this.state1 = newState;
            this.state2 = newState * 2;
        }

        @Override
        public void run() {
            int c = 0;
            for (long i = 0; ; i++) {
                c = checkStates(c, i);
            }
        }

        private synchronized int checkStates(int c, long i) {
            int i1 = state1;
            int i2 = state2;
            if (i1 * 2 != i2) {
                c++;
                System.err.printf("Fuck! Got invalid CombinationStat!! check time=%s, happen time=%s(%s%%), count value=%s|%s\n",
                        i + 1, c, (float) c / (i + 1) * 100, i1, i2);
            } else {
                // if remove blew output,
                // the probability of invalid combination on my dev machine goes from ~5% to ~0.1%
                System.out.printf("Emm... %s|%s\n", i1, i2);
            }
            return c;
        }
    }

}
