package org.openforis.collect.android.tabs;

import org.openforis.collect.android.R;
import org.openforis.collect.android.misc.RunnableHandler;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.EditText;
import android.widget.TextView;

public class AboutTab extends Activity implements TextWatcher{

	private static final String TAG = "AboutTab";
	
	private TextView txtApplicationName;
	
	private TextView txtProgramVersionName;
	
	private TextView txtFormVersionName;
	
	private TextView lblGpsTimeout;
	private EditText txtGpsTimeout;
	private SharedPreferences sharedPreferences;
	
	private GestureDetector gestureDetector;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.abouttab);
        try{
        	Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
        	this.txtGpsTimeout = (EditText)findViewById(R.id.txtGpsTimeout);
        	this.sharedPreferences = getPreferences(MODE_PRIVATE);
            Integer gpsWaitingTime = this.sharedPreferences.getInt(getResources().getString(R.string.gpsPreferredWaitingTime), getResources().getInteger(R.integer.gpsPreferredWaitingTime))/1000;
            this.txtGpsTimeout.setText(String.valueOf(gpsWaitingTime));
            
            this.lblGpsTimeout = (TextView)findViewById(R.id.lblGpsTimeout);
            final ViewTreeObserver observer= this.txtGpsTimeout.getViewTreeObserver();
			observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					lblGpsTimeout.setHeight(txtGpsTimeout.getHeight());
					ViewTreeObserver observer = txtGpsTimeout.getViewTreeObserver();
					observer.removeGlobalOnLayoutListener(this);
				}
			});			

            this.txtApplicationName = (TextView)findViewById(R.id.txtApplicationName);
            this.txtApplicationName.setText(getResources().getString(R.string.app_name));
            
            this.txtProgramVersionName = (TextView)findViewById(R.id.txtProgramVersionName);
            this.txtProgramVersionName.setText(this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
            
            this.txtFormVersionName = (TextView)findViewById(R.id.txtFormVersionName);
            this.txtFormVersionName.setText(TabManager.getSurvey().getProjectName(null)+" "+TabManager.getSurvey().getVersions().get(TabManager.getSurvey().getVersions().size()-1).getName());
            
        } catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onCreate",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}
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

	@Override
	public void afterTextChanged(Editable arg0) {
		try{
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt(getResources().getString(R.string.gpsPreferredWaitingTime), Integer.valueOf(arg0.toString()));
			editor.commit();
		} catch (NumberFormatException e){
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt(getResources().getString(R.string.gpsPreferredWaitingTime), getResources().getInteger(R.integer.gpsPreferredWaitingTime));
			editor.commit();
		} catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":afterTextChanged",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}

}