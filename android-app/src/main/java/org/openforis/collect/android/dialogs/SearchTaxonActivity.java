package org.openforis.collect.android.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openforis.collect.android.R;
import org.openforis.collect.android.fields.TaxonField;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.manager.SpeciesManager;
import org.openforis.collect.persistence.TaxonDao;
import org.openforis.collect.persistence.TaxonVernacularNameDao;
import org.openforis.collect.persistence.TaxonomyDao;
import org.openforis.idm.model.TaxonOccurrence;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.QwertyKeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SearchTaxonActivity extends Activity {

	private CharSequence content;
	private String criteria;
	private int taxonFieldId;
	private SpeciesManager taxonManager;
	private String taxonomy;
	private int backgroundColor;
	//UI elements
//	private EditText txtSearch;
//	private Button btnSearch;
	private ListView lstResult;
	private TextView lblSearch;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getWindow().requestFeature(Window.FEATURE_OPTIONS_PANEL);
	    Log.i(getResources().getString(R.string.app_name), "SearchTaxon activity started");
	    Bundle extras = getIntent().getExtras(); 
	    setContentView(R.layout.searchtaxon);
		//Add UI
	    this.lblSearch = (TextView)findViewById(R.id.lblSearch);
//	    this.txtSearch = (EditText)findViewById(R.id.txtSearch);
//	    this.btnSearch = (Button)findViewById(R.id.btnSearch);
		this.lstResult = (ListView)findViewById(R.id.lstResult);
		
	    if (extras != null) {
	    	//get extras
	    	this.content = extras.getCharSequence("content");
	    	this.criteria = extras.getString("criteria");  
	    	this.taxonFieldId = extras.getInt("taxonId");
	    	//Set up species manager
			this.taxonManager = new SpeciesManager();
			this.taxonManager.setTaxonomyDao(new TaxonomyDao());
			this.taxonManager.setTaxonDao(new TaxonDao());
			this.taxonManager.setTaxonVernacularNameDao(new TaxonVernacularNameDao());
			this.taxonomy = "trees";	  
	    }
	    else{
	    	Log.i(getResources().getString(R.string.app_name), "Cannot get extras in SearchTaxon activity");
	    }
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getResources().getString(R.string.app_name), "SearchTaxon activity onResume");
        // The activity has become visible (it is now "resumed").
    	Log.i(getResources().getString(R.string.app_name), "Content is: " + this.content);
    	Log.i(getResources().getString(R.string.app_name), "Criteria is: " + this.criteria);
    	//Set background color
		this.backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);		
		changeBackgroundColor(this.backgroundColor);
		//Do search
		this.doSearch(this.content.toString(), this.taxonFieldId);
		
		
		//Add label for search field
//    	this.lblSearch.setText("Search by " + criteria);
		//Add text box for searching
//    	this.txtSearch.setText(this.content);
//		// When txtCode gets focus
//		this.txtSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
//		    @Override
//		    public void onFocusChange(View v, boolean hasFocus) {
//		    	// Get current settings about software keyboard for text fields
//		    	if(hasFocus){
//			    	if(this.getClass().toString().contains("TaxonField")){
//				    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
//				    	Boolean valueForText = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnTextField));
//				    	// Switch on or off Software keyboard depend of settings
//				    	if(valueForText){
//				    		Log.i(getResources().getString(R.string.app_name), "From ChangeFocus: Setting taxon field is: " + valueForText);
//				    		txtSearch.setKeyListener(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
//				        }
//				    	else {
//				    		Log.i(getResources().getString(R.string.app_name), "From ChangeFocus: Setting taxon field is: " + valueForText);
//				    		txtSearch.setInputType(InputType.TYPE_NULL);
//				    	}
//			    	}
//		    	}
//		    }
//		});	
		
		//When user click inside txtSearch
//		this.txtSearch.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View arg0) {
//		    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
//		    	Boolean valueForText = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnTextField));
//		    	// Switch on or off Software keyboard depend of settings
//		    	if(valueForText){
//		    		Log.i(getResources().getString(R.string.app_name), "From ClickListener: Setting taxon field is: " + valueForText);
//		    		txtSearch.setKeyListener(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
//		        }
//		    	else {
//		    		Log.i(getResources().getString(R.string.app_name), "From ClickListener: Setting taxon field is: " + valueForText);
//		    		txtSearch.setInputType(InputType.TYPE_NULL);
//		    	}
//			}			
//		});
		
		//Add button Search !!!!TODO - in future change it to some event
