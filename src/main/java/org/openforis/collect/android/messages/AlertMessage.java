package org.openforis.collect.android.messages;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

public class AlertMessage{
	
	public static AlertDialog createYesNoDialog(Context ctx, boolean isCancelable, Drawable icon,
			String messageTitle,String messageBody, String yes, String no,
			DialogInterface.OnClickListener positiveButtonListener,
			DialogInterface.OnClickListener negativeButtonListener){
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);	
		builder.setTitle(messageTitle);
	    builder.setMessage(messageBody);
	    builder.setIcon(icon);
	    builder.setCancelable(isCancelable);
	    builder.setPositiveButton(yes, positiveButtonListener);
	    builder.setNegativeButton(no, negativeButtonListener);
	    return builder.create();
	}
}
