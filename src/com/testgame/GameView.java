package com.testgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.R.array;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameView extends SurfaceView {
	private SurfaceHolder holder;
	private GameLoopThread gameLoopThread;

	private List<DiscoverySquare> discoverySquares = new ArrayList<DiscoverySquare>();
	private List<SequenceSquare> sequenceSquares = new ArrayList<SequenceSquare>();
	private List<MemorySquare> memorySquares = new ArrayList<MemorySquare>();
	private List<ComplementarySquare> complementarySquares = new ArrayList<ComplementarySquare>();
	private ArrayList<ArrayList<Integer>> ways = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> tempWays = new ArrayList<ArrayList<Integer>>();
	// private List<ComplementaryEndGameSquare> complementaryEndGameSquare = new
	// ArrayList<ComplementaryEndGameSquare>();

	private long lastClick;
	private long lastWin;
	private long lastError;
	private static int GAP = 5;
	private static int MARGIN_X;
	private static int MARGIN_Y;
	private static int MARGIN_SMALL_X;
	private static int MARGIN_SMALL_Y;
	private static int GAP_SMALL = 10;
	private static int SIZE = 100;
	private static int SIZE_SMALL = 25;
	private static int COLUMNS = 6;
	private static int ROWS = 12;
	private static int NUMBER_SQUARES = COLUMNS * ROWS;
	private final static int SEQUENCE = 0;
	private final static int DISCOVERY = 1;
	private final static int MEMORY = 2;
	private final static int COMPLEMENTARY = 3;
	private static int LIVES = 20;
	private static int currentLives = LIVES;
	private final static int FIRST = 0;
	private final static int SECOND = 1;
	private final static int THIRD = 1;

	private boolean playing = false;
	private boolean correct = false;
	private boolean isWaterfall = false;
	private boolean firstTime = false;
	private boolean animation = false;
	private boolean globalAnimation = false;
	private boolean incremented = true;
	private boolean dialogCalled = false;
	private boolean isCreatingLevel = false;
	private boolean isDrawingComplementary = false;
	private boolean isPlaying = false;
	private ArrayList<Integer> game = new ArrayList<Integer>();
	private ArrayList<Integer> discovery = new ArrayList<Integer>();
	private ArrayList<Integer> memory = new ArrayList<Integer>();
	private ArrayList<Integer> complementaryGame = new ArrayList<Integer>();
	private int lenghtGame = 0;
	private int userTurn = 0;
	private int waterfall = 0;
	private int gameType;
	private int selectedCards = 0;
	private int currentSquareSelected = -1;
	private boolean actionOnUP = false;
	private int discoveredSquares = 0;
	private int width;
	private int height;
	private float volume;
	private SoundPool sounds;
	private int ding;
	private int gameOver;
	private int error;
	private int select;
	private int block;
	private ArrayList<Integer> solution = new ArrayList<Integer>();

	private final static int RESET_SIZE = 180;
	private final static int RESET_MARGIN = 0;
	private static int PLUS_MARGIN = 0;

	private final static int RED = Color.rgb(190, 0, 0);
	private final static int GREEN = Color.rgb(0, 160, 0);
	private final static int BLUE = Color.rgb(11, 97, 164);
	private final static int ORANGE = Color.rgb(255, 146, 0);
	private final static int PURPLE = Color.rgb(159, 62, 213);
	private final static int YELLOW = Color.rgb(255, 200, 0);
	private static int BACKGROUND = Color.rgb(255, 255, 0);

	private SquareEyes squareEyes;
	private Paint paint = new Paint();

	final static int GAME_OVER = 1;
	final static int DING = 2;
	final static int NO_SOUND = 0;
	final static int ERROR = 3;
	final static int SELECT = 4;
	final static int BLOCK = 5;

	// final static int RED = Color.RED;
	// final static int RED = -2;

	// private Bitmap red_left;
	// private Bitmap red_right;
	// private Bitmap red_closed;
	// private Bitmap red_straight;

	private ArrayList<Integer> colors = new ArrayList<Integer>();
	private ArrayList<Bitmap> redSquares = new ArrayList<Bitmap>();

	private Random rnd = new Random();
	private MainActivity mainActivity;

	private DiscoveryLevelData discoveryLevel = new DiscoveryLevelData();

	// private int discoveryGameType = 0;

	final static int D_REDS = 0;
	final static int D_RAINBOW = 1;
	final static int D_RAINBOW_BOMBS = 9;
	final static int D_REDS_RAINBOW = 2;
	final static int D_SIMILAR_RED = 4;
	final static int D_SIMILAR_MULTI = 5;

	final static int S_NORMAL = 0;
	final static int S_SWITCH = 1;
	final static int S_BW = 2;
	final static int S_MELODY = 2;

	private final static int YELLOW_PURPLE = 0;
	private final static int BLUE_ORANGE = 1;
	private final static int GREEN_RED = 2;
	private final static int YELLOW_PURPLE_GREEN = 3;
	private final static int BLUE_ORANGE_GREEN = 4;
	private final static int GREEN_RED_YELLOW = 5;

	private MyBitmap plus = new MyBitmap();
	private MyBitmap minus = new MyBitmap();

	private int lastComplementarySquareTouched = -1;
	int perfectMoves = 0;
	int currentMoves = 0;

	public GameView(MainActivity mainActivity, int columns, int rows,
			int gameType) {
		super(mainActivity);
		this.gameType = gameType;
		this.mainActivity = mainActivity;
		Display display = mainActivity.getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		COLUMNS = columns;
		ROWS = rows;
		NUMBER_SQUARES = COLUMNS * ROWS;

		initButtons();
		initSound();

		holder = getHolder();
		holder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				gameLoopThread.setRunning(false);
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				if (GameView.this.gameType != COMPLEMENTARY) {
					initializeSquareSize(COLUMNS, ROWS);
				}
				createSquares();
				gameLoopThread = new GameLoopThread(GameView.this);
				gameLoopThread.setRunning(true);
				gameLoopThread.start();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
			}
		});

	}

	private void initButtons() {
		minus.bmp = getResizedBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.minus),
				RESET_SIZE / 2, RESET_SIZE / 2);

		PLUS_MARGIN = (width - RESET_MARGIN * 2 - RESET_SIZE * 2 - minus.bmp
				.getWidth() * 2) / 3;
		minus.X = RESET_MARGIN + RESET_SIZE + PLUS_MARGIN;

		plus.bmp = getResizedBitmap(
				BitmapFactory.decodeResource(getResources(), R.drawable.plus),
				RESET_SIZE / 2, RESET_SIZE / 2);

		plus.X = minus.X + minus.bmp.getWidth() + PLUS_MARGIN;
	}

	public void initializeSquareSize(int columns, int rows) {
		COLUMNS = columns;
		ROWS = rows;
		NUMBER_SQUARES = columns * rows;

		if (gameType == DISCOVERY || gameType == COMPLEMENTARY) {
			GAP = 20;
			MARGIN_X = 20;
		} else {
			MARGIN_X = GAP = 5;
		}

		boolean toobig = false;
		while ((width - (SIZE * COLUMNS + GAP * (COLUMNS - 1))) < 2 * MARGIN_X
				|| (height - (SIZE * ROWS + GAP * (ROWS - 1))) < 2 * MARGIN_X) {
			toobig = true;
			if (SIZE <= GAP * 4) {
				GAP--;
			} else {
				SIZE = SIZE - 2;
			}
		}

		while (!toobig
				&& (width - (SIZE * COLUMNS + GAP * (COLUMNS - 1))) >= 2 * MARGIN_X
				&& (height - (SIZE * ROWS + GAP * (ROWS - 1))) >= 2 * MARGIN_X) {

			SIZE++;

		}

		if ((gameType == DISCOVERY || gameType == COMPLEMENTARY)
				&& SIZE > width / 5) {
			SIZE = width / 5;
		}

		MARGIN_X = (width - (SIZE * COLUMNS + GAP * (COLUMNS - 1))) / 2;
		MARGIN_Y = (height - (SIZE * ROWS + GAP * (ROWS - 1))) / 2;

		/*
		 * redSquares.clear();
		 * 
		 * redSquares.add(getResizedBitmap(BitmapFactory.decodeResource(
		 * getResources(), R.drawable.square_eyes_closed), SIZE, SIZE));
		 * redSquares.add(getResizedBitmap(BitmapFactory.decodeResource(
		 * getResources(), R.drawable.square_eyes_straight), SIZE, SIZE));
		 * redSquares.add(getResizedBitmap(BitmapFactory.decodeResource(
		 * getResources(), R.drawable.square_eyes_right), SIZE, SIZE));
		 * redSquares.add(getResizedBitmap(BitmapFactory.decodeResource(
		 * getResources(), R.drawable.square_eyes_left), SIZE, SIZE));
		 */
	}

	public void initializeComplementarySquareSize(int columns, int rows) {
		COLUMNS = columns;
		ROWS = rows;
		NUMBER_SQUARES = columns * rows;

		GAP = 20;
		GAP_SMALL = 10;
		MARGIN_X = 20;

		boolean toobig = false;
		while ((width - (SIZE * COLUMNS + GAP * (COLUMNS - 1))) < 2 * MARGIN_X
				|| (height - (SIZE * ROWS + GAP * (ROWS - 1))) < 2 * MARGIN_X) {
			toobig = true;
			if (SIZE <= GAP * 4) {
				GAP--;
			} else {
				SIZE = SIZE - 2;
			}
		}

		while (!toobig
				&& (width - (SIZE * COLUMNS + GAP * (COLUMNS - 1))) >= 2 * MARGIN_X
				&& (height - (SIZE * ROWS + GAP * (ROWS - 1))) >= 2 * MARGIN_X) {
			SIZE++;
		}

		if ((gameType == DISCOVERY || gameType == COMPLEMENTARY)
				&& SIZE > width / 5) {
			SIZE = width / 5;
		}

		// SIZE_SMALL = SIZE / 3;

		MARGIN_X = (width - (SIZE * COLUMNS + GAP * (COLUMNS - 1))) / 2;
		MARGIN_Y = RESET_SIZE / 2
				+ (height - (SIZE * ROWS + GAP * (ROWS - 1) + RESET_SIZE / 2))
				/ 2;

		// MARGIN_SMALL_Y = (height - (SIZE * ROWS + GAP * ROWS + SIZE_SMALL
		// * ROWS + GAP_SMALL * ROWS)) / 6;

		// MARGIN_Y = (height - (SIZE * ROWS + GAP * ROWS + SIZE_SMALL * ROWS
		// + GAP_SMALL * ROWS + 2 * MARGIN_SMALL_Y))
		// / 2
		// + (SIZE_SMALL * ROWS + GAP_SMALL * ROWS + 2 * MARGIN_SMALL_Y);

		// MARGIN_SMALL_X = (width - (SIZE_SMALL * COLUMNS + GAP_SMALL
		// * (COLUMNS - 1))) / 2;

	}

	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// CREATE A MATRIX FOR THE MANIPULATION
		Matrix matrix = new Matrix();
		// RESIZE THE BIT MAP
		matrix.postScale(scaleWidth, scaleHeight);

		// "RECREATE" THE NEW BITMAP
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;
	}

	public ArrayList<Bitmap> getRedSquares() {
		return redSquares;
	}

	public void createSquares() {

		switch (gameType) {
		case DISCOVERY:
			initializeDiscovery();
			break;

		case MEMORY:
			initializeMemory();
			break;

		case SEQUENCE:
			initializeSequence();
			break;
		case COMPLEMENTARY:
			createComplementaryLevel(COLUMNS, ROWS);
			// initializeComplementary();
			break;
		}

	}

	private void initializeDiscovery() {
		discoveryLevel = mainActivity.getDiscoveryLevels().get(lenghtGame);
		playing = true;
		game.clear();
		for (int q = 0; q < discoveryLevel.squares; q++) {
			while (game.size() <= q) {
				int ran = rnd.nextInt(NUMBER_SQUARES);
				boolean add = true;

				if (!game.isEmpty()) {
					for (int g : game) {
						if (g == ran) {
							add = false;
						}
					}
				}
				if (add) {
					game.add(ran);
				} else {
					// game.add(-1);
				}
			}
		}

		colors.clear();

		int w = 1;
		int d = 0;
		if (mainActivity.getDiscoveryGameType() == D_REDS_RAINBOW) {
			w = 2;
			d = 1;
		}

		while (colors.size() < w * 12) {
			boolean added = false;
			while (!added) {

				int col = randomColor(rnd.nextInt(w * 12 + d));

				boolean add = true;
				for (int g : colors) {
					if (g == col
							|| (mainActivity.getDiscoveryGameType() == D_REDS_RAINBOW && col == Color.RED)) {
						add = false;
						break;
					}
				}
				if (add) {
					colors.add(col);
					added = true;
				}

			}
		}

		int x = MARGIN_X;
		int y = MARGIN_Y;
		int counter = 0;
		int color = 0;

		for (int i = 1; i <= NUMBER_SQUARES; i++) {
			color = -1;
			for (int g : game) {
				if (g + 1 == i) {

					switch (mainActivity.getDiscoveryGameType()) {

					case D_REDS:
						color = RED;
						color = this.getResources().getColor(
								R.color.light_green);
						// color = -2;
						break;
					case D_RAINBOW:
						color = colors.get(counter);
						counter++;
						break;
					case D_RAINBOW_BOMBS:

						break;
					case D_REDS_RAINBOW:
						// color = Color.RED;
						color = RED;
						break;
					case D_SIMILAR_RED:
						break;
					case D_SIMILAR_MULTI:
						break;
					}

					break;
				} else {
					if (mainActivity.getDiscoveryGameType() == D_REDS_RAINBOW) {
						if (i - 1 < colors.size()) {
							color = colors.get(i - 1);
						}
					}
				}
			}

			discoverySquares.add(createDiscoverySquare(x, y, color));

			if (i % COLUMNS == 0) {
				y = y + SIZE + GAP;
				x = MARGIN_X;
			} else {
				x = x + SIZE + GAP;
			}
		}

		for (DiscoverySquare s : discoverySquares) {
			s.startFirstAnimation();
		}

	}

	private void initializeMemory() {
		colors.clear();
		for (int q = 0; q < NUMBER_SQUARES; q++) {
			while (game.size() <= q) {
				int ra = rnd.nextInt(NUMBER_SQUARES / 2);
				boolean add = true;
				boolean first = true;

				if (!game.isEmpty()) {
					for (int g : game) {
						if (g == ra) {
							if (first) {
								first = false;
							} else {
								add = false;
								break;
							}
						}
					}
				}
				if (add) {
					game.add(ra);
					if (first) {
						memory.add(ra);
						if (colors.size() < 27) {
							boolean added = false;
							while (!added) {
								int col = randomColor(rnd.nextInt(27));
								boolean add2 = true;
								for (int g : colors) {
									if (g == col) {
										add2 = false;
										break;
									}
								}
								if (add2) {
									colors.add(col);
									added = true;
								}
							}
						} else {
							colors.add(randomColor(-1));
						}
					}
				}
			}
		}

		int x = MARGIN_X;
		int y = MARGIN_Y;
		int color = 0;

		for (int i = 1; i <= NUMBER_SQUARES; i++) {
			color = game.get(i - 1);
			memorySquares.add(createMemorySquare(x, y, color));

			if (i % COLUMNS == 0) {
				y = y + SIZE + GAP;
				x = MARGIN_X;
			} else {
				x = x + SIZE + GAP;
			}
		}

	}

	private void initializeSequence() {
		colors.clear();
		while (colors.size() < 24) {
			boolean added = false;
			while (!added) {
				int col = randomColor(rnd.nextInt(24));
				boolean add = true;
				for (int g : colors) {
					if (g == col) {
						add = false;
						break;
					}
				}
				if (add) {
					colors.add(col);
					added = true;
				}
			}
		}
		for (int i = 0; i < NUMBER_SQUARES - 24; i++) {
			colors.add(rnd.nextInt(24 + i), randomColor(-1));
		}

		int x = MARGIN_X;
		int y = MARGIN_Y;
		int color = 0;

		for (int i = 1; i <= NUMBER_SQUARES; i++) {
			if (i - 1 < colors.size()) {
				color = colors.get(i - 1);
			} else {
				color = randomColor(-1);// di sicurezza
			}

			sequenceSquares.add(createSequenceSquare(x, y, color));
			if (i % COLUMNS == 0) {
				y = y + SIZE + GAP;
				x = MARGIN_X;
			} else {
				x = x + SIZE + GAP;
			}
		}

		for (SequenceSquare s : sequenceSquares) {
			s.startAnimation(false);
		}

	}

	private void initializeComplementary() {
		lastComplementarySquareTouched = -1;
		complementaryGame.clear();
		int x = MARGIN_X;
		int y = MARGIN_Y;
		// int xs = MARGIN_SMALL_X;
		// int ys = MARGIN_SMALL_Y;
		int level = 0;
		complementarySquares.clear();
		// complementaryEndGameSquare.clear();
		for (int i = 1; i <= NUMBER_SQUARES; i++) {
			ComplementaryLevelData currentLevel = mainActivity
					.getComplementaryLevels().get(level);
			complementarySquares.add(createComplementarySquare(x, y,
					currentLevel.squaresType.get(i - 1),
					currentLevel.firstColor.get(i - 1)));
			// complementaryEndGameSquare.add(createComplementaryEndGameSquare(xs,
			// ys, currentLevel.squaresType.get(i - 1),
			// currentLevel.endGame.get(i - 1)));

			if (i % COLUMNS == 0) {
				y = y + SIZE + GAP;
				x = MARGIN_X;
			} else {
				x = x + SIZE + GAP;
			}

			// if (i % COLUMNS == 0) {
			// ys = ys + SIZE_SMALL + GAP_SMALL;
			// /xs = MARGIN_SMALL_X;
			// } else {
			// xs = xs + SIZE_SMALL + GAP_SMALL;
			// }
		}
	}

	private SequenceSquare createSequenceSquare(float x, float y, int color) {
		return new SequenceSquare(this, x, y, SIZE, color);
	}

	private DiscoverySquare createDiscoverySquare(float x, float y, int color) {
		return new DiscoverySquare(this, x, y, SIZE, color);
	}

	private MemorySquare createMemorySquare(float x, float y, int color) {
		return new MemorySquare(this, x, y, SIZE, color);
	}

	private ComplementarySquare createComplementarySquare(float x, float y,
			int type, int color) {
		return new ComplementarySquare(this, x, y, SIZE, type, color);
	}

	private ComplementaryEndGameSquare createComplementaryEndGameSquare(
			float x, float y, int type, int color) {
		return new ComplementaryEndGameSquare(this, x, y, SIZE_SMALL, type,
				color);
	}

	private SquareEyes createSquareEyes() {
		return new SquareEyes(this, 0, 0);
	}

	@SuppressLint("WrongCall")
	@Override
	protected void onDraw(Canvas canvas) {
		switch (gameType) {
		case SEQUENCE:
			canvas.drawColor(Color.BLACK);
			for (SequenceSquare square : sequenceSquares) {
				square.onDraw(canvas);
			}
			break;

		case DISCOVERY:
			canvas.drawColor(Color.BLACK);
			canvas.drawColor(this.getResources().getColor(
					com.testgame.R.color.marrone));

			for (DiscoverySquare square : discoverySquares) {
				square.onDraw(canvas);
			}

			break;

		case MEMORY:
			canvas.drawColor(Color.BLACK);
			for (MemorySquare square : memorySquares) {
				square.onDraw(canvas);
			}
			break;

		case COMPLEMENTARY:
			canvas.drawColor(BACKGROUND);
			int i = 0;
			if (!isCreatingLevel) {
				isDrawingComplementary = true;
				for (ComplementarySquare square : complementarySquares) {
					square.onDraw(canvas);
					i++;
				}
				isDrawingComplementary = false;
			}

			// for (ComplementaryEndGameSquare endSquare :
			// complementaryEndGameSquare) {
			// endSquare.onDraw(canvas);
			// }

			// RESET BUTTON
			paint.setColor(Color.BLACK);
			canvas.drawRect(RESET_MARGIN, RESET_MARGIN, RESET_MARGIN
					+ RESET_SIZE, RESET_MARGIN + RESET_SIZE / 2, paint);

			// NEXT LEVEL BUTTON
			paint.setColor(Color.WHITE);
			canvas.drawRect(width - RESET_MARGIN - RESET_SIZE, RESET_MARGIN,
					width - RESET_MARGIN, RESET_MARGIN + RESET_SIZE / 2, paint);

			// PLUS BUTTON
			canvas.drawBitmap(plus.bmp, plus.X, plus.Y, null);

			// MINUS BUTTON
			canvas.drawBitmap(minus.bmp, minus.X, minus.Y, null);

			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStyle(Style.FILL);
			paint.setTextSize(25);

			// BEST MOVES
			canvas.drawText("BEST: " + Integer.toString(perfectMoves),
					RESET_SIZE / 2, height - RESET_SIZE / 2, paint);
			// CURRENT MOVES
			canvas.drawText("DONE: " + Integer.toString(currentMoves), width
					- (RESET_SIZE), height - RESET_SIZE / 2, paint);

		}

		// if(gameType!=SEQUENCE)
		synchronized (getHolder()) {
			if (gameType == 4) {
				canvas.drawColor(Color.BLACK);
				squareEyes.onDraw(canvas);
			}
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP) { // ACTION UP
			actionOnUP = true;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN) { // ACTION DOWN
			actionOnUP = false;
			isPlaying = true;
		}

		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (gameType == COMPLEMENTARY && isPlaying)
				onTouchComplementary(event);
		}

		if (actionOnUP) {
			if (!isWaterfall && System.currentTimeMillis() - lastClick > 100) {

				switch (gameType) {

				case SEQUENCE:
					onTouchSequence(event);
					break;

				case DISCOVERY:
					onTouchDiscovery(event);
					break;

				case MEMORY:
					onTouchMemory(event);
					break;
				case COMPLEMENTARY:
					if (isPlaying) {
						onTouchComplementary(event);
						if (RESET_MARGIN <= event.getX()
								&& event.getX() <= RESET_MARGIN + RESET_SIZE
								&& RESET_MARGIN <= event.getY()
								&& event.getY() <= RESET_MARGIN + RESET_SIZE
										/ 2) {
							complementaryGame.clear();
							resetComplementary();
						} else if (width - RESET_MARGIN - RESET_SIZE <= event
								.getX()
								&& event.getX() <= width - RESET_MARGIN
								&& RESET_MARGIN <= event.getY()
								&& event.getY() <= RESET_MARGIN + RESET_SIZE
										/ 2) {
							complementaryWin(COLUMNS, ROWS);
						} else if (clickOnBitmap(plus, event)) {
							COLUMNS++;
							ROWS++;
							complementaryWin(COLUMNS, ROWS);
						} else if (clickOnBitmap(minus, event)) {
							if (COLUMNS > 3 && ROWS > 3) {
								COLUMNS--;
								ROWS--;
								complementaryWin(COLUMNS, ROWS);
							} else {
								sound(ERROR);
							}
						}
					}
					break;
				}
			}
		}

		return true;
	}

	private boolean clickOnBitmap(MyBitmap myBitmap, MotionEvent event) {

		float xEnd = myBitmap.X + myBitmap.bmp.getWidth();
		float yEnd = myBitmap.Y + myBitmap.bmp.getWidth();
		;

		if ((event.getX() >= myBitmap.X && event.getX() <= xEnd)
				&& (event.getY() >= myBitmap.Y && event.getY() <= yEnd)) {
			int pixX = (int) (event.getX() - myBitmap.X);
			int pixY = (int) (event.getY() - myBitmap.Y);
			if (!(myBitmap.bmp.getPixel(pixX, pixY) == 0)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	private void onTouchSequence(MotionEvent event) {
		int z = 0;
		lastClick = System.currentTimeMillis();

		boolean collision = false;

		for (int i = sequenceSquares.size() - 1; i >= 0; i--) {
			SequenceSquare square = sequenceSquares.get(i);

			if (square.isCollition(event.getX(), event.getY())) {
				collision = true;
				z = i;
			}
		}
		if (collision) {
			int clicked = 0;

			synchronized (getHolder()) {

				if (!playing) {
					game.clear();
					animation = false;
					clicked = z;
				} else {
					if (!game.isEmpty() && userTurn < game.size()
							&& z == game.get(userTurn)) {
						correct = true;
					} else {
						correct = false;
						game.clear();
						userTurn = 0;
					}
				}

			}

			if (!globalAnimation) {
				if (playing) {
					if (!correct) {
						playing = false;
						for (SequenceSquare sq : sequenceSquares) {
							sq.startAnimation(false);
						}
						sound(GAME_OVER);
						globalAnimation = true;
						mainActivity.dialogLost();
					} else {
						if (userTurn == lenghtGame) {
							userTurn = 0;
							lenghtGame++;
							int r = rnd.nextInt(NUMBER_SQUARES);
							int last = game.get(game.size() - 1);
							int last2 = game.get(game.size() - 2);
							while (r == last || r == last2) {
								r = rnd.nextInt(NUMBER_SQUARES);
							}
							game.add(r);
							sequenceGameAnimation(true);
						} else {
							userTurn++;
							SequenceSquare square2 = sequenceSquares.get(game
									.get(userTurn - 1));
							animation = true;
							square2.startAnimation(false);
							sound(DING);

						}
					}
				} else {
					game.add(clicked);
					int r = rnd.nextInt(NUMBER_SQUARES);
					while (r == clicked) {
						r = rnd.nextInt(NUMBER_SQUARES);
					}
					game.add(r);
					userTurn = 0;
					lenghtGame = 1;
					playing = true;
					sequenceGameAnimation(false);
				}
			}

		}
	}

	private void onTouchDiscovery(MotionEvent event) {
		int z = 0;
		lastClick = System.currentTimeMillis();
		boolean collision = false;

		for (int i = discoverySquares.size() - 1; i >= 0; i--) {
			DiscoverySquare square = discoverySquares.get(i);

			if (square.isCollition(event.getX(), event.getY())) {
				collision = true;
				z = i;
			}
		}

		if (playing) {
			if (collision && !firstTime) {
				boolean remove = false;
				boolean add = true;

				int index = 0;
				for (int g : game) {
					if (z == g) {
						remove = true;
						break;
					} else {
						index++;
					}

				}
				if (remove) {
					game.remove(index);
					discovery.add(z);
					discoveredSquares++;
				} else {
					for (int d : discovery) {
						if (d == z) {
							add = false;
							break;
						}
					}
					if (add) {
						discovery.add(z);
						currentLives--;
					}

				}
				if (currentLives > 0) {
					if (!game.isEmpty()) {
						if (remove) {
							sound(DING);
							discoverySquares.get(z).startAnimation(false);
						} else if (add) {
							sound(ERROR);
							discoverySquares.get(z).startAnimation(false);
						}
					} else {
						sound(DING);
						discoverySquares.get(z).startAnimation(true);
						while (!discoverySquares.get(z).isFlipAnimation()) {
						}
						while (discoverySquares.get(z).isFlipAnimation()) {
						}
						discoveryNextLevel();

					}
				} else {
					for (DiscoverySquare square : discoverySquares) {
						square.startAnimation(false);
						playing = false;
						if (!dialogCalled) {
							currentLives = LIVES;
							dialogLost();
							sound(GAME_OVER);
						}
						dialogCalled = true;
					}
				}
			}
		} else {
			// currentLives = LIVES;
			// mainActivity.restartDiscovery();
		}
	}

	private void onTouchMemory(MotionEvent event) {
		int z = 0;
		lastClick = System.currentTimeMillis();
		boolean collision = false;

		for (int i = memorySquares.size() - 1; i >= 0; i--) {
			MemorySquare square = memorySquares.get(i);

			if (square.isCollition(event.getX(), event.getY())) {
				collision = true;
				z = i;
			}
		}

		if (collision && !animation) {
			if (mainActivity.getStartTime() == 0) {
				mainActivity.setStartTime(System.currentTimeMillis());

			}
			if (!memorySquares.get(z).getDiscovered()) {
				memorySquares.get(z).setVisible(true);
				switch (selectedCards) {
				case 0:
					sound(SELECT);
					currentSquareSelected = z;
					selectedCards = 1;
					break;
				case 1:
					if (selectedCards == 1) {
						if (currentSquareSelected != z) {
							if (game.get(currentSquareSelected) == game.get(z)) {
								memorySquares.get(z).setDiscovered(true);
								memorySquares.get(currentSquareSelected)
										.setDiscovered(true);
								sound(DING);
								int gm = game.get(z);
								int index = 0;
								for (int m : memory) {
									if (m == gm) {
										break;
									} else {
										index++;
									}
								}

								memory.remove(index);

								if (memory.size() == 1) {
									incremented = false;
								}

								if (memory.isEmpty() && !incremented) {
									incremented = true;
									mainActivity.incrementMemoryLevel();

								}

							} else {
								memorySquares.get(z).startAnimation(false);
								memorySquares.get(currentSquareSelected)
										.startAnimation(false);
								sound(ERROR);
								currentSquareSelected = -1;

							}
							selectedCards = 0;

						}
						break;
					}
				}
			}
		}

	}

	private void onTouchComplementary(MotionEvent event) {
		int z = 0;

		boolean collision = false;

		for (int i = complementarySquares.size() - 1; i >= 0; i--) {
			ComplementarySquare square = complementarySquares.get(i);

			if (square.isCollition(event.getX(), event.getY())) {
				collision = true;
				z = i;
			}
		}

		if (collision && z != lastComplementarySquareTouched
				&& System.currentTimeMillis() - lastWin > 500) {

			if (isAdiacent(z, lastComplementarySquareTouched)) {
				boolean backward = false;
				if (complementaryGame.size() >= 2
						&& complementaryGame.get(complementaryGame.size() - 2) == z) {
					backward = true;
				}

				if (!backward) {
					complementarySquares.get(z).nextColor();
					lastComplementarySquareTouched = z;
					complementaryGame.add(z);
					currentMoves++;
					if (currentMoves > perfectMoves) {
						isPlaying = false;
						resetComplementary();
						sound(ERROR);
					} else {
						sound(SELECT);
					}
				} else {
					complementarySquares.get(lastComplementarySquareTouched)
							.lastColor();
					complementaryGame.remove(complementaryGame.size() - 1);
					lastComplementarySquareTouched = z;
					currentMoves--;

					sound(SELECT);

				}
				boolean win = true;
				int j = 0;
				for (ComplementarySquare square : complementarySquares) {
					if (square.getCurrentColor() != mainActivity
							.getComplementaryLevels().get(0).endGame.get(j)) {
						win = false;
						break;
					}
					j++;
				}
				if (win) {
					complementaryWin(COLUMNS, ROWS);
				}
				setSignals();

			} else {
				if (System.currentTimeMillis() - lastError > 300) {
					sound(BLOCK);
					lastError = System.currentTimeMillis();
				}

			}
		}

	}

	private void complementaryWin(int columns, int rows) {
		sound(DING);
		if (perfectMoves == currentMoves) {
			Toast.makeText(mainActivity, "PERFECT", Toast.LENGTH_SHORT).show();
		}
		COLUMNS = columns;
		ROWS = rows;
		NUMBER_SQUARES = COLUMNS * ROWS;
		createComplementaryLevel(COLUMNS, ROWS);
	}

	private boolean isAdiacent(int z, int last) {
		if (last == -1) {
			return true;
		}
		int cz = (z + 1) % COLUMNS;
		int rz = (z + 1) / COLUMNS + 1;
		if (cz == 0) {
			cz = COLUMNS;
			rz -= 1;
		}

		int clast = (last + 1) % COLUMNS;
		int rlast = (last + 1) / COLUMNS + 1;
		if (clast == 0) {
			clast = COLUMNS;
			rlast -= 1;
		}

		if (cz == clast && (rz == rlast + 1 || rz == rlast - 1)) {
			return true;
		}
		if (rz == rlast && (cz == clast + 1 || cz == clast - 1)) {
			return true;
		}
		return false;
	}

	private ArrayList<Integer> getAdiacents(int z, int beforeLast) {
		ArrayList<Integer> adiacents = new ArrayList<Integer>();

		if (z / COLUMNS > 0 && z - COLUMNS != beforeLast) {
			adiacents.add(z - COLUMNS);
		}
		if (z / COLUMNS < (ROWS - 1) && z + COLUMNS != beforeLast) {
			adiacents.add(z + COLUMNS);
		}
		if (z % COLUMNS > 0 && z - 1 != beforeLast) {
			adiacents.add(z - 1);
		}
		if (z % COLUMNS < (COLUMNS - 1) && z + 1 != beforeLast) {
			adiacents.add(z + 1);
		}

		return adiacents;
	}

	private ArrayList<Integer> getRedAdiacents(int z, int beforeLast,
			ArrayList<Integer> game, int endColor, boolean first) {
		ArrayList<Integer> adiacents = new ArrayList<Integer>();

		if (game.get(z) != endColor || first) {

			if (z / COLUMNS > 0 && z - COLUMNS != beforeLast
					&& game.get(z - COLUMNS) != endColor) {
				adiacents.add(z - COLUMNS);
			}
			if (z / COLUMNS < (ROWS - 1) && z + COLUMNS != beforeLast
					&& game.get(z + COLUMNS) != endColor) {
				adiacents.add(z + COLUMNS);
			}
			if (z % COLUMNS > 0 && z - 1 != beforeLast
					&& game.get(z - 1) != endColor) {
				adiacents.add(z - 1);
			}
			if (z % COLUMNS < (COLUMNS - 1) && z + 1 != beforeLast
					&& game.get(z + 1) != endColor) {
				adiacents.add(z + 1);
			}
		}
		return adiacents;

	}

	private void setSignals() {

		for (ComplementarySquare square : complementarySquares) {
			square.setPenultimo(false);
			square.setUltimo(false);
		}
		if (isPlaying) {
			if (complementaryGame.size() >= 2) {
				complementarySquares.get(
						complementaryGame.get(complementaryGame.size() - 2))
						.setPenultimo(true);
			}
			if (!complementaryGame.isEmpty()) {
				complementarySquares.get(
						complementaryGame.get(complementaryGame.size() - 1))
						.setUltimo(true);
			}
		}
	}

	public void discoveryNextLevel() {
		lenghtGame++;
		discovery.clear();
		gameLoopThread.setRunning(false);
		while (gameLoopThread.isWorking()) {
		}

		discoverySquares.clear();
		// if (lenghtGame < NUMBER_SQUARES / 2) {
		if (lenghtGame < mainActivity.getDiscoveryLevels().size()) {
			discoveryLevel = mainActivity.getDiscoveryLevels().get(lenghtGame);
			if (discoveryLevel.columns != COLUMNS
					|| discoveryLevel.rows != ROWS) {
				initializeSquareSize(discoveryLevel.columns,
						discoveryLevel.rows);
			}
			createSquares();
			gameLoopThread = new GameLoopThread(this);
			gameLoopThread.setRunning(true);
			gameLoopThread.start();
		} else {
			mainActivity.incrementDiscoveryLevel();
		}
	}

	public void sequenceGameAnimation(boolean last) {
		isWaterfall = true;
		if (last) {
			SequenceSquare square = sequenceSquares
					.get(game.get(game.size() - 2));
			square.startAnimationWaterfall(0);
		} else {
			if (waterfall < game.size()) {
				SequenceSquare square = sequenceSquares
						.get(game.get(waterfall));
				square.startAnimationWaterfall(lenghtGame + 1);
				waterfall++;
			} else {
				waterfall = 0;
				isWaterfall = false;
			}
		}
	}

	public static void shuffleArray(ArrayList<Integer> a) {
		int n = a.size();
		Random random = new Random();
		random.nextInt();
		for (int i = 0; i < n; i++) {
			int change = i + random.nextInt(n - i);
			swap(a, i, change);
		}
	}

	private static void swap(ArrayList<Integer> a, int i, int change) {
		int helper = a.get(i);
		int c = a.get(change);
		if (i < change) {
			a.remove(i);
			a.remove(change - 1);
			a.add(i, c);
			a.add(change, helper);
		} else {
			a.remove(i);
			a.remove(change);
			a.add(change, helper);
			a.add(i, c);
		}

	}

	public void setRunning(boolean run) {
		gameLoopThread.setRunning(run);
	}

	public void setPaused(boolean pause) {
		gameLoopThread.setPaused(pause);
	}

	public boolean isWorking() {
		return gameLoopThread.isWorking();
	}

	public long getFPS() {
		return mainActivity.getFPS();
	}

	public int getGameType() {
		return gameType;
	}

	public void setFristTime(boolean b) {
		firstTime = b;
	}

	public int getColor(int index) {
		return colors.get(index);
	}

	public int getSelectedCards() {
		return selectedCards;
	}

	public void setSelectedCards(int i) {
		selectedCards = i;
	}

	public int getDiscoveredSquares() {
		return discoveredSquares;
	}

	public int getGameLenght() {
		return lenghtGame;
	}

	public void resetGameLenght() {
		lenghtGame = 0;
	}

	public void setGlobalAnimation(boolean b) {
		globalAnimation = b;
	}

	public void dialogLost() {
		mainActivity.dialogLost();
	}

	public void incrementMemoryLevel() {
		mainActivity.incrementMemoryLevel();
	}

	public boolean getDialogCalled() {
		return dialogCalled;
	}

	public void setDialogCalled(boolean b) {
		dialogCalled = b;
	}

	public int getLevel() {
		return mainActivity.getLevel();
	}
	public ComplementaryLevelData getComplementaryLevel() {
		return mainActivity.getComplementaryLevels().get(0);
	}

	public void setAnimation(boolean b) {
		animation = b;
	}

	public boolean isPlaying() {
		return playing;
	}

	public MainActivity getActivityContext() {
		return mainActivity;
	}

	public void squareAnimationCallBack(int gametype) {
		switch (gametype) {

		case SEQUENCE:
			break;

		case DISCOVERY:
			// discoveryNextLevel();
			break;

		case MEMORY:
			memorySquares.get(memorySquares.size() - 1).setDiscovered(false);
			mainActivity.incrementMemoryLevel();
			break;

		}

	}

	private int randomColor(int color) {
		int myColor = 0;
		if (color < 0) {
			myColor = Color.rgb(rnd.nextInt(256), rnd.nextInt(256),
					rnd.nextInt(256));
		} else {
			switch (color) {
			case 0:
				myColor = this.getResources().getColor(
						com.testgame.R.color.yellow);
				break;
			case 1:
				myColor = this.getResources().getColor(
						com.testgame.R.color.blue);
				break;
			case 2:
				myColor = this.getResources().getColor(
						com.testgame.R.color.brown);
				break;
			case 3:
				myColor = this.getResources()
						.getColor(com.testgame.R.color.red);
				break;
			case 4:
				myColor = this.getResources().getColor(
						com.testgame.R.color.purple);
				break;
			case 5:
				myColor = this.getResources().getColor(
						com.testgame.R.color.dark_green);
				break;
			case 6:
				myColor = this.getResources().getColor(
						com.testgame.R.color.fucsia);
				break;
			case 7:
				myColor = this.getResources().getColor(
						com.testgame.R.color.verdeacqua_scuro);
				break;
			case 8:
				myColor = this.getResources().getColor(
						com.testgame.R.color.gold);
				break;
			case 9:
				myColor = this.getResources().getColor(
						com.testgame.R.color.green);
				break;
			case 10:
				myColor = this.getResources().getColor(
						com.testgame.R.color.indian_red);
				break;
			case 11:
				myColor = this.getResources().getColor(
						com.testgame.R.color.steel_blue);
				break;
			case 12:
				myColor = this.getResources().getColor(
						com.testgame.R.color.lemon);
				break;
			case 13:
				myColor = this.getResources().getColor(
						com.testgame.R.color.light_gray);
				break;
			case 14:
				myColor = this.getResources().getColor(
						com.testgame.R.color.light_green);
				break;
			case 15:
				myColor = this.getResources().getColor(
						com.testgame.R.color.violetto);
				break;
			case 16:
				myColor = this.getResources().getColor(
						com.testgame.R.color.marrone);
				break;
			case 17:
				myColor = this.getResources().getColor(
						com.testgame.R.color.white);
				break;
			case 18:
				myColor = this.getResources().getColor(
						com.testgame.R.color.cyan);
				break;
			case 19:
				myColor = this.getResources().getColor(
						com.testgame.R.color.chocolate);
				break;
			case 20:
				myColor = this.getResources().getColor(
						com.testgame.R.color.kakhi);
				break;
			case 21:
				myColor = this.getResources().getColor(
						com.testgame.R.color.salmon);
				break;
			case 22:
				myColor = this.getResources().getColor(
						com.testgame.R.color.rosa);
				break;
			case 23:
				myColor = this.getResources().getColor(
						com.testgame.R.color.giallo_chiaro);
				break;
			case 24:
				myColor = this.getResources().getColor(
						com.testgame.R.color.verdeacqua);
				break;
			case 25:
				myColor = this.getResources().getColor(
						com.testgame.R.color.light_seagreen);
				break;
			case 26:
				myColor = this.getResources().getColor(
						com.testgame.R.color.orange);
				break;
			}
		}
		return myColor;
	}

	public void restartgameLoop() {
		gameLoopThread.setRunning(false);
		while (gameLoopThread.isWorking()) {
		}
		gameLoopThread = new GameLoopThread(this);
		gameLoopThread.setRunning(true);
		gameLoopThread.start();
	}

	public void sound(int sound) {
		switch (sound) {

		case NO_SOUND:
			break;

		case GAME_OVER:
			sounds.play(gameOver, volume, volume, 1, 0, 1f);
			break;

		case DING:
			sounds.play(ding, volume, volume, 1, 0, 1.5f);
			break;
		case ERROR:
			sounds.play(error, volume, volume, 1, 0, 1.5f);
			break;
		case SELECT:
			sounds.play(select, volume, volume, 1, 0, 1.5f);
			break;
		case BLOCK:
			sounds.play(block, volume, volume, 1, 0, 1.5f);
			break;
		}

	}

	public void initSound() {
		AudioManager mgr = (AudioManager) getContext().getSystemService(
				Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		final float streamVolumeMax = mgr
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volume = streamVolumeCurrent / streamVolumeMax;
		sounds = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		ding = sounds.load(getContext(), R.raw.newmail, 1);
		gameOver = sounds.load(getContext(), R.raw.gameover, 1);
		error = sounds.load(getContext(), R.raw.error, 1);
		select = sounds.load(getContext(), R.raw.select, 1);
		block = sounds.load(getContext(), R.raw.block, 1);
		sounds.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				// sounds.play(ding, volume, volume, 1, 0, 2f);
			}
		});

	}

	public void createComplementaryLevel(int columns, int rows) {
		isCreatingLevel = true;
		isPlaying = false;
		while (isDrawingComplementary) {
		}

		perfectMoves = 0;
		currentMoves = 0;

		while (perfectMoves == 0) {

			ArrayList<Integer> colors = new ArrayList<Integer>();

			initializeComplementarySquareSize(columns, rows);
			ComplementaryLevelData level = new ComplementaryLevelData();
			int difficulty = mainActivity.getComplementaryDifficulty();

			int type = rnd.nextInt(3) + difficulty * 3;
			int color = 0;
			colors = initializeColors(type);

			int tempBackground = BACKGROUND;
			while (tempBackground == BACKGROUND) {
				color = rnd.nextInt(2 + difficulty);
				setGameBackgroundColor(type, color);
			}

			for (int i = 0; i < columns * rows; i++) {
				level.squaresType.add(type);
				level.firstColor.add(color);
				level.endGame.add(color);
			}

			int lenght = rnd.nextInt(5) + COLUMNS * 5;

			int current = rnd.nextInt(columns * rows);
			int last = current;
			int beforeLast = current;

			for (int i = 1; i < lenght; i++) {

				ArrayList<Integer> adiacents = getAdiacents(last, beforeLast);
				current = adiacents.get(rnd.nextInt(adiacents.size()));

				nextColor(level.firstColor, colors, current);
				beforeLast = last;
				last = current;

			}

			mainActivity.getComplementaryLevels().clear();
			mainActivity.getComplementaryLevels().add(level);
			initializeComplementary();
			lastWin = System.currentTimeMillis();

			perfectMoves = everyWay(mainActivity.getComplementaryLevels()
					.get(0).firstColor, color, colors);

		}

		// findOnePerfectSolution();

		// initPossibilities3x3();

		isCreatingLevel = false;

		// resetComplementary();

	}

	/*
	 * private void initPossibilities3x3() { ArrayList<ComplementaryLevelData>
	 * possibilities = new ArrayList<ComplementaryLevelData>();
	 * 
	 * for (int q = 1; q < 20; q++) { // possibilities.clear(); for (int counter
	 * = 0; counter < 10000; counter++) { ComplementaryLevelData level = new
	 * ComplementaryLevelData(); int type = 1; int color = PRIMARY; for (int i =
	 * 0; i < 9; i++) { level.squaresType.add(type);
	 * level.firstColor.add(color); level.endGame.add(color); } int lenght = q;
	 * int beforeLast = -1; int last; int current = last = rnd.nextInt(9);
	 * 
	 * if (level.firstColor.get(current) == PRIMARY) {
	 * level.firstColor.remove(current); level.firstColor.add(current,
	 * SECONDARY);
	 * 
	 * } else { level.firstColor.remove(current); level.firstColor.add(current,
	 * PRIMARY); }
	 * 
	 * for (int i = 1; i < lenght; i++) {
	 * 
	 * while (current == last || current == beforeLast || !isAdiacent(current,
	 * last)) { current = rnd.nextInt(9); } if (level.firstColor.get(current) ==
	 * PRIMARY) { level.firstColor.remove(current);
	 * level.firstColor.add(current, SECONDARY);
	 * 
	 * } else { level.firstColor.remove(current); level.firstColor.add(current,
	 * PRIMARY); } beforeLast = last; last = current;
	 * 
	 * }
	 * 
	 * boolean add = true;
	 * 
	 * for (ComplementaryLevelData l : possibilities) { if
	 * (l.firstColor.equals(level.firstColor)) { add = false; break; } }
	 * 
	 * if (add) { possibilities.add(level); }
	 * 
	 * // Log.i("counter",Integer.toString(q) + " " + //
	 * Integer.toString(counter));
	 * 
	 * }
	 * 
	 * Log.i("MosquitoLabs", "possibilitˆ " + Integer.toString(q) + " " +
	 * Integer.toString(possibilities.size()));
	 * 
	 * } String a = new String(); for (int x :
	 * possibilities.get(possibilities.size() - 1).firstColor) { a +=
	 * Integer.toString(x); a += " "; }
	 * 
	 * Log.i("MosquitoLabs", "difficile " + a);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * private void findOnePerfectSolution() {
	 * 
	 * // SCEGLI I QUADRATI DA CUI INIZIARE int MAX = 4; // int nextSquare = 0;
	 * int last = 0; int beforeLast = 0; int nearbySquares = 4;
	 * 
	 * ArrayList<Integer> startSquares = new ArrayList<Integer>();
	 * ArrayList<Integer> nextSquares = new ArrayList<Integer>();
	 * 
	 * ArrayList<Integer> solve = new ArrayList<Integer>();
	 * 
	 * for (int i = 0; i < NUMBER_SQUARES; i++) { int x = 0; if
	 * (mainActivity.getComplementaryLevels().get(0).firstColor.get(i) !=
	 * mainActivity .getComplementaryLevels().get(0).endGame.get(i)) { for (int
	 * z = 0; z < NUMBER_SQUARES; z++) { if (z != i &&
	 * mainActivity.getComplementaryLevels().get(0).firstColor .get(z) !=
	 * mainActivity .getComplementaryLevels().get(0).endGame .get(z) &&
	 * isAdiacent(z, i)) { x++; } } if (x <= nearbySquares) { ArrayList<Integer>
	 * remove = new ArrayList<Integer>(); for (int j = startSquares.size() - 1;
	 * j >= 0; j--) { if (startSquares.get(j) > x) { remove.add(j); } } for (int
	 * r : remove) { startSquares.remove(r); } nearbySquares = x;
	 * startSquares.add(i);
	 * 
	 * } } }
	 * 
	 * // CERCA SOLUZIONE OTTIMA PER OGNI PUNTO DI PARTENZA for (int startSquare
	 * : startSquares) { ways.clear(); ArrayList<Integer> w = new
	 * ArrayList<Integer>(); w.add(startSquare); ways.add(w); solve.clear(); for
	 * (int color : mainActivity.getComplementaryLevels().get(0).firstColor) {
	 * solve.add(color); }
	 * 
	 * beforeLast = last = startSquare; invertColor(solve, last);
	 * 
	 * boolean finished = true; int solution = 0; for (ArrayList<Integer> way :
	 * ways) { finished = true; solve.clear(); for (int color :
	 * mainActivity.getComplementaryLevels().get(0).firstColor) {
	 * solve.add(color); } for (int x : way) { invertColor(solve, x); }
	 * 
	 * for (int color : solve) { if (color !=
	 * mainActivity.getComplementaryLevels().get(0).endGame .get(0)) { finished
	 * = false; break; } } if (finished) { break; } solution++; }
	 * 
	 * int iterazioni = 0;
	 * 
	 * while (!finished && iterazioni < 18) { tempWays.clear(); for
	 * (ArrayList<Integer> way : ways) { tempWays.add(way); } for
	 * (ArrayList<Integer> way : tempWays) {
	 * 
	 * solve.clear(); for (int color :
	 * mainActivity.getComplementaryLevels().get( 0).firstColor) {
	 * solve.add(color); } for (int x : way) { invertColor(solve, x); }
	 * 
	 * beforeLast = last = way.get(way.size() - 1); if (way.size() > 1) {
	 * beforeLast = way.get(way.size() - 2); } // SE NON COLORATO if
	 * ((solve.get(last) == mainActivity
	 * .getComplementaryLevels().get(0).endGame.get(0)) || (checkCicle(solve,
	 * last, beforeLast))) { nextSquares = nextColoredSquare(solve, last,
	 * beforeLast); if (!nextSquares.isEmpty()) { for (int n : nextSquares) {
	 * ArrayList<Integer> tempWay = new ArrayList<Integer>(); for (int h : way)
	 * { tempWay.add(h); } tempWay.add(n); ways.remove(0); ways.add(tempWay); }
	 * 
	 * } else { nextSquares = nextEmptySquare(solve, last, beforeLast); if
	 * (!nextSquares.isEmpty()) { for (int n : nextSquares) { ArrayList<Integer>
	 * tempWay = new ArrayList<Integer>(); for (int h : way) { tempWay.add(h); }
	 * tempWay.add(n); ways.remove(0); ways.add(tempWay); } }
	 * 
	 * } }
	 * 
	 * // SE COLORATO
	 * 
	 * else { nextSquares = nextEmptySquare(solve, last, beforeLast); if
	 * (!nextSquares.isEmpty()) { for (int n : nextSquares) { ArrayList<Integer>
	 * tempWay = new ArrayList<Integer>(); for (int h : way) { tempWay.add(h); }
	 * tempWay.add(n); ways.remove(0); ways.add(tempWay); } } else { nextSquares
	 * = nextColoredSquare(solve, last, beforeLast); if (!nextSquares.isEmpty())
	 * { for (int n : nextSquares) { ArrayList<Integer> tempWay = new
	 * ArrayList<Integer>(); for (int h : way) { tempWay.add(h); }
	 * tempWay.add(n); ways.remove(0); ways.add(tempWay); } } } } }
	 * 
	 * // CONTROLLA SE GIOCO FINITO O POSSIBILITA CICLI SEMPLICI finished =
	 * true; solution = 0; for (ArrayList<Integer> way : ways) { finished =
	 * true; solve.clear(); for (int color :
	 * mainActivity.getComplementaryLevels().get( 0).firstColor) {
	 * solve.add(color); } for (int x : way) { invertColor(solve, x); }
	 * 
	 * for (int color : solve) { if (color !=
	 * mainActivity.getComplementaryLevels().get( 0).endGame.get(0)) { finished
	 * = false; break; } } if (finished) { break; } solution++; }
	 * 
	 * iterazioni++; }
	 * 
	 * if (finished) { String a = new String(); for (int s : ways.get(solution))
	 * { a += Integer.toString(s + 1); a += " "; }
	 * 
	 * Log.e("MosquitoLabs", "solution: " + a); } else { Log.e("MosquitoLabs",
	 * "solution not found"); }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * private ArrayList<Integer> nextColoredSquare(ArrayList<Integer> game, int
	 * last, int beforeLast) { int nearbySquares = 4; ArrayList<Integer>
	 * nextSquare = new ArrayList<Integer>(); ArrayList<Integer> remove = new
	 * ArrayList<Integer>(); int i = 0; if (last % 2 == 0) { i = 1; } for (; i <
	 * NUMBER_SQUARES && i <= last + ROWS; i += (COLUMNS - 1)) { if (i != last
	 * && i != beforeLast && game.get(i) !=
	 * mainActivity.getComplementaryLevels() .get(0).endGame.get(i) &&
	 * isAdiacent(i, last)) {
	 * 
	 * int z = 0; if (i % 2 == 0) { z = 1; } int x = 0; for (; z <
	 * NUMBER_SQUARES && z <= i + ROWS; z += (COLUMNS - 1)) { if (z != i && z !=
	 * last && game.get(z) != mainActivity
	 * .getComplementaryLevels().get(0).endGame .get(z) && isAdiacent(z, i)) {
	 * x++; } } remove.clear(); if (x <= nearbySquares) { if (x < nearbySquares
	 * && false) { for (int n : nextSquare) { if (n > x) { remove.add(n); } }
	 * 
	 * for (int g = remove.size() - 1; g >= 0; g--) {
	 * nextSquare.remove(remove.get(g)); } } // nearbySquares = x;
	 * nextSquare.add(i); }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * return nextSquare; }
	 * 
	 * private ArrayList<Integer> nextEmptySquare(ArrayList<Integer> game, int
	 * last, int beforeLast) { int nearbySquares = 0; ArrayList<Integer>
	 * nextSquare = new ArrayList<Integer>(); ArrayList<Integer> remove = new
	 * ArrayList<Integer>();
	 * 
	 * int i = 0; if (last % 2 == 0) { i = 1; } for (; i < NUMBER_SQUARES && i
	 * <= last + ROWS; i += (COLUMNS - 1)) { if (i != last && i != beforeLast &&
	 * game.get(i) == mainActivity.getComplementaryLevels()
	 * .get(0).endGame.get(i) && isAdiacent(i, last)) {
	 * 
	 * int z = 0; if (i % 2 == 0) { z = 1; } int x = 0; for (; z <
	 * NUMBER_SQUARES && z <= i; z += (COLUMNS - 1)) { if (z != i && z != last
	 * && game.get(z) != mainActivity .getComplementaryLevels().get(0).endGame
	 * .get(z) && isAdiacent(z, i)) { x++; } }
	 * 
	 * if (x >= nearbySquares) {
	 * 
	 * if (x > nearbySquares && false) { for (int n : nextSquare) { if (n < x) {
	 * remove.add(n); } }
	 * 
	 * for (int g = remove.size() - 1; g >= 0; g--) {
	 * nextSquare.remove(remove.get(g)); } } // nearbySquares = x;
	 * nextSquare.add(i); }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * return nextSquare; }
	 * 
	 * 
	 * 
	 * private boolean checkCicle(ArrayList<Integer> solve, int l, int b) { int
	 * last = l; int beforeLast = b; ArrayList<Integer> game = new
	 * ArrayList<Integer>(); for (int g : solve) { game.add(g); } int lenght =
	 * 0;
	 * 
	 * for (int g : game) { if (g !=
	 * mainActivity.getComplementaryLevels().get(0).endGame .get(0)) { lenght++;
	 * } }
	 * 
	 * // CERCA UN COLORATO ADIACENTE int iterazioni = 0; while (iterazioni <
	 * lenght) { ArrayList<Integer> next = nextColoredSquare(game, last,
	 * beforeLast); if (next.isEmpty()) { return false; } else { int nextSquare
	 * = next.get(0); beforeLast = last; last = nextSquare; invertColor(game,
	 * nextSquare); iterazioni++;
	 * 
	 * }
	 * 
	 * }
	 * 
	 * for (int g : game) { if (g !=
	 * mainActivity.getComplementaryLevels().get(0).endGame .get(0)) { return
	 * false; } }
	 * 
	 * Log.e("MosquitoLabs", "simpleCicle"); return true;
	 * 
	 * // if (checkSimpleCicle(game)) {
	 * 
	 * // }
	 * 
	 * // CONTROLLA SEQUENZA SEMPLICE
	 * 
	 * }
	 */

	/*
	 * private boolean checkSimpleCicle(ArrayList<Integer> solve) { //
	 * solution.clear();
	 * 
	 * // SCEGLI UN QUADRATO DA CUI INIZIARE int MAX = 4; int nextSquare = 0;
	 * int last = 0; int beforeLast = 0; int nearbySquares = 4;
	 * 
	 * for (int i = 0; i < NUMBER_SQUARES; i++) { int x = 0; if
	 * (mainActivity.getComplementaryLevels().get(0).firstColor.get(i) !=
	 * mainActivity .getComplementaryLevels().get(0).endGame.get(i)) { for (int
	 * z = 0; z < NUMBER_SQUARES; z++) { if (z != i &&
	 * mainActivity.getComplementaryLevels().get(0).firstColor .get(z) !=
	 * mainActivity .getComplementaryLevels().get(0).endGame .get(z) &&
	 * isAdiacent(z, i)) { x++; } } if (x < nearbySquares) { nearbySquares = x;
	 * beforeLast = last = i; } } }
	 * 
	 * invertColor(solve, last); // solution.add(last); boolean finished =
	 * false; finished = true; for (int color : solve) { if (color !=
	 * mainActivity.getComplementaryLevels().get(0).endGame .get(0)) { finished
	 * = false; break; } }
	 * 
	 * int iterazioni = 0; int max = 0; for (int color : solve) { if (color !=
	 * mainActivity.getComplementaryLevels().get(0).endGame .get(0)) { max++; }
	 * }
	 * 
	 * while (!finished && iterazioni < max) { // SE NON COLORATO if
	 * (solve.get(last) == mainActivity.getComplementaryLevels().get(0).endGame
	 * .get(0)) { nextSquare = nextColoredSquare(solve, last, beforeLast); if
	 * (nextSquare > 0) { beforeLast = last; last = nextSquare; } else {
	 * nextSquare = nextEmptySquare(solve, last, beforeLast); if (nextSquare >
	 * 0) { beforeLast = last; last = nextSquare; } }
	 * 
	 * }
	 * 
	 * // SE COLORATO
	 * 
	 * else { nextSquare = nextEmptySquare(solve, last, beforeLast); if
	 * (nextSquare > 0) { beforeLast = last; last = nextSquare; } else {
	 * nextSquare = nextColoredSquare(solve, last, beforeLast); if (nextSquare >
	 * 0) { beforeLast = last; last = nextSquare; } } }
	 * 
	 * invertColor(solve, last); // solution.add(last);
	 * 
	 * // CONTROLLA SE GIOCO FINITO O POSSIBILITA CICLI SEMPLICI
	 * checkCicle(solve, last, beforeLast);
	 * 
	 * finished = true; for (int color : solve) { if (color !=
	 * mainActivity.getComplementaryLevels().get(0).endGame .get(0)) { finished
	 * = false; break; } }
	 * 
	 * iterazioni++;
	 * 
	 * }
	 * 
	 * return finished;
	 * 
	 * }
	 * 
	 * /*
	 * 
	 * // private int nextRandomEmptySquare() {
	 * 
	 * // }
	 */
	private int everyWay(ArrayList<Integer> firstColors, int endColor,
			ArrayList<Integer> colors) {
		solution.clear();
		long sTime = System.currentTimeMillis();
		boolean found = false;
		boolean keepSearching = true;
		ArrayList<ArrayList<ArrayList<Integer>>> squareWays = new ArrayList<ArrayList<ArrayList<Integer>>>();
		ArrayList<ArrayList<ArrayList<Integer>>> tempSquareWays = new ArrayList<ArrayList<ArrayList<Integer>>>();
		ArrayList<ArrayList<Integer>> solutions = new ArrayList<ArrayList<Integer>>();

		int coloredSquares = 0;

		for (int color : firstColors) {
			if (color != endColor) {
				coloredSquares++;
			}
		}

		if (coloredSquares <= 1 || coloredSquares == NUMBER_SQUARES) {
			keepSearching = false;
		}

		// int counter = 0;

		while (keepSearching) {
			tempSquareWays.clear();
			for (ArrayList<ArrayList<Integer>> ways : squareWays) {
				tempSquareWays.add(ways);
			}

			for (int i = 0; i < NUMBER_SQUARES; i++) {
				int last = i;
				int beforeLast = -1;
				ArrayList<ArrayList<Integer>> ways = new ArrayList<ArrayList<Integer>>();
				ArrayList<ArrayList<Integer>> tempWays = new ArrayList<ArrayList<Integer>>();
				if (i == tempSquareWays.size() || tempSquareWays.isEmpty()) {
					ArrayList<Integer> w = new ArrayList<Integer>();
					w.add(last);
					ways.clear();
					ways.add(w);
					squareWays.add(ways);
				} else {
					ways.clear();
					ways = squareWays.get(i);
				}

				// for (int z = 0; z < 17; z++) {

				tempWays.clear();
				for (ArrayList<Integer> way : ways) {
					tempWays.add(way);
				}

				// INCREASE EACH WAY'S LENGHT

				for (ArrayList<Integer> way : tempWays) {

					if (way.size() > 1) {
						beforeLast = way.get(way.size() - 2);
					}
					last = way.get(way.size() - 1);

					ways.remove(0);

					ArrayList<Integer> adiacents = getAdiacents(last,
							beforeLast);
					for (int q : adiacents) {
						ArrayList<Integer> tempWay = new ArrayList<Integer>();
						for (int h : way) {
							tempWay.add(h);
						}
						tempWay.add(q);
						ways.add(tempWay);
					}

				}

				// CHECK IF FOUND SOLUTION

				// found = true;
				int p = 0;
				ArrayList<Integer> remove = new ArrayList<Integer>();

				for (ArrayList<Integer> way : ways) {
					boolean circle = false;
					int numberOfAdiacents = 0;
					int numberOfColored = 0;
					ArrayList<Integer> cycle = new ArrayList<Integer>();
					ArrayList<Integer> solution = new ArrayList<Integer>();
					ArrayList<Integer> solve = new ArrayList<Integer>();

					for (int color : firstColors) {
						solve.add(color);
					}
					for (int s : way) {
						nextColor(solve, colors, s);
					}

					int square = 0;
					for (int sol : solve) {
						numberOfAdiacents += getRedAdiacents(square, square,
								solve, endColor, false).size();
						if (endColor != sol) {
							numberOfColored++;
						}
						square++;
					}
					found = true;

					if ((numberOfColored == 1 || numberOfColored < 3
							&& numberOfAdiacents == numberOfColored)
							|| (numberOfColored > 2 && (numberOfColored - 1) * 2 <= numberOfAdiacents)) {

						last = way.get(way.size() - 1);
						beforeLast = way.get(way.size() - 2);

						for (int y = 0; y < numberOfColored; y++) {

							int numberOfColoredAdiacents = 0;
							int c = 0;
							int next = 0;
							ArrayList<Integer> adiacents = getRedAdiacents(
									last, beforeLast, solve, endColor, true);
							if (!adiacents.isEmpty()) {
								for (int ad : adiacents) {
									int x = getRedAdiacents(ad, last, solve,
											endColor, false).size();
									if (x > numberOfColoredAdiacents) {
										numberOfColoredAdiacents = x;
										next = c;
									}
									c++;
								}

								beforeLast = last;
								last = adiacents.get(next);
								cycle.add(last);
								nextColor(solve, colors, last);
							} else {
								next = 0;
								found = false;
								break;
							}

						}

					}
					if (found) {
						for (int sol : solve) {
							if (endColor != sol) {
								found = false;
								break;
							}
						}
					}

					if (found) {
						keepSearching = false;
						String a = new String();
						for (int c : way) {
							solution.add(c);
						}
						for (int c : cycle) {
							a += (c + 1);
							a += " ";
							solution.add(c);
						}
						Log.i("mosquitoLabs", a);

						solutions.add(solution);

						break;
					} else if (way.get(0) == way.get(way.size() - 1)) {
						circle = true;
						int c = 0;
						for (int sol : solve) {
							if (firstColors.get(c) != sol) {
								circle = false;
								break;
							}
						}
					}
					if (circle) {
						remove.add(0, p);
					}

					p++;
				}

				for (int r : remove) {
					ways.remove(r);
				}

				// CHECK EASY SOLUTION

				// squareWays.remove(0);
				// squareWays.add(newWays);

			}
			//
			// counter++;

		}

		int sol = 0;
		int c = 0;
		int size = 0;
		String a = new String();

		if (!solutions.isEmpty()) {
			size = solutions.get(0).size();

			for (ArrayList<Integer> solution : solutions) {
				if (solution.size() < size) {
					sol = c;
					size = solution.size();
				}
				c++;
			}

			for (int s : solutions.get(sol)) {
				a += Integer.toString(s + 1);
				a += " ";
			}
		} else {
			sol = 5;
		}

		if (coloredSquares > 1 && coloredSquares < size) {
			long eTime = System.currentTimeMillis();
			Log.e("timeEnd", Long.toString(eTime - sTime) + "millis");
			Log.e("numero mosse", Integer.toString(size));
			Log.e("solution", a);
		} else {
			return 0;
		}

		return (size);

	}

	private void nextColor(ArrayList<Integer> game, ArrayList<Integer> colors,
			int index) {
		int currentColor = game.get(index);
		if (currentColor < colors.size() - 1) {
			game.remove(index);
			currentColor++;
			game.add(index, currentColor);
		} else {
			game.remove(index);
			currentColor = 0;
			game.add(index, currentColor);
		}

	}

	private void resetComplementary() {
		complementaryGame.clear();
		currentMoves = 0;
		int i = 0;
		for (ComplementarySquare square : complementarySquares) {
			lastComplementarySquareTouched = -1;
			square.setPenultimo(false);
			square.setUltimo(false);
			square.setCurrentColor(mainActivity.getComplementaryLevels().get(0).firstColor
					.get(i));

			i++;
		}
	}

	private ArrayList<Integer> initializeColors(int type) {
		ArrayList<Integer> colors = new ArrayList<Integer>();

		switch (type) {
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

		case YELLOW_PURPLE_GREEN:
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
		return colors;
	}

	private void setGameBackgroundColor(int type, int color) {

		ArrayList<Integer> colors = initializeColors(type);
		BACKGROUND = colors.get(color);
		paint.setColor(BACKGROUND);
	}

}
