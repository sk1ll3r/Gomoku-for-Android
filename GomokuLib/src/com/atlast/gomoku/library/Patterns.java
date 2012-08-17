package com.atlast.gomoku.library;

import com.atlast.gomoku.library.GameView.State;

public class Patterns {
	// @formatter:off
	public State[]
			//MY TOP THREATS
			my_straight4,
			my_four_1,
			my_four_2,
			my_edges_four_1,
			my_edges_four_2,
			my_broken4_1,
			my_broken4_2,
			my_broken4_3,
			my_three_1,
			my_three_2,
			my_three_3,
			my_edges_three_1,
			my_edges_three_2,
			my_broken3_1,
			my_broken3_2,
			//MY WEAK THREATS
			my_covered3_1,
			my_covered3_2,
			my_edges_covered3_1,
			my_edges_covered3_2, 
			my_brokenCovered3_1,
			my_brokenCovered3_2,
			my_brokenCovered3_3,
			my_brokenCovered3_4,
			my_edges_brokenCovered3_1,
			my_edges_brokenCovered3_2,
			my_edges_brokenCovered3_3,
			my_edges_brokenCovered3_4,
			my_two_1,
			my_two_2,
			my_two_3,
			my_broken2_1,
			my_broken2_2,
			my_broken2_3,
			//HIS TOP THREATS
			his_straight4,
			his_four_1,
			his_four_2,
			his_edges_four_1,
			his_edges_four_2,
			his_broken4_1,
			his_broken4_2,
			his_broken4_3,
			his_three_1,
			his_three_2,
			his_three_3,
			his_edges_three_1,
			his_edges_three_2,
			his_broken3_1,
			his_broken3_2,
			//HIS WEAK THREATS
			his_covered3_1,
			his_covered3_2,
			his_edges_covered3_1,
			his_edges_covered3_2,
			his_brokenCovered3_1,
			his_brokenCovered3_2,
			his_brokenCovered3_3,
			his_brokenCovered3_4,
			his_edges_brokenCovered3_1,
			his_edges_brokenCovered3_2,
			his_edges_brokenCovered3_3,
			his_edges_brokenCovered3_4,
			his_two_1,
			his_two_2,
			his_two_3,
			his_broken2_1,
			his_broken2_2,
			his_broken2_3;

