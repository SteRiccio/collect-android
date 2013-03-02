package org.openforis.collect.android.fields;

import java.util.List;

import org.openforis.collect.android.messages.ToastMessage;
import org.openforis.idm.metamodel.FileAttributeDefinition;
import org.openforis.idm.metamodel.NodeDefinition;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class FileField extends Field {
	
	protected List<String> extensionsList;
	
	public FileField(Context context, NodeDefinition nodeDef) {
		super(context, nodeDef);
		
		FileAttributeDefinition fileDef = (FileAttributeDefinition)nodeDef;
		this.extensionsList = fileDef.getExtensions();
		
		this.label.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
		//this.label.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		this.label.setOnLongClickListener(new OnLongClickListener() {
	        @Override
	        public boolean onLongClick(View v) {
	        	ToastMessage.displayToastMessage(FileField.this.getContext(), FileField.this.getLabelText(), Toast.LENGTH_LONG);
	            return true;
	        }
	    });
	}
}
