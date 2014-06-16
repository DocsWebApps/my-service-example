package com.example.myserviceexample;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class DoSomethingService extends Service {
	
	private volatile Looper mServiceLooper;
	private volatile ServiceHandler mServiceHandler;
	
	// Factory method to make an Intent used to start this service
	public static Intent makeIntent(Context context) {
		Log.i("MyService", "makeIntent");
		Intent intent=new Intent(context, DoSomethingService.class);
		return intent;
	}
	
	// Start a looper thread and create a ServiceHandler passing in the looper reference
	public void onCreate() {
		super.onCreate();
		Log.i("MyService", "onCreate");
		
		HandlerThread thread=new HandlerThread("DoSomethingService");
		thread.start();
		
		mServiceLooper=thread.getLooper();
		mServiceHandler=new ServiceHandler(mServiceLooper);
	}
	
	// Create a message to doSomething and post to the Service Handler's looper
    public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("MyService", "onStartCommand");
    	Message message=mServiceHandler.makeDoSomethingMessage(intent, startId);
    	mServiceHandler.sendMessage(message);
    	return Service.START_NOT_STICKY;
    }
    
    private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}
		
		// Entry point for messages, triggered by mServiceHandler.sendMessage(message);
		public void handleMessage(Message message) {
			doSomethingAndReply((Intent) message.obj);
			stopSelf(message.arg1);
		}
		
		// Main code here
		private void doSomethingAndReply(Intent intent) {
			String returnItem=intent.getExtras().getString("message");
			Message message = makeReplyMessage(returnItem);
			Messenger messenger = (Messenger)intent.getExtras().get("messenger");
            try {
            	Thread.sleep(2000);
                messenger.send(message);
            } catch (Exception e) { }
		}
		
		// Make DoSomething message
		private Message makeDoSomethingMessage(Intent intent, int startId) {
			Message message=Message.obtain();
			message.arg1=startId;
			message.obj=intent;
			return message;
		}
		
		// Make Reply message
		private Message makeReplyMessage(String returnItem) {
			Message message=Message.obtain();
			message.arg1= returnItem==null ? Activity.RESULT_CANCELED : Activity.RESULT_OK;
			Bundle bundle=new Bundle();
			bundle.putString("RETURN",returnItem);
			message.setData(bundle);
			return message;
		}
	}
	
    // ****************************************************
	// Must have this method even if you don't implement it
    // It is for bound services
	@Override
	public IBinder onBind(Intent arg0) {return null;}
}
