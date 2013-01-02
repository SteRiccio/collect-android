package org.openforis.collect.android.data;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class DataTreeNode {

	protected List<FieldValue> data;
    protected DataTreeNode parent;
    protected List<DataTreeNode> children;
    
    protected int idNo;
    protected int instanceNo;
    
    private String path;
    
    public DataTreeNode(int id, int instance, String parentPath, DataTreeNode parentNode, List<FieldValue> valuesList){
    	this.idNo = id;
    	this.instanceNo = instance;
    	this.data = valuesList;
    	if (!parentPath.equals("")){
    		this.path = parentPath + ";" + this.idNo + "," + this.instanceNo;
    	} else {
    		this.path = this.idNo + "," + this.instanceNo;
    	}
    	this.parent = parentNode;
    	this.children = new ArrayList<DataTreeNode>();
    }
    
	public void addChildNode(DataTreeNode dataNode, boolean override){
		boolean exists = nodeExists(dataNode);
		if (exists&&override){
			updateNode(dataNode);
		}
		else if (!exists){
			//Log.e("new node","ADDED TO TREE");
			this.children.add(dataNode);	
		}		
	}
	
	public void updateNode(DataTreeNode dataNode){		
		this.children.set(getNodeLocation(dataNode), dataNode);
	}
	
	public boolean nodeExists(DataTreeNode dataNode){
		for (DataTreeNode childNode : this.children){
			if (childNode.idNo==dataNode.idNo
					&&
				childNode.instanceNo==dataNode.instanceNo){
				//Log.e("nodeExists","=="+true);
				return true;
			}			
		}
		//Log.e("nodeExists","=="+false);
		return false;
	}
	
	public int getNodeLocation(DataTreeNode dataNode){
		int counter = -1;
		for (DataTreeNode childNode : this.children){
			counter++;
			if (childNode.idNo==dataNode.idNo
					&&
				childNode.instanceNo==dataNode.instanceNo){
				return counter;
			}			
		}
		return counter;
	}
	
	public DataTreeNode getChildNode(int idNo, int instanceNo){
		for (DataTreeNode childNode : this.children){
			if (childNode.idNo==idNo
					&&
				childNode.instanceNo==instanceNo){
				return childNode;
			}			
		}
		return null;
	}
	
	public void addFieldValue(FieldValue fieldValue){
		if (fieldValueExists(fieldValue)){
			updateFieldValue(fieldValue);
		} else {
			this.data.add(fieldValue);
		}
	}

	public FieldValue getFieldValue(int fieldId){
		FieldValue fieldValue = null;
		for (FieldValue value : this.data){
			if (fieldId==value.getId()){
				fieldValue = value;
				break;
			}
		}	
		return fieldValue;
	}
	
	private void updateFieldValue(FieldValue fieldValue){
		int counter = 0;
		for (FieldValue value : this.data){
			if (fieldValue.getId()==value.getId()){
				this.data.set(counter, fieldValue);
				break;
			}
			counter++;
		}	
	}
	
	private boolean fieldValueExists(FieldValue fieldValue){
		boolean valueExists = false;
		//if (this.data!=null){
			for (FieldValue value : this.data){
				if (fieldValue.getId()==value.getId()){
					valueExists = true;
					break;
				}
			}	
		//}		
		return valueExists;
	}
	
	public int getFieldsNo(){
		return this.data.size();
	}
	
	public List<FieldValue> getNodeValues(){
		return this.data;
	}
	
	public void setNodeValues(List<FieldValue> values){
		this.data = values;
	}
	
	public DataTreeNode getNodeParent(){
		return this.parent;
	}
	
	public String getNodePath(){
		return this.path;
	}
}
