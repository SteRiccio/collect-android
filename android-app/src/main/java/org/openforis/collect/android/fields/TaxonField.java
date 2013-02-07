package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openforis.collect.android.R;
import org.openforis.collect.android.data.FieldValue;
import org.openforis.collect.android.dialogs.SearchTaxonActivity;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.idm.metamodel.NodeDefinition;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.text.method.QwertyKeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class TaxonField extends Field {
	
//	public EditText txtBox;
	private ArrayAdapter<String> aa;
	
//	private SpeciesManager taxonManager;
	
	private TextView sciNameLabel;
	private TextView venacNamesLabel;
	private TextView venacLangLabel;
	private TextView langVariantLabel;
	
	private EditText txtCodes; //Code
	private EditText txtSciName; //Scientific name
	private EditText txtVernacularName; //Vernacular names
	private Spinner comboVernacularLang; //List of vernacular languages
	private EditText txtLangVariant; //Language variant
	private Button btnSearch;
	
	private ArrayList<String> venacularLangs;
	boolean searchable;
	private static FormScreen form;
	private List<ArrayList<String>> values;
	private String strCategory;
	
	public TaxonField(Context context, NodeDefinition nodeDef, 
			ArrayList<String> codes, ArrayList<String> options, 
			String selectedItem, FieldValue fieldValue) {
		super(context, nodeDef);

		this.strCategory = "";
		//this.setHint(hintText);
		TaxonField.form = (FormScreen)context;
		
		Log.i(getResources().getString(R.string.app_name), "Size of field value is: " + fieldValue.size());
		Log.i(getResources().getString(R.string.app_name), "Field value is: " + fieldValue.getValues().toString());
		
		this.values = new ArrayList<ArrayList<String>>();
//		TaxonField.this.values.add(TaxonField.this.currentInstanceNo, "");
		ArrayList<String> initialValue = new ArrayList<String>();
		/*initialValue.add(initialText[0]);
		initialValue.add(initialText[1]);
		initialValue.add(initialText[2]);
		initialValue.add(initialText[3]);
		initialValue.add(initialText[4]);*/
		initialValue.add("");
		initialValue.add("");
		initialValue.add("");
		initialValue.add("");
		initialValue.add("");
		TaxonField.this.values.add(initialValue);
		
		this.label.setText("Code");
//		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
//		this.label.setOnLongClickListener(new OnLongClickListener() {
//	        @Override
//	        public boolean onLongClick(View v) {
//	        	ToastMessage.displayToastMessage(TaxonField.this.getContext(), TaxonField.this.getLabelText(), Toast.LENGTH_LONG);
//	            return true;
//	        }
//	    });

		//Set up SpeciesManager
//		this.taxonManager = new SpeciesManager();
//		this.taxonManager.setTaxonomyDao(new TaxonomyDao());
//		this.taxonManager.setTaxonDao(new TaxonDao());
//		this.taxonManager.setTaxonVernacularNameDao(new TaxonVernacularNameDao());
		
		//Create labels for textboxes
		this.sciNameLabel = new TextView(context);
		this.sciNameLabel.setMaxLines(1);
		this.sciNameLabel.setTextColor(Color.BLACK);
		this.sciNameLabel.setText("Scientific names");
		
		this.venacNamesLabel = new TextView(context);
		this.venacNamesLabel.setMaxLines(1);
		this.venacNamesLabel.setTextColor(Color.BLACK);
		this.venacNamesLabel.setText("Vernacular names");
		
		this.venacLangLabel = new TextView(context);
		this.venacLangLabel.setMaxLines(1);
		this.venacLangLabel.setTextColor(Color.BLACK);
		this.venacLangLabel.setText("Vernacular language");	
		
		this.langVariantLabel = new TextView(context);
		this.langVariantLabel.setMaxLines(1);
		this.langVariantLabel.setTextColor(Color.BLACK);
		this.langVariantLabel.setText("Language variant");		
		
		if (this.searchable){
			this.label.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}});
		}

		this.searchable = true;//isSearchable;
		
		//Add text field where user can type a code
		this.txtCodes = new EditText(context);
		this.txtCodes.setText(""/*initialText[0]*/);

