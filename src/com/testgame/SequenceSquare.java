package com.testgame;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class SequenceSquare {

	private GameView gameView;
	private Paint paint = new Paint();
	private Paint cover = new Paint();
	private Paint frame = new Paint();
	private int alpha = 150;
	private float animSpeed = 12;
	private int acceleration = 0;
	private int x;
	private int y;
	private int size;
	private boolean growing = false;
	private boolean callBack = false;
	private boolean callBackCounter = false;
	private boolean animation = false;
	private boolean flipAnimation = false;
	private boolean waterfall = false;
	private boolean discovered = false;
	// private boolean firstTime = false;
	private final static int SEQUENCE = 0;
	private final static int DISCOVERY = 1;
	private final static int MEMORY = 2;
	private static int ALPHA = 255;

	private int counter = 0;

	private float flip = 0;

	static long FPS;
	private final static int ANIM_TIME_STD = 850; // millis

	private long startAnimationTime = 0;

	private long lastTime = 0;

	Random rnd = new Random();

	public SequenceSquare(GameView gameView, float xp, float yp, int size,
			int color) {
		this.gameView = gameView;
		this.size = size;
		x = (int) xp;
		y = (int) yp;

		FPS = gameView.getFPS();
		int anim_partition = (ANIM_TIME_STD / (1000 / (int) FPS));
		animSpeed = (float) (2 * ALPHA) / (float) anim_partition;

		frame.setColor(Color.WHITE);
		cover.setColor(Color.BLACK);

		lastTime = System.currentTimeMillis();

		paint.setColor(color);
		// paint.setColor(Color.WHITE);
		frame.setAlpha(0);
		alpha = ALPHA = 200;

	}

	private void update() {

		if (animation) {
			if (growing) {
				if (alpha + animSpeed + acceleration > ALPHA) {
					alpha = ALPHA;
					growing = false;
					animation = false;
					if (callBack) {
						if (gameView.isPlaying()) {
							if (waterfall) {
								waterfall = false;
								gameView.sequenceGameAnimation(false);
							} else {
								gameView.setAnimation(false);
							}
						} else {

							if (!gameView.getDialogCalled()) {
								gameView.dialogLost();
								gameView.setDialogCalled(true);
							}
						}
					}
					Log.i("alpha", "alpha = ALPHA");
				} else {
					alpha += animSpeed + acceleration;
				}

			} else {
				if (alpha > 0) {
					if (alpha < animSpeed + acceleration) {
						alpha = 0;
					} else {
						alpha -= (animSpeed + acceleration);
					}
				} else {
					alpha = 0;
					growing = true;
				}
			}
		}
		cover.setAlpha(alpha);

	}

	public void onDraw(Canvas canvas) {
		update();

		canvas.drawRect(x - 2 + flip, y - 2, x + size + 2 - flip, y + size + 2,
				frame);

		canvas.drawRect(x + flip, y, x + size - flip, y + size, paint);

		canvas.drawRect(x + flip, y, x + size - flip, y + size, cover);

		if (!animation && callBack) {
			callBack = false;
			gameView.squareAnimationCallBack(SEQUENCE);
		}

	}

	public boolean isCollition(float x2, float y2) {
		return x2 > x && x2 < x + size && y2 > y && y2 < y + size;
	}

	public void startAnimationWaterfall(int a) {
		if (a != 0) {
			acceleration = 4 * (a - 2);
		} else {
			acceleration = 0;
		}
		callBack = true;
		waterfall = true;
		animation = true;
		gameView.sound(2);
	}

	public void startAnimation(boolean callBack) {
		startAnimationTime = System.currentTimeMillis();
		this.callBack = callBack;
		acceleration = 0;
		animation = true;
		gameView.setAnimation(true);
	}

	public void startFirstAnimation() {
		startAnimationTime = System.currentTimeMillis();
		acceleration = 0;
		animation = true;
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

}
