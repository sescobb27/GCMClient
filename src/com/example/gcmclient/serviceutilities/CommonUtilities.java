package com.example.gcmclient.serviceutilities;

import android.content.Context;
import android.content.Intent;

public class CommonUtilities {

	public static final String SENDER_ID = "171777071834";
	public static final String SERVER_URL = "http://192.168.0.109:3000";
	//public static final String SERVER_URL = "http://192.168.42.232:3000";
	public static final String REGISTER_PATH = "/receivers.json";
	public static final String UNREGISTER_PATH = "/receivers/1s.json";
	public static final String DISPLAY_MESSAGE_ACTION =
            "com.example.gcmclient.DISPLAY_MESSAGE";
	public static final String TAG = "GCM";
	public static final String EXTRA_MESSAGE = "message";
	
	/**
     * Notifies UI to display a message.
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