//		this.btnSearch.setText("Search");
//		this.btnSearch.setOnClickListener(new View.OnClickListener() {
//		    public void onClick(View v) {
//		        String strSearch = txtSearch.getText().toString();
//		        Log.i(getResources().getString(R.string.app_name), "Search string is: " + strSearch);
//		        doSearch(strSearch, taxonFieldId);
//		    }
//		});		
	
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	Log.i(getResources().getString(R.string.app_name), "Button BACK pressed form SearchTaxon activity");
		    //Finish activity  	
		    finish();	    	
	    }
	    return super.onKeyDown(keyCode, event);
	}
	    
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
		int color = (backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK;
		//Set text color
		this.lblSearch.setTextColor(color);
//		this.txtSearch.setTextColor(color);
//		this.btnSearch.setTextColor(color);
    }	
    
    private void doSearch(String strSearch, int parentTaxonFieldId){
    	//Open connection with database
    	JdbcDaoSupport jdbcDao  = new JdbcDaoSupport();
    	jdbcDao.getConnection();    	
    	List<TaxonOccurrence> lstTaxonOccurence = new ArrayList<TaxonOccurrence>();
    	if(this.taxonManager != null){
    		if(this.criteria.equalsIgnoreCase("Code")){
				Log.i(getResources().getString(R.string.app_name), "Search by Code");
				lstTaxonOccurence = this.taxonManager.findByCode(this.taxonomy, strSearch, 1000);
				this.populateResultList(lstTaxonOccurence, parentTaxonFieldId);				
			}
			else if (this.criteria.equalsIgnoreCase("SciName")){
				Log.i(getResources().getString(R.string.app_name), "Search by Scientific name");
				lstTaxonOccurence = this.taxonManager.findByScientificName(this.taxonomy, strSearch, 1000);
				this.populateResultList(lstTaxonOccurence, parentTaxonFieldId);			
			}
			else if (this.criteria.equalsIgnoreCase("VernacularName")){
				Log.i(getResources().getString(R.string.app_name), "Search by VernacularName");
				//lstTaxonOccurence = this.taxonManager.findByVernacularNameTmp(this.taxonomy, strSearch, 1000);
				//this.populateResultList(lstTaxonOccurence, parentTaxonFieldId);			
			}
			else if (this.criteria.equalsIgnoreCase("LangVariant")){
				Log.i(getResources().getString(R.string.app_name), "Search by Language Variant");
//				lstTaxonOccurence = this.taxonManager.findByScientificName(this.taxonomy, strSearch, 1000);
//				this.populateResultList(lstTaxonOccurence, parentTaxonFieldId);			
			}    		
			else{
				Log.i(getResources().getString(R.string.app_name), "???Criteria is: " + this.criteria);
			}
		}else{
			Log.i(getResources().getString(R.string.app_name), "Species Manager is NULL!");
		}   	
	    	
    	//Close connection
    	JdbcDaoSupport.close();
    }
    
    private void populateResultList(List<TaxonOccurrence> lstTaxonOccurence, final int parentTaxonFieldId){
    	String[] arrResults = new String[lstTaxonOccurence.size()];
    	int idx = 0;
		for (TaxonOccurrence taxonOcc : lstTaxonOccurence) {
			arrResults[idx] = taxonOcc.getCode() + ";\n" + taxonOcc.getScientificName() + ";\n";
			idx++;
		}   
		this.lstResult.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		//Create and set adapter for result list
		int layout = (backgroundColor!=Color.WHITE)?R.layout.localclusterrow_white:R.layout.localclusterrow_black;	
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), layout, R.id.plotlabel, arrResults);
		this.lstResult.setAdapter(adapter);
		this.lblSearch.setText("Searching results are: ");
		changeBackgroundColor(this.backgroundColor);
    	//Set item click listener 
    	this.lstResult.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				// Back to previous screen and pass chosen results there
				String strItem = lstResult.getAdapter().getItem(position).toString();
				String[] arrItemValues = strItem.replaceAll(";\n", ";").split(";");
				for(int i=0; i<arrItemValues.length;i++){
					Log.i(getResources().getString(R.string.app_name), "Value is: " + arrItemValues[i]);
				}
			
				//Set textboxes in TaxonField by given values
				TaxonField parentTaxonField = (TaxonField)ApplicationManager.getUIElement(parentTaxonFieldId);
				if(parentTaxonField != null){
//					Log.i(getResources().getString(R.string.app_name), "Parent taxon field id is: " + parentTaxonFieldId);
					parentTaxonField.setValue(0, arrItemValues[0], arrItemValues[1], "", "", "");
//					parentTaxonField.setValue(0,strItem.replaceAll(";\n", ";"));
				}
				else{
					Log.i(getResources().getString(R.string.app_name), "Parent taxon field is: NULL");
				}
			    //Finish activity
			    finish();				
			}
    	});
    }
}
