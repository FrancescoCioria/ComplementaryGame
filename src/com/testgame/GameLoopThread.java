package com.testgame;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.util.Log;

public class GameLoopThread extends Thread {
	static long FPS = 40;
	private GameView view;
	private boolean running = false;
	private boolean paused = false;
	private boolean working = false;

	public GameLoopThread(GameView view) {
		this.view = view;
		FPS = view.getFPS();
	}

	public void setRunning(boolean run) {
		running = run;
	}

	public void setPaused(boolean pause) {
		paused = pause;
	}

	public boolean isWorking() {
		return working;
	}

	public int getFPS() {
		int x = (int) FPS;
		return x;
	}

	@SuppressLint("WrongCall")
	@Override
	public void run() {
		long ticksPS = 1000 / FPS;
		long startTime;
		long sleepTime;
		while (running) {
			if (!paused) {
				working = true;
				Canvas c = null;
				startTime = System.currentTimeMillis();
				try {
					c = view.getHolder().lockCanvas();
					synchronized (view.getHolder()) {
						view.onDraw(c);
					}
				} finally {
					if (c != null) {
						view.getHolder().unlockCanvasAndPost(c);
					}
				}
				sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
				try {
					if (sleepTime > 0)
						sleep(sleepTime);
					else {
						sleep(5);
						Log.i("FPS loop", "over time");
					}
				} catch (Exception e) {
				}
				working = false;
			}
		}
	}
}
