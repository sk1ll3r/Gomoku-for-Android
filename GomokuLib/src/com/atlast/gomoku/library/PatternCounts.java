package com.atlast.gomoku.library;

import java.util.Arrays;

import com.atlast.gomoku.library.GameView.State;

public class PatternCounts {
	// @formatter:off
	public int
		//MY VALUES
		my_straight4_value,
		my_four_value,
		my_broken4_value,
		my_three_value,
		my_broken3_value,
		my_covered3_value,
		my_brokenCovered3_value,
		my_two_value,
		my_broken2_value,
		//HIS VALUES
		his_straight4_value,
		his_four_value,
		his_broken4_value,
		his_three_value,
		his_broken3_value,
		his_covered3_value,
		his_brokenCovered3_value,
		his_two_value,
		his_broken2_value,
		
		A,
		B,
		C,
		D,
		E;
	// @formatter:on

	public PatternCounts(State[][] gameState, State AIplayer) {
		Patterns myPatterns = new Patterns(AIplayer);
		// MY VALUES
		my_straight4_value = countPatterns(myPatterns.my_straight4, gameState);
		my_four_value = countPatterns(myPatterns.my_four_1, gameState) + countPatterns(myPatterns.my_four_2, gameState) + countPatterns(myPatterns.my_edges_four_1, gameState) + countPatterns(myPatterns.my_edges_four_2, gameState);
		my_broken4_value = countPatterns(myPatterns.my_broken4_1, gameState) + countPatterns(myPatterns.my_broken4_2, gameState) + countPatterns(myPatterns.my_broken4_3, gameState);
		my_three_value = countPatterns(myPatterns.my_three_1, gameState) + countPatterns(myPatterns.my_three_2, gameState) + countPatterns(myPatterns.my_three_3, gameState) + countPatterns(myPatterns.my_edges_three_1, gameState) + countPatterns(myPatterns.my_edges_three_2, gameState);
		my_broken3_value = countPatterns(myPatterns.my_broken3_1, gameState) + countPatterns(myPatterns.my_broken3_2, gameState);
		my_covered3_value = countPatterns(myPatterns.my_covered3_1, gameState) + countPatterns(myPatterns.my_covered3_2, gameState) + countPatterns(myPatterns.my_edges_covered3_1, gameState) + countPatterns(myPatterns.my_edges_covered3_2, gameState);
		my_brokenCovered3_value = countPatterns(myPatterns.my_brokenCovered3_1, gameState) + countPatterns(myPatterns.my_brokenCovered3_2, gameState) + countPatterns(myPatterns.my_brokenCovered3_3, gameState) + countPatterns(myPatterns.my_brokenCovered3_4, gameState) + countPatterns(myPatterns.my_edges_brokenCovered3_1, gameState) + countPatterns(myPatterns.my_edges_brokenCovered3_2, gameState) + countPatterns(myPatterns.my_edges_brokenCovered3_3, gameState) + countPatterns(myPatterns.my_edges_brokenCovered3_4, gameState);
		my_two_value = countPatterns(myPatterns.my_two_1, gameState) + countPatterns(myPatterns.my_two_2, gameState) - countPatterns(myPatterns.my_two_3, gameState);
		my_broken2_value = countPatterns(myPatterns.my_broken2_1, gameState) + countPatterns(myPatterns.my_broken2_2, gameState) - countPatterns(myPatterns.my_broken2_3, gameState);
		// HIS VALUES
		his_straight4_value = countPatterns(myPatterns.his_straight4, gameState);
		his_four_value = countPatterns(myPatterns.his_four_1, gameState) + countPatterns(myPatterns.his_four_2, gameState) + countPatterns(myPatterns.his_edges_four_1, gameState) + countPatterns(myPatterns.his_edges_four_2, gameState);
		his_broken4_value = countPatterns(myPatterns.his_broken4_1, gameState) + countPatterns(myPatterns.his_broken4_2, gameState) + countPatterns(myPatterns.his_broken4_3, gameState);
		his_three_value = countPatterns(myPatterns.his_three_1, gameState) + countPatterns(myPatterns.his_three_2, gameState) + countPatterns(myPatterns.his_three_3, gameState) + countPatterns(myPatterns.his_edges_three_1, gameState) + countPatterns(myPatterns.his_edges_three_2, gameState);
		his_broken3_value = countPatterns(myPatterns.his_broken3_1, gameState) + countPatterns(myPatterns.his_broken3_2, gameState);
		his_covered3_value = countPatterns(myPatterns.his_covered3_1, gameState) + countPatterns(myPatterns.his_covered3_2, gameState) + countPatterns(myPatterns.his_edges_covered3_1, gameState) + countPatterns(myPatterns.his_edges_covered3_2, gameState);
		his_brokenCovered3_value = countPatterns(myPatterns.his_brokenCovered3_1, gameState) + countPatterns(myPatterns.his_brokenCovered3_2, gameState) + countPatterns(myPatterns.his_brokenCovered3_3, gameState) + countPatterns(myPatterns.his_brokenCovered3_4, gameState) + countPatterns(myPatterns.his_edges_brokenCovered3_1, gameState) + countPatterns(myPatterns.his_edges_brokenCovered3_2, gameState) + countPatterns(myPatterns.his_edges_brokenCovered3_3, gameState) + countPatterns(myPatterns.his_edges_brokenCovered3_4, gameState);
		his_two_value = countPatterns(myPatterns.his_two_1, gameState) + countPatterns(myPatterns.his_two_2, gameState) - countPatterns(myPatterns.his_two_3, gameState);
		his_broken2_value = countPatterns(myPatterns.his_broken2_1, gameState) + countPatterns(myPatterns.his_broken2_2, gameState) - countPatterns(myPatterns.his_broken2_3, gameState);

		A = his_straight4_value;
		B = his_four_value + his_broken4_value;
		C = his_three_value + his_broken3_value;
		D = my_straight4_value + my_four_value + my_broken4_value;
		E = my_three_value + my_broken3_value;
	}

