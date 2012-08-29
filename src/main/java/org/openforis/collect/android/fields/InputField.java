package org.openforis.collect.android.fields;

import android.content.Context;
import android.text.method.KeyListener;
import android.widget.EditText;

public class InputField extends Field {
	public EditText txtBox;
	
	public InputField(Context context) {
		super(context);
		this.txtBox = new EditText(context);
	}
	
	public void setKeyboardType(KeyListener keyListener){
		this.txtBox.setKeyListener(keyListener);
	}
}
