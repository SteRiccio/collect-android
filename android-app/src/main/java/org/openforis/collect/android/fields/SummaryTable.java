package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.TextAttributeDefinition;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.IntegerAttribute;
import org.openforis.idm.model.IntegerValue;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.RealAttribute;
import org.openforis.idm.model.RealValue;
import org.openforis.idm.model.TextValue;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SummaryTable extends UIElement {
	
	private TableLayout tableLayout;
	
	private List<List<String>> values;
	
	public SummaryTable(Context context, NodeDefinition nodeDef, List<String> columnHeader, Entity parentEntity/*List<List<String>> rows*/,
			OnClickListener listener) {
		super(context, nodeDef);
		
		this.tableLayout  = new TableLayout(context);
		this.tableLayout.setStretchAllColumns(true);  
	    this.tableLayout.setShrinkAllColumns(true);
	    this.tableLayout.setPadding(5, 10, 5, 10);
	    
	    //this.values = rows;
	    this.values = new ArrayList<List<String>>();	    
	   
	    List<Node<?>> listOfNodes = parentEntity.getAll(nodeDef.getName());
	    if (listOfNodes.size()==0){
		    ArrayList<String> newValue = new ArrayList<String>();
		    newValue.add("");
		    this.values.add(newValue);
			//EntityBuilder.addValue(parentEntity, nodeDef.getName(), newValue, 0);
	    }
	    for (int i=0;i<listOfNodes.size();i++){
	    	Node<?> foundNode = parentEntity.get(nodeDef.getName(), i);
		    String loadedValue = "";
			if (foundNode!=null){
				if (nodeDef instanceof TextAttributeDefinition){
					TextValue textValue = (TextValue)parentEntity.getValue(nodeDef.getName(), i);
					loadedValue = textValue.getValue();
				    ArrayList<String> newValue = new ArrayList<String>();
				    newValue.add(loadedValue);
				    this.values.add(newValue);	
				} else if (nodeDef instanceof NumberAttributeDefinition){
					if (((NumberAttributeDefinition) this.nodeDefinition).isInteger()){
						IntegerAttribute intAttr = (IntegerAttribute)parentEntity.getValue(nodeDef.getName(), i);
						IntegerValue intValue = (IntegerValue)intAttr.getValue();
						if (intValue!=null)
    						loadedValue = intValue.getValue().toString();
						ArrayList<String> newValue = new ArrayList<String>();
						newValue.add(loadedValue);
						this.values.add(newValue);
					} else {
						RealAttribute realAttr = (RealAttribute)parentEntity.getValue(nodeDef.getName(), i);
						RealValue realValue = (RealValue)realAttr.getValue();
						if (realValue!=null)
    						loadedValue = realValue.getValue().toString();						
						loadedValue = realValue.getValue().toString();
						ArrayList<String> newValue = new ArrayList<String>();
						newValue.add(loadedValue);
						this.values.add(newValue);
					}
				}
			}
	    }
	    
	    
		int colNo = columnHeader.size();
		//int rowNo = rows.size();
		int rowNo = this.values.size();
		
		TextView header = new TextView(context);
		header.setText(this.label.getText());
		this.tableLayout.addView(header);
		
		TableRow colHeaders = new TableRow(context);
		
		TextView colTitle = new TextView(context);
		//colTitle.setBackgroundDrawable(getResources().getDrawable(R.drawable.cellshape));
		colTitle.setPadding(20, 5, 20, 5);
		colTitle.setGravity(Gravity.CENTER);
		colTitle.setText("ID");
		colHeaders.addView(colTitle);
		for (int i=0;i<colNo;i++){
			colTitle = new TextView(context);
			//colTitle.setBackgroundDrawable(getResources().getDrawable(R.drawable.cellshape));
			colTitle.setPadding(20, 5, 20, 5);
			colTitle.setGravity(Gravity.CENTER);
			colTitle.setText(columnHeader.get(i));
			colHeaders.addView(colTitle);
		}
		this.tableLayout.addView(colHeaders);
		
		for (int i=0;i<rowNo;i++){
			TableRow tempRow = new TableRow(context);
			//List<String> rowValues = rows.get(i);
			List<String> rowValues = this.values.get(i);
			for (int j=-1;j<colNo;j++){
				TextView cell = new TextView(context);
				//cell.setBackgroundDrawable(getResources().getDrawable(R.drawable.cellshape));
				cell.setPadding(20, 5, 20, 5);
				cell.setGravity(Gravity.CENTER);
				if (j>=0){
					cell.setText(rowValues.get(j));	
				} else {
					cell.setText(""+i);	
				}				
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
		return this.label.getText().toString();
	}
	
	/*public void setTitle(String title){
		this.label.setText(title);
	}*/
	
	public List<List<String>> getValues(){
		return this.values;
	}
	
	public void setValues(List<List<String>> values){
		this.values = values;
	}	
}
