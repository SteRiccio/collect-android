package org.openforis.collect.android.messages;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.widget.EditText;

public class InputMessage{
	
	public static AlertDialog createInputDialog(Context ctx, boolean isCancelable, Drawable icon,
			String messageTitle,String messageBody, String positive, String negative,
			DialogInterface.OnClickListener positiveButtonListener,
			DialogInterface.OnClickListener negativeButtonListener){
		final EditText input = new EditText(ctx);		
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);	
		builder.setTitle(messageTitle);
	    builder.setMessage(messageBody);
	    builder.setIcon(icon);
	    builder.setCancelable(isCancelable);
	    builder.setPositiveButton(positive, positiveButtonListener);
	    if (negativeButtonListener!=null)
	    	builder.setNegativeButton(negative, negativeButtonListener);
	    builder.setView(input);
	    return builder.create();
	}
}
