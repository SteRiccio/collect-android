package org.openforis.collect.android.tabs;

import org.openforis.collect.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class AboutTab extends Activity {

	private static final String TAG = "AboutTab";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.abouttab);
        Log.i(getResources().getString(R.string.app_name),TAG+":onCreate"); 
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
	
	@Override 
	public void onBackPressed() { 
		this.getParent().onBackPressed();
	}

}