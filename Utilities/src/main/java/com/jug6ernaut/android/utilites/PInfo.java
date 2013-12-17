package com.jug6ernaut.android.utilites;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PInfo {

    public String appname = "";
    public String pname = "";
    public String versionName = "";
    public int versionCode = 0;
    public Drawable icon;
    public Context context = null;

    public PInfo(Context ctx) {
        this.context = ctx;
    }

    private void prettyPrint() {
        //    Log.v(appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
    }


    private ArrayList<PInfo> getPackages(Context context) {
        ArrayList<PInfo> apps = getInstalledApps(context, false); /* false = no system packages */
        final int max = apps.size();
        for (int i = 0; i < max; i++) {
            apps.get(i).prettyPrint();
        }
        return apps;
    }

    public static ArrayList<PInfo> getInstalledApps(Context context, boolean getSysPackages) {

        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        ArrayList<PInfo> res = new ArrayList<PInfo>();

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo p = packages.get(i);

            if (getSysPackages) {
                if ((!getSysPackages) && (p.versionName == null)) {
                    continue;
                }

                if ((packages.get(i).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                    continue;
                }
            } else if ((packages.get(i).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                continue;
            }

            PInfo newInfo = new PInfo(context);
            newInfo.appname = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
            newInfo.pname = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            newInfo.icon = p.applicationInfo.loadIcon(context.getPackageManager());
            res.add(newInfo);
        }

        return res;
    }

    public static PInfo getAppInfo(Context context, String packageName) {
        PInfo newInfo = new PInfo(context);
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);
            newInfo.appname = info.loadLabel(context.getPackageManager()).toString();
            newInfo.pname = info.packageName;
            newInfo.versionName = "";
            newInfo.versionCode = -1;
            newInfo.icon = info.loadIcon(context.getPackageManager());

        } catch (Exception e) {
            return null;
        }
        return newInfo;
    }

    public static class byAppName implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            PInfo u1 = (PInfo) o1;
            PInfo u2 = (PInfo) o2;
            return u1.appname.compareTo(u2.appname);
        }
    }
}