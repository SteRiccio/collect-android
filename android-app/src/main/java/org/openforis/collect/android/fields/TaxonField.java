package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.Map;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.misc.SearchTaxonActivity;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.TaxonAttribute;
import org.openforis.idm.model.TaxonOccurrence;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.method.QwertyKeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaxonField extends InputField {
	
	private TextView codeLabel;
	private TextView sciNameLabel;
	private TextView venacNamesLabel;
	private TextView venacLangLabel;
	private TextView langVariantLabel;
	
	private EditText txtCodes; //Code
	private EditText txtSciName; //Scientific name
	private EditText txtVernacularName; //Vernacular names
	private EditText txtVernacularLang; //List of vernacular languages
	private EditText txtLangVariant; //Language variant
	
	private Button btnSearchByCode;
	private Button btnSearchBySciName;
	private Button btnSearchByVernName;
	
	boolean searchable;
	private static FormScreen form;
	
	public TaxonField(Context context, NodeDefinition nodeDef, 
			ArrayList<String> codes, ArrayList<String> options, 
			String selectedItem) {
		super(context, nodeDef);

		TaxonField.form = (FormScreen)context;
		
		//Create input field "Code"
		//Label "Code"
		//Create label "Scientific name"
		this.codeLabel = new TextView(context);
		this.codeLabel.setText("Code");
		this.codeLabel.setTextColor(Color.BLACK);

		//this.codeLabel.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		//Add text field where user can type a code
		this.txtCodes = new EditText(context);
		this.txtCodes.setText(""/*initialText[0]*/);
		this.txtCodes.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));

		// When txtCode gets focus
		this.txtCodes.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
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
		//Button "Search By Code"
		this.btnSearchByCode = new Button(context);
		this.btnSearchByCode.setText("Search");
		this.btnSearchByCode.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 4));
		this.btnSearchByCode.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//Get category and value
				String strValue = TaxonField.this.txtCodes.getText().toString();				
				//Add current value to FormScreen.currentFieldValue
		    	ArrayList<String> tempValue = new ArrayList<String>();
				tempValue.add(TaxonField.this.txtCodes.getText().toString());
				tempValue.add(TaxonField.this.txtSciName.getText().toString());
				tempValue.add(TaxonField.this.txtVernacularName.getText().toString());
				tempValue.add(TaxonField.this.txtVernacularLang.getText().toString());
				tempValue.add(TaxonField.this.txtLangVariant.getText().toString());
				//Check the value is not empty  and Run SearchTaxon activity
				//TODO: in future validator should check it
				if (!strValue.isEmpty())
					TaxonField.this.startSearchScreen(strValue, "Code");
				else
					Log.i(getResources().getString(R.string.app_name), "Value of Code field is EMPTY!!!! ");
			}
		});		
		
		//Create layout and add input field "Code" into there
		LinearLayout codeLL = new LinearLayout(context);		
		codeLL.setOrientation(HORIZONTAL);
		//codeLL.addView(this.codeLabel);
		codeLL.addView(this.txtCodes);
		codeLL.addView(this.btnSearchByCode);
		this.addView(this.codeLabel);
		this.addView(codeLL);
		
		//Create input field "Scientific name"
		//Create label "Scientific name"
		this.sciNameLabel = new TextView(context);
		this.sciNameLabel.setMaxLines(1);
		this.sciNameLabel.setTextColor(Color.BLACK);
		this.sciNameLabel.setText("Scientific names");
		//this.sciNameLabel.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		//Add text box for scientific names
		this.txtSciName = new EditText(context);
		this.txtSciName.setText(""/*initialText[1]*/);	
		this.txtSciName.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));

		// When txtSciName gets focus
		this.txtSciName.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
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
		//Button "Search By Scientific names"
		this.btnSearchBySciName = new Button(context);
		this.btnSearchBySciName.setText("Search");
		this.btnSearchBySciName.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 4));
		this.btnSearchBySciName.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//Get category and value
				String strValue = TaxonField.this.txtSciName.getText().toString();				
				//Add current value to FormScreen.currentFieldValue
		    	ArrayList<String> tempValue = new ArrayList<String>();
				tempValue.add(TaxonField.this.txtCodes.getText().toString());
				tempValue.add(TaxonField.this.txtSciName.getText().toString());
				tempValue.add(TaxonField.this.txtVernacularName.getText().toString());
				tempValue.add(TaxonField.this.txtVernacularLang.getText().toString());
				tempValue.add(TaxonField.this.txtLangVariant.getText().toString());
				//Check the value is not empty  and Run SearchTaxon activity
				//TODO: in future validator should check it
				if (!strValue.isEmpty())
					TaxonField.this.startSearchScreen(strValue, "SciName");
				else
					Log.i(getResources().getString(R.string.app_name), "Value of SciName field is EMPTY!!!! ");
			}
		});		
		
		//Create layout and add input field "Scientific name" into there
		LinearLayout sciNameLL = new LinearLayout(context);		
		sciNameLL.setOrientation(HORIZONTAL);
		//sciNameLL.addView(this.sciNameLabel);
		sciNameLL.addView(this.txtSciName);
		sciNameLL.addView(this.btnSearchBySciName);
		this.addView(this.sciNameLabel);
		this.addView(sciNameLL);
		
		//Create input field "Vernacular name"
		//Create label "Vernacular name"
		this.venacNamesLabel = new TextView(context);
		this.venacNamesLabel.setMaxLines(1);
		this.venacNamesLabel.setTextColor(Color.BLACK);
		this.venacNamesLabel.setText("Vernacular name");
		//this.venacNamesLabel.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		//Add textbox for vernacular names
		this.txtVernacularName = new EditText(context);

		this.txtVernacularName.setText(""/*initialText[2]*/);		
		this.txtVernacularName.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));

		// When txtVernacularName gets focus
		this.txtVernacularName.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
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
		//Button "Search By Vernacular names"
		this.btnSearchByVernName = new Button(context);
		this.btnSearchByVernName.setText("Search");
		this.btnSearchByVernName.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 4));
		this.btnSearchByVernName.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				//Get category and value
				String strValue = TaxonField.this.txtVernacularName.getText().toString();				
				//Add current value to FormScreen.currentFieldValue
		    	ArrayList<String> tempValue = new ArrayList<String>();
				tempValue.add(TaxonField.this.txtCodes.getText().toString());
				tempValue.add(TaxonField.this.txtSciName.getText().toString());
				tempValue.add(TaxonField.this.txtVernacularName.getText().toString());
				tempValue.add(TaxonField.this.txtVernacularLang.getText().toString());
				tempValue.add(TaxonField.this.txtLangVariant.getText().toString());
				//Check the value is not empty  and Run SearchTaxon activity
				//TODO: in future validator should check it
				if (!strValue.isEmpty())
					TaxonField.this.startSearchScreen(strValue, "VernacularName");
				else
					Log.i(getResources().getString(R.string.app_name), "Value of VernName field is EMPTY!!!! ");
			}
		});			
		//Create layout and add input field "Vernacular name" into there
		LinearLayout vernNameLL = new LinearLayout(context);		
		vernNameLL.setOrientation(HORIZONTAL);
		//vernNameLL.addView(this.venacNamesLabel);
		vernNameLL.addView(this.txtVernacularName);
		vernNameLL.addView(this.btnSearchByVernName);
		this.addView(this.venacNamesLabel);
		this.addView(vernNameLL);
		
		//Create input field "Vernacular language"
		//Create label "Vernacular language"
		this.venacLangLabel = new TextView(context);
		this.venacLangLabel.setMaxLines(1);
		this.venacLangLabel.setTextColor(Color.BLACK);
		this.venacLangLabel.setText("Vernacular language");			
		//this.venacLangLabel.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		//Add text box for vernacular languages
		this.txtVernacularLang = new EditText(context);
		this.txtVernacularLang.setText(""/*initialText[3]*/);
		//this.txtVernacularLang.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		// When txtLangVariant gets focus
		this.txtVernacularLang.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
		    		if(this.getClass().toString().contains("TaxonField")){
				    	Map<String, ?> settings = ApplicationManager.appPreferences.getAll();
				    	Boolean valueForText = (Boolean)settings.get(getResources().getString(R.string.showSoftKeyboardOnTextField));
				    	// Switch on or off Software keyboard depend of settings
				    	if(valueForText){
				    		TaxonField.this.txtVernacularLang.setKeyListener(new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false));
				        }
				    	else {
				    		txtVernacularLang.setInputType(InputType.TYPE_NULL);
				    	}
			    	}
		    	}
		    }
	    });		
		//Create layout and add input field "Vernacular language" into there
