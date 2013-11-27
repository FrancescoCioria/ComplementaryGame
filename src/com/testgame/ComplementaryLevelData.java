package com.testgame;

import java.io.Serializable;
import java.util.ArrayList;

public class ComplementaryLevelData implements Serializable {
	private static final long serialVersionUID = 1L;

	public ArrayList<Integer> squaresType = new ArrayList<Integer>();
	public ArrayList<Integer> firstColor = new ArrayList<Integer>();
	public ArrayList<Integer> endGame = new ArrayList<Integer>();
	
	public int rows = 1;
	public int columns = 1;
	public int startSquare = 0;
	

}