//		this.txtCodes.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		
		// When txtCode gets focus
		this.txtCodes.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
		    		TaxonField.this.strCategory = "Code";
//		    		FormScreen.currentFieldValue = TaxonField.this.value;
			    	if(this.getClass().toString().contains("TaxonField")){
				    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	Boolean valueForText = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnTextField));
				    	// Switch on or off Software keyboard depend of settings
				    	if(valueForText){
				    		//Log.i(getResources().getString(R.string.app_name), "Setting taxon field is: " + valueForText);
				    		TaxonField.this.txtCodes.setKeyListener(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
				        }
				    	else {
				    		//Log.i(getResources().getString(R.string.app_name), "Setting taxon field is: " + valueForText);
				    		txtCodes.setInputType(InputType.TYPE_NULL);
				    	}
			    	}
		    	}
		    }
	    });			
			
		//Add text box for scientific names
		this.txtSciName = new EditText(context);
		this.txtSciName.setText(""/*initialText[1]*/);

//		this.txtSciName.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		
		// When txtSciName gets focus
		this.txtSciName.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
//		    		FormScreen.currentFieldValue = TaxonField.this.value;
		    		TaxonField.this.strCategory = "SciName";
		    		if(this.getClass().toString().contains("TaxonField")){
				    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	Boolean valueForText = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnTextField));
				    	// Switch on or off Software keyboard depend of settings
				    	if(valueForText){
				    		//Log.i(getResources().getString(R.string.app_name), "Setting taxon field is: " + valueForText);
				    		TaxonField.this.txtSciName.setKeyListener(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
				        }
				    	else {
				    		//Log.i(getResources().getString(R.string.app_name), "Setting taxon field is: " + valueForText);
				    		txtSciName.setInputType(InputType.TYPE_NULL);
				    	}
			    	}
		    	}
		    }
	    });	
			
		//Add textbox for vernacular names
		this.txtVernacularName = new EditText(context);
		this.txtVernacularName.setText(""/*initialText[2]*/);
//		this.txtVernacularName.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		
		// When txtVernacularName gets focus
		this.txtVernacularName.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
//		    		FormScreen.currentFieldValue = TaxonField.this.value;
		    		TaxonField.this.strCategory = "VernacularName";
		    		if(this.getClass().toString().contains("TaxonField")){
				    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	Boolean valueForText = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnTextField));
				    	// Switch on or off Software keyboard depend of settings
				    	if(valueForText){
				    		//Log.i(getResources().getString(R.string.app_name), "Setting taxon field is: " + valueForText);
				    		TaxonField.this.txtVernacularName.setKeyListener(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
				        }
				    	else {
				    		//Log.i(getResources().getString(R.string.app_name), "Setting taxon field is: " + valueForText);
				    		txtVernacularName.setInputType(InputType.TYPE_NULL);
				    	}
			    	}
		    	}
		    }
	    });	
		
		//Add list of vernacular languages
		this.comboVernacularLang = new Spinner(context);
		this.comboVernacularLang.setPrompt("Venacular languages");
		this.venacularLangs = new ArrayList<String>();
		this.venacularLangs.add("");
//		this.venacularLangs.add(initialText[3]);
		this.aa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, this.venacularLangs);
		this.aa.setDropDownViewResource(R.layout.codelistitem);
		this.comboVernacularLang.setAdapter(aa);
//		this.comboVernacularLang.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));		
		this.comboVernacularLang.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	
		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    	
		    }
		});
		
		//Add textbox for language variants
		this.txtLangVariant = new EditText(context);
		this.txtLangVariant.setText(""/*initialText[4]*/);
