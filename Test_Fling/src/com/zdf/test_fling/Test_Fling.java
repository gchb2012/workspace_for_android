package com.zdf.test_fling;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class Test_Fling extends Activity{
	MyLayout myLayout = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fling);
        
        myLayout = (MyLayout) findViewById(R.id.mylayout);
        myLayout.init();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_test_fling, menu);
        return true;
    }
    
}
