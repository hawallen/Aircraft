package com.hawallen.aircraft;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TipsDialog extends Dialog {

	private static final String PREFERENCES_NAME = "cn_edu_stu_aircraft";
	private TextView tvTitle;
	private TextView tvTips;
	private Button btnOK;
	private Button btnCancel;
	private int killNum;
	
	public TipsDialog(Context context, int theme, int killNum) {
		super(context, theme);
		this.killNum = killNum;
		setContentView(R.layout.tips_dialog);
		init();
		setCancelable(false);
	}
	
	private void init() {
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTips = (TextView) findViewById(R.id.tv_tips);
		btnOK = (Button) findViewById(R.id.btn_retry);
		btnCancel = (Button) findViewById(R.id.btn_finish);
		tvTitle.setText("ɱ������" + killNum);
		saveRecord(killNum);
	}
	
	public void setTips() {
		int[] record = getRecord();
		StringBuffer buffer = new StringBuffer();
		buffer.append("��1����" + record[0]+ "\n");
		buffer.append("��2����" + record[1]+ "\n");
		buffer.append("��3����" + record[2]);
		tvTips.setText(buffer);
	}

	public void setOnOKClickListener(View.OnClickListener listener) {
		btnOK.setOnClickListener(listener);
	}
	
	public void setOnCancelClickListener(View.OnClickListener listener) {
		btnCancel.setOnClickListener(listener);
	}

	public void setTipsText(String tipsText) {
		tvTips.setText(tipsText);
	}

	private void saveRecord(int killNum) {
		SharedPreferences preferences = getContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = preferences.edit();
		int record[] = getRecord();
		if (killNum > record[0]) {
			editor.putInt("first", killNum);
			editor.putInt("second", record[0]);
			editor.putInt("third", record[1]);
		} else if (killNum > record[1]) {
			editor.putInt("second", killNum);
			editor.putInt("third", record[1]);
		} else if (killNum > record[2]) {
			editor.putInt("third", killNum);
		}
		editor.commit();
	}
	
	private int[] getRecord() {
		int[] record = new int[3];
		SharedPreferences preferences = getContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		record[0] = preferences.getInt("first", 0);
		record[1] = preferences.getInt("second", 0);
		record[2] = preferences.getInt("third", 0);
		return record;
	}

}