	public Patterns(State AIplayer) {
		State x = AIplayer;
		State o = getOtherPlayer(x);
		State e = State.EDGE;
		State n = State.EMPTY;
		
		//MY TOP THREATS
		my_straight4 = new State[]					{n,x,x,x,x,n};
		my_four_1 = new State[]						{o,x,x,x,x,n};
		my_four_2 = new State[]						{n,x,x,x,x,o};
		my_edges_four_1 = new State[]				{e,x,x,x,x,n};
		my_edges_four_2 = new State[]				{n,x,x,x,x,e};
		my_broken4_1 = new State[]					{x,x,n,x,x};
		my_broken4_2 = new State[]					{x,n,x,x,x};
		my_broken4_3 = new State[]					{x,x,x,n,x};
		my_three_1 = new State[]					{o,n,x,x,x,n,n};
		my_three_2 = new State[]					{n,n,x,x,x,n,o};
		my_three_3 = new State[]					{n,n,x,x,x,n,n};
		my_edges_three_1 = new State[]				{e,n,x,x,x,n,n};
		my_edges_three_2 = new State[]				{n,n,x,x,x,n,e};
		my_broken3_1 = new State[]					{n,x,x,n,x,n};
		my_broken3_2 = new State[]					{n,x,n,x,x,n};
		//MY WEAK THREATS
		my_covered3_1 = new State[]					{n,n,x,x,x,o};
		my_covered3_2 = new State[]					{o,x,x,x,n,n};
		my_edges_covered3_1 = new State[]			{n,n,x,x,x,e};
		my_edges_covered3_2 = new State[]			{e,x,x,x,n,n};
		my_brokenCovered3_1 = new State[]			{o,x,x,n,x,n};
		my_brokenCovered3_2 = new State[]			{o,x,n,x,x,n};
		my_brokenCovered3_3 = new State[]			{n,x,n,x,x,o};
		my_brokenCovered3_4 = new State[]			{n,x,x,n,x,o};
		my_edges_brokenCovered3_1 = new State[]		{e,x,x,n,x,n};
		my_edges_brokenCovered3_2 = new State[]		{e,x,n,x,x,n};
		my_edges_brokenCovered3_3 = new State[]		{n,x,n,x,x,e};
		my_edges_brokenCovered3_4 = new State[]		{n,x,x,n,x,e};
		my_two_1 = new State[]						{n,x,x,n,n,n};
		my_two_2 = new State[]						{n,n,n,x,x,n};
		my_two_3 = new State[]						{n,n,n,x,x,n,n,n};
		my_broken2_1 = new State[]					{n,x,n,x,n,n};
		my_broken2_2 = new State[]					{n,n,x,n,x,n};
		my_broken2_3 = new State[]					{n,n,x,n,x,n,n};
		//HIS TOP THREATS
		his_straight4 = new State[]					{n,o,o,o,o,n};
		his_four_1 = new State[]					{x,o,o,o,o,n};
		his_four_2 = new State[]					{n,o,o,o,o,x};
		his_edges_four_1 = new State[]				{e,o,o,o,o,n};
		his_edges_four_2 = new State[]				{n,o,o,o,o,e};
		his_broken4_1 = new State[]					{o,o,n,o,o};
		his_broken4_2 = new State[]					{o,n,o,o,o};
		his_broken4_3 = new State[]					{o,o,o,n,o};
		his_three_1 = new State[]					{x,n,o,o,o,n,n};
		his_three_2 = new State[]					{n,n,o,o,o,n,x};
		his_three_3 = new State[]					{n,n,o,o,o,n,n};
		his_edges_three_1 = new State[]				{e,n,o,o,o,n,n};
		his_edges_three_2 = new State[]				{n,n,o,o,o,n,e};
		his_broken3_1 = new State[]					{n,o,o,n,o,n};
		his_broken3_2 = new State[]					{n,o,n,o,o,n};
		//HIS WEAK THREATS
		his_covered3_1 = new State[]				{n,n,o,o,o,x};
		his_covered3_2 = new State[]				{x,o,o,o,n,n};
		his_edges_covered3_1 = new State[]			{n,n,o,o,o,e};
		his_edges_covered3_2 = new State[]			{e,o,o,o,n,n};
		his_brokenCovered3_1 = new State[]			{x,o,o,n,o,n};
		his_brokenCovered3_2 = new State[]			{x,o,n,o,o,n};
		his_brokenCovered3_3 = new State[]			{n,o,n,o,o,x};
		his_brokenCovered3_4 = new State[]			{n,o,o,n,o,x};
		his_edges_brokenCovered3_1 = new State[]	{e,o,o,n,o,n};
		his_edges_brokenCovered3_2 = new State[]	{e,o,n,o,o,n};
		his_edges_brokenCovered3_3 = new State[]	{n,o,n,o,o,e};
		his_edges_brokenCovered3_4 = new State[]	{n,o,o,n,o,e};
		his_two_1 = new State[]						{n,o,o,n,n,n};
		his_two_2 = new State[]						{n,n,n,o,o,n};
		his_two_3 = new State[]						{n,n,n,o,o,n,n,n};
		his_broken2_1 = new State[]					{n,o,n,o,n,n};
		his_broken2_2 = new State[]					{n,n,o,n,o,n};
		his_broken2_3 = new State[]					{n,n,o,n,o,n,n};
	}
	// @formatter:on

	private State getOtherPlayer(State player) {
		return player == State.PLAYER1 ? State.PLAYER2 : State.PLAYER1;
	}
}
