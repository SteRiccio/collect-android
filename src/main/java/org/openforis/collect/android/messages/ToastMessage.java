package org.openforis.collect.android.messages;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastMessage {
	
	public static void displayToastMessage(Context ctx, String message, int time){
		Toast msg = Toast.makeText(ctx, message, time);
    	msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
    	msg.show();
	}
	
}
