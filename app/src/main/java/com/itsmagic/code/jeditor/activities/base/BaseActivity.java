package com.itsmagic.code.jeditor.activities.base;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.itsmagic.code.jeditor.activities.CrashActivity;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class BaseActivity extends AppCompatActivity {
    private Thread.UncaughtExceptionHandler thrUEH;
    
    public final void setUEH() {
        thrUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread curThr, Throwable ex) {
				Intent caInt = new Intent(BaseActivity.this.getApplicationContext(), CrashActivity.class);
                caInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                caInt.putExtra("crashStackTrace", getThrowableStackTrace(ex));
				
				PendingIntent pdInt = PendingIntent.getActivity(BaseActivity.this.getApplicationContext(), 69, caInt, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager aMan = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                
				finishAffinity();
				aMan.set(AlarmManager.RTC_WAKEUP, 50, pdInt);
                
                thrUEH.uncaughtException(curThr, ex);
            }
        });
    }
    
    private final String getThrowableStackTrace(final Throwable err) {
        final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
        
		try {
			Throwable cause = err;
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
        
			printWriter.close();
        	result.close();
        } catch (IOException ioe) {}
        
		return result.toString();
	}
	
	public final void unsetUEH() {
		if (thrUEH != null) Thread.setDefaultUncaughtExceptionHandler(thrUEH); thrUEH = null;
	}
}
