package com.testgame;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class MemorySquare {

	private GameView gameView;

	private Paint paint = new Paint();
	private Paint cover = new Paint();
	private Paint frame = new Paint();
	private int alpha = 150;
	private float animSpeed = 15;
	private int x;
	private int y;
	private int size;
	private boolean callBack = false;
	private boolean callBackCounter = false;
	private boolean animation = false;
	private boolean flipAnimation = false;
	private boolean discovered = false;
	private int counter = 0;

	private final static int MEMORY = 2;
	private static int ALPHA = 255;
	static long FPS;
	private final static int ANIM_TIME_STD = 850; // millis

	private long startAnimationTime = 0;
	private long lastTime = 0;

	Random rnd = new Random();

	public MemorySquare(GameView gameView, float xp, float yp, int size,
			int color) {
		lastTime = System.currentTimeMillis();
		this.gameView = gameView;
		this.size = size;
		x = (int) xp;
		y = (int) yp;

		FPS = gameView.getFPS();
		int anim_partition = (ANIM_TIME_STD / (1000 / (int) FPS));
		animSpeed = (float) (2 * ALPHA) / (float) anim_partition;

		frame.setColor(Color.WHITE);
		cover.setColor(Color.BLACK);

		if (color == -1) {
			color = 0;
		}
		int c = gameView.getColor(color);
		paint.setColor(c);
		alpha = ALPHA = 255;

	}

	private void update() {

		if (animation) {
			if (alpha + animSpeed < 255) {
				alpha += animSpeed;
			} else {
				alpha = 255;
				animation = false;
				gameView.setAnimation(false);
			}
		}
		cover.setAlpha(alpha);

	}

	public void onDraw(Canvas canvas) {
		update();

		canvas.drawRect(x - 2, y - 2, x + size + 2, y + size + 2, frame);

		canvas.drawRect(x, y, x + size, y + size, paint);

		canvas.drawRect(x, y, x + size, y + size, cover);

		if (!animation && callBack) {
			callBack = false;
			gameView.squareAnimationCallBack(MEMORY);
		}

	}

	public boolean isCollition(float x2, float y2) {
		return x2 > x && x2 < x + size && y2 > y && y2 < y + size;
	}

	public void startAnimation(boolean callBack) {
		startAnimationTime = System.currentTimeMillis();
		this.callBack = callBack;
		animation = true;
		flipAnimation = true;
		gameView.setAnimation(true);
	}

	public void setVisible(boolean b) {
		animation = false;
		if (b) {
			alpha = 0;
		} else {
			alpha = ALPHA;
		}
	}

	public void setDiscovered(boolean b) {
		discovered = b;
	}

	public boolean getDiscovered() {
		return discovered;
	}

	public void stopAnimation() {
		animation = false;
	}

	public boolean isFlipAnimation() {
		return flipAnimation;
	}

	public void counter(boolean fromGameView) {
		if (fromGameView) {
			callBack = true;
			callBackCounter = true;
			animation = true;
			counter = 0;
		}

		if (counter < 150) {
			counter += 10;
		} else {
			callBack = false;
			counter = 0;
			if (callBackCounter) {
				animation = false;
				callBackCounter = false;
				gameView.incrementMemoryLevel();
			}

		}
	}

}
