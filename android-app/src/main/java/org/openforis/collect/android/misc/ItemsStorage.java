package org.openforis.collect.android.misc;

import java.util.ArrayList;
import java.util.List;

import org.openforis.idm.metamodel.CodeListItem;

public class ItemsStorage {
	public Integer definitionId;
	public Integer selectedPositionInParent;
	public List<CodeListItem> items;
	
	public ItemsStorage(){
		this.definitionId = null;
		this.selectedPositionInParent = null;
		this.items = new ArrayList<CodeListItem>();
	}
	
	public void addSelectedPositionInParent(Integer selectedItemNo){
		this.selectedPositionInParent = selectedItemNo;
	}
	
	public void setItems(List<CodeListItem> items){
		this.items = items;
	}
	
	public void setDefinitionId(Integer id){
		this.definitionId = id;
	}
}