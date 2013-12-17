
package com.jug6ernaut.android.utilites;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


public class BaseAsyncTask extends AsyncTask<Object, Object, Integer> {
    
    public ProgressDialog pd = null; 
    WakelockHelper wlh = null;
    Context ctx = null;
    String tag = "";
    
    // Will just log in
    public BaseAsyncTask(Context context, String title) {
    	
    	if(Looper.myLooper()==null)Looper.prepare();
    	
    	ctx = context;
    	pd = new ProgressDialog(ctx);
    	tag = title + "_AST";
    	wlh = new WakelockHelper(ctx,tag);
    }

    @Override
    protected void onPreExecute(){
    	
    	wlh.acquire();
    	
    	try{
        	if(ctx!=null && (ctx instanceof Activity)){
        		pd.setTitle(tag);
        		pd.setCancelable(false);
        		pd.show();
        	}
        	else Log.e(tag,"Context NULL or NOT Activity");
        	}catch(Exception e){
        		e.printStackTrace();
        	}
    }

    @Override
    protected void onPostExecute(Integer results) {
    
    	try{
        	if(ctx!=null  && (ctx instanceof Activity)){
        		pd.hide();
        	}}
        	catch(Exception e){
        		e.printStackTrace(	);
        	}
        wlh.release();
    }
    
    protected void onProgressUpdate(Object... progress) {
    	if(ctx!=null && (ctx instanceof Activity)){
    		if(!pd.isShowing())pd.show();
    		
    		pd.setTitle((String)progress[0]);
    		pd.setMessage((String)progress[1]); 
    	}
    }
    
    public void progress(String title, String msg){
    	this.publishProgress(title,msg);
    }
    
    
    
    public void showToast(Context context,String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
    }

	@Override
	protected Integer doInBackground(Object... arg0) {
		// TODO Auto-generated method stub
		return null;
	}
    
}
