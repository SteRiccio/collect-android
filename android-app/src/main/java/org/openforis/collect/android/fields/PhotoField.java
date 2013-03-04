package org.openforis.collect.android.fields;

import org.openforis.collect.android.R;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.File;
import org.openforis.idm.model.FileAttribute;
import org.openforis.idm.model.Node;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
		/*File imageFile = new File("filePath");
	    if(imageFile.exists()){
		    BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inSampleSize = 10;	    	
	    	Bitmap myBitmap = BitmapFactory.decodeFile(filePath, options);	
	    	this.image.setImageBitmap(myBitmap);
	    	Log.e("USTAWIONO","ZDJECIE");
	    }
	    else{
	    	this.image.setImageResource(R.drawable.emptyimage);
	    	Log.e("USTAWIONO","TEMPLATE");
	    }*/	    
		this.addView(image);
		
	}
	
	public void setValue(Integer position, String photoName, String path, boolean isPhotoChanged)
	{		
		java.io.File imageFile = new java.io.File(photoName);
	    if(imageFile.exists()){
			Node<? extends NodeDefinition> node = this.findParentEntity(path).get(this.nodeDefinition.getName(), position);
			if (node!=null){
				FileAttribute photoAtr = (FileAttribute)node;
				photoAtr.setValue(new File(photoName,Long.valueOf(imageFile.length())));
			} else {
				EntityBuilder.addValue(this.findParentEntity(path), this.nodeDefinition.getName(), new File(photoName,Long.valueOf("123")), position);	
			}
		    BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inSampleSize = 10;	    	
	    	Bitmap myBitmap = BitmapFactory.decodeFile(photoName, options);	
	    	this.image.setImageBitmap(myBitmap);
	    }
	    else{
	    	this.image.setImageResource(R.drawable.emptyimage);
	    }
	}

	@Override
	public void onClick(View arg0) {
		this.form.currentPictureField = this;
		this.form.startCamera(this);
	}
}
