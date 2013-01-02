package org.openforis.collect.android.data;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;


public class FieldValue {
	
	private int fieldId;
	private String fieldPath;
	private int instancesNo;
	private List<List<String>> valuesList;

	public FieldValue(int fieldId, String fieldPath, List<List<String>> valuesList) {
		this.fieldId = fieldId;
		this.fieldPath = fieldPath;
		this.valuesList = valuesList;
		if (valuesList!=null)
			this.instancesNo = valuesList.size();
		else {
			this.instancesNo = 0;
			this.valuesList = new ArrayList<List<String>>();
		}			
	}
	
	public List<List<String>> getValues(){
		return this.valuesList;
	}
	
	public List<String> getValue(int instanceNo){
		return this.valuesList.get(instanceNo);
	}
	
	public void addValue(List<String> valueToAdd){
		this.valuesList.add(valueToAdd);
		this.instancesNo++;
	}
	
	public void addValue(int position, List<String> valueToAdd){
		this.valuesList.add(position, valueToAdd);
		this.instancesNo++;
	}
	
	public void setValue(int position, List<String> valueToAdd){
		this.valuesList.set(position, valueToAdd);
	}
	
	public int size(){
		return this.instancesNo;
	}
	
	public int getId(){
		return this.fieldId;
	}
}
