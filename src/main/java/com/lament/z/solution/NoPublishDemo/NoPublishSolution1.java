package com.lament.z.solution.NoPublishDemo;

/**
 * 解决办法1：使用 volatile 为 stop 提供可见性。 <br/>
 * 对于 NoPublishDemo 而言最简单实用的解决方案。
 * */
public class NoPublishSolution1 {

	volatile boolean stop = false;

	public static void main(String[] args) throws Exception {
		// LoadMaker.makeLoad();

		NoPublishSolution1 demo = new NoPublishSolution1();

		Thread thread = new Thread(demo.getConcurrencyCheckTask());
		thread.start();

		Thread.sleep(1000);
		System.out.println("Set stop to true in main!");
		demo.stop = true;
		System.out.println("Exit main.");
	}

	NoPublishSolution1.ConcurrencyCheckTask getConcurrencyCheckTask() {
		return new NoPublishSolution1.ConcurrencyCheckTask();
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
			while (!stop) {
			}
			System.out.println("ConcurrencyCheckTask stopped!");
		}
	}
}
