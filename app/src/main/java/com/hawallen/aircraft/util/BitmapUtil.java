package com.hawallen.aircraft.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hawallen.aircraft.GameAcitvity;

import java.io.InputStream;

public class BitmapUtil {

	/**
	 * ��ȡ��Ϸ����ͼƬ��Դ
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBgBitmap(GameAcitvity gameAcitvity, int resId) {
		Bitmap bitmap = readBitmap(gameAcitvity, resId);
		return gameAcitvity.scale.getResizedBitmap(bitmap);
	}
	
	/**
	 * ��ȡͼƬ��Դ
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitmap(GameAcitvity gameAcitvity, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// ��ȡ��ԴͼƬ
		InputStream is = gameAcitvity.getResources().openRawResource(resId);
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
		return bitmap;
	}
	
	
//	/**
//	 * ��ȡͼƬ������Ŀ��
//	 * @param gameAcitvity
//	 * @param bitmap
//	 * @return
//	 */
//	public static int getBitmapWidth(GameAcitvity gameAcitvity, Bitmap bitmap) {
//		return (int) (gameAcitvity.scale.scaleWidth*bitmap.getWidth());
//	}	
//	
//	/**
//	 * ��ȡͼƬ������ĸ߶�
//	 * @param gameAcitvity
//	 * @param bitmap
//	 * @return
//	 */
//	public static int getBitmapHeight(GameAcitvity gameAcitvity, Bitmap bitmap) {
//		return (int) (gameAcitvity.scale.scaleHeight*bitmap.getHeight());
//	}
	
	/**
	 * ��ȡһ��ͼƬ��Դ
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap[] readBitmaps(GameAcitvity gameAcitvity, int[] frameBitmapID) {
//		Bitmap[] mframeBitmap = new Bitmap[frameBitmapID.length];
		Bitmap[] bitmaps = new Bitmap[frameBitmapID.length];
		for (int i = 0; i < frameBitmapID.length; i++) {
			bitmaps[i] = readBitmap(gameAcitvity, frameBitmapID[i]);
//			mframeBitmap[i] = gameAcitvity.scale.getResizedBitmap(bitmaps[i]);
		}
		return bitmaps;
	}
}
