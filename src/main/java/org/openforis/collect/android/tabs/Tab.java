package org.openforis.collect.android.tabs;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.model.UITab;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class Tab extends Activity {

	private static final String TAG = "Tab";
	private String name;
	private String label;
	private List<UITab> children;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.tab);
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