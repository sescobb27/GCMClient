package com.example.gcmclient.sessionmanager;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

	private static Context context;
	private static SessionManager session;
	private static final String PREFS_NAME = "GCM::Device";
	public static final String LOGGED_IN = "GCM_LoggedIn";
	public static final String USER_NAME = "GCM_name";
	public static final String USER_EMAIL = "GCM_email";
	public static final String USER_PASS = "GCM_pass";
	public static final String USER_KEY = "GCM_user_key";
	private SharedPreferences shared_preferences;
	private SharedPreferences.Editor editor; 
	
	private SessionManager(Context new_context) {
		context = new_context;
	}
	
	public static SessionManager getSessionManager(Context context){
		if(session == null)
		{
			session = new SessionManager(context);
		}else{
			changeContext(context);
		}
		return session;
	}
	
	private static void changeContext(Context new_context){
		if(context != new_context)
		{
			context = new_context;
		}
	}
	
	private void getSharedFromContext()
	{
		shared_preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	public boolean isLoggedIn()
	{
		getSharedFromContext();
		return shared_preferences.getBoolean(LOGGED_IN, false);
	}
	
	public String getString(String name)
	{
		getSharedFromContext();
		return shared_preferences.getString(name, null);
	}
	
	public void putAttributes(Map<String, Object> values)
	{
		getSharedFromContext();
		editor = shared_preferences.edit();
		for (Map.Entry<String, Object> entry: values.entrySet()) {
			if(entry.getValue() instanceof Boolean)
			{
				editor.putBoolean(entry.getKey(), (Boolean) entry.getValue());
			}else if(entry.getValue() instanceof String){
				editor.putString(entry.getKey(), (String) entry.getValue());
			}
		}
		editor.commit();
	}
	
	public void putUserKey(String regId){
		getSharedFromContext();
		editor = shared_preferences.edit();
		editor.putString(USER_KEY, regId);
		editor.commit();
	}
	
	public void logOut(){
		getSharedFromContext();
		editor = shared_preferences.edit();
		editor.putBoolean(LOGGED_IN, false);
		editor.commit();
	}
}
