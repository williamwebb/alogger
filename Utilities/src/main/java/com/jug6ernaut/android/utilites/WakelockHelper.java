package com.jug6ernaut.android.utilites;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class WakelockHelper {
	
	/**
	 * @uml.property  name="tag"
	 */
	String tag ="";
    /**
	 * @uml.property  name="wakeLock"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    WakeLock wakeLock = null;
    /**
	 * @uml.property  name="wifiLock"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    WifiLock wifiLock = null;
    /**
	 * @uml.property  name="mName"
	 */
    String mName = "";
    
    public WakelockHelper(Context ctx, String name){
 
    	mName = name;
    	
    	PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,name + ".WakeLock");
        
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL , name + ".WifiLock");
        
    }

     
      public boolean acquire(){
        	
        	
        	try {
                
                if(!wakeLock.isHeld()){
                    wakeLock.acquire();
                }
                Log.d(tag,mName + ".WakeLock acquired!");

                if(!wifiLock.isHeld()){
                    wifiLock.acquire();
                }

                Log.d(tag, mName + ".WifiLock acquired!");
                
        	}catch(Exception e){
        		Log.e(tag,"Error getting wakelock");
        		return false;
        		};
        
			return true;
        }
        
      public void release(){
    	  
    	  if (wakeLock != null) {
              if (wakeLock.isHeld()) {
                  wakeLock.release();
                  Log.d(tag, mName + ".WakeLock released!");
              }
          }

          // release the WifiLock
          if (wifiLock != null) {
              if (wifiLock.isHeld()) {
                  wifiLock.release();
                  Log.d(tag, mName + ".WiFiLock released!");
              }
          }
      }
}