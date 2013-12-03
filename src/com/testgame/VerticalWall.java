package com.testgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class VerticalWall {

	private GameView gameView;

	private int x;
	private int y;
	private int size;

	private final static int YELLOW_PURPLE = 0;
	private final static int BLUE_ORANGE = 1;
	private final static int GREEN_RED = 2;

	Paint paint = new Paint();

	public VerticalWall(GameView gameView, float xp, float yp, int size) {
		this.gameView = gameView;
		this.size = size;

		x = (int) xp;
		y = (int) yp;

		paint.setColor(Color.BLACK);
	}

	public void onDraw(Canvas canvas) {
		canvas.drawRect(x + size - 1, y + 7, x + size + 21, y + size - 7, paint);
	}

	private void update() {
	}

	public boolean isCollition(float x2, float y2) {
		return x2 > x && x2 < x + size && y2 > y && y2 < y + size;
	}

}
