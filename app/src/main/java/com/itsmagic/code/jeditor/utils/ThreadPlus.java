package com.itsmagic.code.jeditor.utils;

import androidx.annotation.NonNull;
import java.lang.Thread;

/** ThreadPlus is a layer on top of {@link java.lang.Thread}
 * for easy use.
 *
 * @author ThatMG393
 */
public class ThreadPlus implements Runnable {
	// private final Logger LOG = new Logger("ESM/" + this.getClass().getName());
	
	private volatile boolean isRunning;
	private volatile boolean isDead;
	
	private boolean loopThread = true;
	
	private final Thread thread;
	private final Runnable runnable;
	
	public ThreadPlus(@NonNull Runnable runnable) {
		this.runnable = runnable;
		this.thread = new Thread(this);
		
		thread.setDaemon(true);
	}
	
	public ThreadPlus(@NonNull Runnable runnable, boolean loopThread) {
		this.runnable = runnable;
		this.thread = new Thread(this);
		this.loopThread = loopThread;
		
		thread.setDaemon(true);
	}

	@Override
	public void run() {
		while (!thread.isInterrupted()) {
			if (isRunning() && !loopThread) {
				runnable.run();
				stop();
			} else if (isRunning() && loopThread) {
				runnable.run();
			}
		}
		isDead = true;
		// LOG.d("Shutting down -> " + thread);
	}
	
	/** Starts the current {@link java.lang.Thread}
	 */
	public synchronized void start() {
		if (isRunning()) return;
		
		try {
			thread.start();
			isRunning = true;
		} catch (IllegalThreadStateException itse) {
			// LOG.e("Thread is already running or dead!");
			// LOG.i("The thread state is " + thread.getState().name());
		}
	}
	
	/** Stops the current {@link java.lang.Thread}
	 */
	public synchronized void stop() {
		if (!isRunning()) return;
		isRunning = false;
	}
	
	/** Kills the current {@link java.lang.Thread}
	 * When you call kill, you cannot start it anymore.
	 */
	public synchronized void kill() {
		if (isDead) return;
		stop();
		thread.interrupt();
	}
	
	/** To see if {@link java.lang.Thread} is running
	 * @return is the thread running
	 */
	public synchronized boolean isRunning() {
		return this.isRunning && !this.isDead;
	}
	
	/** Gets the currently created {@link java.lang.Thread}
	 * @return the created thread
	 */
	public Thread getThread() {
		return thread;
	}
}
