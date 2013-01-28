package org.openforis.collect.android.fields;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.idm.metamodel.EntityDefinition;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SummaryList extends UIElement {
	
	private TableLayout tableLayout;
	
	private String title;
	private EntityDefinition entityDefinition;

	public SummaryList(Context context, int id, EntityDefinition entityDef, int threshold,
			String title, List<List<String>> keysList, List<List<String>> detailsList,
			OnClickListener listener) {
		super(context, id, false);
		
		this.tableLayout  = new TableLayout(context);  
		this.tableLayout.setStretchAllColumns(true);  
	    this.tableLayout.setShrinkAllColumns(true);
		this.tableLayout.setPadding(5, 10, 5, 10);
		
		this.title = title;
		this.entityDefinition = entityDef;
		
		TextView titleView = new TextView(context);
		titleView.setText(this.title);
		this.tableLayout.addView(titleView);
		
		int rowNo = detailsList.size();

		for (int i=0;i<rowNo;i++){			
			
			List<String> keys = keysList.get(i);
			String keysLine = "";
			for (String key : keys){
				keysLine += key + getResources().getString(R.string.valuesSeparator1);
				if (keysLine.length()>threshold){
					break;
				}
			}
		
			if (keysLine.length()>threshold){
				keysLine = keysLine.substring(0,threshold-3)+"...";
			} else {
				if (!keysLine.equals("")){
					keysLine = keysLine.substring(0,keysLine.length()-1);	
				}				
			}
			
			String detailsLine = "";
			List<String> details = detailsList.get(i);
			for (String detail : details){
				detailsLine += detail + getResources().getString(R.string.valuesSeparator1);
				if (detailsLine.length()>threshold){
					break;
				}
			}
			if (detailsLine.length()>threshold){
				String visibleDetails = detailsLine.substring(0,threshold-3);
				if (visibleDetails.substring(visibleDetails.length()-1, visibleDetails.length()).equals(getResources().getString(R.string.valuesSeparator1))){
					visibleDetails = visibleDetails.substring(0,visibleDetails.length()-1);
				}
				detailsLine = visibleDetails;
				detailsLine += getResources().getString(R.string.valuesNotVisibleSign);
			} else {
				detailsLine = detailsLine.substring(0,detailsLine.length()-1);
			}
			
			TextView tv = new TextView(context);
			tv.setText(Html.fromHtml("<font size=\"32px\"><b>"+keysLine+"</b></font>")+"\n"+detailsLine);
			//tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.cellshape));
			tv.setId(i);
			tv.setOnClickListener(listener);
			/*tv.setOnClickListener(new OnClickListener() {                      
				//@Override
				public void onClick(View arg0) {
					TextView tv = (TextView)arg0;
					Log.e("klikniety","=="+tv.getText().toString());
					
				}
			});*/
			TableRow tr = new TableRow(context);
			tr.addView(tv);
			this.tableLayout.addView(tr);
		}
		
		this.container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		this.container.addView(this.tableLayout);
		this.addView(this.container);
	}
	
	public void changeBackgroundColor(int backgroundColor){
		TextView titleView = (TextView)this.tableLayout.getChildAt(0);
		titleView.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		int childrenNo = this.tableLayout.getChildCount();
		for (int i=1;i<childrenNo;i++){		
			TableRow tableRowView = (TableRow)this.tableLayout.getChildAt(i);
			TextView rowView = (TextView) tableRowView.getChildAt(0);
			rowView.setBackgroundDrawable((backgroundColor!=Color.WHITE)?getResources().getDrawable(R.drawable.cellshape_white):getResources().getDrawable(R.drawable.cellshape_black));			
			//rowView.setBackgroundColor(backgroundColor);
			rowView.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		}
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}	
	
	public EntityDefinition getEntityDefinition(){
		return this.entityDefinition;
	}
	
	public void setTitle(EntityDefinition entityDef){
		this.entityDefinition = entityDef;
	}	
}
