package com.example.myserviceexample;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private String myMessage="Please return me to my Activity?";
	private TextView textView;
	private ActivityHandler activityHandler=new ActivityHandler();
	private WeakReference<ActivityHandler> activityHandlerRef=new WeakReference<ActivityHandler>(activityHandler); 

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView=(TextView) findViewById(R.id.textView1);
		
		doSomething();
	}

    public void doSomething() {
    	// Starts a service in a new process - see Manifest file
        Intent intent = DoSomethingService.makeIntent(this);
        intent.putExtra("message", myMessage);
        intent.putExtra("messenger", new Messenger((ActivityHandler) activityHandlerRef.get()));
        startService(intent);
    }
	
	public class ActivityHandler extends Handler{
		public void handleMessage(Message message) {
			Log.i("MainActivity","DownloadHandler");
			Bundle bundle=message.getData();
			textView.setText((String) bundle.getString("RETURN"));
		}
	}
}
