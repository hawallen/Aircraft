package com.hawallen.aircraft;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

import com.hawallen.aircraft.util.BitmapUtil;

public class Bullet implements Parcelable{
	/** �ӵ����ٶ� **/
	public static final int STEP = 20;
	/** �ӵ���XY���� **/
	public int mPosX;
	public int mPosY;
	/** �ӵ��ĳߴ� **/
	public int mWidth;
	public int mHeight;
	/** �ӵ��Ķ��� **/
	private Animation mAnimation;

	public Bullet(GameAcitvity gameAcitvity, int mPosX, int mPosY) {
		Bitmap[] bitmap = BitmapUtil.readBitmaps(gameAcitvity, new int[] { R.drawable.bullet});
		mAnimation = new Animation(bitmap, true);
		mWidth = bitmap[0].getWidth();
		mHeight = bitmap[0].getHeight();
		this.mPosX = mPosX - mWidth/2;
		this.mPosY = mPosY - mHeight;
	}

	/** �����ӵ� **/
	public void drawBullet(Canvas canvas, Paint paint) {
		mAnimation.drawAnimation(canvas, paint, mPosX + mWidth / 2, mPosY);
	}

	/** �����ӵ�������� **/
	public void updateBullet() {
		mPosY -= STEP;
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