//		LinearLayout vernLangLL = new LinearLayout(context);		
//		vernLangLL.setOrientation(HORIZONTAL);
//		vernLangLL.addView(this.venacLangLabel);
//		vernLangLL.addView(this.txtVernacularLang);		
		this.addView(this.venacLangLabel);
		this.addView(this.txtVernacularLang);
		
		//Create input field "Language variant"
		//Create label "Language variant"
		this.langVariantLabel = new TextView(context);
		this.langVariantLabel.setMaxLines(1);
		this.langVariantLabel.setTextColor(Color.BLACK);
		this.langVariantLabel.setText("Language variant");	
		//this.langVariantLabel.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		//Add textbox for language variants
		this.txtLangVariant = new EditText(context);
		this.txtLangVariant.setText(""/*initialText[4]*/);	
		//this.txtLangVariant.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 3));
		// When txtLangVariant gets focus
		this.txtLangVariant.setOnFocusChangeListener(new OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		    	// Get current settings about software keyboard for text fields
		    	if(hasFocus){
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
		
		//Create layout and add input field "Language variant" into there
//		LinearLayout langVariantLL = new LinearLayout(context);
//		langVariantLL.addView(this.langVariantLabel);
//		langVariantLL.addView(this.txtLangVariant);		
		this.addView(this.langVariantLabel);
		this.addView(this.txtLangVariant);
		
		//Check "is Searchable" argument
		if (this.searchable){
			this.label.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}});
		}
		this.searchable = true;			
		
		//Add child layouts to container
		this.setOrientation(VERTICAL);
		//this.addView(codeLL);
