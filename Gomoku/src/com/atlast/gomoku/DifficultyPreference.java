package com.atlast.gomoku;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class DifficultyPreference extends DialogPreference implements OnSeekBarChangeListener {
	private static final String androidns = "http://schemas.android.com/apk/res/android";

	private SeekBar mSeekBar;
	private TextView mValueText;

	private int mDefaultValue, mMinValue, mMaxValue, mCurrentValue = 0;

	public DifficultyPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeAttributes(attrs);
	}

	@Override
	protected View onCreateDialogView() {
		getCurrentValueFromPrefs();
		View inflatedView = inflateLayout();
		return inflatedView;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult && shouldPersist()) {
			persistInt(mCurrentValue);
			notifyChanged();
		}
	}

	@Override
	public CharSequence getSummary() {
		String summary = super.getSummary().toString();
		int value = getPersistedInt(mDefaultValue);
		return String.format(summary, value);
	}

	@Override
	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		mCurrentValue = value + mMinValue;
		mValueText.setText(Integer.toString(mCurrentValue));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	private void initializeAttributes(AttributeSet attrs) {
		mDefaultValue = attrs.getAttributeIntValue(androidns, "defaultValue", 1);
		mMaxValue = attrs.getAttributeIntValue(androidns, "max", 100);
		mMinValue = 1;
	}

	private View inflateLayout() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.difficulty_pref, null);
		initializeViews(view);
		return view;
	}
	
	private void getCurrentValueFromPrefs() {
		mCurrentValue = getPersistedInt(mDefaultValue);		
	}

	private void initializeViews(View viewContainer) {
		mSeekBar = (SeekBar) viewContainer.findViewById(R.id.seek_bar);
		mSeekBar.setMax(mMaxValue - mMinValue);
		mSeekBar.setProgress(mCurrentValue - mMinValue);
		mSeekBar.setOnSeekBarChangeListener(this);
		
		mValueText = (TextView) viewContainer.findViewById(R.id.difficulty_value);
		mValueText.setText(Integer.toString(mCurrentValue));
	}
}
