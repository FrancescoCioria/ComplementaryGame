package com.testgame;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class DiscoverySquare {

	private GameView gameView;

	private Paint paint = new Paint();
	private Paint cover = new Paint();
	private Paint frame = new Paint();

	private int alpha = 150;
	private int x;
	private int y;
	private int size;
	private int counter = 0;
	private int firstCounter = 0;
	private int color = 0;
	private int coverColor = 0;
	private int wrongCardColor = 0;

	private boolean callBack = false;
	private boolean animation = false;
	private boolean flipAnimation = false;
	private boolean discovered = false;
	private boolean firstTime = false;
	private boolean flipGrowing = false;

	private float flip = 0;
	private long startAnimationTime = 0;
	private long lastTime = 0;
	private long currentTime = 0;
	private int currentFrame = 1;

	private final static int FLIP_TIME = 400; // in millis
	private static float FLIP_SPEED;
	private static long FPS;
	private final static int DISCOVERY = 1;
	private static int ALPHA = 255;

	private Random rnd = new Random();

	public DiscoverySquare(GameView gameView, float xp, float yp, int size,
			int color) {

		this.color = color;
		this.gameView = gameView;
		x = (int) xp;
		y = (int) yp;
		this.size = size;
		FPS = gameView.getFPS();
		int size_partition = (FLIP_TIME / (1000 / (int) FPS));
		FLIP_SPEED = (float) size / (float) size_partition;

		wrongCardColor = Color.WHITE;
		wrongCardColor = gameView.getResources().getColor(R.color.red);
		coverColor = gameView.getResources().getColor(R.color.rosa);
		// coverColor = Color.LTGRAY;
		frame.setColor(Color.WHITE);
		frame.setColor(gameView.getResources().getColor(R.color.chocolate));

		lastTime = System.currentTimeMillis();

		if (color == -2) {
			paint = null;
		} else {
			if (color != -1) {
				paint.setColor(color);
			} else {
				paint.setColor(wrongCardColor);
			}
		}

		cover.setColor(coverColor);
		ALPHA = 255;
		alpha = ALPHA;
		animation = true;
		gameView.setFristTime(true);
	}

	private void update() {

		if (firstCounter < 10) {
			firstCounter++;
		} else {
			if (flipAnimation) {
				if (counter == 0) {
					if (!flipGrowing && flip < size / 2) {
						flip += FLIP_SPEED;
						if (flip >= size / 2) {
							flip = size / 2;
						}
						if (flip + FLIP_SPEED * 2 >= size / 2) {
							alpha = 100;
						}
					} else {
						if (flip > FLIP_SPEED) {
							flipGrowing = true;
							flip -= FLIP_SPEED;
							alpha = 0;
						} else {
							flip = 0;
							// firstTime = false;
							flipGrowing = false;
							startAnimationTime = System.currentTimeMillis();
							if (callBack) {
								counter = 1;
							} else {
								flipAnimation = false;
							}

						}
					}
				} else {
					if (callBack && counter < 10) {
						counter++;
					} else {
						callBack = false;
						flipAnimation = false;
						counter = 0;
					}
				}

			} else {
				if (animation) {
					if (firstTime) {
						if (startAnimationTime == 0) {
							if (!flipAnimation
									&& System.currentTimeMillis() > lastTime + 500) {
								flipAnimation = true;
							}
						}

						if (startAnimationTime != 0
								&& System.currentTimeMillis()
										- startAnimationTime > gameView
										.getActivityContext()
										.getDiscoveryLevels()
										.get(gameView.getGameLenght()).viewTime) {

							if (!flipGrowing && flip < size / 2) {
								alpha = 0;
								flip += FLIP_SPEED;
							} else {
								if (flip > 5) {
									flipGrowing = true;
									flip -= FLIP_SPEED;
									alpha = 255;
								} else {
									flip = 0;

									flipGrowing = false;
									animation = false;
									firstTime = false;
									gameView.setFristTime(false);
								}
							}

						}

					} else {
						alpha = 0;
					}
				}
			}

			if (!animation && !flipAnimation) {
				alpha = ALPHA;
			}

			if (color == -2) {
				currentTime = System.currentTimeMillis();
				int timeGap = rnd.nextInt(501) + 350;
				if (currentFrame == 0) {
					timeGap = 300;
				}
				if (currentTime > lastTime + timeGap) {
					int k = currentFrame;
					while (k == currentFrame) {
						k = rnd.nextInt(gameView.getRedSquares().size());
					}
					currentFrame = k;
					lastTime = System.currentTimeMillis();
				}
			}
		}
		cover.setAlpha(alpha);
	}

	public void onDraw(Canvas canvas) {
		update();

		canvas.drawRect(x - 2 + flip, y - 2, x + size + 2 - flip, y + size + 2,
				frame);
		if (flip != size / 2) {
			canvas.drawRect(x + flip, y, x + size - flip, y + size, paint);
		}

		if (false) {
			int srcX = 0;
			int srcY = 0;
			Rect src = new Rect(srcX, srcY, srcX + size, srcY + size);
			Rect dst = new Rect(x, y, x + size, y + size);
			canvas.drawBitmap(gameView.getRedSquares().get(currentFrame), src,
					dst, null);
		}

		canvas.drawRect(x + flip, y, x + size - flip, y + size, cover);

		if (!animation && callBack) {
			callBack = false;
			gameView.squareAnimationCallBack(DISCOVERY);
		}
		if (!flipAnimation && callBack) {
			callBack = false;
			gameView.discoveryNextLevel();
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

	public void startFirstAnimation() {
		startAnimationTime = System.currentTimeMillis();
		flipAnimation = true;
		animation = true;
		firstTime = true;
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
