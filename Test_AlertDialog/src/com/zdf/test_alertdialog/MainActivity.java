package com.zdf.test_alertdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        LayoutInflater inflate = LayoutInflater.from(this);
		View myView = inflate.inflate(R.layout.my_dialog2, null);
//		myView.getBackground().setAlpha(50);
//		myView.findViewById(R.id.fd_dialog_layout).getBackground().setAlpha(50);
		
//		AlertDialog mModeDialog = new AlertDialog.Builder(this, R.style.TANCStyle)
		AlertDialog mModeDialog = new AlertDialog.Builder(this)
//		.setTitle("Title")	
		.setView(myView)
		.create();
		
//		Dialog mModeDialog = new Dialog(this, R.style.TANCStyle);
////		Dialog mModeDialog = new Dialog(this);
//		mModeDialog.setContentView(myView);
//		
//		mModeDialog.setCanceledOnTouchOutside(true);
//		mModeDialog.setCancelable(true);
//		mModeDialog.setOnDismissListener(new OnDismissListener() {
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				
//			}
//		});
		
//		Window window = mModeDialog.getWindow();
//		WindowManager.LayoutParams lp = window.getAttributes();
//		lp.alpha = 0.3f;
//		window.setAttributes(lp);
		
		mModeDialog.show();
		
		Toast.makeText(this, "  First", Toast.LENGTH_LONG).show();
//		Toast toast = Toast.makeText(this, "  First", Toast.LENGTH_LONG);
//		toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
//		toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
