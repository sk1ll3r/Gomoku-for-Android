package com.atlast.gomoku.library;

import java.io.Serializable;

public class NextMove implements Serializable {
	private int moveCol;
	private int moveRow;
	private int maxValue;

	public NextMove() {
	}
	
	public NextMove(int debug) {
		moveCol = 47;
		moveRow = 22;
		maxValue = -23;
	}
	
	@Override
	public String toString() {
		return "NextMove [moveCol=" + moveCol + ", moveRow=" + moveRow + ", maxValue=" + maxValue + "]";
	}

	public int getMaxValue() {
		return maxValue;
	}
	
	public void setMaxValue(int value) {
		this.maxValue = value;
	}

	public void setMove(int i, int j) {
		moveRow = i;
		moveCol = j;
	}

	public int getMoveCol() {
		return moveCol;
	}

	public void setMoveCol(int moveCol) {
		this.moveCol = moveCol;
	}

	public int getMoveRow() {
		return moveRow;
	}

	public void setMoveRow(int moveRow) {
		this.moveRow = moveRow;
	}
}
