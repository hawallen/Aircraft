package com.hawallen.aircraft;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.hawallen.aircraft.util.BitmapUtil;
import com.hawallen.aircraft.util.Scale;

import java.util.ArrayList;
import java.util.List;

public class GameSurfaceView extends SurfaceView implements Callback, Runnable {

	/** ��Ϸ������Դ ����ͼƬ�����л�����Ļ�������� **/
	private Bitmap mBitmapBG0;
	private Bitmap mBitmapBG1;

	/** �ҷ��ɻ� */
	private Aircraft mAircraft;

	/** ��Ч���ӵ� **/
	private List<Bullet> mBullets = new ArrayList<Bullet>();
	/** ����Ч���ӵ����ȴ����� */
	private List<Bullet> mBulletsPool = new ArrayList<Bullet>();

	/** ��Ч�ĵ��� **/
	private List<Missile> mMissiles = new ArrayList<Missile>();
	/** ����Ч�ĵ������ȴ����� */
	private List<Missile> mMissilesPool = new ArrayList<Missile>();

	/** ��ĵл� **/
	private List<Aircraft> mEnemys = new ArrayList<Aircraft>();
	/** �������ĵл����ȴ����� */
	private List<Aircraft> mEnemysPool = new ArrayList<Aircraft>();

	/** ��Ϸ���߳� **/
	public Thread mThread;
	public Thread enemyThread;

	/** ������� **/
	private SurfaceHolder mSurfaceHolder;
	private Canvas mCanvas;

	/** �������ڶ��� */
	private GameAcitvity gameAcitvity;

	/** ��¼���ű���ͼƬʱʱ���µ�Y���� **/
	private int mBitmapBG0PosY = 0;
	private int mBitmapBG1PosY = 0;

	/** XY���������ֵ **/
	private float mGravityX = 0;
	private float mGravityY = 0;

	private Scale scale;

	public int killNum = 0;

	private Paint mPaint;
	
	public void setActivity(GameAcitvity gameAcitvity) {
		this.gameAcitvity = gameAcitvity;
		init();
	}

	public GameSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GameSurfaceView(GameAcitvity gameAcitvity, AttributeSet attrs) {
		super(gameAcitvity, attrs);
		this.gameAcitvity = gameAcitvity;
		init();
	}

	private void init() {
		scale = gameAcitvity.scale;

		mPaint = new Paint();
		/** ��ȡmSurfaceHolder **/
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		
		/** ��Ϸ���� **/
		mBitmapBG0 = BitmapUtil.readBgBitmap(gameAcitvity, R.drawable.map_0);
		mBitmapBG1 = BitmapUtil.readBgBitmap(gameAcitvity, R.drawable.map_1);

		/** ��һ��ͼƬ��������Ļ00�㣬�ڶ���ͼƬ�ڵ�һ��ͼƬ�Ϸ� **/
		mBitmapBG0PosY = 0;
		mBitmapBG1PosY = -mBitmapBG1.getHeight();

		/** �����ɻ����� **/
		mAircraft = new Aircraft(gameAcitvity);
		mAircraft.setType(1);

	}

