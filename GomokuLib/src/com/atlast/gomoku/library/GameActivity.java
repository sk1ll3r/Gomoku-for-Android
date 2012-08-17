package com.atlast.gomoku.library;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Hashtable;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.atlast.gomoku.library.GameView.ICellListener;
import com.atlast.gomoku.library.GameView.State;

public class GameActivity extends Activity {

	public static final String EXTRA_START_PLAYER = "com.atlast.library.GameActivity.EXTRA_START_PLAYER";
	public static final String EXTRA_DEPTH = "com.atlast.library.GameActivity.EXTRA_DEPTH";
	public static final int BOARD_SIZE = 15;
	public static final String[] FILENAME = { "gomoku_transposition_table0", "gomoku_transposition_table1", "gomoku_transposition_table2", "gomoku_transposition_table3" };
	private int DEPTH = 0;
	private Hashtable<BoardState, NextMove> transpositionTable = new Hashtable<BoardState, NextMove>();

	private GameView mGameView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.lib_game);
		initializeViews();
	}

	@Override
	protected void onResume() {
		super.onResume();

		DEPTH = getIntent().getIntExtra(EXTRA_DEPTH, 0);
		readTranspositionTableFromFile();

		State player = mGameView.getCurrentPlayer();
		if (player == State.UNKNOWN) {
			player = State.fromInt(getIntent().getIntExtra(EXTRA_START_PLAYER, 1));
			if (!checkGameFinished()) {
				selectTurn(player);
			}
		}
		if (player == State.PLAYER2) {
			computerMove();
		}
		if (player == State.WIN) {
			setWinState(mGameView.getWinner());
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		writeTranspositionTableToFile();
	}

	// -----

	private void initializeViews() {
		mGameView = (GameView) findViewById(R.id.game_view);
		mGameView.setFocusable(true);
		mGameView.setFocusableInTouchMode(true);
		mGameView.setCellListener(new MyCellListener());
	}

	@SuppressWarnings("unchecked")
	private void readTranspositionTableFromFile() {
		try {
			FileInputStream fis = openFileInput(FILENAME[DEPTH]);
			ObjectInputStream ois = new ObjectInputStream(fis);
			transpositionTable = (Hashtable<BoardState, NextMove>) ois.readObject();
			ois.close();
			deleteFile(FILENAME[DEPTH]);
		} catch (FileNotFoundException e) {
			transpositionTable = new Hashtable<BoardState, NextMove>();
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			transpositionTable = new Hashtable<BoardState, NextMove>();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			transpositionTable = new Hashtable<BoardState, NextMove>();
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeTranspositionTableToFile() {
		try {
			FileOutputStream fos = openFileOutput(FILENAME[DEPTH], Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(transpositionTable);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void selectTurn(State player) {
		mGameView.setCurrentPlayer(player);

		if (player == State.PLAYER1) {
			mGameView.setEnabled(true);
		} else if (player == State.PLAYER2) {
			mGameView.setEnabled(false);
		}
	}

	private void computerMove() {
		mGameView.stopBlink();
		new Thread(new Runnable() {

			@Override
			public void run() {
				final NextMove move = alphaBetaSearch();
				mGameView.post(new Runnable() {
					public void run() {
						mGameView.setCell(move.getMoveRow(), move.getMoveCol(), State.PLAYER2);
						mGameView.startBlink(move.getMoveRow(), move.getMoveCol());
						finishTurn();
					}
				});
			}

		}).start();
	}

	private void finishTurn() {
		State player = mGameView.getCurrentPlayer();
		if (!checkGameFinished()) {
			player = getOtherPlayer(player);
			selectTurn(player);
			if (player == State.PLAYER2) {
				computerMove();
			}
		}
	}

	private State getOtherPlayer(State player) {
		return player == State.PLAYER1 ? State.PLAYER2 : State.PLAYER1;
	}

	private boolean checkGameFinished() {
		if (checkBoardFull()) {
			setFinished(State.EMPTY);
			return true;
		} else if (checkWin(State.PLAYER1, mGameView.getData())) {
			setFinished(State.PLAYER1);
			return true;
		} else if (checkWin(State.PLAYER2, mGameView.getData())) {
			setFinished(State.PLAYER2);
			return true;
		}
		return false;
	}

	private void setFinished(State player) {
		mGameView.setCurrentPlayer(State.WIN);
		mGameView.setWinner(player);
		mGameView.setEnabled(false);

		setWinState(player);
	}

	private void setWinState(State player) {

	}

	private class MyCellListener implements ICellListener {
		public void onCellSelected() {

		}

		public void onCellConfirmed() {
			State player = mGameView.getCurrentPlayer();

			if (player == State.WIN) {
				GameActivity.this.finish();
			} else if (player == State.PLAYER1) {
				int col = mGameView.getSelectionCol();
				int row = mGameView.getSelectionRow();
				if (col >= 0 && row >= 0) {
					mGameView.setCell(row, col, player);
					finishTurn();
				}
			}
		}
	}

	// Start AI

	private NextMove alphaBetaSearch() {
		State[][] data = new State[BOARD_SIZE + 2][BOARD_SIZE + 2];
		NextMove AIMove = new NextMove();
		BoardState currentState = new BoardState();

		for (int i = 0; i < BOARD_SIZE + 2; i++) {
			for (int j = 0; j < BOARD_SIZE + 2; j++) {
				data[i][j] = mGameView.getData()[i][j];
			}
		}

		currentState.setState(data);
		currentState.setMove(1, 1);
		if (isEmpty()) {
			AIMove.setMove(((GameActivity.BOARD_SIZE + 2) / 2), ((GameActivity.BOARD_SIZE + 2) / 2));
		} else if (transpositionTable.containsKey(currentState)) {
			AIMove = (NextMove) transpositionTable.get(currentState);
		} else {
			AIMove = maxValue(currentState, State.PLAYER2, 0, -9999999, 9999999);
			transpositionTable.put(currentState, AIMove);
		}
		return AIMove;
	}

	private NextMove maxValue(BoardState currentState, State whoseTurn, int depth, int alpha, int beta) {
		Log.d("GAME_ACTIVITY - AI", "maxValue");
		int nOfSuccessors = getSuccessors(currentState.getState(), whoseTurn).length;
		int eval;
		int minVal;
		int tempAlpha = alpha;

		BoardState[] successors = new BoardState[nOfSuccessors];
		successors = getSuccessors(currentState.getState(), whoseTurn);
		NextMove result = new NextMove();

		eval = evaluate(currentState.getState(), whoseTurn);
		if (cutoffTest(depth) || ((depth > 0) && ((eval > 800) || (eval < -800)))) {
			result.setMaxValue(eval);
			result.setMove(currentState.getMoveRow(), currentState.getMoveCol());
		} else {
			result.setMaxValue(-999999999);
			for (int i = 0; i < nOfSuccessors; i++) {
				minVal = minValue(successors[i], getOtherPlayer(whoseTurn), (depth + 1), tempAlpha, beta).getMaxValue();
				if (minVal > result.getMaxValue()) {
					result.setMaxValue(minVal);
					result.setMove(successors[i].getMoveRow(), successors[i].getMoveCol());
				}
				if (result.getMaxValue() >= beta)
					return result;
				tempAlpha = max(tempAlpha, result.getMaxValue());
			}
		}
		return result;
	}

	private NextMove minValue(BoardState currentState, State whoseTurn, int depth, int alpha, int beta) {
		Log.d("GAME_ACTIVITY - AI", "minValue");
		int nOfSuccessors = getSuccessors(currentState.getState(), whoseTurn).length;
		int eval;
		int maxVal;
		int tempBeta = beta;

		BoardState[] successors = new BoardState[nOfSuccessors];
		successors = getSuccessors(currentState.getState(), whoseTurn);
		NextMove result = new NextMove();

		eval = evaluate(currentState.getState(), whoseTurn);
		if (cutoffTest(depth) || (eval > 800) || (eval < -800)) {
			result.setMaxValue(eval);
			result.setMove(currentState.getMoveRow(), currentState.getMoveCol());
		} else {
			result.setMaxValue(999999999);
			for (int i = 0; i < nOfSuccessors; i++) {
				maxVal = maxValue(successors[i], getOtherPlayer(whoseTurn), (depth + 1), alpha, tempBeta).getMaxValue();
				if (maxVal < result.getMaxValue()) {
					result.setMaxValue(maxVal);
					result.setMove(successors[i].getMoveRow(), successors[i].getMoveCol());
				}
				if (result.getMaxValue() <= alpha)
					return result;
				tempBeta = min(tempBeta, result.getMaxValue());
			}
		}
		return result;
	}

	private BoardState[] getSuccessors(State[][] currentState, State whoseTurn) {
		boolean[][] possibleMoves = new boolean[GameActivity.BOARD_SIZE + 2][GameActivity.BOARD_SIZE + 2];

		for (int i = 0; i < GameActivity.BOARD_SIZE + 2; i++) {
			for (int j = 0; j < GameActivity.BOARD_SIZE + 2; j++) {
				possibleMoves[i][j] = false;
			}
		}

		for (int i = 1; i < (GameActivity.BOARD_SIZE + 2 - 1); i++) {
			for (int j = 1; j < (GameActivity.BOARD_SIZE + 2 - 1); j++) {
				if ((currentState[i][j] == State.PLAYER1) || (currentState[i][j] == State.PLAYER2)) {
					if ((i > 1) && (currentState[i - 1][j] == State.EMPTY)) {
						possibleMoves[i - 1][j] = true;
						if ((j > 1) && (currentState[i - 1][j - 1] == State.EMPTY)) {
							possibleMoves[i - 1][j - 1] = true;
						}
						if ((j < GameActivity.BOARD_SIZE + 2 - 2) && (currentState[i - 1][j + 1] == State.EMPTY)) {
							possibleMoves[i - 1][j + 1] = true;
						}
					}
					if ((i < GameActivity.BOARD_SIZE + 2 - 2) && (currentState[i + 1][j] == State.EMPTY)) {
						possibleMoves[i + 1][j] = true;
						if ((j > 1) && (currentState[i + 1][j - 1] == State.EMPTY)) {
							possibleMoves[i + 1][j - 1] = true;
						}
						if ((j < GameActivity.BOARD_SIZE + 2 - 2) && (currentState[i + 1][j + 1] == State.EMPTY)) {
							possibleMoves[i + 1][j + 1] = true;
						}
					}
					if ((j > 1) && (currentState[i][j - 1] == State.EMPTY)) {
						possibleMoves[i][j - 1] = true;
					}
					if ((j < GameActivity.BOARD_SIZE + 2 - 2) && (currentState[i][j + 1] == State.EMPTY)) {
						possibleMoves[i][j + 1] = true;
					}
				}
			}
		}
		int k = 0;
		for (int i = 1; i < (GameActivity.BOARD_SIZE + 2 - 1); i++) {
			for (int j = 1; j < (GameActivity.BOARD_SIZE + 2 - 1); j++) {
				if (possibleMoves[i][j]) {
					k++;
				}
			}
		}
		BoardState[] result = new BoardState[k];
		for (int m = 0; m < k; m++) {
			result[m] = new BoardState();
			for (int i = 0; i < GameActivity.BOARD_SIZE + 2; i++) {
				for (int j = 0; j < GameActivity.BOARD_SIZE + 2; j++) {
					// TODO investigate if can use assignment array = array
					result[m].setState(i, j, currentState[i][j]);
				}
			}
		}
		int m = 0;
		for (int i = 1; i < (GameActivity.BOARD_SIZE + 2 - 1); i++) {
			for (int j = 1; j < (GameActivity.BOARD_SIZE + 2 - 1); j++) {
				if (possibleMoves[i][j]) {
					result[m].setMove(i, j);
					result[m].setState(i, j, whoseTurn);
					m++;
				}
			}
		}
		return result;
	}

	private int evaluate(State[][] gameState, State x) {
		// @formatter:off
		if(checkWin(State.PLAYER2, gameState)) return 10000;
		if(checkWin(State.PLAYER1, gameState)) return -10000;
		
		int result = 0;
		
		PatternCounts myPatternCounts = new PatternCounts(gameState, x);
		
		//Heuristics
		if(myPatternCounts.D>0) result = 5000;
		else if(myPatternCounts.A>0) result = -5000;
		else if(myPatternCounts.B>1) result = -1000;
		else if((myPatternCounts.B==1)&&(myPatternCounts.C>0)) result = -1000;
		else if(myPatternCounts.E>1) result = 1000;
		else if(myPatternCounts.E==1){
			if(myPatternCounts.B==1) result = 0;
			else result = 1000;
		} else if(myPatternCounts.C>1) result = -1000;
		else result = 0;
		
		//Weighted Linear Function
		int myEval	=	myPatternCounts.my_broken2_value*10 + 
						myPatternCounts.my_two_value*11 + 
						myPatternCounts.my_brokenCovered3_value*30 +
						myPatternCounts.my_covered3_value*33 + 
						myPatternCounts.my_broken3_value*36 +
						myPatternCounts.my_three_value*39 +
						myPatternCounts.my_broken4_value*40 +
						myPatternCounts.my_four_value*45 +
						myPatternCounts.my_straight4_value*49;
		int hisEval	=	myPatternCounts.his_broken2_value*10 + 
						myPatternCounts.his_two_value*11 + 
						myPatternCounts.his_brokenCovered3_value*30 +
						myPatternCounts.his_covered3_value*33 + 
						myPatternCounts.his_broken3_value*36 +
						myPatternCounts.his_three_value*39 +
						myPatternCounts.his_broken4_value*40 +
						myPatternCounts.his_four_value*45 +
						myPatternCounts.his_straight4_value*49;
		if(State.PLAYER2 == x){
			result = result + (myEval - hisEval);
		} else {
			result = -(result + (myEval - hisEval));
		}
		return result;
		// @formatter:on
	}

	private boolean checkWin(State player, State[][] boardToSearch) {
		int[] inARow = new int[4];

		// Check rows and columns
		inARow[0] = 0;
		inARow[1] = 0;
		for (int j = 0; j < GameActivity.BOARD_SIZE + 2; j++) {
			inARow[0] = 0;
			inARow[1] = 0;
			for (int i = 0; i < GameActivity.BOARD_SIZE + 2; i++) {
				if (boardToSearch[i][j] == player) {
					inARow[0]++;
				} else {
					inARow[0] = 0;
				}
				if (boardToSearch[j][i] == player) {
					inARow[1]++;
				} else {
					inARow[1] = 0;
				}
				if ((inARow[0] > 4) || (inARow[1] > 4)) {
					return true;
				}
			}
		}

		// Check diagonals
		inARow[0] = 0;
		inARow[1] = 0;
		for (int i = 0; i < (GameActivity.BOARD_SIZE + 2 - 4); i++) {
			for (int j = 0; j < (GameActivity.BOARD_SIZE + 2 - i); j++) {
				if (boardToSearch[i + j][j] == player) {
					inARow[0]++;
				} else {
					inARow[0] = 0;
				}
				if (boardToSearch[j][i + j] == player) {
					inARow[1]++;
				} else {
					inARow[1] = 0;
				}
				if (boardToSearch[GameActivity.BOARD_SIZE + 2 - i - j - 1][j] == player) {
					inARow[2]++;
				} else {
					inARow[2] = 0;
				}
				if (boardToSearch[GameActivity.BOARD_SIZE + 2 - 1 - j][i + j] == player) {
					inARow[3]++;
				} else {
					inARow[3] = 0;
				}
				if ((inARow[0] > 4) || (inARow[1] > 4) || (inARow[2] > 4) || (inARow[3] > 4)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean checkBoardFull() {
		State data[][] = mGameView.getData();
		for (int i = 1; i < GameActivity.BOARD_SIZE + 1; i++) {
			for (int j = 1; j < GameActivity.BOARD_SIZE + 1; j++) {
				if (data[i][j] == State.EMPTY)
					return false;
			}
		}
		return true;
	}

	boolean cutoffTest(int d) {
		return (d >= DEPTH);
	}

	private boolean isEmpty() {
		State[][] data = mGameView.getData();
		for (int i = 1; i <= GameActivity.BOARD_SIZE; i++) {
			for (int j = 1; j <= GameActivity.BOARD_SIZE; j++) {
				if (data[i][j] != State.EMPTY) {
					return false;
				}
			}
		}
		return true;
	}

	private int max(int a, int b) {
		return a > b ? a : b;
	}

	private int min(int a, int b) {
		return a < b ? a : b;
	}

	// End AI
}