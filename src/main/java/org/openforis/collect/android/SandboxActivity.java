package org.openforis.collect.android;

import java.io.InputStream;

import org.openforis.collect.context.CollectContext;
import org.openforis.collect.persistence.xml.CollectIdmlBindingContext;
import org.openforis.idm.metamodel.Survey;
import org.openforis.idm.metamodel.SurveyContext;
import org.openforis.idm.metamodel.xml.SurveyUnmarshaller;

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
	        
	        CollectIdmlBindingContext idmlBindingContext = new CollectIdmlBindingContext((SurveyContext) new CollectContext());
			SurveyUnmarshaller su = idmlBindingContext.createSurveyUnmarshaller();
			Survey survey = su.unmarshal(is);
			Log.e("survey","PARSED");
			Log.e("surveyID","=="+survey.getId());
			Log.e("surveyName","=="+survey.getName());
		} catch (Exception e){
			e.printStackTrace();
		}
    }	
		
}