//		this.txtLangVariant.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 2));
		
		// When txtLangVariant gets focus
		this.txtLangVariant.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
//		    		FormScreen.currentFieldValue = TaxonField.this.value;
		    		TaxonField.this.strCategory = "LangVariant";
		    		if(this.getClass().toString().contains("TaxonField")){
				    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	Boolean valueForText = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnTextField));
				    	// Switch on or off Software keyboard depend of settings
				    	if(valueForText){
				    		//Log.i(getResources().getString(R.string.app_name), "Setting taxon field is: " + valueForText);
				    		TaxonField.this.txtLangVariant.setKeyListener(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
				        }
				    	else {
				    		//Log.i(getResources().getString(R.string.app_name), "Setting taxon field is: " + valueForText);
				    		txtLangVariant.setInputType(InputType.TYPE_NULL);
				    	}
			    	}
		    	}
		    }
	    });		
		
		//Add button "Search"
		this.btnSearch = new Button(context);
		this.btnSearch.setText("Search");
		this.btnSearch.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//Get category and value
				String strValue = "";
				if (TaxonField.this.strCategory.equalsIgnoreCase("Code")){
					strValue = TaxonField.this.txtCodes.getText().toString();
				}
				else if (TaxonField.this.strCategory.equals("SciName")){
					strValue = TaxonField.this.txtSciName.getText().toString();
				}
				else if (TaxonField.this.strCategory.equals("VernacularName")){
					strValue = TaxonField.this.txtVernacularName.getText().toString();
				}	
				else if (TaxonField.this.strCategory.equals("LangVariant")){
					strValue = TaxonField.this.txtLangVariant.getText().toString();
				}
				
				//Add current value to FormScreen.currentFieldValue
		    	ArrayList<String> tempValue = new ArrayList<String>();
				tempValue.add(TaxonField.this.txtCodes.getText().toString());
				tempValue.add(TaxonField.this.txtSciName.getText().toString());
				tempValue.add(TaxonField.this.txtVernacularName.getText().toString());
				tempValue.add("");//value from combobox
				tempValue.add(TaxonField.this.txtLangVariant.getText().toString());				
				TaxonField.this.value.setValue(TaxonField.form.currInstanceNo, tempValue);
				FormScreen.currentFieldValue = TaxonField.this.value;
				FormScreen.currentFieldValue.setValue(TaxonField.form.currInstanceNo, tempValue);
				if (TaxonField.form.currentNode!=null){
					TaxonField.form.currentNode.addFieldValue(FormScreen.currentFieldValue);
				}
				
				//Check the value is not empty  and Run SearchTaxon activity
				//TODO: in future validator should check it
				if (!strValue.isEmpty())
					TaxonField.this.startSearchScreen(strValue, TaxonField.this.strCategory);
				else
					Log.i(getResources().getString(R.string.app_name), "Value is EMPTY!!!! ");
			}
		});
		
		//Add chosen values 
