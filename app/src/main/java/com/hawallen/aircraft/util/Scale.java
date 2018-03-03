package com.hawallen.aircraft.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.Display;

public class Scale {
	private static final int WIDTH = 480;
	private static final int HEIGHT = 800;
	private int displayWidth;
	private int displayHeight;
	public float scaleWidth;
	public float scaleHeight;

	public Scale(Activity mActivity) {
		Display display = mActivity.getWindowManager().getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();
		scaleWidth = ((float) displayWidth) / WIDTH;
		scaleHeight = ((float) displayHeight) / HEIGHT;
	}

	/**
	 * ��ȡ�豸��Ļ���
	 * @return
	 */
	public int getDisplayWidth() {
		return displayWidth;
	}

	/**
	 * ��ȡ�豸��Ļ�߶�
	 * @return
	 */
	public int getDisplayHeight() {
		return displayHeight;
	}

	/**
	 * ���µ���ͼƬ������Ӧ��Ļ��С
	 * @param bitmap
	 * @return
	 */
	public Bitmap getResizedBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap,
				0, 0, width, height, matrix, false);
		return resizedBitmap;
	}
}
