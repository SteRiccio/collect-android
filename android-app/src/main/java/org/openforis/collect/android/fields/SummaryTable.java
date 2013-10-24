package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.ViewBacktrack;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.collect.android.service.ServiceFactory;
import org.openforis.idm.metamodel.BooleanAttributeDefinition;
import org.openforis.idm.metamodel.CodeAttributeDefinition;
import org.openforis.idm.metamodel.CoordinateAttributeDefinition;
import org.openforis.idm.metamodel.DateAttributeDefinition;
import org.openforis.idm.metamodel.FileAttributeDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.RangeAttributeDefinition;
import org.openforis.idm.metamodel.TaxonAttributeDefinition;
import org.openforis.idm.metamodel.TextAttributeDefinition;
import org.openforis.idm.metamodel.TimeAttributeDefinition;
import org.openforis.idm.model.BooleanValue;
import org.openforis.idm.model.Code;
import org.openforis.idm.model.Coordinate;
import org.openforis.idm.model.Date;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.File;
import org.openforis.idm.model.IntegerAttribute;
import org.openforis.idm.model.IntegerRange;
import org.openforis.idm.model.IntegerValue;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.RealAttribute;
import org.openforis.idm.model.RealRange;
import org.openforis.idm.model.RealValue;
import org.openforis.idm.model.TaxonOccurrence;
import org.openforis.idm.model.TextValue;
import org.openforis.idm.model.Time;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
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
					if (textValue!=null)
						loadedValue = textValue.getValue();
					if (loadedValue==null)
						loadedValue = "";
				    ArrayList<String> newValue = new ArrayList<String>();
				    newValue.add(loadedValue);
				    this.values.add(newValue);	
				} else if (nodeDef instanceof NumberAttributeDefinition){
					if (((NumberAttributeDefinition) this.nodeDefinition).isInteger()){
						IntegerAttribute intAttr = (IntegerAttribute)parentEntity.getValue(nodeDef.getName(), i);
						IntegerValue intValue = (IntegerValue)intAttr.getValue();
						if (intValue!=null)
							if  (intValue.getValue()!=null)
								loadedValue = intValue.getValue().toString();
							else loadedValue = "";
						ArrayList<String> newValue = new ArrayList<String>();
						newValue.add(loadedValue);
						this.values.add(newValue);
					} else {
						RealAttribute realAttr = (RealAttribute)parentEntity.getValue(nodeDef.getName(), i);
						RealValue realValue = (RealValue)realAttr.getValue();
						if (realValue!=null)
							if (realValue.getValue()!=null)
								loadedValue = realValue.getValue().toString();
							else 
								loadedValue = "";
						loadedValue = realValue.getValue().toString();
						ArrayList<String> newValue = new ArrayList<String>();
						newValue.add(loadedValue);
						this.values.add(newValue);
					}
				} else if (nodeDef instanceof BooleanAttributeDefinition){
					BooleanValue boolValue = (BooleanValue)parentEntity.getValue(nodeDef.getName(), i);
					if (boolValue!=null)
						if (boolValue.getValue())
							loadedValue = boolValue.getValue().toString();
						else
							loadedValue = "";
				    ArrayList<String> newValue = new ArrayList<String>();
				    newValue.add(loadedValue);
				    this.values.add(newValue);
				} else if (nodeDef instanceof CodeAttributeDefinition){
					Code codeValue = (Code)parentEntity.getValue(nodeDef.getName(), i);
					if (codeValue!=null)
						if (codeValue.getCode()!=null)
							loadedValue = codeValue.getCode();
						else
							loadedValue = "";
				    ArrayList<String> newValue = new ArrayList<String>();
				    newValue.add(loadedValue);
				    this.values.add(newValue);
				} else if (nodeDef instanceof CoordinateAttributeDefinition){
					Coordinate coordValue = (Coordinate)parentEntity.getValue(nodeDef.getName(), i);
					loadedValue = coordValue.getX()+getResources().getString(R.string.coordinateSeparator)+coordValue.getY();
				    ArrayList<String> newValue = new ArrayList<String>();
				    newValue.add(loadedValue);
				    this.values.add(newValue);
				} else if (nodeDef instanceof RangeAttributeDefinition){
					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
					if (rangeAttrDef.isReal()){
						RealRange rangeValue = (RealRange)parentEntity.getValue(nodeDef.getName(), i);
						loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
					} else {
						IntegerRange rangeValue = (IntegerRange)parentEntity.getValue(nodeDef.getName(), i);
						loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();	
					}					
				    ArrayList<String> newValue = new ArrayList<String>();
				    newValue.add(loadedValue);
				    this.values.add(newValue);
				} else if (nodeDef instanceof DateAttributeDefinition){
					Date dateValue = (Date)parentEntity.getValue(nodeDef.getName(), i);
					loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
				    ArrayList<String> newValue = new ArrayList<String>();
				    newValue.add(loadedValue);
				    this.values.add(newValue);
				} else if (nodeDef instanceof TimeAttributeDefinition){
					Time timeValue = (Time)parentEntity.getValue(nodeDef.getName(), i);
					String hour = "";
					String minute = "";
					if (timeValue.getHour()!=null){
						hour = timeValue.getHour().toString();
						if (timeValue.getHour()<10){
							hour = "0"+hour;
						}
					}
					if (timeValue.getMinute()!=null){
						minute = timeValue.getMinute().toString();
						if (timeValue.getMinute()<10){
							minute = "0"+minute;
						}
					}
					loadedValue = hour+getResources().getString(R.string.timeSeparator)+minute;
				    ArrayList<String> newValue = new ArrayList<String>();
				    newValue.add(loadedValue);
				    this.values.add(newValue);
				} else if (nodeDef instanceof TaxonAttributeDefinition){
					TaxonOccurrence taxonValue = (TaxonOccurrence)parentEntity.getValue(nodeDef.getName(), i);
					if (taxonValue!=null)
						loadedValue = taxonValue.getCode()+getResources().getString(R.string.taxonSeparator)+
						taxonValue.getScientificName()+getResources().getString(R.string.taxonSeparator)+
						taxonValue.getVernacularName()+getResources().getString(R.string.taxonSeparator)+
						taxonValue.getLanguageCode()+getResources().getString(R.string.taxonSeparator)+
						taxonValue.getLanguageVariety();
				    ArrayList<String> newValue = new ArrayList<String>();
				    newValue.add(loadedValue);
				    this.values.add(newValue);
				} else if (nodeDef instanceof FileAttributeDefinition){
					File fileValue = (File)parentEntity.getValue(nodeDef.getName(), i);
					loadedValue = fileValue.getFilename();
				    ArrayList<String> newValue = new ArrayList<String>();
				    newValue.add(loadedValue);
				    this.values.add(newValue);
				}
			}
	    }
	    
	    
		int colNo = columnHeader.size();
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
				final Node<?> nodeToDelete = parentEntity.get(nodeDef.getName(), i);
				cell.setOnLongClickListener(new OnLongClickListener() {
			        @Override
			        public boolean onLongClick(View v) {
			        	AlertMessage.createPositiveNegativeDialog(SummaryTable.this.getContext(), false, getResources().getDrawable(R.drawable.warningsign),
			 					getResources().getString(R.string.deleteAttributeTitle), getResources().getString(R.string.deleteAttribute),
			 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
			 		    		new DialogInterface.OnClickListener() {
			 						@Override
			 						public void onClick(DialogInterface dialog, int which) {
			 							ServiceFactory.getRecordManager().deleteNode(nodeToDelete);
			 							//ApplicationManager.isToBeScrolled = true;
			 							ViewBacktrack viewBacktrack = new ViewBacktrack(SummaryTable.this,SummaryTable.this.form.getFormScreenId());
			 							ApplicationManager.selectedViewsBacktrackList.add(viewBacktrack);
			 							((FormScreen)SummaryTable.this.getContext()).onResume();
			 						}
			 					},
			 		    		new DialogInterface.OnClickListener() {
			 						@Override
			 						public void onClick(DialogInterface dialog, int which) {
			 							//Log.i("From SummaryTable", "No-button has been pressed from " + nodeToDelete.getName());
			 						}
			 					},
			 					null).show();
			            return true;
			        }
			    });
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
	
	public List<List<String>> getValues(){
		return this.values;
	}
	
	public void setValues(List<List<String>> values){
		this.values = values;
	}	
}
