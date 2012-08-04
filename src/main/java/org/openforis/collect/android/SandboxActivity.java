package org.openforis.collect.android;

import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SandboxActivity extends Activity {
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		try{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);	             
	        setContentView(R.layout.maintabwindow);
	        TextView tempTextView = (TextView)findViewById(R.id.btnFooterSave);
	        
	        InputStream is = this.getClass().getClassLoader().getResourceAsStream("test.idm.xml");
	        byte[] buffer = new byte[1000];
	        is.read(buffer);
	        String napis="";
	        for (int i=0;i<1000;i++){
	        	napis+=(char)buffer[i];    	
	        }
	        tempTextView.setText(napis);
	        Log.e("napis","=="+napis);
	        /*CollectIdmlBindingContext idmlBindingContext = new CollectIdmlBindingContext((SurveyContext) new CollectContext());
			SurveyUnmarshaller su = idmlBindingContext.createSurveyUnmarshaller();
			Survey survey = su.unmarshal(is);
			Log.e("survey","PARSED");
			Log.e("surveyID","=="+survey.getId());
			Log.e("surveyName","=="+survey.getName());*/
		} catch (Exception e){
			Log.e("EXCEPTION"+e.toString(),"=="+e.getStackTrace().length);
			for (int i = 0; i<e.getStackTrace().length;i++)
				Log.e(""+i,"=="+e.getStackTrace()[i].toString());
		}
    }	
		
}
