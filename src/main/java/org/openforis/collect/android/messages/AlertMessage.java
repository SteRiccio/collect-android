package org.openforis.collect.android.messages;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;

public class AlertMessage{
	
	public static AlertDialog createPositiveNegativeDialog(Context ctx, boolean isCancelable, Drawable icon,
			String messageTitle,String messageBody, String positive, String negative,
			DialogInterface.OnClickListener positiveButtonListener,
			DialogInterface.OnClickListener negativeButtonListener,
			View viewToDisplay){
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);	
		builder.setTitle(messageTitle);
	    builder.setMessage(messageBody);
	    builder.setIcon(icon);
	    builder.setCancelable(isCancelable);
	    builder.setPositiveButton(positive, positiveButtonListener);
	    builder.setNegativeButton(negative, negativeButtonListener);
	    builder.setView(viewToDisplay);
	    return builder.create();
	}
	
	public static AlertDialog createPositiveNeutralNegativeDialog(Context ctx, boolean isCancelable, Drawable icon,
			String messageTitle,String messageBody, String positive, String neutral, String negative,
			DialogInterface.OnClickListener positiveButtonListener,
			DialogInterface.OnClickListener negativeButtonListener,
			DialogInterface.OnClickListener cancelButtonListener){
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);	
		builder.setTitle(messageTitle);
	    builder.setMessage(messageBody);
	    builder.setIcon(icon);
	    builder.setCancelable(isCancelable);
	    builder.setPositiveButton(positive, positiveButtonListener);
	    builder.setNeutralButton(neutral, cancelButtonListener);
	    builder.setNegativeButton(negative, negativeButtonListener);
	    return builder.create();
	}
}