	@Override
	public void run() {
		while (gameAcitvity.mState == GameAcitvity.GAME_RUNNING) {
			// ����������̰߳�ȫ��
			synchronized (mSurfaceHolder) {
				/** �õ���ǰ���� Ȼ������ **/
				mCanvas = mSurfaceHolder.lockCanvas();
				draw();
				/** ���ƽ����������ʾ����Ļ�� **/
				mSurfaceHolder.unlockCanvasAndPost(mCanvas);
			}
			try {
				Thread.sleep(GameAcitvity.DELTA_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private class EnemyThread extends Thread {
		@Override
		public void run() {
			while (gameAcitvity.mState == GameAcitvity.GAME_RUNNING) {
				/** ����ɱ�������ӵл����� */
				for (int i = 0; i < killNum / 50 + 1; i++) {
					Aircraft enemy;
					if (mEnemysPool.isEmpty()) {
						enemy = new Aircraft(gameAcitvity);
					} else {
						enemy = mEnemysPool.get(0);
						enemy.reset();
						mEnemysPool.remove(0);
					}
					mEnemys.add(enemy);
				}
				try {
					Thread.sleep(GameAcitvity.DELTA_TIME * 20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// surfaceView�Ĵ�С�����ı��ʱ��
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		/** ��Ϸ�����߳� **/
		mThread = new Thread(this);
		mThread.start();

		/** �����л��߳� **/
		enemyThread = new EnemyThread();
		enemyThread.start();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// surfaceView���ٵ�ʱ��
	}

	public void pause() {
		gameAcitvity.mState = GameAcitvity.GAME_PAUSED;
	}

	public void resume() {
		gameAcitvity.mState = GameAcitvity.GAME_RUNNING;
		if (mThread != null && enemyThread != null) {
			surfaceCreated(mSurfaceHolder);
		}
	}

	public void destroy() {
		gameAcitvity.mState = GameAcitvity.GAME_DESTORY;
	}

	public void clear() {
		mEnemys.clear();
		mBullets.clear();
	}

	private void draw() {
		switch (gameAcitvity.mState) {
		case GameAcitvity.GAME_RUNNING:
			renderBg();
			updateBg();
			break;
		}
	}

	private void renderBg() {
		if (mCanvas != null) {
			/** ������Ϸ��ͼ **/
			mCanvas.drawBitmap(mBitmapBG0, 0, mBitmapBG0PosY, mPaint);
			mCanvas.drawBitmap(mBitmapBG1, 0, mBitmapBG1PosY, mPaint);

			/** ���Ʒɻ����� **/
			mAircraft.drawAircraft(mCanvas, mPaint);

			/** �����ӵ����� */
			for (int i = 0; i < mBullets.size(); i++) {
				mBullets.get(i).drawBullet(mCanvas, mPaint);
			}

			/** ���Ƶ��� */
			for (int i = 0; i < mMissiles.size(); i++) {
				mMissiles.get(i).drawMissile(mCanvas, mPaint);
			}

			/** ���Ƶ��˶��� **/
			for (int i = 0; i < mEnemys.size(); i++) {
				mEnemys.get(i).drawAircraft(mCanvas, mPaint);
			}
		}
	}

	private void updateBg() {
		/** ������Ϸ����ͼƬ����λ�ã�ʵ�����¹���Ч�� **/
		mBitmapBG0PosY += 5;
		mBitmapBG1PosY += 5;
		if (mBitmapBG0PosY == mBitmapBG0.getHeight()) {
			mBitmapBG0PosY = -mBitmapBG0.getHeight();
		}
		if (mBitmapBG1PosY == mBitmapBG1.getHeight()) {
			mBitmapBG1PosY = -mBitmapBG1.getHeight();
		}
		/** ����������Ӧ���·ɻ����� **/
		mAircraft.moveAircraft(mGravityX, mGravityY);
		/** �����ӵ�λ�� **/
		for (int i = 0; i < mBullets.size();) {
			Bullet bullet = mBullets.get(i);
			if (bullet.mPosY < 0) {
				// �ӵ������Ļ���Ƴ��ӵ�
				mBullets.remove(i);
				mBulletsPool.add(bullet);
			} else {
				bullet.updateBullet();
				i++;
			}
		}

		/** ���µ���λ�� */
		for (int i = 0; i < mMissiles.size();) {
			Missile missile = mMissiles.get(i);
			if (missile.mPosX < 0 || missile.mPosX > scale.getDisplayWidth() || missile.mPosY < 0) {
				missile.missTarget();
				mMissiles.remove(i);
				mMissilesPool.add(missile);
			} else {
				missile.updateMissile();
				i++;
			}
		}
		/** ���µ���λ�� **/
		for (int i = 0; i < mEnemys.size();) {
			Aircraft enemy = mEnemys.get(i);
			/** �л����� ���� �л�������Ļ��δ������������ **/
			if (enemy.mState == Aircraft.DEATH_STATE || enemy.mPosY >= scale.getDisplayHeight()) {
				mEnemys.remove(i);
				mEnemysPool.add(enemy);
			} else {
				enemy.updateAircraft();
				i++;
			}
		}
		// �����ӵ�����˵���ײ
		collision(); 
	}

	public void shotBullet() {
		Bullet bullet;
		if (mBulletsPool.isEmpty()) {
			bullet = new Bullet(gameAcitvity, mAircraft.mPosX + mAircraft.mWidth / 2, mAircraft.mPosY - 5);
		} else {
			bullet = mBulletsPool.get(0);
			bullet.mPosX = mAircraft.mPosX + mAircraft.mWidth / 2;
			bullet.mPosY = mAircraft.mPosY - 5;
			mBulletsPool.remove(0);
		}
		mBullets.add(bullet);
	}

	public void fireMissile() {
			Missile missile;
			if (mMissilesPool.isEmpty()) {
				missile = new Missile(gameAcitvity, mAircraft.mPosX + mAircraft.mWidth / 2, mAircraft.mPosY - 5);
			} else {
				missile = mMissilesPool.get(0);
				missile.setmPosX(mAircraft.mPosX + mAircraft.mWidth / 2);
				missile.setmPosY(mAircraft.mPosY - 5);
				mMissilesPool.remove(0);
			}
			mMissiles.add(missile);
			lockTarget(missile);
	}

	public void lockTarget(Missile missile) {
		Aircraft target = null;
		int deltaY;
		int deltaX;
		int deltaXY = 10000;
		for (int i = 0; i < mEnemys.size(); i++) {
			Aircraft enemy = mEnemys.get(i);
			deltaY = missile.mPosY - enemy.mPosY;
			if (!enemy.isLocked && deltaY > missile.mHeight) {
				deltaX = missile.mPosX - enemy.mPosX;
				int temp = Math.abs(deltaX) + deltaY;
				if (temp < deltaXY) {
					deltaXY = temp;
					target = enemy;
				}
			}
		}
		missile.setTarget(target);
	}

	private void collision() {
		// �����ӵ��������ײ
		for (int i = 0; i < mBullets.size(); i++) {
			int x1 = mBullets.get(i).mPosX;
			int y1 = mBullets.get(i).mPosY;
			int w1 = mBullets.get(i).mWidth * 3 / 4;
			int h1 = mBullets.get(i).mHeight * 3 / 4;
			for (int j = 0; j < mEnemys.size(); j++) {
				Aircraft enemy = mEnemys.get(j);
				int x2 = enemy.mPosX;
				int y2 = enemy.mPosY;
				int w2 = enemy.mWidth * 3 / 4;
				int h2 = enemy.mHeight * 3 / 4;
				if (x2 - x1 < w1 && x1 - x2 < w2 &&
						y1 - y2 < h2 && y2 - y1 < h1) {
					if (enemy.mState == Aircraft.ALIVE_STATE) {
						enemy.mState = Aircraft.DEATH_STATE;
						killNum++;
						Message msg = new Message();
						msg.what = GameAcitvity.KILLNUM;
						gameAcitvity.handler.sendMessage(msg);
					}
					mBulletsPool.add(mBullets.get(i));
					mBullets.remove(i);
					if (i > 0) {
						i--;
					}
					break;
				}
			}
		}

		// ���µ����������ײ
		for (int i = 0; i < mMissiles.size(); i++) {
			int x1 = mMissiles.get(i).mPosX;
			int y1 = mMissiles.get(i).mPosY;
			int w1 = mMissiles.get(i).mWidth * 3 / 4;
			int h1 = mMissiles.get(i).mHeight * 3 / 4;
			for (int j = 0; j < mEnemys.size(); j++) {
				Aircraft enemy = mEnemys.get(j);
				int x2 = enemy.mPosX;
				int y2 = enemy.mPosY;
				int w2 = enemy.mWidth * 3 / 4;
				int h2 = enemy.mHeight * 3 / 4;
				if (x2 - x1 < w1 && x1 - x2 < w2 &&
						y1 - y2 < h2 && y2 - y1 < h1) {
					if (enemy.mState == Aircraft.ALIVE_STATE) {
						enemy.mState = Aircraft.DEATH_STATE;
						killNum++;
						Message msg = new Message();
						msg.what = GameAcitvity.KILLNUM;
						gameAcitvity.handler.sendMessage(msg);
					}
					mMissiles.get(i).missTarget();
					mMissilesPool.add(mMissiles.get(i));
					mMissiles.remove(i);
					if (i > 0) {
						i--;
					}
					break;
				}
			}
		}

		//�����ҷ��ɻ���л���ײ
		int x1 = mAircraft.mPosX;
		int y1 = mAircraft.mPosY;
		int w1 = mAircraft.mWidth / 2;
		int h1 = mAircraft.mHeight / 2;
		for (int i = 0; i < mEnemys.size(); i++) {
			Aircraft enemy = mEnemys.get(i);
			int x2 = enemy.mPosX;
			int y2 = enemy.mPosY;
			int w2 = enemy.mWidth / 2;
			int h2 = enemy.mHeight / 2;
			if (enemy.mState == Aircraft.ALIVE_STATE && x2 - x1 < w1 &&
					x1 - x2 < w2 && y1 - y2 < h2 && y2 - y1 < h1) {
				if (i > 0) {
					i--;
				}
				enemy.mState = Aircraft.DEATH_STATE;
				mAircraft.mState = Aircraft.DEATH_STATE;
				killNum++;
				Message msg = new Message();
				msg.what = GameAcitvity.KILLNUM;
				gameAcitvity.handler.sendMessage(msg);
				renderBg();
				pause();
				gameAcitvity.showResault(killNum);
			}
		}
	}

	public void updateGravity(float gX, float gY) {
		switch (gameAcitvity.mState) {
		case GameAcitvity.GAME_RUNNING:
			mGravityX = gX;
			mGravityY = gY;
			break;
		}
	}

	/**
	 * ��������
	 * 
	 * @param canvas
	 * @param str
	 * @param color
	 * @param x
	 * @param y
	 */
	public void drawString(String str, int color, int x, int y) {
		if (mCanvas != null) {
			int backColor = mPaint.getColor();
			mPaint.setColor(color);
			mCanvas.drawText(str, x, y, mPaint);
			mPaint.setColor(backColor);
		}
	}

	// /**
	// * ���ƻ�����Ӱ������
	// *
	// * @param canvas
	// * @param str
	// * @param color
	// * @param x
	// * @param y
	// */
	// public void drawRimString(String str, int color, int x, int y) {
	// if (mCanvas != null) {
	// int backColor = mPaint.getColor();
	// mPaint.setColor(~color);
	// mCanvas.drawText(str, x + 1, y, mPaint);
	// mCanvas.drawText(str, x, y + 1, mPaint);
	// mCanvas.drawText(str, x - 1, y, mPaint);
	// mCanvas.drawText(str, x, y - 1, mPaint);
	// mPaint.setColor(color);
	// mCanvas.drawText(str, x, y, mPaint);
	// mPaint.setColor(backColor);
	// }
	// }
	//
	// /**
	// * ����һ������
	// *
	// * @param canvas
	// * @param color
	// * @param x
	// * @param y
	// * @param w
	// * @param h
	// */
	// private void drawFillRect(Canvas canvas, int color, int x, int y, int w,
	// int h) {
	// int backColor = mPaint.getColor();
	// mPaint.setColor(color);
	// canvas.drawRect(x, y, x + w, y + h, mPaint);
	// mPaint.setColor(backColor);
	// }
	//
	// /**
	// * ����һ������
	// *
	// * @param canvas
	// * @param color
	// * @param oval
	// * @param startAngle
	// * @param sweepAngle
	// * @param useCenter
	// */
	// private void drawFillCircle(Canvas canvas, int color, RectF oval, int
	// startAngle, int sweepAngle, boolean useCenter) {
	// int backColor = mPaint.getColor();
	// mPaint.setColor(color);
	// canvas.drawArc(oval, startAngle, sweepAngle, useCenter, mPaint);
	// mPaint.setColor(backColor);
	// }
	//
	// /**
	// * �����и�ͼƬ
	// *
	// * @param bitmap
	// * @param x
	// * @param y
	// * @param w
	// * @param h
	// * @return
	// */
	// private Bitmap bitmapClipBitmap(Bitmap bitmap, int x, int y, int w, int
	// h)
	// {
	// return Bitmap.createBitmap(bitmap, x, y, w, h);
	// }

}