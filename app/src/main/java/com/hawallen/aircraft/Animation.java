package com.hawallen.aircraft;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Animation {
//	/** ��һ֡����ʱ�� **/
//	private long mLastPlayTime = 0;
	/** ���ŵ�ǰ֡��ID **/
	private int mPlayID = 0;
	/** ����frame���� **/
	private int mFrameCount = 0;
	/** ���ڴ��涯����ԴͼƬ **/
	private Bitmap[] mframeBitmap = null;
	/** �Ƿ�ѭ������ **/
	private boolean mIsLoop = false;
	/** ���Ž��� **/
	public boolean mIsEnd = false;

	/**
	 * ���캯��
	 * 
	 * @param frameBitmap
	 * @param isloop
	 */
	public Animation(Bitmap[] frameBitmap, boolean isloop) {
		mFrameCount = frameBitmap.length;
		mframeBitmap = frameBitmap;
		mIsLoop = isloop;
	}

	/**
	 * ���ö���
	 */
	public void reset() {
//		mLastPlayTime = 0;
		mPlayID = 0;
		mIsEnd = false;
	}

	/**
	 * ���ƶ���
	 * 
	 * @param Canvas
	 * @param paint
	 * @param x
	 * @param y
	 */
	public void drawAnimation(Canvas canvas, Paint paint, int x, int y) {
		// ���û�в��Ž������������
		if (!mIsEnd) {
			canvas.drawBitmap(mframeBitmap[mPlayID], x, y, paint);
//			long time = System.currentTimeMillis();
//			if (time - mLastPlayTime >= GameAcitvity.DELTA_TIME) {
				mPlayID++;
//				mLastPlayTime = time;
				if (mPlayID >= mFrameCount) {
					// ��־�������Ž���
					if (mIsLoop) {
						// ����ѭ������
						reset();
					} else {
						mIsEnd = true;
					}
				}
//			}
		}
	}

}
