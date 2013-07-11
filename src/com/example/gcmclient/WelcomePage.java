package com.example.gcmclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by simon on 7/11/13.
 */
public class WelcomePage extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.welcome_page );
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent register_intent = new Intent( getApplicationContext(), RegisterActivity.class );
                startActivity( register_intent );
                finish();
            }
        };
        Timer timer_on_task = new Timer();
        timer_on_task.schedule( task, 5000 );
    }
}
