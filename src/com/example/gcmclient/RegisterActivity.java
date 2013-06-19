package com.example.gcmclient;

import java.util.HashMap;
import java.util.Map;

import com.example.gcmclient.serviceutilities.ConnectionDetector;
import com.example.gcmclient.sessionmanager.SessionManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import static com.example.gcmclient.serviceutilities.CommonUtilities.SENDER_ID;
import static com.example.gcmclient.serviceutilities.CommonUtilities.SERVER_URL;

public class RegisterActivity extends Activity implements OnClickListener{

	// Internet detector
    private ConnectionDetector internet_detector;
    
    private EditText txt_name, txt_email,txt_pass;
    private Button btn_register;
    private final String TAG = "Register Activity";
    private SessionManager session;
    private String name;
    private String email;
    private String pass;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.register_activity);
    	
    	internet_detector = ConnectionDetector.getConnectionDetector(getApplicationContext());
    	boolean is_connected = internet_detector.isConnectingToInternet();
    	if(!is_connected)
    	{
    		Log.e(TAG, "Please connect to working Internet connection");
    		return;
    	}
    	// Check if GCM configuration is set
        if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
                || SENDER_ID.length() == 0) {
        	Log.e(TAG, "Configuration Error, server url and sender id");
        }
        session = SessionManager.getSessionManager(getApplicationContext());
        boolean is_logged_in = session.isLoggedIn();
        if(is_logged_in)
        {
        	name = session.getString(SessionManager.USER_NAME);
        	email = session.getString(SessionManager.USER_EMAIL);
        	pass = session.getString(SessionManager.USER_PASS);
        	startMainActivity();
        }
        
        txt_name = (EditText) findViewById(R.id.txtName);
        txt_email = (EditText) findViewById(R.id.txtEmail);
        txt_pass = (EditText) findViewById(R.id.txtPass);
        btn_register = (Button) findViewById(R.id.btnRegister);
        btn_register.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		name = txt_name.getText().toString();
        email = txt_email.getText().toString();
        pass = txt_pass.getText().toString();
        
        if(name.trim().length() > 0 && email.trim().length() > 0)
        {
        	Map<String,Object> to_edit = new HashMap<String, Object>();
        	to_edit.put(SessionManager.LOGGED_IN, true);
        	to_edit.put(SessionManager.USER_EMAIL, email);
        	to_edit.put(SessionManager.USER_NAME, name);
        	to_edit.put(SessionManager.USER_PASS, pass);
        	session.putAttributes(to_edit);
        	startMainActivity();
        }
        else
        {
        	Log.e(TAG, "Blank fields");
        }
	}
	
	private void startMainActivity(){
		Intent main_activity_intent = new Intent(getApplicationContext(), Main.class);
    	main_activity_intent.putExtra("name", name);
    	main_activity_intent.putExtra("email", email);
    	main_activity_intent.putExtra("pass", pass);
    	startActivity(main_activity_intent);
        finish();
	}
}
