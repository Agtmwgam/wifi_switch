package com.wifi.service;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.example.esp8266.R.id;

import android.R.string;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

public class WifiService extends Service{
	
	private static String SEND_ACTION ="action.send.broadcast";
	private static String CONNECT_ACTION ="action.connect.broadcast";
	private static Socket socket=null;
	public Thread mySockteThead=null;
	private static Context  serviceContext;
	private IBinder iBinder;
	private ReceiveBroadcastOfSendData receiveBroadcastOfSendData = new ReceiveBroadcastOfSendData();
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("Sercice", "onCreate is successful");
		mySockteThead = new MyThread("wifi_esp8266");
		mySockteThead.start();
		serviceContext = new WifiService();
	}
	@Override
	public IBinder onBind(Intent intent) {
		Log.e("Sercice", "onbind is successful");
		return new MyIbinder();
	}  
	
	@Override
	public boolean onUnbind(Intent intent) {
		if (socket!=null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.onUnbind(intent);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (socket!=null) {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static class MyThread extends Thread {  
  	  
        public String txt1;  
  
        public MyThread(String str) {  
            txt1 = str;  
        }  
  
        @Override  
        public void run() {  
            //定义消息   
//            Message msg = new Message();  
//            msg.what = 0x11;  
//            Bundle bundle = new Bundle();  
//            bundle.clear();  
            try {  
                //连接服务器 并设置连接超时为5秒   
                socket = new Socket();  
                socket.connect(new InetSocketAddress("192.168.4.1", 8266), 5000);  
                Log.e("Sercice-MyThread", "connection is successful");

            } catch (SocketTimeoutException aa) {  
                //连接超时 在UI界面显示消息   
//                bundle.putString("msg", "服务器连接失败！请检查网络是否打开");  
//                msg.setData(bundle);  
                //发送消息 修改UI线程中的组件   
//                myHandler.sendMessage(msg); 
            	Log.e("wifiService", "socketTimeoutEception");
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    }
	
	public  void  Socket_Send(byte dat[])
	 {
		 try {
			 if(socket!=null){
			  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			  PrintWriter out = new PrintWriter(socket.getOutputStream());
		      out.write(dat[0]);
		      out.write(dat[1]);
		      out.write(dat[2]);
		      out.write(dat[3]);
		      out.write(dat[4]);
		      out.write(dat[5]);
		      out.write(dat[6]);
		      
		      out.flush();
			 }else{
				 Log.e("sendData", "socket=null");
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	
	
	public static class  ReceiveBroadcastOfSendData extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context c, Intent intent) {
			if (intent.getAction()==SEND_ACTION) {
			 byte[] data = intent.getByteArrayExtra("data");
			((WifiService) serviceContext).Socket_Send(data);
			}else if (intent.getAction()==CONNECT_ACTION) {
				if(socket != null)
				{
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				//if (((WifiService) serviceContext).mySockteThead!=null) {
					((WifiService) serviceContext).mySockteThead=null;
					((WifiService) serviceContext).mySockteThead = new  MyThread("8266");
					((WifiService) serviceContext).mySockteThead.start();
				//}
			}
		}
		
	}
	
	class MyIbinder extends Binder
	{

		
	}
}
