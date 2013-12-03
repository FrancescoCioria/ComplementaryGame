package com.testgame;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;

public class ComplementarySquare {

	private GameView gameView;

	private Paint paint = new Paint();
	private Paint frame = new Paint();

	private boolean penultimo = false;
	private boolean ultimo = false;
	private boolean visible = false;

	private int x;
	private int y;
	private int size;
	private int square;
	// private int primaryColor = 0;
	// private int complementaryColor = 0;
	private int currentColor;
	private int complementaryType;

	private Bitmap redGlossy;
	private ArrayList<Bitmap> glossySquares = new ArrayList<Bitmap>();

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

	private Bitmap emptySquare;
	private ArrayList<Bitmap> coloredSquares = new ArrayList<Bitmap>();

	private int backgroundColor = 0;

	private ArrayList<Integer> colors = new ArrayList<Integer>();

	public ComplementarySquare(GameView gameView, float xp, float yp, int size,
			int complementaryType, int square, boolean visible) {
		this.gameView = gameView;
		this.complementaryType = complementaryType;
		this.size = size;
		this.square = square;
		this.visible = visible;
		x = (int) xp;
		y = (int) yp;

		colors.clear();
		glossySquares.clear();
		FPS = gameView.getFPS();

		emptySquare = gameView.getEmptySquare();
		coloredSquares = gameView.getColoredSquares();

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
		currentColor = gameView.getComplementaryLevel().firstColor.get(square);
		setColor();
		// colorizeBitmap(coloredSquare);

	}

	public void onDraw(Canvas canvas) {
		if (visible) {
			update();

			// firstVersion(canvas);

			drawBitmaps(canvas);

			// alphaFrame(canvas);

			// SMALL WHITE SQUARES
			if (ultimo) {
				int gap = 2 * size / 3;
				canvas.drawRect(x + gap, y + gap, x + size - gap, y + size
						- gap, frame);
			} else if (penultimo) {
				int gap = 2 * size / 5;
				canvas.drawRect(x + gap, y + gap, x + size - gap, y + size
						- gap, frame);
			}
		}

	}

	private void update() {
	}

	private void firstVersion(Canvas canvas) {
		canvas.drawRect(x - 2, y - 2, x + size + 2, y + size + 2, frame);
		canvas.drawRect(x, y, x + size, y + size, paint);
	}

	private void alphaFrame(Canvas canvas) {
		Rect dst = new Rect(x, y, x + size, y + size);

		if (false) {
			if (currentColor != gameView.getComplementaryLevel().endColor) {
				frame.setAlpha(255);
				canvas.drawRect(x - 2, y - 2, x + size + 2, y + size + 2, frame);
				canvas.drawRect(x, y, x + size, y + size, paint);

				if (false) {
					canvas.drawBitmap(gameView.getResizedBitmap(BitmapFactory
							.decodeResource(gameView.getResources(),
									R.drawable.green_frame), size, size), null,
							dst, null);
					canvas.drawBitmap(gameView.getResizedBitmap(BitmapFactory
							.decodeResource(gameView.getResources(),
									R.drawable.red_glossy_round), size, size),
							null, dst, null);
				}

			} else {
				frame.setAlpha(255);
				// canvas.drawRect(x, y, x + size, y + size, frame);
				canvas.drawRect(x - 2, y - 2, x + size + 2, y + size + 2, frame);

				// canvas.drawBitmap(glossySquares.get(currentColor), null, dst,
				// null);

				canvas.drawRect(x, y, x + size, y + size, paint);
			}
		}
	}

	private void drawBitmaps(Canvas canvas) {
		Rect dst = new Rect(x, y, x + size, y + size);
		if (isEmpty()) {
			paint.setAlpha(80);
			canvas.drawBitmap(emptySquare, null, dst, paint);

		} else {
			canvas.drawBitmap(coloredSquares.get(currentColor), null, dst, null);

		}

	}

	private void setColor() {

		// paint.setColor(colors.get(currentColor));

	}

	private void colorizeBitmap(Bitmap bmp) {
		nextColor();
		int[] allpixels = new int[bmp.getHeight() * bmp.getWidth()];

		bmp.getPixels(allpixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(),
				bmp.getHeight());

		int colorToReplace = bmp.getPixel(size / 2, size / 2);

		for (int i = 0; i < bmp.getHeight() * bmp.getWidth(); i++) {

			if (allpixels[i] == colorToReplace) {
				allpixels[i] = colors.get(currentColor);
			}
		}

		bmp.setPixels(allpixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(),
				bmp.getHeight());
	}

	public boolean isCollition(float x2, float y2) {
		if (visible) {
			return x2 > x && x2 < x + size && y2 > y && y2 < y + size;
		}
		return false;
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

	private boolean isEmpty() {
		if (currentColor != gameView.getComplementaryLevel().endColor) {
			return false;
		}
		return true;

	}

}
