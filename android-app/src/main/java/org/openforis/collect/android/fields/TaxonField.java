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
import android.text.TextWatcher;
import android.text.method.QwertyKeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
	//private EditText txtVernacularLang; //List of vernacular languages
	private ArrayAdapter<String> aa;
	private Spinner spinner;
	ArrayList<String> options;
	ArrayList<String> codes;
	private EditText txtLangVariant; //Language variant
	
	private Button btnSearchByCode;
	private Button btnSearchBySciName;
	private Button btnSearchByVernName;
	
	boolean searchable;
	private static FormScreen form;
	
	private final String[] languageCodes = {"acm", "Mesopotamian Arabic", "afr", "Afrikaans", "ara", "Arabic", "arz", "Egyptian Arabic", "bel", "Belarusian", "ben", "Bengali", "bos", "Bosnian", "bre", "Breton", "bul", "Bulgarian", "cat", "Catalan", "ces", "Czech", "cha", "Chamorro", "cmn", "Mandarin Chinese", "dan", "Danish", "deu", "German", "ell", "Modern Greek (1453-)", "eng", "English", "epo", "Esperanto", "est", "Estonian", "eus", "Basque", "fao", "Faroese", "fin", "Finnish", "fra", "French", "fry", "Western Frisian", "gle", "Irish", "glg", "Galician", "heb", "Hebrew", "hin", "Hindi", "hrv", "Croatian", "hun", "Hungarian", "hye", "Armenian", "ina", "Interlingua (International Auxiliary Language Association)", "ind", "Indonesian", "isl", "Icelandic", "ita", "Italian", "jbo", "Lojban", "kat", "Georgian", "kaz", "Kazakh", "kor", "Korean", "lat", "Latin", "lit", "Lithuanian", "lvs", "Standard Latvian", "lzh", "Literary Chinese", "mal", "Malayalam", "mon", "Mongolian", "nan", "Min Nan Chinese", "nds", "Low German", "nld", "Dutch", "nob", "Norwegian Bokmål", "non", "Old Norse", "orv", "Old Russian", "oss", "Ossetian", "pes", "Iranian Persian", "pol", "Polish", "por", "Portuguese", "que", "Quechua", "roh", "Romansh", "ron", "Romanian", "rus", "Russian", "scn", "Sicilian", "slk", "Slovak", "slv", "Slovenian", "spa", "Spanish", "sqi", "Albanian", "srp", "Serbian", "swe", "Swedish", "swh", "Swahili (individual language)", "tat", "Tatar", "tgl", "Tagalog", "tha", "Thai", "tlh", "Klingon", "tur", "Turkish", "uig", "Uighur", "ukr", "Ukrainian", "urd", "Urdu", "uzb", "Uzbek", "vie", "Vietnamese", "vol", "Volapük", "wuu", "Wu Chinese", "yid", "Yiddish", "yue", "Yue Chinese", "zsm", "Standard Malay"};
	
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
		this.txtCodes.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				TaxonField.this.setValue(0, s.toString(), 
						TaxonField.this.txtSciName.getText().toString(), 
						TaxonField.this.txtVernacularName.getText().toString(), 
						TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(TaxonField.this.spinner.getSelectedItemPosition())-1]/*TaxonField.this.txtVernacularLang.getText().toString()*/, 
						TaxonField.this.txtLangVariant.getText().toString(),
						TaxonField.form.getFormScreenId(),true);
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
				tempValue.add(TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(TaxonField.this.spinner.getSelectedItemPosition())-1]/*TaxonField.this.txtVernacularLang.getText().toString()*/);
				tempValue.add(TaxonField.this.txtLangVariant.getText().toString());
				//Check the value is not empty  and Run SearchTaxon activity
				//TODO: in future validator should check it
				if (!strValue.isEmpty())
					TaxonField.this.startSearchScreen(strValue, "Code");
				else
					Log.i(getResources().getString(R.string.app_name), "Value of Code field is EMPTY!!!! ");
			}
		});
		this.btnSearchByCode.setVisibility(View.GONE);
		
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
		this.txtSciName.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				TaxonField.this.setValue(0, TaxonField.this.txtCodes.getText().toString(), 
						s.toString(), 
						TaxonField.this.txtVernacularName.getText().toString(), 
						TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(TaxonField.this.spinner.getSelectedItemPosition())-1]/*TaxonField.this.txtVernacularLang.getText().toString()*/, 
						TaxonField.this.txtLangVariant.getText().toString(),
						TaxonField.form.getFormScreenId(),true);
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
				tempValue.add(TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(TaxonField.this.spinner.getSelectedItemPosition())-1]/*TaxonField.this.txtVernacularLang.getText().toString()*/);
				tempValue.add(TaxonField.this.txtLangVariant.getText().toString());
				//Check the value is not empty  and Run SearchTaxon activity
				//TODO: in future validator should check it
				if (!strValue.isEmpty())
					TaxonField.this.startSearchScreen(strValue, "SciName");
				else
					Log.i(getResources().getString(R.string.app_name), "Value of SciName field is EMPTY!!!! ");
			}
		});
		this.btnSearchBySciName.setVisibility(View.GONE);
		
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
		this.txtVernacularName.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				TaxonField.this.setValue(0, TaxonField.this.txtCodes.getText().toString(), 
						TaxonField.this.txtSciName.getText().toString(), 
						s.toString(), 
						TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(TaxonField.this.spinner.getSelectedItemPosition())-1]/*TaxonField.this.txtVernacularLang.getText().toString()*/, 
						TaxonField.this.txtLangVariant.getText().toString(),
						TaxonField.form.getFormScreenId(),true);
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
				tempValue.add(TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(TaxonField.this.spinner.getSelectedItemPosition())-1]/*TaxonField.this.txtVernacularLang.getText().toString()*/);
				tempValue.add(TaxonField.this.txtLangVariant.getText().toString());
				//Check the value is not empty  and Run SearchTaxon activity
				//TODO: in future validator should check it
				if (!strValue.isEmpty())
					TaxonField.this.startSearchScreen(strValue, "VernacularName");
				else
					Log.i(getResources().getString(R.string.app_name), "Value of VernName field is EMPTY!!!! ");
			}
		});
		this.btnSearchByVernName.setVisibility(View.GONE);
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
		
		this.spinner = new Spinner(context);
		this.spinner.setPrompt(nodeDef.getName());
		
		this.codes = new ArrayList<String>();
		this.options = new ArrayList<String>();
		for (int i=0;i<Math.floor(this.languageCodes.length/2);i++){
			this.codes.add(this.languageCodes[2*i]);
			this.options.add(this.languageCodes[2*i+1]);
		}

		this.aa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, this.options);
		this.aa.setDropDownViewResource(R.layout.codelistitem);

		this.spinner.setAdapter(aa);
		this.spinner.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 3));
		this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	//ArrayList<String> valueToAdd = new ArrayList<String>();
		    	//valueToAdd.add(TaxonField.this.codes.get((TaxonField.this.spinner.getSelectedItemPosition())));
		    	//Log.e("onItemSelected",CodeField.form.getFormScreenId()+"=="+CodeField.this.currentInstanceNo+"("+CodeField.form.currInstanceNo+")");
		    	if (TaxonField.this.nodeDefinition.isMultiple()){
		    		TaxonField.this.setValue(TaxonField.form.currInstanceNo, 
		    		TaxonField.this.txtCodes.getText().toString(), 
		    		TaxonField.this.txtSciName.getText().toString(),
					TaxonField.this.txtVernacularName.getText().toString(), 
					TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(position)-1]/*TaxonField.this.txtVernacularLang.getText().toString()*/, 
					TaxonField.this.txtLangVariant.getText().toString(),
					TaxonField.form.getFormScreenId(),true);
		    	} else {
		    		TaxonField.this.setValue(0, 
				    		TaxonField.this.txtCodes.getText().toString(), 
				    		TaxonField.this.txtSciName.getText().toString(),
							TaxonField.this.txtVernacularName.getText().toString(), 
							TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(position)-1]/*TaxonField.this.txtVernacularLang.getText().toString()*/, 
							TaxonField.this.txtLangVariant.getText().toString(),
							TaxonField.form.getFormScreenId(),true);
		    	}			    	
		    	
		    	/*if (!CodeField.this.selectedForTheFirstTime){
					
		    	} else {
		    		CodeField.this.selectedForTheFirstTime = false;
		    	}*/
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    	
		    }

		});
		
		boolean isFound = false;
		int position = 0;
		if (selectedItem!=null){
			while (!isFound&&position<this.codes.size()){
				if (this.codes.get(position).equals(selectedItem)){
					isFound = true;
				}
				position++;
			}	
		}
		if (isFound)
			this.spinner.setSelection(position-1);
		else
			this.spinner.setSelection(0);

		
		//this.venacLangLabel.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		//Add text box for vernacular languages
		/*this.txtVernacularLang = new EditText(context);
		this.txtVernacularLang.setText("");
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
		this.txtVernacularLang.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				TaxonField.this.setValue(0, TaxonField.this.txtCodes.getText().toString(), 
						TaxonField.this.txtSciName.getText().toString(), 
						TaxonField.this.txtVernacularName.getText().toString(), 
						TaxonField.this.txtVernacularLang.getText().toString(), 
						TaxonField.this.txtLangVariant.getText().toString(),
						TaxonField.form.getFormScreenId(),true);
			}	
		});*/
		//Create layout and add input field "Vernacular language" into there
