package org.openforis.collect.android.dialogs;

import java.util.Calendar;

import org.openforis.collect.android.R;
import org.openforis.collect.android.fields.TimeField;
import org.openforis.collect.android.management.ApplicationManager;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TimePicker;

public class TimeSetDialog extends FragmentActivity {
	public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		
		public TimeField activity_edittext;
		public TimePickerDialog timePicker;
		
		public TimePickerFragment(TimeField time_field){
		    activity_edittext = time_field;
//		    Log.i(getResources().getString(R.string.app_name), "Id of activity_edittext in constructor is: " + activity_edittext.getElementId());
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			
			// Create a new instance of TimePickerDialog and return it
			this.timePicker = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
			
			//Add listener for button "Cancel"
			this.timePicker.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
			       if (which == DialogInterface.BUTTON_NEGATIVE) {
			          //Finish activity
			    	  finish();
			       }
			    }
			});	
			
			this.timePicker.setOnKeyListener(new OnKeyListener(){
				@Override
				public boolean onKey( DialogInterface dialog , int keyCode , KeyEvent event ){
					// disable search button action
					if (keyCode == KeyEvent.KEYCODE_BACK){
						Log.i(getResources().getString(R.string.app_name), "Button BACK pressed form TimeSettingsDialog");
					    //Finish activity
					    finish();						
						return true;
					}
					return false;
				}
			});
			
			return this.timePicker;
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		    //Set time to the clicked TimeField
			String strTime = pad(hourOfDay) + ":" + pad(minute);		
			//activity_edittext.txtBox.setText(strTime);			
			Log.i(getResources().getString(R.string.app_name), "Id of activity_edittext in time settings is: " + activity_edittext.getElementId());
			activity_edittext.setValue(0, strTime);
			//Finish activity
		    finish();
		}
		
		//Transform values bellow 10 as 01, 02 etc
		private String pad(int c) {
		    if (c >= 10)
		        return String.valueOf(c);
		    else
		        return "0" + String.valueOf(c);
		}
		
//		public boolean onKeyDown(int keyCode, KeyEvent event) {
//			Log.i(getResources().getString(R.string.app_name), "Some key pressed from TimeSettingsDialog");
//			if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//		    	Log.i(getResources().getString(R.string.app_name), "Button BACK pressed from TimeSettingsDialog");
//			    //Finish activity
//			    finish();	    	
//		    }
//		    return true;
//		}		
	}
	
	//Main class
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getWindow().requestFeature(Window.FEATURE_OPTIONS_PANEL);
	    Bundle extras = getIntent().getExtras(); 
	    if (extras != null) {
	    	  int widget_id = extras.getInt("timefield_id");
	    	  View v = ApplicationManager.getUIElement(widget_id);
	    	  if (v != null){
	    		  showTimePickerDialog((TimeField)v);
	    	  }
	    }    
	    
	}	
	
	public void showTimePickerDialog(TimeField timeField) {
		Log.i(getResources().getString(R.string.app_name), "Id of TimeField is is: " + timeField.getId());
	    DialogFragment newFragment = new TimePickerFragment(timeField);
	    newFragment.setCancelable(false);
	    newFragment.show(getSupportFragmentManager(), "timePicker");
	}	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	Log.i(getResources().getString(R.string.app_name), "Button BACK pressed form activity");
		    //Finish activity
		    finish();	    	
	    }
	    return super.onKeyDown(keyCode, event);
	}	
}
