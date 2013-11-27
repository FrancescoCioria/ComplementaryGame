package com.testgame;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;

public class SquareEyes {

	private GameView gameView;
	private Bitmap bmp;

	private static final int BMP_ROWS = 1;
	private static final int BMP_COLUMNS = 4;

	private int currentFrame = 1;
	private int width;
	private int height;

	private final static int EYES_CLOSED = 1;
	private final static int EYES_STREIGHT = 2;
	private final static int EYES_RIGHT = 3;
	private final static int EYES_LEFT = 4;

	private long lastTime = 0;
	private long currentTime = 0;

	Random rnd = new Random();

	int x = 20;
	int y = 20;

	public SquareEyes(GameView gameView, float xp, float yp) {
		bmp=gameView.getRedSquares().get(0);
		this.width = bmp.getWidth();
		this.height = bmp.getHeight();
		this.gameView = gameView;
		//this.bmp = bmp;
		lastTime = System.currentTimeMillis();
		currentFrame = 1;

	}

	private void update() {
		currentTime = System.currentTimeMillis();
		int timeGap = rnd.nextInt(501) + 350;
		if(currentFrame==0){
			timeGap=300;
		}
		if (currentTime > lastTime + timeGap) {
			int k = currentFrame;
			while (k == currentFrame) {
				k = rnd.nextInt(BMP_COLUMNS);
			}
			currentFrame = k;
			lastTime = System.currentTimeMillis();
		}

	}

	public void onDraw(Canvas canvas) {
		update();
		int srcX = 0;
		int srcY = 0;
		Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
		Rect dst = new Rect(x, y, x + width, y + height);
		canvas.drawBitmap(gameView.getRedSquares().get(currentFrame), src, dst, null);

	}

	public boolean isCollition(float x2, float y2) {
		return x2 > x && x2 < x + width && y2 > y && y2 < y + height;
	}

}
