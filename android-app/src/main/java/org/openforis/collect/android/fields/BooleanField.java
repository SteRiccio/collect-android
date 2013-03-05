package org.openforis.collect.android.fields;

import java.util.ArrayList;

import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.idm.metamodel.BooleanAttributeDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.BooleanAttribute;
import org.openforis.idm.model.BooleanValue;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.Node;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class BooleanField extends Field {
	
	private TextView label1;
	private TextView label2;
	private CheckBox chckBox1;
	private CheckBox chckBox2;
	
	private static FormScreen form;
	
	public BooleanField(Context context, NodeDefinition nodeDef, boolean isChecked1, boolean isChecked2, String label1Text, String label2Text) {
		super(context, nodeDef);

		BooleanField.form = (FormScreen)context;

		this.label.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(BooleanField.this.getContext(), BooleanField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
		this.chckBox1 = new CheckBox(context);
		this.chckBox1.setChecked(isChecked1);
		this.chckBox1.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.chckBox1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				chckBox2.setChecked(!chckBox1.isChecked());
				ArrayList<String> value = new ArrayList<String>();
				value.add(String.valueOf(chckBox1.isChecked()));
				value.add(String.valueOf(!chckBox1.isChecked()));
				BooleanField.this.setValue(BooleanField.form.currInstanceNo, chckBox1.isChecked(), BooleanField.form.getFormScreenId(),true);
  			}
	    });		
		this.label1 = new TextView(context);
		this.label1.setText(label1Text);
		this.label1.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.label1.setTextColor(Color.BLACK);
		
		this.chckBox2 = new CheckBox(context);
		this.chckBox2.setChecked(isChecked2);
		this.chckBox2.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.chckBox2.setOnClickListener(new OnClickListener() {
	          @Override
	          public void onClick(View v) {
	        	  	chckBox1.setChecked(!chckBox2.isChecked());		
					ArrayList<String> value = new ArrayList<String>();
					value.add(String.valueOf(!chckBox2.isChecked()));
					value.add(String.valueOf(chckBox2.isChecked()));
					BooleanField.this.setValue(BooleanField.form.currInstanceNo, !chckBox2.isChecked(), BooleanField.form.getFormScreenId(), true);
	          }
	    });
		this.label2 = new TextView(context);
		this.label2.setText(label2Text);
		this.label2.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		this.label2.setTextColor(Color.BLACK);
		
		if (((BooleanAttributeDefinition) nodeDef).isAffirmativeOnly()){
			this.chckBox2.setVisibility(View.GONE);
			this.label2.setVisibility(View.GONE);
			this.label1.setVisibility(View.GONE);
		}

		this.addView(this.chckBox1);
		this.addView(this.label1);
		this.addView(this.chckBox2);
		this.addView(this.label2);
	}
	
	public void setValue(int position, Boolean boolValue, String path, boolean isSelectionChanged)
	{
		if (boolValue==null){
			if (!isSelectionChanged)
				this.chckBox1.setChecked(false);
			if (!isSelectionChanged)
				this.chckBox2.setChecked(false);
		} else {
			if (!isSelectionChanged)
				this.chckBox1.setChecked(boolValue);
			if (!isSelectionChanged)
				this.chckBox2.setChecked(!boolValue);
		}
		
		Entity parentEntity = this.findParentEntity(path);
		Node<? extends NodeDefinition> node = parentEntity.get(this.nodeDefinition.getName(), position);
		if (node!=null){
			BooleanAttribute boolAtr = (BooleanAttribute)node;
			boolAtr.setValue(new BooleanValue(boolValue));
		} else {
			EntityBuilder.addValue(parentEntity, this.nodeDefinition.getName(), boolValue, position);	
		}
	}
	
	public void addOnClickListener(OnClickListener onClickListener1, OnClickListener onClickListener2) {
		this.chckBox1.setOnClickListener(onClickListener1);
		this.chckBox2.setOnClickListener(onClickListener2);
	}
	
	public void setChoiceLabelsTextColor(int color){
		this.label1.setTextColor(color);
		this.label2.setTextColor(color);
	}
}
