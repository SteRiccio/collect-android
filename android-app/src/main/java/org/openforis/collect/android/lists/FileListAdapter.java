package org.openforis.collect.android.lists;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FileListAdapter extends ArrayAdapter<DataFile> {

	private int				resource;
	private LayoutInflater	inflater;
	private Context 		context;
	private String type;
	
	public List<Boolean[]> checkList;
	
	public FileListAdapter (Context ctx, int resourceId, List<DataFile> objects, String type) {
		
		super( ctx, resourceId, objects );
		resource = resourceId;
		inflater = LayoutInflater.from( ctx );
		context=ctx;
		checkList = new ArrayList<Boolean[]>();
		int itemsNo = objects.size();
		for (int i=0;i<itemsNo;i++){
			Boolean[] boolArray = {false,false};
			checkList.add(boolArray);
		}
		this.type = type;
	}

	@Override
	public View getView ( int position, View convertView, ViewGroup parent ) {

		/* create a new view of my layout and inflate it in the row */
		convertView = (LinearLayout) inflater.inflate( resource, null );

		/* Extract the city's object to show */
		//City city = getItem( position );
		
		DataFile dataFile = getItem(position);
		
		/* Take the TextView from layout and set the city's name */
		//TextView txtName = (TextView) convertView.findViewById(R.id.cityName);
		//txtName.setText(city.getName());
		TextView txtFileName = (TextView) convertView.findViewById(R.id.lblFileName);
		txtFileName.setText(dataFile.getName());
		
		/* Take the TextView from layout and set the city's wiki link */
		//TextView txtWiki = (TextView) convertView.findViewById(R.id.cityLinkWiki);
		//txtWiki.setText(city.getUrlWiki());
		
		/* Take the ImageView from layout and set the city's image */
		/*ImageView imageCity = (ImageView) convertView.findViewById(R.id.ImageCity);
		String uri = "drawable/" + city.getImage();
	    int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
	    Drawable image = context.getResources().getDrawable(imageResource);
	    imageCity.setImageDrawable(image);
*/
	    CheckBox chckUpload = (CheckBox) convertView.findViewById(R.id.chckUpload);
	    if (checkList.get(position)[0]){
	    	chckUpload.setChecked(true);
	    }
	    final int pos1 = position;
	    chckUpload.setOnClickListener(new OnClickListener() {

	  	  @Override
	  	  public void onClick(View v) {
	  		if (((CheckBox) v).isChecked()) {
	  			Boolean[] boolArray = {true,checkList.get(pos1)[1]};
	  			checkList.set(pos1, boolArray);
	  		} else {
	  			Boolean[] boolArray = {false,checkList.get(pos1)[1]};
	  			checkList.set(pos1, boolArray);
	  		}
	   
	  	  }
	  	});	    
	    
	    if (type.equals("upload")){
	    	CheckBox chckOverwrite = (CheckBox) convertView.findViewById(R.id.chckOverwrite);
		    if (checkList.get(position)[0]){
		    	chckOverwrite.setChecked(true);
		    }
		    final int pos2 = position;
		    final String fileName2 = dataFile.getName();
		    chckOverwrite.setOnClickListener(new OnClickListener() {

		  	  @Override
		  	  public void onClick(View v) {
		  		if (((CheckBox) v).isChecked()) {
		  			Boolean[] boolArray = {checkList.get(pos1)[0],true};
		  			checkList.set(pos1, boolArray);
		  		} else {
		  			Boolean[] boolArray = {checkList.get(pos1)[0],false};
		  			checkList.set(pos1, boolArray);
		  		}
		   
		  	  }
		  	});
	    }
	    
		return convertView;

	}
}
