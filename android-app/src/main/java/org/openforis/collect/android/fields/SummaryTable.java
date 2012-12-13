package org.openforis.collect.android.fields;

import java.util.List;

import org.openforis.collect.android.R;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SummaryTable extends UIElement {
	
	private TableLayout tableLayout;
	
	private String title;
	
	private List<List<String>> values;

	public SummaryTable(Context context, int id, /*EntityDefinition entityDef,*/
			String tableHeader, List<String> columnHeader, List<List<String>> rows,
			OnClickListener listener) {
		super(context, id, false);
		
		this.tableLayout  = new TableLayout(context);
		this.tableLayout.setStretchAllColumns(true);  
	    this.tableLayout.setShrinkAllColumns(true);
	    this.tableLayout.setPadding(5, 10, 5, 10);
		
	    this.title = tableHeader;
	    this.values = rows;
	    
		int colNo = columnHeader.size();
		int rowNo = rows.size();
		
		TextView header = new TextView(context);
		header.setText(tableHeader);
		//TableRow tr1 = new TableRow(context);
		//TableRow.LayoutParams params = new TableRow.LayoutParams();  
	    //params.span = colNo;
		//tr1.addView(header);
		//this.tableLayout.addView(tr1);
		this.tableLayout.addView(header);
		
		TableRow colHeaders = new TableRow(context);
		
		for (int i=0;i<colNo;i++){
			TextView colTitle = new TextView(context);
			//colTitle.setBackgroundDrawable(getResources().getDrawable(R.drawable.cellshape));
			colTitle.setPadding(20, 5, 20, 5);
			colTitle.setGravity(Gravity.CENTER);
			colTitle.setText(columnHeader.get(i));
			colHeaders.addView(colTitle);
		}
		this.tableLayout.addView(colHeaders);
		
		for (int i=0;i<rowNo;i++){
			TableRow tempRow = new TableRow(context);
			List<String> rowValues = rows.get(i);
			for (int j=0;j<colNo;j++){
				TextView cell = new TextView(context);
				//cell.setBackgroundDrawable(getResources().getDrawable(R.drawable.cellshape));
				cell.setPadding(20, 5, 20, 5);
				cell.setGravity(Gravity.CENTER);
				cell.setText(rowValues.get(j));
				cell.setId(i);
				cell.setOnClickListener(listener);
				/*cell.setOnClickListener(new OnClickListener() {                      
					@Override
					public void onClick(View arg0) {
						TextView tv = (TextView)arg0;
						Log.e("klikniety","=="+tv.getText().toString());
					}
				});*/
				tempRow.addView(cell);
			}
			this.tableLayout.addView(tempRow);
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
			int colNo = tableRowView.getChildCount();
			for (int j=0;j<colNo;j++){
				TextView rowView = (TextView) tableRowView.getChildAt(j);
				rowView.setBackgroundDrawable((backgroundColor!=Color.WHITE)?getResources().getDrawable(R.drawable.cellshape_white):getResources().getDrawable(R.drawable.cellshape_black));
				rowView.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);	
			}			
		}
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public List<List<String>> getValues(){
		return this.values;
	}
	
	public void setValues(List<List<String>> values){
		this.values = values;
	}	
}
