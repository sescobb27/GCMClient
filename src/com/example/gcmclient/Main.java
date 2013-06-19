package com.example.gcmclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.gcmclient.powermanager.WakeLoker;
import com.example.gcmclient.serviceutilities.CommonUtilities;
import com.example.gcmclient.serviceutilities.ConnectionDetector;
import com.example.gcmclient.sessionmanager.SessionManager;
import com.example.gcmclient.webserviceconnection.WebServiceConnection;
import com.google.android.gcm.GCMRegistrar;

public class Main extends Activity {

	private static String name, email, pass;
	private AsyncTask<Void, Void, Void> register_task;
	private ConnectionDetector internet_detector;
	private final String TAG = "Main Activity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		internet_detector = ConnectionDetector.getConnectionDetector(getApplicationContext());
		boolean is_connected = internet_detector.isConnectingToInternet();
		if(!is_connected)
		{
			Log.e(TAG, "Please connect to working Internet connection");
    		return;
		}
		Intent register_intentIntent = getIntent();
		name = register_intentIntent.getStringExtra("name");
		email = register_intentIntent.getStringExtra("email");
		pass = register_intentIntent.getStringExtra("pass");
		
		// verifies that the device supports GCM and throws an exception if it does not
		GCMRegistrar.checkDevice(this);
		// verifies that the application manifest contains meets all the requirements 
		GCMRegistrar.checkManifest(this);
		
		registerReceiver(message_receiver_handler,
				new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
		// the device calls GCMRegsistrar.register() to register the device,
		// passing the SENDER_ID you got when you signed up for GCM. But since
		// the GCMRegistrar singleton keeps track of the registration ID upon
		// the arrival of registration intents, you can call GCMRegistrar.getRegistrationId()
		// first to check if the device is already registered.
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals(""))
		{
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		}
		else if(GCMRegistrar.isRegisteredOnServer(this))
		{
			Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
        }
		else
		{
			// Try to register again, but not in the UI thread.
            // It's also necessary to cancel the thread onDestroy(),
            // hence the use of AsyncTask instead of a raw thread.
            final Context context = this;
            register_task = new AsyncTask<Void, Void, Void>(){

				@Override
				protected Void doInBackground(Void... params) {
					// Register on our server
                    // On server creates a new user
                    WebServiceConnection.
                    		webServiceRegister(context, name, email, pass, regId);
                    return null;
				}
				
				@Override
				protected void onPostExecute(Void result) {
					register_task = null;
				}
            	
            };
            register_task.execute(null,null,null);
		}
	}
	
	private final BroadcastReceiver message_receiver_handler = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String new_message = intent.getStringExtra(CommonUtilities.EXTRA_MESSAGE);
			WakeLoker.acquire(getApplicationContext());
			
			WakeLoker.release();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.GCM_logout:
				SessionManager.getSessionManager(getApplicationContext()).logOut();
				Intent intent = 
						new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(intent);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public static String getName(){
		return name;
	}
	
	public static String getEmail(){
		return email;
	}
	
	public static String getPass(){
		return pass;
	}
	
	@Override
	protected void onDestroy() {
		if (register_task != null) {
			register_task.cancel(true);
        }
        try {
            unregisterReceiver(message_receiver_handler);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
		super.onDestroy();
	}

}
