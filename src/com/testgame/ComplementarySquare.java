package com.testgame;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class ComplementarySquare {

	private GameView gameView;

	private Paint paint = new Paint();
	private Paint frame = new Paint();

	private boolean penultimo = false;
	private boolean ultimo = false;

	private int x;
	private int y;
	private int size;
	// private int primaryColor = 0;
	// private int complementaryColor = 0;
	private int currentColor;
	private int complementaryType;

	private Bitmap redGlossy;

	private static long FPS;

	private final static int YELLOW_PURPLE = 0;
	private final static int BLUE_ORANGE = 1;
	private final static int GREEN_RED = 2;
	private final static int YELLOW_ORANGE_RED = 3;
	private final static int BLUE_ORANGE_GREEN = 4;
	private final static int GREEN_RED_YELLOW = 5;

	private final static int FIRST = 0;
	private final static int SECOND = 1;
	private final static int THIRD = 2;

	private final static int RED = Color.rgb(190, 0, 0);
	private final static int GREEN = Color.rgb(0, 160, 0);
	private final static int BLUE = Color.rgb(11, 97, 164);
	private final static int ORANGE = Color.rgb(255, 146, 0);
	private final static int PURPLE = Color.rgb(159, 62, 213);
	private final static int YELLOW = Color.rgb(255, 200, 0);
	private static int FRAME_COLOR = Color.WHITE;

	private ArrayList<Integer> colors = new ArrayList<Integer>();

	public ComplementarySquare(GameView gameView, float xp, float yp, int size,
			int complementaryType, int colorType) {
		this.gameView = gameView;
		this.complementaryType = complementaryType;
		this.size = size;
		x = (int) xp;
		y = (int) yp;

		colors.clear();
		FPS = gameView.getFPS();
		redGlossy = gameView.getResizedBitmap(BitmapFactory.decodeResource(
				gameView.getResources(), R.drawable.red_glossy_square), size,
				size);

		frame.setColor(FRAME_COLOR);

		switch (complementaryType) {
		case YELLOW_PURPLE:
			colors.add(YELLOW);
			colors.add(PURPLE);
			break;

		case BLUE_ORANGE:
			colors.add(BLUE);
			colors.add(ORANGE);
			break;

		case GREEN_RED:
			colors.add(GREEN);
			colors.add(RED);

			break;

		case YELLOW_ORANGE_RED:
			colors.add(YELLOW);
			colors.add(ORANGE);
			colors.add(RED);
			break;

		case BLUE_ORANGE_GREEN:
			colors.add(YELLOW);
			colors.add(ORANGE);
			colors.add(RED);
			break;

		case GREEN_RED_YELLOW:
			colors.add(YELLOW);
			colors.add(ORANGE);
			colors.add(RED);
			break;

		}
		currentColor = colorType;
		setColor();

	}

	public void onDraw(Canvas canvas) {
		update();

		// FRAME

		// SQUARE
		if (currentColor != gameView.getComplementaryLevel().endGame.get(0) && false) {
			Rect src = new Rect(x, y, x + size, y + size);
			Rect dst = new Rect(x, y, x + size, y + size);
			canvas.drawBitmap(redGlossy, null, dst, null);
		} else {
			canvas.drawRect(x - 2, y - 2, x + size + 2, y + size + 2, frame);
			canvas.drawRect(x, y, x + size, y + size, paint);
		}

		// SMALL WHITE SQUARES
		if (ultimo) {
			int gap = 2 * size / 3;
			canvas.drawRect(x + gap, y + gap, x + size - gap, y + size - gap,
					frame);
		} else if (penultimo) {
			int gap = 2 * size / 5;
			canvas.drawRect(x + gap, y + gap, x + size - gap, y + size - gap,
					frame);
		}

	}

	private void update() {
	}

	private void setColor() {
		paint.setColor(colors.get(currentColor));
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

	public int getCurrentColor() {
		return currentColor;
	}

	public void setCurrentColor(int c) {
		currentColor = c;
		setColor();
	}

	public void nextColor() {

		if (currentColor < colors.size() - 1) {
			currentColor++;
		} else {
			currentColor = 0;
		}
		setColor();
	}

	public void lastColor() {

		if (currentColor > 0) {
			currentColor--;
		} else {
			currentColor = colors.size() - 1;
		}
		setColor();
	}

}
