package com.jug6ernaut.android.utilites;

import android.app.Application;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.PowerManager;

/**
 * Created by williamwebb on 11/2/13.
 */
public class DeviceWake {

    KeyguardManager km;
    PowerManager pm;

    private KeyguardManager.KeyguardLock myKeyLock;
    private PowerManager.WakeLock myWakeLock;

    private boolean keyGuardState;

    public DeviceWake(Application context){
        km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    public void release(){
        if (myWakeLock.isHeld()){
            myWakeLock.release();
            if(keyGuardState)
                myKeyLock.reenableKeyguard();
        }
    }

    public DeviceWake attain(){
        if(myKeyLock==null)
            myKeyLock = km.newKeyguardLock("MyKeyguardLock");

        keyGuardState = km.isKeyguardLocked();

        if(keyGuardState)
            myKeyLock.disableKeyguard();

        if(myWakeLock==null)
            myWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
        myWakeLock.acquire();

        return this;
    }

    public void setTimeout(long milli){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                release();
            }
        }, milli);
    }

    public static void checkManifest(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        String permissionName = packageName + ".permission.C2D_MESSAGE";
        // check permission
        try {
            packageManager.getPermissionInfo(permissionName,
                    PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException(
                    "Application does not define permission " + permissionName);
        }


        // check receivers
//        android.content.pm.PackageInfo receiversInfo;
//        try {
//            receiversInfo = packageManager.getPackageInfo(
//                    packageName, PackageManager.GET_RECEIVERS);
//        } catch (PackageManager.NameNotFoundException e) {
//            throw new IllegalStateException(
//                    "Could not get receivers for package " + packageName);
//        }
//        ActivityInfo[] receivers = receiversInfo.receivers;
//        if (receivers == null || receivers.length == 0) {
//            throw new IllegalStateException("No receiver for package " +
//                    packageName);
//        }
//        if (Log.isLoggable(TAG, Log.VERBOSE)) {
//            Log.v(TAG, "number of receivers for " + packageName + ": " +
//                    receivers.length);
//        }
//        Set<String> allowedReceivers = new HashSet<String>();
//        for (ActivityInfo receiver : receivers) {
//            if (GCMConstants.PERMISSION_GCM_INTENTS.equals(
//                    receiver.permission)) {
//                allowedReceivers.add(receiver.name);
//            }
//        }
//        if (allowedReceivers.isEmpty()) {
//            throw new IllegalStateException("No receiver allowed to receive " +
//                    GCMConstants.PERMISSION_GCM_INTENTS);
//        }

    }
}
