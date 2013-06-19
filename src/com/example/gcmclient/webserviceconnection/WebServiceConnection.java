package com.example.gcmclient.webserviceconnection;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import com.example.gcmclient.R;
import com.example.gcmclient.serviceutilities.CommonUtilities;
import com.example.gcmclient.sessionmanager.SessionManager;

public class WebServiceConnection {

	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private static String serverURL = 
    		CommonUtilities.SERVER_URL + CommonUtilities.REGISTER_PATH;
    private static String unregisterURL = 
    		CommonUtilities.SERVER_URL + CommonUtilities.UNREGISTER_PATH;
    private static String TAG = CommonUtilities.TAG;
    
    public static void webServiceRegister(final Context context,
    		String name,String email,String pass, final String regId){
    	Log.i(TAG, "registering device (regId = " + regId + ")");
    	// ==============================================================
    	// request params
	    	Map<String, String> params = new HashMap<String, String>();
	    	params.put("regId", regId);
	        params.put("name", name);
	        params.put("email", email);
	        params.put("pass", pass);
        // ===============================================================
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        for(int times = 0; times < MAX_ATTEMPTS; ++times)
        {
        	 Log.d(TAG, "Attempt #" + times + " to register");
        	 try{
        		 CommonUtilities.displayMessage(context, context.getString(
                         R.string.server_registering, times, MAX_ATTEMPTS));
        		 postRegistrationToWebService(params);
        		 GCMRegistrar.setRegisteredOnServer(context, true);
        		 String message = context.getString(R.string.server_registered);
        		 CommonUtilities.displayMessage(context, message);
        		 SessionManager.getSessionManager(context).putUserKey(regId);
        		 return;
        	 }catch (IOException e) {
        		 // Here we are simplifying and retrying on any error; in a real
                 // application, it should retry only on unrecoverable errors
                 // (like HTTP error code 503).
                 Log.e(TAG, "Failed to register on attempt " + times + ":" + e);
                 if (times == MAX_ATTEMPTS) {
                     break;
                 }
                 try {
                     Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                     Thread.sleep(backoff);
                 } catch (InterruptedException e1) {
                     // Activity finished before we complete - exit.
                     Log.d(TAG, "Thread interrupted: abort remaining retries!");
                     Thread.currentThread().interrupt();
                     return;
                 }
                 // increase backoff exponentially
                 backoff *= 2;
        	 } catch (JSONException e) {
				e.printStackTrace();
			}
        }
        // if this part of the code is executed, an error occur and was unnable
        // to register the device in the server
        String message = context.getString(R.string.server_register_error,
                MAX_ATTEMPTS);
        CommonUtilities.displayMessage(context, message);
    }
    
    private static void postRegistrationToWebService
    		(Map<String, String> params) throws IOException, JSONException{
    	URL url;
    	try{
    		url = new URL(serverURL);
    	}catch (MalformedURLException e) {
    		throw new IllegalArgumentException("invalid url: " + serverURL);
		}
    	JSONObject json_obj_req = new JSONObject(params);
    	Log.v(TAG,"Posting '" + json_obj_req.toString() + "' to " + url);
    	HttpURLConnection conn_to_server = null;
    	try{
    		 Log.i("URL", "> " + url);
    		 conn_to_server = (HttpURLConnection) url.openConnection();
    		 conn_to_server.setDoOutput(true);
    		 conn_to_server.setUseCaches(false);
    		 conn_to_server.setRequestMethod("POST");
    		 conn_to_server.setRequestProperty("Content-Type", "application/json");
    		 sendRequest(conn_to_server.getOutputStream(), json_obj_req);
             
             // handle the response
             int status = conn_to_server.getResponseCode();
             if (status != 200) {
            	 
            	 throw new IOException("Post failed with error code " + status);
             }
    	}finally{
    		if (conn_to_server != null) {
    			conn_to_server.disconnect();
            }
    	}
    }
    
    private static void deleteRegistration
    		(String unreg_URL, JSONObject params) throws IOException {
		URL url;
		try {
			url = new URL(unreg_URL);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + unreg_URL);
		}
		Log.v(TAG,"Posting '" + params.toString() + "' to " + url);
    	HttpURLConnection conn_to_server = null;
    	try{
			Log.i("URL", "> " + url);
			conn_to_server = (HttpURLConnection) url.openConnection();
			conn_to_server.setDoOutput(true);
			conn_to_server.setUseCaches(false);
			conn_to_server.setRequestMethod("DELETE");
			conn_to_server.setRequestProperty("Content-Type", "application/json");
			sendRequest(conn_to_server.getOutputStream(),params);			
			// handle the response
			int status = conn_to_server.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
    	}
    	finally
    	{
    		if (conn_to_server != null) {
    			conn_to_server.disconnect();
            }
    	}
	}
    
    private static void sendRequest
    		(OutputStream conn_to_server, JSONObject params) throws IOException
    {
    	// open the connection stream
		DataOutputStream printout =
				new DataOutputStream(conn_to_server);
		// convert the json message to be send into string
		String body = params.toString();
		// write into the output stream the message to be send
		printout.writeBytes(body);
		// send the request
		printout.flush ();
		// close the output stream
		printout.close ();
    }
    
    public static void webServiceUpdateUserKey
    		(final Context context, final String regId){
    	
    }
    
    /**
     * Unregister this account/device pair within the server.
     */
    public static void webServiceUnregister(final Context context, final String regId) {
    	String message = "";
        Log.i(TAG, "unregistering device (regId = " + regId + ")");
        String unreg_URL = String.format(unregisterURL, regId);
        try {
        	JSONObject params = new JSONObject();
            params.put("regId", regId);
        	deleteRegistration(unreg_URL, params);
            GCMRegistrar.unregister(context);
            message = context.getString(R.string.server_unregistered);
            CommonUtilities.displayMessage(context, message);
        } catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
			message = context.getString(R.string.server_register_error,e.getMessage());
            CommonUtilities.displayMessage(context, message);
		}
    }
}
