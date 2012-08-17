package com.atlast.gomoku.library;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.Handler.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.atlast.gomoku.library.GameActivity;

public class GameView extends View {

	public static final long FPS_MS = 1000 / 2;

	public enum State {
		UNKNOWN(-3), WIN(-2), EMPTY(0), PLAYER1(1), PLAYER2(2), EDGE(3);

		private int mValue;

		private State(int value) {
			mValue = value;
		}

		public int getValue() {
			return mValue;
		}

		public static State fromInt(int i) {
			for (State s : values()) {
				if (s.getValue() == i) {
					return s;
				}
			}
			return EMPTY;
		}
	}

	private static final int MARGIN = 4;
	private static final int MSG_BLINK = 1;

	private final Handler mHandler = new Handler(new MyHandler());

	private final Rect mSrcRect = new Rect();
	private final Rect mDstRect = new Rect();

	private int mSxy;
	private int mOffsetX;
	private int mOffsetY;
	private Paint mWinPaint;
	private Paint mLinePaint;
	private Paint mBmpPaint;
	private Bitmap mBmpPlayer1;
	private Bitmap mBmpPlayer2;
	private Bitmap mBmpPlayer1Select;
	private Drawable mDrawableBg;

	private ICellListener mCellListener;

	/**
	 * Contains one of {@link State#EMPTY}, {@link State#PLAYER1} or
	 * {@link State#PLAYER2}.
	 */
	private final State[][] mData = new State[GameActivity.BOARD_SIZE + 2][GameActivity.BOARD_SIZE + 2];

	private int mSelectedCellRow = -1;
	private int mSelectedCellCol = -1;
	private State mSelectedValue = State.EMPTY;
	private State mCurrentPlayer = State.UNKNOWN;
	private State mWinner = State.EMPTY;

