package com.example.gcmclient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.gcmclient.R;
import com.example.gcmclient.serviceutilities.CommonUtilities;
import com.example.gcmclient.sessionmanager.SessionManager;
import com.example.gcmclient.webserviceconnection.WebServiceConnection;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	
	public GCMIntentService() {
		super(CommonUtilities.SENDER_ID);
	}
	/*
	 * (non-Javadoc)
	 * @see com.google.android.gcm.GCMBaseIntentService#onError(android.content.Context, java.lang.String)
	 * Called when the device tries to register or unregister,
	 * but GCM returned an error. Typically, there is nothing to be done
	 * other than evaluating the error (returned by errorId) and trying to fix the problem.
	 */
	@Override
	protected void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.android.gcm.GCMBaseIntentService#onMessage(android.content.Context, android.content.Intent)
	 * Called when your server sends a message to GCM, and GCM delivers
	 * it to the device. If the message has a payload, its contents are
	 * available as extras in the intent.
	 */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message");
        /*String message = intent.getExtras().getString("price");
 
        CommonUtilities.displayMessage(context, message);
        // notifies user
        generateNotification(context, message);*/
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.android.gcm.GCMBaseIntentService#onRegistered(android.content.Context, java.lang.String)
	 * Called after a registration intent is received, passes the registration
	 * ID assigned by GCM to that device/application pair as parameter.
	 * Typically, you should send the regid to your server so it can use it
	 * to send messages to this device.
	 */
	@Override
	protected void onRegistered(Context context, String regId) {
		Log.i(TAG, "Device registered: regId = " + regId);
		CommonUtilities.displayMessage(context, "Your device registred with GCM");
        Log.d("NAME", Main.getName());
        SessionManager session = 
        		SessionManager.getSessionManager(context.getApplicationContext());
        String user_reg_id = session.getString(SessionManager.USER_KEY);
        if(user_reg_id == null)
        {
	        WebServiceConnection.webServiceRegister(
	        		context, Main.getName(), Main.getEmail(),Main.getPass(), regId);
        }else if(!user_reg_id.equals(regId))
        {
        	WebServiceConnection.webServiceUpdateUserKey(context, regId);
        }
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.android.gcm.GCMBaseIntentService#onUnregistered(android.content.Context, java.lang.String)
	 * Called after the device has been unregistered from GCM.
	 * Typically, you should send the regid to the server so it unregisters the device.
	 */
	@Override
	protected void onUnregistered(Context context, String regId) {
		Log.i(TAG, "Device unregistered");
		WebServiceConnection.webServiceUnregister(context, regId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_unregistered));
	}
	
	private void generateNotification(Context context, String message){
		int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationCompat.Builder msg_builder = 
        		new NotificationCompat.Builder(context)
        		.setWhen(when)
        		.setContentTitle("New Coupon")
        		.setSmallIcon(icon)
        		.setContentText(message);
        /*Intent notificationIntent = new Intent(context, GCMIntentService.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(Main.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent resultPendingIntent = stackBuilder.
        		getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        msg_builder.setContentIntent(resultPendingIntent);
        // create a notification manager form message notifications 
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, msg_builder.build());
        
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;*/
	}

}
