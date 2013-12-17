/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jug6ernaut.android.logging;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class LogFragment extends Fragment implements
	LoaderManager.LoaderCallbacks<ArrayList<LogEntry>>, ALogger.OnLogListener {
	
	private FragmentActivity mActivity;
	private ALogger logger = ALogger.getRootLogger();
	private ArrayList<LogEntry> logs = null;
	private ListView listView = null;
	private boolean pauseLog = false;

    private boolean showActionBar = false;
	
    public LogFragment(){}

	public LogFragment(ALogger logger,boolean showActionBar){
		this.logger = logger;
        this.showActionBar = showActionBar;
	}
	
	public void attachTo(FragmentActivity a){
		this.attachTo(a,android.R.id.content);
	}
    public void attachTo(FragmentActivity a, int viewId) {
        a.getSupportFragmentManager().beginTransaction().add(
                viewId, this).commit();
    }
	
	public void detach(FragmentActivity a){
		a.getSupportFragmentManager().beginTransaction().remove(this).commit();
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        mActivity = this.getActivity();
        getLoaderManager().initLoader(0, null, this);

        if(showActionBar)
 	        mActivity.startActionMode(new LogActionMode(mActivity.getActionBar(),this));
 	  
    }
        
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
           Bundle savedInstanceState) {


       	listView = new ListView(this.getActivity());
       	listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
       	listView.setBackgroundColor(Color.BLACK);
	 	listView.setOnItemClickListener(new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,	int arg2, long arg3) {
			//Will eventually load stacktraces here
			//LogEntry entry = logs.get(arg2);
				
		}});
	 	        	
        return listView;
   }
        
   @Override 
   public void onStart(){
	   super.onStart();
	   logger.setOnLogListener(this);
   }
   @Override
   public void onSaveInstanceState(Bundle outState){
	   super.onSaveInstanceState(outState);
   }
   @Override
   public void onStop(){
	   super.onStop();
	   logger.removeOnLogListener(this);
   }
   
   @Override 
   public void onDestroy(){
	   super.onDestroy();
	   
   }

        
	private final class LogActionMode implements ActionMode.Callback {
		
		private static final int MENU_SEND_LOG = 1;
		private static final int MENU_CLEAR_LOG = 2;
		private static final int MENU_PAUSE_LOG = 3;
		private String SEND_LOG = "Send Logs";
		private String CLEAR_LOG = "Clear Logs";
		private String PAUSE_LOG = "Pause";
		
		private int previusNavigationMode = -1;
		
		Fragment f = null;
		ActionBar ab = null;
		public LogActionMode(ActionBar ab, Fragment f){
			this.ab = ab;
			this.f = f;
		}
		
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        	
        	mode.setTitle("Log");

        	SubMenu subMenu1 = menu.addSubMenu("");
            subMenu1.add(0, 1, MENU_SEND_LOG, SEND_LOG);
            subMenu1.add(0, 1, MENU_CLEAR_LOG, CLEAR_LOG);
            subMenu1.add(0, 1, MENU_PAUSE_LOG, PAUSE_LOG);

            MenuItem subMenu1Item = subMenu1.getItem();
            subMenu1Item.setIcon(android.R.drawable.ic_menu_add);
            subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        	previusNavigationMode = ab.getNavigationMode();
        	ab.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            return true;
        }
        
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        	switch(item.getOrder()){
        	case MENU_SEND_LOG:{
        		sendDebugLog(f.getActivity(),logger);
        	}break;
        	case MENU_CLEAR_LOG:{
        		logger.clearLogFile();
        		((LogAdapter)listView.getAdapter()).mEntries.clear();
        		((LogAdapter)listView.getAdapter()).notifyDataSetChanged();
        	}break;
        	case MENU_PAUSE_LOG:{
        		if(pauseLog){
        			item.setTitle("Start");
        			pauseLog = false;
        			PAUSE_LOG = "Start";
        		}else {
        			item.setTitle("Pause");
        			pauseLog = true;
        			PAUSE_LOG = "Pause";
        		}
        		mode.invalidate();
        	}break;
        	}
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        	getFragmentManager().beginTransaction().remove(f).commit();
        	ab.setNavigationMode(previusNavigationMode);
        }
    }
	
		
	class LogLoader extends AsyncTaskLoader<ArrayList<LogEntry>> {
		 
	    public LogLoader(Context context,ALogger logger ) {
	        super(context);
	    }

	    @Override
	    public ArrayList<LogEntry> loadInBackground() {
	    	return JSONLogReader.getLogsFromFile(logger.getLogFile().getPath());
	    }
	}


	@Override
	public Loader<ArrayList<LogEntry>> onCreateLoader(int arg0, Bundle arg1) {
		
        AsyncTaskLoader<ArrayList<LogEntry>> loader = new AsyncTaskLoader<ArrayList<LogEntry>>(getActivity()) {
        	 
        	@Override
		    public ArrayList<LogEntry> loadInBackground() {
		    	return JSONLogReader.getLogsFromFile(logger.getLogFile().getPath());
		    }
        };

        loader.forceLoad();
		
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<LogEntry>> arg0,ArrayList<LogEntry> arg1) {
		logs = arg1;
		listView.setAdapter(new LogAdapter(mActivity, logs));
		listView.postDelayed(new Runnable(){

			@Override
			public void run() {
				listView.setSelection(logs.size()-1);
		}}, 1000);
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<LogEntry>> arg0) {
		listView.setAdapter(null);
	}

	@Override
	public void onLog(final LogEntry log) {
		if(!pauseLog)
		this.getActivity().runOnUiThread(new Runnable(){

			@Override
			public void run() {
                if(logs==null){
                    return;
                }
				logs.add(log);
				((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
				listView.post(new Runnable(){

					@Override
					public void run() {
						listView.setSelection(logs.size()-1);
				}});
			}});
		
	}
	
 	public void sendDebugLog(Context context, ALogger logger){

        if(true) throw new RuntimeException("FIXME");

 		String subject = context.getApplicationInfo().className;

        String body = "";
        body += "MANUFACTURER: " + Build.MANUFACTURER + "\n";
        body += "DEVICE: " + Build.DEVICE + "\n";
        body += "MODEL: " + Build.MODEL + "\n";
        //body += "HARDWARE: " + Build.HARDWARE + "\n";
        //body += "FINGERPRINT: " + Build.FINGERPRINT + "\n";
        //body += "PRODUCT: " + Build.PRODUCT + "\n";
        //body += "BRAND: " + Build.BRAND + "\n";
        body += "OS LEVEL: " + Build.VERSION.RELEASE + "\n";
        body += "SDK: " + Build.VERSION.SDK_INT + "\n\n";
        body += "Comments: ";
        
        if(!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
        	Toast.makeText(context, "Send Failed: Need sdcard access.", Toast.LENGTH_LONG).show();
        	return;
        }
        
        File source = logger.getLogFile();
        File dest = null;
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.FROYO){
        	dest = new File(context.getExternalFilesDir(null).getPath() + "/" + source.getName());
        }else {
        	dest = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/jug6ernaut.util.ldn/files/");
        	dest.mkdirs();
        }
        dest.deleteOnExit();
        
        boolean b = false;// CopyUtils.copy(source,dest); //FIXME
        
        Log.e("LDN","Copy: " + String.valueOf(b));        

		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent .putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"jug6ernaut.feedback@gmail.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(dest));
        
        startActivity(Intent.createChooser(emailIntent, "Email:"));
 	}
   
}


