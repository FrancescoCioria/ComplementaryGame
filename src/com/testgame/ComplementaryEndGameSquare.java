package com.testgame;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ComplementaryEndGameSquare {

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
	private boolean penultimo = false;
	private boolean ultimo = false;
	private int counter = 0;

	private final static int MEMORY = 2;
	private static int ALPHA = 255;
	static long FPS;

	private final static int YELLOW_PURPLE = 0;
	private final static int BLUE_ORANGE = 1;
	private final static int GREEN_RED = 2;

	private final static int PRIMARY = 3;
	private final static int COMPLEMENTARY = 4;
	private final static int ANIM_TIME_STD = 850; // millis

	private int primaryColor = 0;
	private int complementaryColor = 0;

	private long startAnimationTime = 0;
	private long lastTime = 0;

	private int currentColor;
	private int complementaryType;

	Random rnd = new Random();

	private int YELLOW;
	private int PURPLE;
	private int RED;
	private int GREEN;
	private int BLUE;
	private int ORANGE;

	public ComplementaryEndGameSquare(GameView gameView, float xp, float yp, int size,
			int complementaryType,int colorType) {
		lastTime = System.currentTimeMillis();
		this.gameView = gameView;
		this.complementaryType = complementaryType;
		this.size = size;
		x = (int) xp;
		y = (int) yp;

		FPS = gameView.getFPS();
		int anim_partition = (ANIM_TIME_STD / (1000 / (int) FPS));
		animSpeed = (float) (2 * ALPHA) / (float) anim_partition;

		frame.setColor(Color.WHITE);
		cover.setColor(Color.BLACK);

		RED = Color.rgb(255, 0, 0);
		GREEN = Color.rgb(0, 204, 0);
		BLUE = Color.rgb(11, 97, 164);
		ORANGE = Color.rgb(255, 146, 0);
		PURPLE = Color.rgb(159, 62, 213);
		YELLOW = Color.rgb(255, 255, 0);

		switch (complementaryType) {
		case YELLOW_PURPLE:
			primaryColor = YELLOW;
			complementaryColor = PURPLE;
			break;
		case BLUE_ORANGE:
			primaryColor = BLUE;
			complementaryColor = ORANGE;
			break;
		case GREEN_RED:
			primaryColor = GREEN;
			complementaryColor = RED;
			break;

		}
		currentColor = colorType;
		if(colorType==PRIMARY){
		paint.setColor(primaryColor);
		}else{
			paint.setColor(complementaryColor);
		}
		alpha = ALPHA = 0;

	}

	private void update() {
	}

	private void setColor() {
		int color = 0;
		switch (complementaryType) {
		case YELLOW_PURPLE:
			if (currentColor == PRIMARY) {
				color = YELLOW;
			} else {
				color = PURPLE;
			}

			break;
		case BLUE_ORANGE:
			if (currentColor == PRIMARY) {
				color = BLUE;
			} else {
				color = ORANGE;
			}
			break;
		case GREEN_RED:
			if (currentColor == PRIMARY) {
				color = GREEN;
			} else {
				color = RED;
			}
			break;

		}
		paint.setColor(color);
	}

	public void onDraw(Canvas canvas) {
		update();

		canvas.drawRect(x - 2, y - 2, x + size + 2, y + size + 2, frame);

		canvas.drawRect(x, y, x + size, y + size, paint);

		

		// canvas.drawRect(x, y, x + size, y + size, cover);

	}

	public boolean isCollition(float x2, float y2) {
		return x2 > x && x2 < x + size && y2 > y && y2 < y + size;
	}

	public void setPenultimo(boolean b) {
		penultimo = b;
	}

	public void setUltimo(boolean b) {
		ultimo = b;
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

	public int getCurrentColor() {
		return currentColor;
	}

	public void invertCurrentColor() {
		if (currentColor == PRIMARY) {
			currentColor = COMPLEMENTARY;
		} else {
			currentColor = PRIMARY;
		}
		setColor();
	}

}
