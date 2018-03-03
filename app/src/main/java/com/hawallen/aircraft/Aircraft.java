package com.hawallen.aircraft;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

import com.hawallen.aircraft.util.BitmapUtil;
import com.hawallen.aircraft.util.Calculator;

public class Aircraft implements Parcelable {
	/** ���ڶ��� */
	private GameAcitvity gameAcitvity;
	/** �ɻ����״̬ **/
	public static final int ALIVE_STATE = 0;
	/** �ɻ�����״̬ **/
	public static final int DEATH_STATE = 1;
	/** ���Ŷ���״̬ **/
	public int mState = ALIVE_STATE;
	/** �ɻ����ߵ�Y���ٶ� **/
	public static final int STEP = 10;
	/** �ɻ�����Ļ�е����� **/
	public int mPosX;
	public int mPosY;
	/** �ɻ��Ŀ�� **/
	public int mWidth;
	public int mHeight;
	/** �ɻ��Ļ��Χ **/
	public int maxPosX;
	public int maxPosY;
	/** �ɻ����ߵĶ��� **/
	private Animation moveAnimation;
	/** �ɻ������Ķ��� **/
	private Animation deadAnimation;
	/** 0:�л���1���ҷ��ɻ� */
	public int type = 0;
	/** �Ƿ񱻵������� */
	public boolean isLocked = false;

	public Aircraft(GameAcitvity gameAcitvity) {
		this.gameAcitvity = gameAcitvity;
		/** �����ɻ��������� **/
		Bitmap[] moveBitmap = BitmapUtil.readBitmaps(gameAcitvity, new int[] { R.drawable.enemy1, R.drawable.enemy2, R.drawable.enemy3 });
		Bitmap[] deadBitmap = BitmapUtil.readBitmaps(gameAcitvity, new int[] { R.drawable.bomb_0, R.drawable.bomb_1, R.drawable.bomb_2, R.drawable.bomb_3, R.drawable.bomb_4, R.drawable.bomb_5 });
		moveAnimation = new Animation(moveBitmap, true);
		deadAnimation = new Animation(deadBitmap, false);
		mWidth = moveBitmap[0].getWidth();
		mHeight = moveBitmap[0].getHeight();

		maxPosX = gameAcitvity.scale.getDisplayWidth() - mWidth;
		maxPosY = gameAcitvity.scale.getDisplayHeight() - mHeight;

		// ����Ĭ��Ϊ�л������������������λ��
		mPosX = Calculator.utilRandom(0, maxPosX);

	}

	/**
	 * 
	 * 
	 */
	public void reset() {
		mPosX = Calculator.utilRandom(0, maxPosX);
		mPosY = 0;
		isLocked = false;
		mState = Aircraft.ALIVE_STATE;
	}

	/**
	 * ���÷ɻ��������ͣ�0���л���1���ҷ��ɻ�
	 * 
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
		if (type == 1) {
			mPosX = maxPosX / 2;
			mPosY = maxPosY;
			Bitmap[] moveBitmap = BitmapUtil.readBitmaps(gameAcitvity, new int[] { R.drawable.aircraft1, R.drawable.aircraft2, R.drawable.aircraft3 });
			moveAnimation = new Animation(moveBitmap, true);
		}
	}

	/**
	 * ���Ʒɻ�
	 * 
	 * @param canvas
	 * @param paint
	 */
	public void drawAircraft(Canvas canvas, Paint paint) {
		if (mState == ALIVE_STATE) {
			moveAnimation.drawAnimation(canvas, paint, mPosX, mPosY);
		} else if (mState == DEATH_STATE) {
			System.out.println("dead");
			deadAnimation.drawAnimation(canvas, paint, mPosX, mPosY);
		}
	}

	/** �л������ٶ��ƶ��ɻ� **/
	public void updateAircraft() {
		if (type == 0) {
			mPosY += STEP;
		}
	}

	/**
	 * �ҷ��ɻ�����x��y�����ٶ��ƶ��ɻ�
	 * 
	 * @param x
	 * @param y
	 */
	public void moveAircraft(float x, float y) {
		if (type == 1) {
			mPosX += x * 8;
			mPosY += y * 8;
			if (mPosX < 0) {
				mPosX = 0;
			}
			if (mPosX > maxPosX) {
				mPosX = maxPosX;
			}
			if (mPosY < 0) {
				mPosY = 0;
			}
			if (mPosY > maxPosY) {
				mPosY = maxPosY;
			}
		}
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}
}
