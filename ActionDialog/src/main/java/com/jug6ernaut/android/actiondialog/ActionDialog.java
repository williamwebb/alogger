package com.jug6ernaut.android.actiondialog;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 6/10/13
 * Time: 9:03 PM
 */
/*
    Class used to encapsulate dialogs where you have some functionality and ether want the results of the functionality
    or the error/cancel.
 */
public abstract class ActionDialog<T> {

    public interface Callback<T>{
        public void onResult(T t);
        public void onCancel();
        public void onFailure(Exception e);
    }

    protected AlertDialog dialog = null;
    protected Activity activity;
    protected LayoutInflater inflater;
    protected Callback<T> callback;

    protected ActionDialog(final Activity activity,final Callback<T> callback) {
        ActionDialog.this.callback = callback;
        ActionDialog.this.activity = activity;
        ActionDialog.this.inflater = LayoutInflater.from(activity);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new AlertDialog.Builder(activity, R.style.Theme_Holo_Dialog).create();
            }
        });

    }

    public void cancel(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.cancel();
                callback.onCancel();
            }
        });
    }

    public void show(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setView(onCreateView(activity, inflater));

                dialog.show();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.horizontalMargin=100;
                lp.verticalMargin=100;
                dialog.getWindow().setAttributes(lp);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
        });

    }

    public void dismiss(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        });
    }

    public void setCancelable(final boolean cancelable) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setCancelable(cancelable);
            }
        });
    }

    public abstract View onCreateView(Context context,LayoutInflater inflater);
}