package com.example.gcmclient.powermanager;

import android.content.Context;
import android.os.PowerManager;

public abstract class WakeLoker {

	private static PowerManager.WakeLock wakelock;
	
	public static void acquire(Context context) {
		if(wakelock != null){
			wakelock.release();
			wakelock = null;
		}
		PowerManager power_manager = (PowerManager)
				context.getSystemService(Context.POWER_SERVICE);
		wakelock = power_manager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "WakeLock");
		wakelock.acquire();
	}
	
	public static void release() {
        if (wakelock != null){
        	wakelock.release();
        	wakelock = null;
        }
    }
}
