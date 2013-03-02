package org.openforis.collect.android.fields;

import java.io.File;

import org.openforis.collect.android.R;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.TextAttribute;
import org.openforis.idm.model.TextValue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PhotoField extends FileField implements OnClickListener{
	
	ImageView image;
	
	public PhotoField(Context context, NodeDefinition nodeDef) {
		super(context, nodeDef);
		
		this.image = new ImageView(context);
		this.image.setLayoutParams(new LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,(float) 1));
		this.image.setOnClickListener(this);
		File imageFile = new File("filePath");
	    if(imageFile.exists()){
		   /* BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inSampleSize = 10;	    	
	    	Bitmap myBitmap = BitmapFactory.decodeFile(filePath, options);	
	    	this.image.setImageBitmap(myBitmap);*/
	    	Log.e("USTAWIONO","ZDJECIE");
	    }
	    else{
	    	this.image.setImageResource(R.drawable.emptyimage);
	    	Log.e("USTAWIONO","TEMPLATE");
	    }	    
		this.addView(image);
	}
	
	public void setValue(Integer position, String photoPath, String path, boolean isPhotoChanged)
	{
		/*Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
		if (node!=null){
			TextAttribute textAtr = (TextAttribute)node;
			textAtr.setValue(new TextValue(value));
		} else {
			EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), value, position);	
		}*/
	}

	@Override
	public void onClick(View arg0) {
		Log.e("IMAGE"+this.nodeDefinition.getName(),"CLICKED");
	}
}
