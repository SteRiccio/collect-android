package org.openforis.collect.android.lists;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.idm.metamodel.CodeListItem;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ListSearchActivity extends ListActivity implements TextWatcher{

	private EditText txtSearchByName;
	private ArrayList<String> treeData;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> codes;
	private String listName;
	
	private List<CodeListItem> itemsList;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.listsearchactivity);
		this.txtSearchByName = (EditText)this.findViewById(R.id.txtSearchByName);
		this.txtSearchByName.addTextChangedListener(this);
		
		this.listName = this.getIntent().getStringExtra("listName");
		
		/*this.itemsList = TabManager.survey.getCodeList(this.listName).getItems();
		for (CodeListItem codeListItem : this.itemsList){
			this.codes.add(codeListItem.getCode());
			this.treeData.add(codeListItem.getLabel(null));
		}
        //this.codes = ;//this.getIntent().getStringExtra("codes").split("\\\");
        //this.treeData = ;//this.getIntent().getStringExtra("options").split("\\\");
        this.adapter = new ArrayAdapter<String>(this, R.layout.row, R.id.label, treeData);
		this.setListAdapter(this.adapter);		*/		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		/*if (!getListAdapter().getItem(position).toString().equals("No tree matches keyword entered...")){
				//saving selected value
				this.pref_db.saveItem(constants.PREFselectedItem, getListAdapter().getItem(position).toString());
				for (int i=0;i<treeData.length;i++){
					if (treeData[i].equals(getListAdapter().getItem(position).toString())){
						position = i;
						break;
					}
				}
				this.pref_db.saveItem(constants.PREFselectedItemCode, this.codes[position]);
		        finish();
		}*/
	}
    
	private void doSearch(){
		String query = this.txtSearchByName.getText().toString().trim();
		ArrayList<String> resultList = new ArrayList<String>();
	    /*for ( int i = 0; i<treeData.size(); i++){
	    	resultList[i] = "";
	    }*/
            
		if (query.equals("")){
			resultList = treeData;
		}
		else{
			query = query.toLowerCase();
			ArrayList<String> foundSpecies = new ArrayList<String>();
			for (String s : treeData) {
        		if (s.toLowerCase().contains(query.toLowerCase())){
        			foundSpecies.add(s);
        		}
        	}
	        int numberFound = foundSpecies.size();
	        if (numberFound==0){
	        	//resultList = new String[1];
	        	resultList.add("No tree matches keyword entered...");
	        }
	        else{
	        	//resultList = new String[foundSpecies.size()];
		        
		        for (int i=0; i<numberFound;i++){
		        	//resultList[i] = foundSpecies.get(i);
		        	resultList.add(foundSpecies.get(i));
		        }	
	        }	        
	        
			
		}
		this.adapter = new ArrayAdapter<String>(this, R.layout.row, R.id.label, resultList);
		this.setListAdapter(this.adapter);
	}

	
	public void afterTextChanged(Editable s) {
		doSearch();
	}


	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		
	}

	
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}
}