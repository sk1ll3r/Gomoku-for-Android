package com.atlast.gomoku;

import com.atlast.gomoku.library.GameActivity;
import com.atlast.gomoku.library.GameView.State;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	private boolean startWithHuman = true;
	private int depth;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initializeViews();
	}

	@Override
	protected void onStart() {
		super.onStart();
		getPrefs();
	}

	private void initializeViews() {
		findViewById(R.id.start_game).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startGame(startWithHuman);
			}
		});

		findViewById(R.id.preferences).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startPreferences();
			}
		});
		
		findViewById(R.id.about).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startAbout();
			}
		});

		findViewById(R.id.quit).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void startAbout() {
		showDialog(0);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = new Dialog(this);
		
		dialog.setContentView(R.layout.about_dialog);
		dialog.setTitle(R.string.about);
		dialog.setCancelable(true);
		
		return dialog;
	}

	private void startGame(boolean startWithHuman) {
		Intent i = new Intent(this, GameActivity.class);
		i.putExtra(GameActivity.EXTRA_START_PLAYER, startWithHuman ? State.PLAYER1.getValue() : State.PLAYER2.getValue());
		i.putExtra(GameActivity.EXTRA_DEPTH, depth);
		startActivity(i);
	}
	
	private void startPreferences() {
		Intent i = new Intent(getBaseContext(), Preferences.class);
		startActivity(i);		
	}

	private void getPrefs() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		startWithHuman = prefs.getBoolean("startWithHuman", true);
		depth = prefs.getInt("difficulty", 1);
	}
}