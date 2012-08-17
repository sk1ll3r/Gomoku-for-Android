package com.atlast.gomoku.library;

import java.io.Serializable;
import java.util.Arrays;

import android.util.Log;

import com.atlast.gomoku.library.GameActivity;
import com.atlast.gomoku.library.GameView.State;

public class BoardState implements Serializable {
	private State[][] state;
	private int moveCol;
	private int moveRow;

	public BoardState() {
		state = new State[GameActivity.BOARD_SIZE + 2][GameActivity.BOARD_SIZE + 2];
	}
	
	// for debugging
	public BoardState(int debug) {
		state = new State[GameActivity.BOARD_SIZE + 2][GameActivity.BOARD_SIZE + 2];
		for (int i = 0; i < GameActivity.BOARD_SIZE + 2; i++) {
			for (int j = 0; j < GameActivity.BOARD_SIZE + 2; j++) {
				state[i][j] = State.EMPTY;
			}
		}

		for (int i = 0; i < GameActivity.BOARD_SIZE + 2; i++) {
			state[0][i] = State.EDGE;
			state[GameActivity.BOARD_SIZE + 1][i] = State.EDGE;
			state[i][0] = State.EDGE;
			state[i][GameActivity.BOARD_SIZE + 1] = State.EDGE;
		}
		
		state[5][2] = State.PLAYER1;
		state[2][2] = State.PLAYER1;
		state[5][8] = State.PLAYER1;
		state[3][8] = State.PLAYER2;
		state[2][8] = State.PLAYER2;
		state[14][1] = State.PLAYER2;
		
		moveCol = 2;
		moveRow = 4;
	}

	@Override
	public int hashCode() {
		final int prime = 7793;
		int result = 0;
		for (int i = 1, k = 1; i <= GameActivity.BOARD_SIZE; i++) {
			for (int j = 1; j <= GameActivity.BOARD_SIZE; j++, k = k * 3) {
				result = (result + state[i][j].getValue() * k) % prime;
			}
		}
		Log.d("TRANSPOSITION_TABLE", "BoardState.hashCode() returned " + result);
		return result;
	}

	@Override
	public String toString() {
		String result = "";
		for(int i = 0; i < GameActivity.BOARD_SIZE + 2; i++){
			for(int j = 0; j < GameActivity.BOARD_SIZE + 2; j++){
				result = result + state[i][j].getValue() + " ";
			}
			result = result + "\n";
		}
		result = result + "moveCol = " + moveCol + "\n";
		result = result + "moveRow = " + moveRow + "\n";
		result = result + "hashCode() = " + hashCode() + "\n";
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		BoardState other = (BoardState) obj;
		for(int i = 1; i <= GameActivity.BOARD_SIZE; i++){
			for(int j = 1; j <= GameActivity.BOARD_SIZE; j++){
				if(other.getState()[i][j] != state[i][j]){
					Log.d("TRANSPOSITION_TABLE", "BoardState.equals() returned false");
					return false;
				}
			}
		}
		Log.d("TRANSPOSITION_TABLE", "BoardState.equals() returned true");
		return true;
	}

	public void setState(int i, int j, State move) {
		state[i][j] = move;
	}

	public void setState(State[][] data) {
		this.state = data;
	}

	public State[][] getState() {
		return state;
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
