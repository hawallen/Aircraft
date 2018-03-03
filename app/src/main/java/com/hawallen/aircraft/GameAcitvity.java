package com.hawallen.aircraft;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hawallen.aircraft.util.Scale;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class GameAcitvity extends Activity {

	/** ��Ϸ����״̬ **/
	public static final int GAME_RUNNING = 0;
	/** ��Ϸ��ͣ״̬ **/
	public static final int GAME_PAUSED = 1;
	/** ��Ϸֹͣ״̬ **/
	public static final int GAME_DESTORY = 2;
	/** ��Ϸ״̬ **/
	public int mState = GAME_RUNNING;

	public static final int DELTA_TIME = 100;
	private RelativeLayout reGameView;
	private GameSurfaceView mGameView;
	private TextView tvKill;
	private ImageButton iBtn;
	public Scale scale;

	private RecordThread recordThread;
	private int frequency = 8000;
	private int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	private int bufferSize;
	private AudioRecord audioRecord;
	private double db;

	public static final int KILLNUM = 10;
	private static final int RECORD = 11;

	private boolean isExit = false;

	public static MediaPlayer mpBomb;
	public static MediaPlayer mpBullet;
	public static MediaPlayer mpMissile;

	private MySensorEventListener sensorEventListener;
	private SensorManager sensorManager;

	private Calendar lastCalendar = Calendar.getInstance();

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case KILLNUM:
				tvKill.setText("ɱ������" + mGameView.killNum);
				mpBomb.start();
				break;
			case RECORD:
				final TipsDialog dialog = new TipsDialog(GameAcitvity.this, R.style.MyDialog, mGameView.killNum);
				dialog.setTips();
				dialog.setOnCancelClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.cancel();
						finish();
					}
				});
				dialog.setOnOKClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.cancel();
						Intent intent = new Intent(GameAcitvity.this, GameAcitvity.class);
						startActivity(intent);
						finish();
					}
				});
				dialog.show();
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		scale = new Scale(this);
		setContentView(R.layout.game_view);
		mGameView = (GameSurfaceView) findViewById(R.id.gs);
		mGameView.setActivity(this);
		tvKill = (TextView) findViewById(R.id.tv_kill);
		iBtn = (ImageButton) findViewById(R.id.ibtn_fire_missile);
		iBtn.setOnClickListener(new OnFireMissileListener());

		sensorEventListener = new MySensorEventListener();
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		mpBomb = MediaPlayer.create(GameAcitvity.this, R.raw.bomb);
		mpMissile = MediaPlayer.create(GameAcitvity.this, R.raw.missile);
		mpBullet = MediaPlayer.create(GameAcitvity.this, R.raw.shot);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGameView.pause();
		sensorManager.unregisterListener(sensorEventListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGameView.resume();
		sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},0);
		} else {
			recordThread = new RecordThread();
			recordThread.start();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 0: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					recordThread = new RecordThread();
					recordThread.start();
				} else {
					Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mGameView.destroy();
		// android.os.Process.killProcess(android.os.Process.myPid());
	}

	// @Override
	// public void onAttachedToWindow() {
	// this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	// super.onAttachedToWindow();
	// }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		// if (keyCode == KeyEvent.KEYCODE_HOME) {
		// finish();
		// }
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mState == GAME_PAUSED) {
				showResault(RECORD);
			} else if (mState == GAME_RUNNING) {
				exitBy2Click();
			}
		}
		return false;
	}

	private void exitBy2Click() {
		Timer timer;
		if (isExit == false) {
			isExit = true; // ׼���˳�
			Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // ȡ���˳�
				}
			}, 2000); // ���2������û�а��·��ؼ�����������ʱ��ȡ�����ղ�ִ�е�����
		} else {
			finish();
			// System.exit(0);
		}
	}

	public void showResault(int killNum) {
		Message message = new Message();
		message.what = RECORD;
		handler.sendMessage(message);
	}

	private class OnFireMissileListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Calendar calendar = Calendar.getInstance();
			Calendar compareCalendar = Calendar.getInstance();
			compareCalendar.setTimeInMillis(lastCalendar.getTimeInMillis() + 1500);
			if (calendar.after(compareCalendar)) {
				lastCalendar = calendar;
				mGameView.fireMissile();
				mpMissile.start();
			}
		}
	}

	private class MySensorEventListener implements SensorEventListener {
		public void onSensorChanged(SensorEvent event) {
			float[] values = event.values;
			float x = values[0];
			float y = values[1];
			mGameView.updateGravity(-x, y);
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	}

	private class RecordThread extends Thread {
		@Override
		public void run() {
			bufferSize = AudioRecord.getMinBufferSize(frequency, 
					channelConfiguration, audioEncoding);
			short[] buffer = new short[bufferSize];
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
					frequency, channelConfiguration, audioEncoding, bufferSize);
			audioRecord.startRecording();
			while (mState == GameAcitvity.GAME_RUNNING) {
				int r = audioRecord.read(buffer, 0, bufferSize);
				double v = 0;
				for (int i = 0; i < buffer.length; i++) {
					v += buffer[i] * buffer[i];
				}
				db = 10 * Math.log10(v / (double) r);
				System.out.println(db);
				if (db > 55) {
					mGameView.shotBullet();
					mpBullet.start();
				}
				try {
					Thread.sleep(DELTA_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			audioRecord.stop();
			audioRecord.release();
		}

	}

}