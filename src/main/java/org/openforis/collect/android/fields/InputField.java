package org.openforis.collect.android.fields;

import android.content.Context;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class InputField extends Field {
	
	public EditText txtBox;
	
	public InputField(Context context, boolean isMultiple) {
		super(context, isMultiple);
		this.txtBox = new EditText(context);
		this.setAlignment(Gravity.LEFT);
	}
	
	public void setKeyboardType(KeyListener keyListener){
		this.txtBox.setKeyListener(keyListener);
	}
	
	public String getValue()
	{
		return this.txtBox.getText().toString();
	}
	
	public void setValue(String value)
	{
		this.txtBox.setText(value);
	}
	
	public String getHint()
	{
		return this.txtBox.getHint().toString();
	}
	
	public void setHint(String value)
	{
		this.txtBox.setHint(value);
	}
	
	public void setAlignment(int alignment){
		this.txtBox.setGravity(alignment);
	}
}
