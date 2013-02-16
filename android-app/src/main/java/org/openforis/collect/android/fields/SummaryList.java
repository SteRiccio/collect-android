package org.openforis.collect.android.fields;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.screens.FormScreen;
import org.openforis.idm.metamodel.AttributeDefinition;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeLabel.Type;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.TextValue;
import org.openforis.idm.model.Value;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SummaryList extends UIElement {
	
	private TableLayout tableLayout;
	
	private String title;
	private EntityDefinition entityDefinition;
	
	private FormScreen context;

	public SummaryList(Context context, EntityDefinition entityDef, int threshold,
			/*String title, List<List<String>> keysList, List<List<String>> detailsList,*/
			OnClickListener listener, int entityInstanceNo) {
		super(context, entityDef);
		
		this.context = (FormScreen)context;
		
		this.tableLayout  = new TableLayout(context);  
		this.tableLayout.setStretchAllColumns(true); 
	    this.tableLayout.setShrinkAllColumns(true);
		this.tableLayout.setPadding(5, 10, 5, 10);
		
		this.title = entityDef.getLabel(Type.INSTANCE, null);
		if (this.title==null){
			if (entityDef.getLabels().size()>0)
				this.title = entityDef.getLabels().get(0).getText();
		}
		this.entityDefinition = entityDef;
		
		TextView titleView = new TextView(context);
		titleView.setText(this.title);
		this.tableLayout.addView(titleView);
		
		ArrayList<List<String>> keysList = new ArrayList<List<String>>();
		ArrayList<String> detailsList = new ArrayList<String>();
		
		List<AttributeDefinition> keyAttrDefsList = entityDef.getKeyAttributeDefinitions();
		Log.e("#entityKeysNo","=="+keyAttrDefsList.size());
		
		Entity parentEntity = this.findParentEntity(this.context.getFormScreenId());
		Entity currentEntity = null;
		if (parentEntity.equals(ApplicationManager.currentRecord.getRootEntity())){
			currentEntity = parentEntity;
			//parentEntity = null;
		} else {
			currentEntity = (Entity)parentEntity.get(entityDef.getName(), entityInstanceNo);
		}
		//Log.e("parentEntity"+parentEntity.getName(),"currentEntity="+currentEntity.getName());

		if (this.context.getFormScreenId()!=null){			
			for (AttributeDefinition attrDef : keyAttrDefsList){
				List<String> key = new ArrayList<String>();
				Value attrValue = null;
				if (entityDef.getId()==currentEntity.getId()){//entityDef isn't yet in currentRecord
					attrValue = (Value)currentEntity.getValue(attrDef.getName(),0);	
				}				
				key.add(attrDef.getName());
				if (attrValue!=null){
					if (attrValue instanceof TextValue){
						TextValue textValue = (TextValue)attrValue;
						key.add(textValue.getValue());
					}	
				}
				/*try {
					Log.e("attrDef"+key.get(0),"value=="+key.get(1));
				}catch (Exception e){
					Log.e("attrDef"+key.get(0),"==");
				}*/
				keysList.add(key);
				/*DataTreeNode currTreeNode = ApplicationManager.valuesTree.getChild(this.context.getFormScreenId()+getResources().getString(R.string.valuesSeparator2)+entityDef.getId()+getResources().getString(R.string.valuesSeparator1)+entityInstanceNo);
				if (currTreeNode!=null)
					keysList.add(attrDef.getName()+getResources().getString(R.string.valuesEqualTo)+currTreeNode.getFieldValue(attrDef.getId()).getValue(0).get(0));
				else 
					keysList.add(attrDef.getName()+"");*/
			}
			
			String keysLine = "";
			for (List<String> key : keysList){
				if (key.size()==1){
					keysLine += key.get(0) + getResources().getString(R.string.valuesSeparator1);
				} else {
					keysLine += key.get(0) + "=" + key.get(1) + getResources().getString(R.string.valuesSeparator1);	
				}
				
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
			
			TextView tv = new TextView(context);
			tv.setText(Html.fromHtml("<font size=\"32px\"><b>"+keysLine+"</b></font>")+"\n"+"detailsLine");
			//tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.cellshape));
			tv.setId(entityInstanceNo);
			tv.setOnClickListener(listener);

			TableRow tr = new TableRow(context);
			tr.addView(tv);
			this.tableLayout.addView(tr);
		}
		/*int rowNo = detailsList.size();

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

			TableRow tr = new TableRow(context);
			tr.addView(tv);
			this.tableLayout.addView(tr);
		}*/
		
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
