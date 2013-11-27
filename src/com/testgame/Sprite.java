package com.testgame;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Sprite {
	// direction = 0 up, 1 left, 2 down, 3 right,
	// animation = 3 back, 1 left, 0 front, 2 right
	int[] DIRECTION_TO_ANIMATION_MAP = { 3, 1, 0, 2 };
	private static final int BMP_ROWS = 4;
	private static final int BMP_COLUMNS = 3;
	private static final int MAX_SPEED = 5;
	private GameView gameView;
	private Bitmap bmp;
	private int x = 0;
	private int y = 0;
	private int xSpeed;
	private int ySpeed;
	private int currentFrame = 1;
	private int width;
	private int height;
	private boolean growing = true;
	private double speed;
	private double xy;
	private double b = 0;

	public Sprite(GameView gameView, Bitmap bmp, float xp, float yp) {
		this.width = bmp.getWidth() / BMP_COLUMNS;
		this.height = bmp.getHeight() / BMP_ROWS;
		this.gameView = gameView;
		this.bmp = bmp;

		Random rnd = new Random();
		if (xp < 0) {
			x = rnd.nextInt(gameView.getWidth() - width);
		} else {
			x = (int) xp;
		}
		if (yp < 0) {
			y = rnd.nextInt(gameView.getHeight() - height);
		} else {
			y = (int) yp;
		}
		xSpeed = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
		ySpeed = rnd.nextInt(MAX_SPEED * 2) - MAX_SPEED;
		double xS2 =  Math.pow((double) xSpeed, 2.0);
		double yS2 =  Math.pow((double) ySpeed, 2.0);
		speed= Math.pow(xS2+yS2, 0.5);
		
		b = speed*5;
	}

	private void update() {
		if (x >= gameView.getWidth() - width - xSpeed || x + xSpeed <= 0) {
			xSpeed = -xSpeed;
		}
		x = x + xSpeed;
		if (y >= gameView.getHeight() - height - ySpeed || y + ySpeed <= 0) {
			ySpeed = -ySpeed;
		}
		y = y + ySpeed;
		
		double x2 =  Math.pow((double) x, 2.0);
		double y2 =  Math.pow((double) y, 2.0);
		xy= Math.pow(x2+y2, 0.5);
		
		if (b/speed==5) {
			if (growing) {
				if (currentFrame == BMP_COLUMNS - 1) {
					currentFrame--;
					growing = false;
				} else {
					currentFrame++;
				}
			} else {
				if (currentFrame == 0) {
					currentFrame++;
					growing = true;
				} else {
					currentFrame--;
				}
			}
			// currentFrame = ++currentFrame % BMP_COLUMNS;
			b =0;
		}else{
			b+=speed;
		}
	}

	public void onDraw(Canvas canvas) {
		update();
		int srcX = currentFrame * width;
		int srcY = getAnimationRow() * height;
		Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
		Rect dst = new Rect(x, y, x + width, y + height);
		canvas.drawBitmap(bmp, src, dst, null);
	}

	private int getAnimationRow() {
		double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);
		int direction = (int) Math.round(dirDouble) % BMP_ROWS;
		return DIRECTION_TO_ANIMATION_MAP[direction];
	}

	public boolean isCollition(float x2, float y2) {
		return x2 > x && x2 < x + width && y2 > y && y2 < y + height;
	}
}