	private int mBlinkCol;
	private int mBlinkRow;
	private boolean mBlinkDisplayOff;
	private final Rect mBlinkRect = new Rect();

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		requestFocus();
		initializePaints();
		initializeData();
	}

	public interface ICellListener {
		public void onCellSelected();

		public void onCellConfirmed();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawLines(canvas);
		drawAllMoves(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Keep the view squared
		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);
		int d = w == 0 ? h : h == 0 ? w : w < h ? w : h;
		setMeasuredDimension(d, d);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		int sx = w / GameActivity.BOARD_SIZE;
		int sy = h / GameActivity.BOARD_SIZE;

		int size = sx < sy ? sx : sy;

		mSxy = size;
		mOffsetX = (w - GameActivity.BOARD_SIZE * size) / 2;
		mOffsetY = (h - GameActivity.BOARD_SIZE * size) / 2;

		mDstRect.set(0, 0, size - 2 * MARGIN, size - 2 * MARGIN);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();

		if (action == MotionEvent.ACTION_DOWN) {
			return true;
		} else if (action == MotionEvent.ACTION_UP) {
			int col = (int) event.getX();
			int row = (int) event.getY();

			col = (col - mOffsetX) / mSxy + 1;
			row = (row - mOffsetY) / mSxy + 1;

			if (isTouchValid(row, col)) {
				if (col == mSelectedCellCol && row == mSelectedCellRow) {
					confirmSelection();
				} else if (mData[row][col] == State.EMPTY) {
					selectEmptySquare(row, col);
				} else {
					resetSelection();
				}
			}

			return true;
		}

		return false;
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle b = new Bundle();

		Parcelable s = super.onSaveInstanceState();
		b.putParcelable("gv_super_state", s);

		b.putBoolean("gv_en", isEnabled());

		int[] data = new int[(GameActivity.BOARD_SIZE + 2) * (GameActivity.BOARD_SIZE + 2)];
		for (int i = 0, k = 0; i < GameActivity.BOARD_SIZE + 2; i++) {
			for (int j = 0; j < GameActivity.BOARD_SIZE + 2; j++, k++) {
				data[k] = mData[i][j].getValue();
			}
		}
		b.putIntArray("gv_data", data);

		b.putInt("gv_sel_cell_col", mSelectedCellCol);
		b.putInt("gv_sel_cell_row", mSelectedCellRow);
		b.putInt("gv_sel_val", mSelectedValue.getValue());
		b.putInt("gv_curr_play", mCurrentPlayer.getValue());
		b.putInt("gv_winner", mWinner.getValue());

		return b;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof Bundle)) {
			// Not supposed to happen.
			super.onRestoreInstanceState(state);
			return;
		}

		Bundle b = (Bundle) state;
		Parcelable superState = b.getParcelable("gv_super_state");

		setEnabled(b.getBoolean("gv_en", true));

		int[] data = b.getIntArray("gv_data");
		if (data != null && data.length == (GameActivity.BOARD_SIZE + 2) * (GameActivity.BOARD_SIZE + 2)) {
			for (int i = 0, k = 0; i < GameActivity.BOARD_SIZE + 2; i++) {
				for (int j = 0; j < GameActivity.BOARD_SIZE + 2; j++, k++) {
					mData[i][j] = State.fromInt(data[k]);
				}
			}
		}

		mSelectedCellCol = b.getInt("gv_sel_cell_col", -1);
		mSelectedCellRow = b.getInt("gv_sel_cell_row", -1);
		mSelectedValue = State.fromInt(b.getInt("gv_sel_val", State.EMPTY.getValue()));
		mCurrentPlayer = State.fromInt(b.getInt("gv_curr_play", State.EMPTY.getValue()));
		mWinner = State.fromInt(b.getInt("gv_winner", State.EMPTY.getValue()));

		super.onRestoreInstanceState(superState);
	}

	public State[][] getData() {
		return mData;
	}

	public void setCell(int row, int col, State value) {
		mData[row][col] = value;
		// TODO get rid of this
		if (value == State.PLAYER1)
			stopBlink();
		invalidate();
	}

	public void setCellListener(ICellListener cellListener) {
		mCellListener = cellListener;
	}

	public int getSelectionRow() {
		if (mSelectedValue == mCurrentPlayer)
			return mSelectedCellRow;

		return -1;
	}

	public int getSelectionCol() {
		if (mSelectedValue == mCurrentPlayer)
			return mSelectedCellCol;

		return -1;
	}

	public State getCurrentPlayer() {
		return mCurrentPlayer;
	}

	public void setCurrentPlayer(State player) {
		mCurrentPlayer = player;
		// TODO find way to delete this
		mSelectedCellRow = -1;
		mSelectedCellCol = -1;
	}

	public State getWinner() {
		return mWinner;
	}

	public void setWinner(State winner) {
		mWinner = winner;
	}

	public void setFinished(int col, int row, int diagonal) {
		// TODO
	}

	public void startBlink(int row, int col) {
		mBlinkRow = row;
		mBlinkCol = col;
		mBlinkRect.set(mOffsetX + (col - 1) * mSxy, mOffsetY + (row - 1) * mSxy, mOffsetX + col * mSxy, mOffsetY + row * mSxy);
		mHandler.sendEmptyMessageDelayed(MSG_BLINK, FPS_MS);
	}

	public void stopBlink() {
		mBlinkRow = -1;
		mBlinkCol = -1;
		mBlinkDisplayOff = false;
		invalidate(mBlinkRect);
		mBlinkRect.setEmpty();
		mHandler.removeMessages(MSG_BLINK);
	}
	
	//------
	
	private void initializePaints() {
		mDrawableBg = getResources().getDrawable(R.drawable.lib_bg);
		setBackgroundDrawable(mDrawableBg);

		mBmpPlayer1 = getResBitmap(R.drawable.lib_cross);
		mBmpPlayer2 = getResBitmap(R.drawable.lib_circle);
		mBmpPlayer1Select = getResBitmap(R.drawable.lib_cross_sel);

		if (mBmpPlayer1 != null) {
			mSrcRect.set(0, 0, mBmpPlayer1.getWidth() - 1, mBmpPlayer1.getHeight() - 1);
		}

		mBmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		mLinePaint = new Paint();
		mLinePaint.setColor(0xFFFFFFFF);
		mLinePaint.setStrokeWidth(2);
		mLinePaint.setStyle(Style.STROKE);

		mWinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mWinPaint.setColor(0xFFFF0000);
		mWinPaint.setStrokeWidth(10);
		mWinPaint.setStyle(Style.STROKE);		
	}

	private void initializeData() {
		for (int i = 0; i < GameActivity.BOARD_SIZE + 2; i++) {
			for (int j = 0; j < GameActivity.BOARD_SIZE + 2; j++) {
				mData[i][j] = State.EMPTY;
			}
		}

		for (int i = 0; i < GameActivity.BOARD_SIZE + 2; i++) {
			mData[0][i] = State.EDGE;
			mData[GameActivity.BOARD_SIZE + 1][i] = State.EDGE;
			mData[i][0] = State.EDGE;
			mData[i][GameActivity.BOARD_SIZE + 1] = State.EDGE;
		}		
	}
	
	private class MyHandler implements Callback {
		public boolean handleMessage(Message msg) {
			if (msg.what == MSG_BLINK) {
				mBlinkDisplayOff = !mBlinkDisplayOff;
				invalidate(mBlinkRect);
	
				if (!mHandler.hasMessages(MSG_BLINK))
					mHandler.sendEmptyMessageDelayed(MSG_BLINK, FPS_MS);
				return true;
			}
			return false;
		}
	}

	private Bitmap getResBitmap(int bmpResId) {
		Options opts = new Options();
		opts.inDither = false;

		Resources res = getResources();
		Bitmap bmp = BitmapFactory.decodeResource(res, bmpResId, opts);

		if (bmp == null && isInEditMode()) {
			// BitmapFactory.decodeResource doesn't work from the rendering
			// library in Eclipse's Graphical Layout Editor. Use this workaround
			// instead.

			Drawable d = res.getDrawable(bmpResId);
			int w = d.getIntrinsicWidth();
			int h = d.getIntrinsicHeight();
			bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			Canvas c = new Canvas(bmp);
			d.setBounds(0, 0, w - 1, h - 1);
			d.draw(c);
		}

		return bmp;
	}
	
	private void drawLines(Canvas canvas) {
		int sXY = mSxy * GameActivity.BOARD_SIZE;
		for (int i = 0, k = mSxy; i < GameActivity.BOARD_SIZE - 1; i++, k += mSxy) {
			canvas.drawLine(mOffsetX, mOffsetY + k, mOffsetX + sXY - 1, mOffsetY + k, mLinePaint);
			canvas.drawLine(mOffsetX + k, mOffsetY, mOffsetX + k, mOffsetY + sXY - 1, mLinePaint);
		}
	}

	private void drawAllMoves(Canvas canvas) {
		for (int i = 1, y = mOffsetY; i < GameActivity.BOARD_SIZE + 1; i++, y += mSxy) {
			for (int j = 1, x = mOffsetX; j < GameActivity.BOARD_SIZE + 1; j++, x += mSxy) {
				mDstRect.offsetTo(MARGIN + x, MARGIN + y);
				if (!isBlinkDisplayOff(i, j)) {
					drawMove(canvas, i, j);
				}
			}
		}
	}

	private boolean isBlinkDisplayOff(int i, int j) {
		return mBlinkRow == i && mBlinkCol == j && mBlinkDisplayOff;
	}

	private void drawMove(Canvas canvas, int i, int j) {
		if (mSelectedCellRow == i && mSelectedCellCol == j) {
			drawMoveSelected(canvas);

		} else {
			State current = mData[i][j];
			drawMoveNormal(canvas, current);
		}
	}

	private void drawMoveSelected(Canvas canvas) {
		canvas.drawBitmap(mBmpPlayer1Select, mSrcRect, mDstRect, mBmpPaint);
	}

	private void drawMoveNormal(Canvas canvas, State current) {
		switch (current) {
		case PLAYER1:
			canvas.drawBitmap(mBmpPlayer1, mSrcRect, mDstRect, mBmpPaint);
			break;
		case PLAYER2:
			canvas.drawBitmap(mBmpPlayer2, mSrcRect, mDstRect, mBmpPaint);
			break;
		}
	}
	
	private boolean isTouchValid(int row, int col) {
		return isEnabled() && mCurrentPlayer == State.PLAYER1 && col >= 1 && col <= GameActivity.BOARD_SIZE && row >= 1 && row <= GameActivity.BOARD_SIZE;
	}

	private void confirmSelection() {
		mCellListener.onCellConfirmed();
	}

	private void selectEmptySquare(int row, int col) {
		mSelectedCellRow = row;
		mSelectedCellCol = col;
		mSelectedValue = mCurrentPlayer;

		mCellListener.onCellSelected();
		invalidate();
	}

	private void resetSelection() {
		mSelectedCellRow = -1;
		mSelectedCellCol = -1;
		mSelectedValue = State.UNKNOWN;
	}
}