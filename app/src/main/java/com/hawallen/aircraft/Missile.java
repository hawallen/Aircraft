package com.hawallen.aircraft;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

import com.hawallen.aircraft.util.BitmapUtil;

public class Missile implements Parcelable {

	/** ����Y����ٶ� **/
	public static final int STEP_Y = 30;
	/** ����X����ٶ� */
	public int stepX;
	/** ������XY���� **/
	public int mPosX;
	public int mPosY;
	/** �����ĳߴ� **/
	public int mWidth;
	public int mHeight;
	/** Ŀ�� */
	public Aircraft target;
	/** �����Ķ��� **/
	private Animation mAnimation;

	public Missile(GameAcitvity gameAcitvity, int mPosX, int mPosY) {
		Bitmap[] bitmap = BitmapUtil.readBitmaps(gameAcitvity, new int[] { R.drawable.missile_blue, R.drawable.missile_green, R.drawable.missile_orange, R.drawable.missile_red, R.drawable.missile_yellow });
		mAnimation = new Animation(bitmap, true);
		mWidth = bitmap[0].getWidth();
		mHeight = bitmap[0].getHeight();
		this.mPosX = mPosX - mWidth / 2;
		this.mPosY = mPosY - mHeight;
	}

	/**
	 * ���Ƶ���
	 */
	public void drawMissile(Canvas canvas, Paint paint) {
		mAnimation.drawAnimation(canvas, paint, mPosX + mWidth / 2, mPosY);
	}

	/**
	 * �趨Ŀ��
	 * 
	 * @param target
	 *            Ŀ��ɻ�
	 */
	public void setTarget(Aircraft target) {
		if (target != null) {
			int deltaX = mPosX - target.mPosX - target.mWidth / 2;
			int deltaY = mPosY - target.mPosY;
			stepX = deltaX * (STEP_Y + Aircraft.STEP) / deltaY;
			if (stepX <= STEP_Y) {
				this.target = target;
				target.isLocked = true;
			} 
		}
	}

	/**
	 * ʧȥĿ��
	 */
	public void missTarget() {
		if (target != null) {
			target.isLocked = false;
		}
		mPosX = 0;
		mPosY = 0;
		stepX = 0;
		target = null;
	}

	/** ���µ���������� **/
	public void updateMissile() {
		mPosY -= STEP_Y;
		mPosX -= stepX;
	}

	public int getmPosX() {
		return mPosX;
	}

	public void setmPosX(int mPosX) {
		this.mPosX = mPosX;
	}

	public int getmPosY() {
		return mPosY;
	}

	public void setmPosY(int mPosY) {
		this.mPosY = mPosY;
	}

	public int getmWidth() {
		return mWidth;
	}

	public void setmWidth(int mWidth) {
		this.mWidth = mWidth;
	}

	public int getmHeight() {
		return mHeight;
	}

	public void setmHeight(int mHeight) {
		this.mHeight = mHeight;
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
