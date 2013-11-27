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

public class Square {

	private GameView gameView;
	private Bitmap bmp;
	private Paint paint = new Paint();
	private Paint black = new Paint();
	private Paint white = new Paint();
	private int alpha = 150;
	private int animSpeed = 12;
	private int acceleration = 0;
	private Path path = new Path();
	private int x;
	private int y;
	private int size;
	private int gametype;
	private int level = 0;;
	private boolean growing = false;
	private boolean callBack = false;
	private boolean callBackCounter = false;
	private boolean animation = false;
	private boolean flipAnimation = false;
	private boolean waterfall = false;
	private boolean discovered = false;
	private boolean firstTime = false;
	private final static int SEQUENCE = 0;
	private final static int DISCOVERY = 1;
	private final static int MEMORY = 2;
	private static int ALPHA = 255;
	private final static int BEGINNER = 0;
	private final static int NORMAL = 1;
	private final static int HARD = 2;
	private final static int CUSTOM = -1;
	private int counter = 0;
	private int firstCounter = 0;
	private int color = 0;
	private int coverColor = 0;

	private float flip = 0;
	private boolean flipGrowing = false;

	static int FLIP_TIME = 500; // in millis
	static float FLIP_SPEED;
	static long FPS;

	private int counterFlip = 1;
	private int r1;
	private int r2;

	private long startAnimationTime = 0;

	private long lastTime = 0;
	private long currentTime = 0;
	private int currentFrame = 1;

	Random rnd = new Random();

	public Square(GameView gameView, float xp, float yp, int size, int color) {
		this.color = color;
		this.gameView = gameView;
		x = (int) xp;
		y = (int) yp;
		this.size = size;
		FPS = gameView.getFPS();
		int size_partition = (FLIP_TIME / (1000 / (int) FPS));
		FLIP_SPEED = (float) size / (float) size_partition;
		// FLIP_SPEED = size / 12;
		// coverColor = gameView.getResources().getColor(R.color.kakhi);
		coverColor = Color.LTGRAY;
		white.setColor(Color.WHITE);

		lastTime = System.currentTimeMillis();

		// Random rnd = new Random();
		gametype = this.gameView.getGameType();
		switch (gametype) {
		case SEQUENCE:

			paint.setColor(color);
			white.setAlpha(0);
			// paint.setColor(Color.rgb(rnd.nextInt(256), rnd.nextInt(256),
			// rnd.nextInt(256)));
			black.setColor(Color.BLACK);
			ALPHA = 200;
			alpha = ALPHA;
			break;

		case DISCOVERY:
			switch (gameView.getLevel()) {
			case BEGINNER:
				level = 0;
				break;

			case NORMAL:
				level = 2;
				break;

			case HARD:
				level = 4;
				break;

			}
			if (color == -2) {
				paint = null;
			} else {
				if (color != -1) {
					paint.setColor(color);
				} else {
					paint.setColor(Color.WHITE);
					// paint.setColor(Color.rgb(245, 222, 179));
				}
			}
			black.setColor(coverColor);
			ALPHA = 255;
			alpha = ALPHA;
			animation = true;
			gameView.setFristTime(true);
			break;

		case MEMORY:
			if (color == -1) {
				color = 0;
			}
			int c = gameView.getColor(color);
			paint.setColor(c);

			ALPHA = 255;
			alpha = 255;
			break;

		}

	}

	private void update() {

		black.setAlpha(alpha);
		switch (gametype) {

		case SEQUENCE:
			if (animation) {
				if (growing) {
					if (alpha < ALPHA) {
						if (alpha + animSpeed + acceleration > ALPHA) {
							alpha = ALPHA;
						} else {
							alpha += animSpeed + acceleration;
						}
					} else {
						alpha -= (animSpeed + acceleration);
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

			break;

		case DISCOVERY:

			if (firstCounter < 10) {
				firstCounter++;
			} else {
				if (flipAnimation) {
					if (counter == 0) {
						if (!flipGrowing && flip < size / 2) {
							flip += FLIP_SPEED;
						} else {
							if (flip > 5) {
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
							/*
							 * if (alpha + level + 5 < 255) { alpha += level +
							 * 5; } else {
							 */
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
										// firstTime = false;
										// flipAnimation = false;
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

			break;

		case MEMORY:
			if (false) {
				counter(false);
			} else {
				if (animation) {

					if (alpha + 15 < 255) {
						alpha += 15;
					} else {
						alpha = 255;
						animation = false;
						gameView.setAnimation(false);

					}
				}
			}

			break;

		}

	}

	public void onDraw(Canvas canvas) {
		update();

		/*
		 * int srcX = 0; int srcY = 0; Rect src = new Rect(srcX, srcY, srcX +
		 * size, srcY + size); Rect dst = new Rect(x, y, x + width, y + height);
		 * //canvas.drawBitmap(bmp, src, dst, paint);
		 */

		canvas.drawRect(x - 2 + flip, y - 2, x + size + 2 - flip, y + size + 2,
				white);

		if (color != -2) {
			canvas.drawRect(x + flip, y, x + size - flip, y + size, paint);
		} else {
			int srcX = 0;
			int srcY = 0;
			Rect src = new Rect(srcX, srcY, srcX + size, srcY + size);
			Rect dst = new Rect(x, y, x + size, y + size);
			canvas.drawBitmap(gameView.getRedSquares().get(currentFrame), src,
					dst, null);
		}
		
			canvas.drawRect(x + flip, y, x + size - flip, y + size, black);

		if (!animation && callBack) {
			callBack = false;
			gameView.squareAnimationCallBack(gametype);
		}
		if (!flipAnimation && callBack) {
			callBack = false;
			gameView.discoveryNextLevel();
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

	public void flipAnimation() {

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
