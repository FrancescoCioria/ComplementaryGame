package com.testgame;

import java.io.Serializable;
import java.util.ArrayList;

public class ComplementaryLevelData implements Serializable {
	private static final long serialVersionUID = 1L;

	public ArrayList<Integer> game = new ArrayList<Integer>();
	// public ArrayList<Integer> endGame = new ArrayList<Integer>();
	public ArrayList<Integer> verticalWalls = new ArrayList<Integer>();
	public ArrayList<Integer> horizontalWalls = new ArrayList<Integer>();
	public ArrayList<Integer> iceBlocks = new ArrayList<Integer>();

	public int rows = 1;
	public int columns = 1;
	public int squaresType = 0;
	public int startSquare = 0;
	public int star = 0;
	public int endColor = 0;

}
