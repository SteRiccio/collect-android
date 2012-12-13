package org.openforis.collect.android.dialogs;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.openforis.collect.android.R;
import org.openforis.collect.android.fields.DateField;
import org.openforis.collect.android.management.ApplicationManager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;

public class DateSetDialog extends FragmentActivity {
	
	//Subclass which creates DatePicker
	public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		public DateField activity_edittext;
		public DatePickerDialog datePicker;
		
		public DatePickerFragment(DateField date_field) {
		    activity_edittext = date_field;
		}	    
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			// Create a new instance of DatePickerDialog and return it
			this.datePicker = new DatePickerDialog(getActivity(), this, year, month, day);
			
			//Add listener for button "Cancel"
			this.datePicker.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
			       if (which == DialogInterface.BUTTON_NEGATIVE) {
			          //Finish activity
			    	   finish();
			       }
			    }
			});			
			return this.datePicker;
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			//Set date to the clicked DateField 
			Calendar cal = new GregorianCalendar(year, month, day);
			String strDate = DateFormat.getDateFormat(getActivity()).format(cal.getTime());
			Log.i(getResources().getString(R.string.app_name), "Date in DateSetDialog is: " + strDate);
			activity_edittext.txtBox.setText(strDate);	
		    //Finish activity
		    finish();
		}
		
	}
	
	//Main class
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getWindow().requestFeature(Window.FEATURE_OPTIONS_PANEL);
	    Bundle extras = getIntent().getExtras(); 
	    if (extras != null) {
	    	  int widget_id = extras.getInt("datefield_id");
	    	  Log.i(getResources().getString(R.string.app_name), "Id of target field in DateSetDialog is: " + widget_id);
	    	  View v = ApplicationManager.getUIElement(widget_id);
	    	  if (v != null){
//	    		  Log.i(getResources().getString(R.string.app_name), "View for DateSetDialog is: " +v.toString());
	    		  showDatePickerDialog((DateField)v);
	    	  }
	    	  else
	    		  Log.i(getResources().getString(R.string.app_name), "View is NULL!");
	    	  
	    }    
	    
	}	
	
	public void showDatePickerDialog(DateField dateField) {
	    DialogFragment newFragment = new DatePickerFragment(dateField);
	    newFragment.show(getSupportFragmentManager(), "datePicker");
	}	
}
