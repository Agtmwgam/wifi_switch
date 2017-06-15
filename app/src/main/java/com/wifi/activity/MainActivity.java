package com.wifi.activity;

import com.example.esp8266.R;
import com.wifi.bar.SystemBarTintManager;
import com.wifi.service.WifiService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnCheckedChangeListener,OnClickListener {

	// ×´Ì¬À¸±³¾°¹ÜÀíÀà
	private SystemBarTintManager tintManager;
	// Toggle_button
	private ToggleButton bt_switch;
	// data
	private byte socket_dat[] = { 0x24, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00 };
	// SEND_ACTION
	private static final String SEND_ACTION = "action.send.broadcast";
	private static String CONNECT_ACTION ="action.connect.broadcast";
	// Bundle
	private Bundle bundle = null;
	//action string for service
	private String ACTION_SERVICE = "action.internet.service";
	//ServiceConnection for bind Service
	private ServiceConnection conn;
	//Button to connect ;
	private TextView connect;
	private Intent broadcastIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
			tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintResource(R.color.bar_color);
		}
		setContentView(R.layout.activity_main);
		bt_switch = (ToggleButton) findViewById(R.id.tgbt_1);
		bundle = new Bundle();
		broadcastIntent = new Intent();
		connect = (TextView) findViewById(R.id.bt_connect);
		startAndBindInternetService();
	}

	private void startAndBindInternetService() {
		Intent intent = new Intent(this,WifiService.class);
		startService(intent);
//		intent.setAction(ACTION_SERVICE);
		conn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName paramComponentName) {
				Log.e("MainActivity", "bind is fail");
			}			
			@Override
			public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder) {
				Log.e("MainActivity", "bind is successful");
			}
		};
		
		bindService(intent, conn, BIND_AUTO_CREATE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		bt_switch.setOnCheckedChangeListener(this);
		connect.setOnClickListener(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(conn != null)
		{
			unbindService(conn);
			Log.e("MainActivity", "unbind is successful");
		}
	}
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		if (bundle != null) {
			bundle.clear();
		} else {
			bundle = new Bundle();
		}
		broadcastIntent.setAction(SEND_ACTION);
		if (arg1 == true) {
			socket_dat[1]=1<<6;
			broadcastIntent.putExtra("data", socket_dat);
			sendBroadcast(broadcastIntent);
		} else {
			socket_dat[1]=0x00;
			broadcastIntent.putExtra("data", socket_dat);
			sendBroadcast(broadcastIntent);
		}

	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.bt_connect:
			broadcastIntent.setAction(CONNECT_ACTION);
			sendBroadcast(broadcastIntent);
			break;

		default:
			break;
		}
	}
}