//		this.values = new ArrayList<ArrayList<String>>();
//		ArrayList<String> initialValue = new ArrayList<String>();
//		initialValue.add(String.valueOf(this.spinner.getSelectedItemPosition()));
//		initialValue.add(initialText);
//		this.values.add(currentInstanceNo, initialValue);
				
		//Add child views to container
		this.setOrientation(VERTICAL);
		this.addView(this.label);
		this.addView(this.txtCodes);
		this.addView(this.sciNameLabel);
		this.addView(this.txtSciName);
		this.addView(this.venacNamesLabel);
		this.addView(this.txtVernacularName);
		this.addView(this.venacLangLabel);
		this.addView(this.comboVernacularLang);
		this.addView(this.langVariantLabel);
		this.addView(this.txtLangVariant);
		this.addView(this.btnSearch);
		//this.addView(this.scrollLeft);
		//this.addView(this.scrollRight);
		
		this.value = fieldValue;
	}
	

	/*@Override
	public void scrollLeft(){
    	if (TaxonField.this.currentInstanceNo>0){
    		ArrayList<String> tempValue = new ArrayList<String>();
    		tempValue.add(String.valueOf(TaxonField.this.spinner.getSelectedItemPosition()));
    		tempValue.add(TaxonField.this.txtBox.getText().toString());
    		TaxonField.this.values.set(TaxonField.this.currentInstanceNo, tempValue);
    		TaxonField.this.txtBox.setText(TaxonField.this.values.get(TaxonField.this.currentInstanceNo-1).get(1).toString());
			TaxonField.this.spinner.setSelection(Integer.valueOf(TaxonField.this.values.get(TaxonField.this.currentInstanceNo-2).get(0)));
    		TaxonField.this.currentInstanceNo--;
    	}
	}
	
	@Override
	public void scrollRight(){
    	if (TaxonField.this.values.size()==TaxonField.this.currentInstanceNo){
    		ArrayList<String> tempValue = new ArrayList<String>();
    		tempValue.add("0");
    		tempValue.add("");
    		this.values.add(tempValue);
    		TaxonField.this.values.add(TaxonField.this.currentInstanceNo, tempValue);	        		
    	}
		ArrayList<String> tempValue = new ArrayList<String>();
		tempValue.add(String.valueOf(TaxonField.this.spinner.getSelectedItemPosition()));
		tempValue.add(TaxonField.this.txtBox.getText().toString());
    	TaxonField.this.values.set(TaxonField.this.currentInstanceNo, tempValue);        			        		
		if (TaxonField.this.values.size()>TaxonField.this.currentInstanceNo){
			TaxonField.this.txtBox.setText(TaxonField.this.values.get(TaxonField.this.currentInstanceNo).get(1).toString());
			TaxonField.this.spinner.setSelection(Integer.valueOf(TaxonField.this.values.get(TaxonField.this.currentInstanceNo).get(0)));
		}			
		TaxonField.this.currentInstanceNo++;
	}*/
	
	public String getHint()
	{
//		return this.txtBox.getHint().toString();
		return "";
	}
	
	public void setHint(String value)
	{
//		this.txtBox.setHint(value);
	}
	
	@Override
	public int getInstancesNo(){
		return this.values.size();
	}
	
	/* 
	 * getValue and saving field value after state changed i.e. user selected from menu or typed sth
	 */
	public List<String> getValue(int index){
		return TaxonField.this.value.getValue(index);
	}

	public void setValue(int position, String code, String sciName, String vernName, String vernLang, String langVariant){
		//Set text to textboxes
		this.txtCodes.setText(code);
		this.txtSciName.setText(sciName);
		this.txtVernacularName.setText(vernName);
		this.txtLangVariant.setText(langVariant);
		Log.i(getResources().getString(R.string.app_name), "Value was set!!! " + code + " " + sciName);
		
		ArrayList<String> valueToAdd = new ArrayList<String>();
		valueToAdd.add(code);
		valueToAdd.add(sciName);
		valueToAdd.add(vernName);
		valueToAdd.add(vernLang);
		valueToAdd.add(langVariant);
		TaxonField.this.value.setValue(position, valueToAdd);
	}
	
	public void resetValues(){
		this.values = new ArrayList<ArrayList<String>>();

	}
	
	public void addValue(ArrayList<String> value){
		this.values.add(value);
		this.currentInstanceNo++;
	}	
	
	private void startSearchScreen(CharSequence strContent, String strCriteria){
		int taxonId = TaxonField.this.elemId;
		Intent searchTaxonIntent = new Intent(TaxonField.this.getContext(), SearchTaxonActivity.class);
		searchTaxonIntent.putExtra("content", strContent);
		searchTaxonIntent.putExtra("criteria", strCriteria);
		searchTaxonIntent.putExtra("taxonId", taxonId);
		searchTaxonIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	super.getContext().startActivity(searchTaxonIntent);			
		
	}
}