//		LinearLayout vernLangLL = new LinearLayout(context);		
//		vernLangLL.setOrientation(HORIZONTAL);
//		vernLangLL.addView(this.venacLangLabel);
//		vernLangLL.addView(this.txtVernacularLang);		
		this.addView(this.venacLangLabel);
		this.addView(this.spinner);
		//this.addView(this.txtVernacularLang);
		
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
		this.txtLangVariant.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {}
			public void beforeTextChanged(CharSequence s, int start,  int count, int after) {}				 
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				TaxonField.this.setValue(0, TaxonField.this.txtCodes.getText().toString(), 
						TaxonField.this.txtSciName.getText().toString(), 
						TaxonField.this.txtVernacularName.getText().toString(), 
						TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(TaxonField.this.spinner.getSelectedItemPosition())-1]/*TaxonField.this.txtVernacularLang.getText().toString()*/, 
						TaxonField.this.txtLangVariant.getText().toString(),
						TaxonField.form.getFormScreenId(),true);
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
		if (!isTextChanged){
			this.txtCodes.setText(code);
			this.txtSciName.setText(sciName);
			this.txtVernacularName.setText(vernName);
			//this.txtVernacularLang.setText(vernLang);
			this.txtLangVariant.setText(langVariant);
		}
		//Log.e("TAXON",code+"=="+sciName);
		//Log.e("TAXON"+langVariant,vernName+"=="+vernLang);
		Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		if (node!=null){
			TaxonAttribute taxonAttr= (TaxonAttribute)node;
			if (vernLang.equals("")){
				vernLang = null;
			}
			taxonAttr.setValue(new TaxonOccurrence(code, sciName, vernName, vernLang, langVariant));
			/*Log.e("reloaded",code+"code=="+taxonAttr.getValue().getCode());
			//Log.e("reloaded",sciName+"sciName=="+taxonAttr.getValue().getScientificName());
			Log.e("reloaded",vernName+"vernName=="+taxonAttr.getValue().getVernacularName());
			//Log.e("reloaded",vernLang+"vernLang=="+taxonAttr.getValue().getLanguageCode());
			//Log.e("reloaded",langVariant+"langVar=="+taxonAttr.getValue().getLanguageVariety());*/
		} else {
			if (vernLang.equals("")){
				vernLang = null;
			}
			EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new TaxonOccurrence(code, sciName, vernName, vernLang, langVariant), position);	
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
	
	/*@Override
	public void afterTextChanged(Editable s) {
		this.setValue(0, TaxonField.this.txtCodes.getText().toString(), TaxonField.this.txtSciName.getText().toString(), TaxonField.this.txtVernacularName.getText().toString(),TaxonField.this.languageCodes[getVernacularLanguageCodeIndex(TaxonField.this.spinner.getSelectedItemPosition())-1] TaxonField.this.txtVernacularLang.getText().toString(), TaxonField.this.txtLangVariant.getText().toString(), TaxonField.form.getFormScreenId(), true);
	}*/
	
	public int getVernacularLanguageCodeIndex(int selectedItemPosition){
		return 2*selectedItemPosition+1;		
	}
}
