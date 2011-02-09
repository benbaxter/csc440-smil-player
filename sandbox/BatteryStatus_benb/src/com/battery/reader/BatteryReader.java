package com.battery.reader;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class BatteryReader extends Activity {
    /** Called when the activity is first created. */
	private static final String TAG = "MyPrints";
	String FILENAME = "batteryStats.txt";
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        Log.i(TAG, "Start monitor method");
        monitorBatteryState(this);
        Log.i(TAG, "End monitor method");
    }
    
    private void monitorBatteryState(Context context) {
    	BroadcastReceiver battReceiver = new BroadcastReceiver() {
    		@Override
    		public void onReceive(Context context, Intent intent) {
	
	            context.unregisterReceiver(this);
	            
	            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
	            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
	            boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
	            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
	            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
	            int plug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
	            int volts = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
	            double temp = (9/5)*(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10) + 32;
	            String tech = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
	            double batteryLevel = (100.0 * rawlevel) / scale;
	            String healthDisplay = "Crippled";
	            if( health == BatteryManager.BATTERY_HEALTH_GOOD )
	            	healthDisplay = "Good";
	            else if( health == BatteryManager.BATTERY_HEALTH_OVERHEAT )
	            	healthDisplay = "Overheating";
	            String statusDisplay = "";
	            if( status == BatteryManager.BATTERY_STATUS_CHARGING )
	            	statusDisplay = "Charging";
	            else if( status == BatteryManager.BATTERY_STATUS_NOT_CHARGING ) 
	            {
	            	statusDisplay = "Not Charging";
	            }
	            else if( status == BatteryManager.BATTERY_STATUS_FULL)
	            	statusDisplay = "Full";
	            String plugDisplay = "Not plugged in";
	            if ( plug == BatteryManager.BATTERY_PLUGGED_AC )
	            	plugDisplay = "AC";
	            else if ( plug == BatteryManager.BATTERY_PLUGGED_USB )
	            	plugDisplay = "USB";
	            DecimalFormat df = new DecimalFormat("###.#%");
	            StringBuilder display = new StringBuilder("Battery Level: ");
	            display.append(df.format(batteryLevel/100));
	            display.append("\nBattery Status: " + statusDisplay);
	            display.append("\nBattery Health: " + healthDisplay);
	            display.append("\nBattery Present: " + present);
	            display.append("\nBattery Plug: " + plugDisplay);
	            display.append("\nBattery Volts: " + volts);
	            display.append("\nBattery Temp: " + temp +  "\u00b0F");
	            display.append("\nBattery Tech: " + tech);
	            
				TextView batteryStatusView = new TextView(context);
				batteryStatusView.setText(display);
				setContentView(batteryStatusView);
			}
        };
        IntentFilter battFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(battReceiver, battFilter);
    }
    
    static final int REFRESH = 0;
    static final int CLEARFILE = 1;
    static final int EXIT = 2;
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	menu.add(0, REFRESH, 0, "Refresh");
    	menu.add(0, CLEARFILE, 0, "Clear File");
    	menu.add(0,EXIT,0,"Exit");
    	return true;
    }
    public boolean onOptionsItemSelected (MenuItem item){
    	switch (item.getItemId())
    	{
	    	case EXIT:
	    		System.exit(0);
		    	return true;
	    	case REFRESH:
	            monitorBatteryState(getApplicationContext());
	            break;
	    	case CLEARFILE:
	    		File file = new File( getFilesDir() + "/" + FILENAME);
	    		file.delete();
	    		monitorBatteryState(getApplicationContext());
	    		break;
    	}
    	return false;
    }
}