	private int countPatterns(State[] typeOfPattern, State[][] boardToSearch) {
		int length = typeOfPattern.length;
		int result = 0;
		State[][] temp = new State[4][length];

		// Check horizontally and vertically.
		for (int i = 0; i < GameActivity.BOARD_SIZE + 2; i++) {
			for (int j = 0; j < GameActivity.BOARD_SIZE + 2 - length; j++) {
				for (int k = 0; k < length; k++) {
					temp[0][k] = boardToSearch[i][j + k];
					temp[1][k] = boardToSearch[j + k][i];
				}
				if (Arrays.equals(temp[0], typeOfPattern)) {
					result++;
				}
				if (Arrays.equals(temp[1], typeOfPattern)) {
					result++;
				}
			}
		}

		// Check diagonals.
		for (int i = 0; i < GameActivity.BOARD_SIZE + 2 - (length - 1); i++) {
			for (int j = 0; j < GameActivity.BOARD_SIZE + 2 - (i + length - 1); j++) {
				for (int k = 0; k < length; k++) {
					temp[0][k] = boardToSearch[i + j + k][j + k];
					temp[1][k] = boardToSearch[j + k][i + j + k];
					temp[2][k] = boardToSearch[GameActivity.BOARD_SIZE + 2 - i - j - k - 1][j + k];
					temp[3][k] = boardToSearch[GameActivity.BOARD_SIZE + 2 - 1 - j - k][i + j + k];
				}
				if (i == 0) {
					if (Arrays.equals(temp[0], typeOfPattern))
						result++;
					if (Arrays.equals(temp[2], typeOfPattern))
						result++;
				} else {
					for (int k = 0; k < 4; k++) {
						if (Arrays.equals(temp[k], typeOfPattern)) {
							result++;
						}
					}
				}
			}
		}
		return result;
	}
}
