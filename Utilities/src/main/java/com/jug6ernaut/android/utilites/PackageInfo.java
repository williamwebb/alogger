package com.jug6ernaut.android.utilites;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class PackageInfo{
		/**
		 * @uml.property  name="myContext"
		 * @uml.associationEnd  
		 */
		Context myContext = null;
		/**
		 * @uml.property  name="packageName"
		 */
		private String PackageName = "";
		/**
		 * @uml.property  name="dataPath"
		 */
		private String DataPath = "";
		
		public PackageInfo(Context context){
			PackageManager packMgmr = null;
			ApplicationInfo appInfo = null;
			
			try{
			packMgmr = context.getPackageManager();
			}
			catch(Exception e){e.printStackTrace();}

			try {
				appInfo = packMgmr.getApplicationInfo(context.getPackageName(), 0);
			} catch (Exception e) {
				Log.d("PackageInfo","NameNotFoundException thrown");
				e.printStackTrace();
			}	
				
			PackageName = context.getPackageName();
			DataPath = appInfo.dataDir;
		}
		public String getDataDir(){
			return DataPath;
		}
		/**
		 * @return
		 * @uml.property  name="packageName"
		 */
		public String getPackageName(){
			return PackageName;
		}
		
	}