//		this.addView(sciNameLL);
//		this.addView(vernNameLL);
//		this.addView(vernLangLL);
//		this.addView(langVariantLL);
	}
	
	public String getHint()
	{
//		return this.txtBox.getHint().toString();
		return "";
	}
	
	public void setHint(String value)
	{
//		this.txtBox.setHint(value);
	}
	
	public void setValue(int position, String code, String sciName, String vernName, String vernLang, String langVariant, String path, boolean isTextChanged){
		//Set text to textboxes
		if (!isTextChanged){
			this.txtCodes.setText(code);
			this.txtSciName.setText(sciName);
			this.txtVernacularName.setText(vernName);
			this.txtVernacularLang.setText(vernLang);
			this.txtLangVariant.setText(langVariant);
		}
		
		Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		if (node!=null){
			TaxonAttribute taxontAtr = (TaxonAttribute)node;
			taxontAtr.setValue(new TaxonOccurrence(code, sciName, vernName, null, langVariant));
		} else {
			EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new TaxonOccurrence(code, sciName, vernName, null, langVariant), position);	
		}
	}

	private void startSearchScreen(String strContent, String strCriteria){
		int taxonId = TaxonField.this.elemId;
		Intent searchTaxonIntent = new Intent(TaxonField.this.getContext(), SearchTaxonActivity.class);
		searchTaxonIntent.putExtra("content", strContent);
		searchTaxonIntent.putExtra("criteria", strCriteria);
		searchTaxonIntent.putExtra("taxonId", taxonId);
		searchTaxonIntent.putExtra("path", TaxonField.form.getFormScreenId());
		searchTaxonIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	super.getContext().startActivity(searchTaxonIntent);	
	}
	
	public void setFieldsLabelsTextColor(int color){
		this.codeLabel.setTextColor(color);
		this.sciNameLabel.setTextColor(color);
		this.venacNamesLabel.setTextColor(color);
		this.venacLangLabel.setTextColor(color);
		this.langVariantLabel.setTextColor(color);
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		this.setValue(0, TaxonField.this.txtCodes.getText().toString(), TaxonField.this.txtSciName.getText().toString(), TaxonField.this.txtVernacularName.getText().toString(), TaxonField.this.txtVernacularLang.getText().toString(), TaxonField.this.txtLangVariant.getText().toString(), TaxonField.form.getFormScreenId(), true);
	}
}
