package com.testgame;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private GameView gameView = null;
	private MenuItem settings;
	private int checked = -1;
	private final static int BEGINNER = 0;
	private final static int NORMAL = 1;
	private final static int HARD = 2;
	private final static int CUSTOM = -1;
	private final static int SEQUENCE = 0;
	private final static int DISCOVERY = 1;
	private final static int MEMORY = 2;
	private final static int COMPLEMENTARY = 3;
	private int gameType;
	private long startTime = 0;
	private long beginnerTime = 0;
	private long normalTime = 0;
	private long hardTime = 0;
	private boolean firstTime = true;
	private Button sequence;
	private Button discovery;
	private Button memory;
	private Button complementary;

	ComplementaryLevelSquareData colored = new ComplementaryLevelSquareData();
	ComplementaryLevelSquareData iceBlock = new ComplementaryLevelSquareData();
	ComplementaryLevelSquareData coloredAndWall = new ComplementaryLevelSquareData();
	ComplementaryLevelSquareData iceBlockAndWall = new ComplementaryLevelSquareData();
	ComplementaryLevelSquareData invisible = new ComplementaryLevelSquareData();

	private int complementaryDifficulty = 0;

	static final long FPS = 45;

	private ArrayList<DiscoveryLevelData> discoveryLevels = new ArrayList<DiscoveryLevelData>();
	private ArrayList<ComplementaryLevelData> complementaryLevels = new ArrayList<ComplementaryLevelData>();

	private int discoveryGameType = 0;

	final static int D_REDS = 0;
	final static int D_RAINBOW = 1;
	final static int D_RAINBOW_BOMBS = 2;
	final static int D_REDS_RAINBOW = 3;
	final static int D_SIMILAR_RED = 4;
	final static int D_SIMILAR_MULTI = 5;

	private final static int YELLOW_PURPLE = 0;
	private final static int BLUE_ORANGE = 1;
	private final static int GREEN_RED = 2;

	private final static int PRIMARY = 3;
	private final static int SECONDARY = 4;

	final static int EMPTY = 0;
	final static int COLORED = 1;
	final static int ICE_BLOCK = 2;
	final static int ICE_BLOCK_BROKEN = 3;
	final static int INVISIBLE = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initialize();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		settings = menu.findItem(R.id.menu_settings);
		settings.setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			settings(gameType);
			break;
		}

		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onPause() {
		try {
			while (gameView.isWorking()) {
			}
			gameView.setPaused(true);
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		try {
			gameView.setPaused(false);
		} catch (Exception e) {
			// TODO: handle exception
		}

		super.onResume();

		// setRunning(true);

	}

	@Override
	public void onBackPressed() {

		backToMenu();

	}

	private void initialize() {

		setContentView(R.layout.activity_main);
		sequence = (Button) findViewById(R.id.buttonSequence);
		discovery = (Button) findViewById(R.id.buttonDiscovery);
		memory = (Button) findViewById(R.id.buttonMemory);
		complementary = (Button) findViewById(R.id.buttonSquareEyes);

		sequence.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gameType = SEQUENCE;
				settings(gameType);
			}
		});
		discovery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initializeDiscovery();
				startDiscovery(1);
			}
		});
		memory.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checked = BEGINNER;
				gameType = MEMORY;
				setRunning(false);
				startTime = 0;
				gameView = new GameView(MainActivity.this, 2, 3, gameType);
				setContentView(gameView);
			}
		});

		complementary.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// initializeComplementary();
				gameType = COMPLEMENTARY;
				initializeComplementaryLevels();
				startComplementary(0);

				// settings(gameType);
			}
		});

	}

	private void backToMenu() {
		setPaused(true);

		// da mettere menu di scelta

		setRunning(false);
		initialize();
	}

	private void initializeDiscovery() {

		// 3X3

		createDiscoveryLevel(3, 3, 2, 0, 0);
		createDiscoveryLevel(3, 3, 3, 0, 0);
		createDiscoveryLevel(3, 3, 4, 0, 0);

		// 3X4

		createDiscoveryLevel(3, 4, 3, 0, 0);
		createDiscoveryLevel(3, 4, 4, 0, 0);
		createDiscoveryLevel(3, 4, 5, 0, 0);

		// 4X4

		createDiscoveryLevel(4, 4, 4, 0, 0);
		createDiscoveryLevel(4, 4, 5, 0, 0);
		createDiscoveryLevel(4, 4, 6, 0, 0);

		// 4X5

		createDiscoveryLevel(4, 5, 5, 0, 0);
		createDiscoveryLevel(4, 5, 6, 0, 0);
		createDiscoveryLevel(4, 5, 7, 0, 0);

		// 5X5

		createDiscoveryLevel(5, 5, 6, 0, 0);
		createDiscoveryLevel(5, 5, 7, 0, 0);
		createDiscoveryLevel(5, 5, 8, 0, 0);

		// 5X6

		createDiscoveryLevel(5, 6, 7, 0, 0);
		createDiscoveryLevel(5, 6, 8, 0, 0);
		createDiscoveryLevel(5, 6, 9, 0, 0);
		createDiscoveryLevel(5, 6, 10, 0, 0);

		// 5X7

		createDiscoveryLevel(5, 7, 9, 0, 0);
		createDiscoveryLevel(5, 7, 10, 0, 0);
		createDiscoveryLevel(5, 7, 11, 0, 0);
		createDiscoveryLevel(5, 7, 12, 0, 0);

	}

	private void createDiscoveryLevel(int columns, int rows, int squares,
			int viewTime, int gameTime) {
		DiscoveryLevelData level = new DiscoveryLevelData();
		level.columns = columns;
		level.rows = rows;
		level.squares = squares;
		discoveryLevels.add(level);
	}

	private void startDiscovery(int level) {
		gameType = DISCOVERY;
		setRunning(false);
		DiscoveryLevelData levelData = discoveryLevels.get(level - 1);

		gameView = new GameView(MainActivity.this, levelData.columns,
				levelData.rows, gameType);
		setContentView(gameView);
	}

	private void initializeComplementary() {
		// 3X3
		int type = 0;
		int color = 0;
		int end = 0;
		ComplementaryLevelData level = new ComplementaryLevelData();
		for (int i = 0; i < 9; i++) {
			level.columns = 3;
			level.rows = 3;
			switch (i + 1) {
			case 1:
				type = YELLOW_PURPLE;
				color = PRIMARY;
				end = PRIMARY;
				break;

			case 2:
				type = YELLOW_PURPLE;
				color = PRIMARY;
				end = PRIMARY;
				break;

			case 3:
				type = YELLOW_PURPLE;
				color = PRIMARY;
				end = PRIMARY;
				break;

			case 4:
				type = YELLOW_PURPLE;
				color = PRIMARY;
				end = PRIMARY;
				break;

			case 5:
				type = GREEN_RED;
				color = PRIMARY;
				end = SECONDARY;
				break;

			case 6:
				type = YELLOW_PURPLE;
				color = PRIMARY;
				end = PRIMARY;
				break;

			case 7:
				type = YELLOW_PURPLE;
				color = PRIMARY;
				end = PRIMARY;
				break;

			case 8:
				type = YELLOW_PURPLE;
				color = PRIMARY;
				end = PRIMARY;
				break;

			case 9:
				type = YELLOW_PURPLE;
				color = PRIMARY;
				end = PRIMARY;
				break;
			}
			level.squaresType = type;
			level.game.add(color);
			level.endColor = end;
		}
		complementaryLevels.add(level);

	}

	public ArrayList<DiscoveryLevelData> getDiscoveryLevels() {
		return discoveryLevels;
	}

	private void startComplementary(int level) {
		gameType = COMPLEMENTARY;
		setRunning(false);
		ComplementaryLevelData levelData = complementaryLevels.get(level);

		gameView = new GameView(MainActivity.this, levelData.columns,
				levelData.rows, gameType);
		setContentView(gameView);
	}

	public ArrayList<ComplementaryLevelData> getComplementaryLevels() {
		return complementaryLevels;
	}

	private void settings(final int gameType) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

		switch (gameType) {
		case SEQUENCE:
			final CharSequence[] itemsSeq = { "Beginner", "Normal", "Hard",
					"Custom" };
			builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle("Game Style");
			builder.setSingleChoiceItems(itemsSeq, checked,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							if (item != checked || firstTime) {

								switch (item) {
								case 0:
									checked = BEGINNER;
									setRunning(false);

									gameView = new GameView(MainActivity.this,
											2, 3, gameType);
									break;

								case 1:
									checked = NORMAL;
									setRunning(false);

									gameView = new GameView(MainActivity.this,
											4, 6, gameType);

									break;

								case 2:
									checked = HARD;
									setRunning(false);

									gameView = new GameView(MainActivity.this,
											6, 9, gameType);

									break;
								case 3:
									checked = CUSTOM;
									custom();

									break;

								}
								if (checked != CUSTOM) {
									setContentView(gameView);
								} else {
									if (firstTime) {
										firstTime = false;
									}
								}

							}

							dialog.dismiss();

						}
					});
			AlertDialog alert = builder.create();
			alert.show();
			break;

		case COMPLEMENTARY:

			final CharSequence[] itemsCom = { "Two Colors", "Three Colors" };
			builder = new AlertDialog.Builder(MainActivity.this);

			builder.setTitle("Game Style");
			builder.setSingleChoiceItems(itemsCom, checked,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							switch (item) {
							case 0:
								complementaryDifficulty = item;

								break;

							case 1:

								complementaryDifficulty = item;

								break;

							}

							startComplementary(0);

							dialog.dismiss();

						}
					});
			AlertDialog alertCom = builder.create();
			alertCom.show();

			break;
		}

	}

	private void custom() {
		LayoutInflater inflater = getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.custom,
				(ViewGroup) getCurrentFocus());
		final EditText textColumns = (EditText) dialoglayout
				.findViewById(R.id.editTextColumns);
		final EditText textRow = (EditText) dialoglayout
				.findViewById(R.id.editTextRows);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(dialoglayout);
		builder.setTitle("Title")
				.setCancelable(false)
				.setNegativeButton("Back",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								settings(gameType);
							}
						})
				.setPositiveButton("Go", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {

						int rows = Integer.parseInt(textRow.getText()
								.toString());
						int columns = Integer.parseInt(textColumns.getText()
								.toString());
						setRunning(false);
						gameView = new GameView(MainActivity.this, columns,
								rows, 0);
						setContentView(gameView);

					}
				});

		builder.show();
	}

	private void setRunning(boolean run) {
		if (gameView != null) {
			gameView.setRunning(run);
		}
	}

	private void setPaused(boolean pause) {
		if (gameView != null) {
			gameView.setPaused(pause);
		}
	}

	public void incrementDiscoveryLevel() {
		discoveryGameType++;
		setRunning(false);
		gameView = new GameView(MainActivity.this, 3, 3, gameType);
		setContentView(gameView);
	}

	public void incrementSequenceLevel() {

	}

	public void incrementMemoryLevel() {

		switch (checked) {
		case BEGINNER:
			checked = NORMAL;
			setRunning(false);
			gameView = new GameView(MainActivity.this, 4, 6, gameType);
			beginnerTime = System.currentTimeMillis() - startTime;
			setContentView(gameView);
			break;

		case NORMAL:
			checked = HARD;
			setRunning(false);
			gameView = new GameView(MainActivity.this, 6, 9, gameType);
			normalTime = System.currentTimeMillis() - startTime;
			setContentView(gameView);
			break;

		case HARD:
			hardTime = System.currentTimeMillis() - startTime;
			dialogWin();
			break;
		}
	}

	public void restartDiscovery() {

		discoveryGameType = 0;
		setRunning(false);
		gameView = new GameView(MainActivity.this, 4, 6, gameType);
		setContentView(gameView);

	}

	public void dialogWin() {
		MainActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				String time = Long.toString(beginnerTime + normalTime
						+ hardTime);
				String bTime = Long.toString(beginnerTime);
				String nTime = Long.toString(normalTime + 1000);
				String hTime = Long.toString(hardTime);

				time = time.substring(0, time.length() - 3);
				bTime = bTime.substring(0, bTime.length() - 3);
				nTime = nTime.substring(0, nTime.length() - 3);
				hTime = hTime.substring(0, hTime.length() - 3);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle("Congratulations!");
				builder.setMessage("You completed the Memory game in " + time
						+ " seconds!\n\nBEGINNER: " + bTime + "\nNORMAL: "
						+ nTime + "\nHARD: " + hTime);
				builder.setPositiveButton("Play Again",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								checked = BEGINNER;
								setRunning(false);
								startTime = 0;
								gameView = new GameView(MainActivity.this, 2,
										3, gameType);
								setContentView(gameView);
								dialog.dismiss();

							}
						});
				builder.setNegativeButton("Back to menu",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								backToMenu();
							}
						});

				AlertDialog alert = builder.create();
				alert.show();
			}
		});

	}

	public void dialogLost() {
		MainActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle("Game Over");
				builder.setCancelable(false);
				switch (gameType) {
				case SEQUENCE:
					String level = "";
					switch (checked) {
					case BEGINNER:
						level = "beginner";
						break;
					case NORMAL:
						level = "normal";
						break;
					case HARD:
						level = "hard";
						break;
					case CUSTOM:
						level = "custom";
						break;
					}
					String sequence = "";
					if (gameView.getGameLenght() < 2) {
						sequence = "0";
					} else {
						sequence = Integer.toString(gameView.getGameLenght());
					}
					builder.setMessage("Game Over!\nYou completed a sequence of "
							+ sequence + " squares in " + level + " mode!");
					break;

				case DISCOVERY:
					int squares = 0;
					switch (checked) {
					case BEGINNER:
						break;
					case NORMAL:
						squares = 6;
						break;
					case HARD:
						squares = 84;
						break;
					}

					squares += gameView.getDiscoveredSquares();

					builder.setMessage("Game Over!\nYou dicovered "
							+ Integer.toString(squares) + " red squares!");
					break;
				}
				builder.setPositiveButton("Play Again",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								switch (gameType) {
								case SEQUENCE:
									gameView.resetGameLenght();
									gameView.setDialogCalled(false);
									gameView.setGlobalAnimation(false);
									dialog.dismiss();
									break;

								case DISCOVERY:
									restartDiscovery();
									dialog.dismiss();

									break;
								}

							}
						});
				builder.setNegativeButton("Back to menu",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								backToMenu();
							}
						});

				AlertDialog alert = builder.create();
				alert.show();
			}
		});

	}

	public int getLevel() {
		return checked;
	}

	public void setStartTime(long t) {
		startTime = t;
	}

	public long getStartTime() {
		return startTime;
	}

	public int getDiscoveryGameType() {
		return discoveryGameType;
	}

	public long getFPS() {
		return FPS;
	}

	public int getComplementaryDifficulty() {
		return complementaryDifficulty;
	}

	private void initializeComplementaryLevels() {

		complementaryLevels.clear();

		createComplementaryLevel(new int[] { EMPTY, COLORED, EMPTY, EMPTY,
				COLORED, EMPTY, EMPTY, COLORED, EMPTY }, new int[] {},
				new int[] { 4 }, 3, 3);

		// TUTORIAL
		createComplementaryLevel(new int[] { EMPTY, EMPTY, COLORED, EMPTY,
				EMPTY, COLORED, EMPTY, EMPTY, COLORED }, new int[] {},
				new int[] {}, 3, 3);
		createComplementaryLevel(new int[] { EMPTY, COLORED, EMPTY, EMPTY,
				EMPTY, COLORED, EMPTY, COLORED, COLORED }, new int[] {},
				new int[] {}, 3, 3);

		// NORMAL
		createComplementaryLevel(new int[] { COLORED, EMPTY, EMPTY, EMPTY,
				EMPTY, EMPTY, COLORED, COLORED, EMPTY }, new int[] {},
				new int[] {}, 3, 3);
		createComplementaryLevel(new int[] { EMPTY, EMPTY, COLORED, EMPTY,
				EMPTY, EMPTY, EMPTY, COLORED, EMPTY }, new int[] {},
				new int[] {}, 3, 3);
		createComplementaryLevel(new int[] { COLORED, EMPTY, EMPTY, EMPTY,
				EMPTY, EMPTY, COLORED, EMPTY, COLORED }, new int[] {},
				new int[] {}, 3, 3);
		createComplementaryLevel(new int[] { EMPTY, COLORED, EMPTY, EMPTY,
				EMPTY, EMPTY, EMPTY, COLORED, EMPTY }, new int[] {},
				new int[] {}, 3, 3);
		createComplementaryLevel(new int[] { COLORED, EMPTY, EMPTY, EMPTY,
				EMPTY, EMPTY, EMPTY, EMPTY, COLORED }, new int[] {},
				new int[] {}, 3, 3);
		createComplementaryLevel(new int[] { EMPTY, EMPTY, EMPTY, EMPTY,
				COLORED, EMPTY, COLORED, EMPTY, COLORED }, new int[] {},
				new int[] {}, 3, 3);
		createComplementaryLevel(new int[] { EMPTY, COLORED, EMPTY, COLORED,
				EMPTY, COLORED, EMPTY, COLORED, EMPTY }, new int[] {},
				new int[] {}, 3, 3);

		// INVISIBLE
		createComplementaryLevel(new int[] { COLORED, EMPTY, EMPTY, INVISIBLE,
				EMPTY, EMPTY, EMPTY, INVISIBLE, INVISIBLE, EMPTY, COLORED,
				EMPTY, INVISIBLE, COLORED, EMPTY, COLORED }, new int[] {},
				new int[] {}, 4, 4);

		// ICE_BLOCK
		createComplementaryLevel(new int[] { COLORED, EMPTY, EMPTY, ICE_BLOCK,
				EMPTY, EMPTY, EMPTY, COLORED, EMPTY }, new int[] {},
				new int[] {}, 3, 3);

		// WALL
		createComplementaryLevel(new int[] { EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
				EMPTY, COLORED, COLORED, COLORED }, new int[] { 5 },
				new int[] {}, 3, 3);

		createComplementaryLevel(new int[] { EMPTY, COLORED, EMPTY, EMPTY,
				COLORED, EMPTY, EMPTY, COLORED, EMPTY }, new int[] {},
				new int[] { 4 }, 3, 3);

	}

	private void createComplementaryLevel(int[] squares, int[] verticalWalls,
			int[] horizontalWalls, int rows, int columns) {
		ComplementaryLevelData level = new ComplementaryLevelData();
		level.rows = rows;
		level.columns = columns;

		for (int square : squares) {
			level.game.add(square);
			level.verticalWalls.add(0);
			level.horizontalWalls.add(0);
		}

		for (int vWall : verticalWalls) {
			level.verticalWalls.remove(vWall);
			level.verticalWalls.add(vWall, 1);
		}
		for (int hWall : horizontalWalls) {
			level.horizontalWalls.remove(hWall);
			level.horizontalWalls.add(hWall, 1);
		}

		complementaryLevels.add(level);

	}
}
