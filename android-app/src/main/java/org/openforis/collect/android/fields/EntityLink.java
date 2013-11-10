package org.openforis.collect.android.fields;

import org.openforis.collect.android.R;
import org.openforis.idm.metamodel.EntityDefinition;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class EntityLink extends UIElement {
	
	private TableLayout tableLayout;
	private RelativeLayout relativeLayout;
	
	private EntityDefinition entityDefinition;
	
	//private FormScreen context;

	//private int instanceNo;
	
	public EntityLink(Context context, EntityDefinition entityDef, int threshold,
			OnClickListener listener) {
		super(context, entityDef);
		
		this.entityDefinition = entityDef;
		
		this.relativeLayout = new RelativeLayout(context);
		
		


		
		TextView titleView = new TextView(context);		
		titleView.setText(this.label.getText());
		
		ImageView linkView = new ImageView(context);
		linkView.setBackgroundResource(R.drawable.multiple_entity_arrow_black);
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(32,32);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		linkView.setLayoutParams(params);
		
		this.relativeLayout.addView(titleView);
		this.relativeLayout.addView(linkView);
		
		this.container.addView(this.relativeLayout);
		this.addView(this.container);
		//this.context = (FormScreen)context;
		
		/*this.tableLayout  = new TableLayout(context);  
		this.tableLayout.setStretchAllColumns(true); 
	    this.tableLayout.setShrinkAllColumns(true);
		this.tableLayout.setPadding(5, 10, 5, 10);
		
		this.entityDefinition = entityDef;
		//Log.e("entityDef",this.entityDefinition.getName()+"=="+this.instanceNo);
		TextView titleView = new TextView(context);
		//if (this.entityDefinition.isMultiple())
		//	titleView.setText(this.label.getText()+" "+(this.instanceNo+1));
		//else
		titleView.setText(this.label.getText());
		//this.tableLayout.addView(titleView);
		
		//TextView linkView = new TextView(context);
		//linkView.setText(">>>");
		ImageView linkView = new ImageView(context);
		linkView.setBackgroundResource(R.drawable.multiple_entity_arrow);
		
		TableRow tr = new TableRow(context);
		tr.addView(titleView);
		tr.addView(linkView);
		
		this.tableLayout.addView(tr);
		
		this.tableLayout.setColumnStretchable(0, true);
		
		this.container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		this.container.addView(this.tableLayout);
		this.addView(this.container);*/
		//adding the entity and its nodes if do not exist yet
		/*for (int i=0;i<entityDef.getChildDefinitions().size();i++){
			try{
				Entity parentEntity1 = ApplicationManager.currentRecord.getRootEntity();
				String screenPath = this.context.getFormScreenId();
				String[] entityPath = screenPath.split(getResources().getString(R.string.valuesSeparator2));
				int pathLength = entityPath.length;
				for (int m=2;m<pathLength;m++){
					String[] instancePath = entityPath[m].split(getResources().getString(R.string.valuesSeparator1));
					int id = Integer.valueOf(instancePath[0]);
					int instanceNo = Integer.valueOf(instancePath[1]);
					parentEntity1 = (Entity) parentEntity1.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
					parentEntity1.setId(id);
				}
				parentEntity1 = (Entity)parentEntity1.get(entityDef.getName(), entityInstanceNo);			
			} catch (Exception e){
			
			}
		}
		
		ArrayList<List<String>> keysList = new ArrayList<List<String>>();
		ArrayList<List<String>> detailsList = new ArrayList<List<String>>();
		
		Entity parentEntity = this.findParentEntity(this.context.getFormScreenId());
		Entity currentEntity = null;
		if (parentEntity.getName().equals(ApplicationManager.currentRecord.getRootEntity().getName())
				&&
				entityDef.getName().equals(ApplicationManager.currentRecord.getRootEntity().getName())){
			currentEntity = parentEntity;
			parentEntity = null;
		} else {
			currentEntity = (Entity)parentEntity.get(entityDef.getName(), entityInstanceNo);
		}

		if (this.context.getFormScreenId()!=null){			
			//fetching keys and their values
			List<AttributeDefinition> keyAttrDefsList = entityDef.getKeyAttributeDefinitions();
			for (AttributeDefinition attrDef : keyAttrDefsList){
				List<String> key = new ArrayList<String>();
				Value attrValue = null;			
				attrValue = (Value)currentEntity.getValue(attrDef.getName(),0);
				key.add(attrDef.getName());
				String stringValue = convertValueToString(attrValue, (NodeDefinition)attrDef);
				if (stringValue!=null)
					key.add(stringValue);	
				keysList.add(key);
			}
			
			String keysLine = "";
			for (List<String> key : keysList){
				if (key.size()==1){
					keysLine += key.get(0) + getResources().getString(R.string.valuesSeparator1);
				} else {
					keysLine += key.get(0) + getResources().getString(R.string.valuesEqualsTo) + key.get(1) + getResources().getString(R.string.valuesSeparator1);	
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
			
			//fetching details and their values
			List<NodeDefinition> detailNodeDefsList = entityDef.getChildDefinitions();
			for (NodeDefinition nodeDef : detailNodeDefsList){
				List<String> detail = new ArrayList<String>();
				Value attrValue = null;
				if (nodeDef instanceof EntityDefinition){
					attrValue = new TextValue("entitydefinitionnode");
				} else {
					attrValue = (Value)currentEntity.getValue(nodeDef.getName(),0);	
				}
				detail.add(nodeDef.getName());
				String stringValue = convertValueToString(attrValue, nodeDef);
				if (stringValue!=null)
					detail.add(stringValue);				
				detailsList.add(detail);
			}
			
			String detailsLine = "";
			for (List<String> detail : detailsList){
				if (detail.size()==1){
					detailsLine += detail.get(0) + getResources().getString(R.string.valuesSeparator1);
				} else {
					if (detail.get(1).equals("entitydefinitionnode")){
						detailsLine += "["+detail.get(0)+"]" + getResources().getString(R.string.valuesSeparator1);
					} else {
						detailsLine += detail.get(0) + getResources().getString(R.string.valuesEqualsTo) + detail.get(1) + getResources().getString(R.string.valuesSeparator1);	
					}						
				}
				
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
			tv.setText(keysLine+"\r\n"+detailsLine);
			tv.setId(entityInstanceNo);
			tv.setOnClickListener(listener);
			final Entity entityToRemove = currentEntity;
			final Entity parent = parentEntity;
			tv.setOnLongClickListener(new OnLongClickListener() {
		        @Override
		        public boolean onLongClick(View v) {		        
		        	AlertMessage.createPositiveNegativeDialog(EntityLink.this.context, false, getResources().getDrawable(R.drawable.warningsign),
		 					getResources().getString(R.string.deleteEntityTitle), getResources().getString(R.string.deleteEntity),
		 					getResources().getString(R.string.yes), getResources().getString(R.string.no),
		 		    		new DialogInterface.OnClickListener() {
		 						@Override
		 						public void onClick(DialogInterface dialog, int which) {
		 							Log.i("SummaryList", "Yes-button has been pressed from " + entityToRemove.getName());
		 							ServiceFactory.getRecordManager().deleteNode(entityToRemove);
		 							ApplicationManager.isToBeScrolled = true;
		 							ApplicationManager.selectedView = EntityLink.this;
		 							((FormScreen)EntityLink.this.context).onResume();
		 							//TODO: Validation. What exactly to validate if entityToRemove was deleted?
		 						}
		 					},
		 		    		new DialogInterface.OnClickListener() {
		 						@Override
		 						public void onClick(DialogInterface dialog, int which) {
		 							Log.i("SummaryList", "No-button has been pressed from " + entityToRemove.getName());
		 							Log.i("SummaryList", "No-button has been pressed. Parent entity is:  " + parent.getName());
		 						}
		 					},
		 					null).show();
		            return true;
		        }
		    });

			TableRow tr = new TableRow(context);
			tr.addView(tv);
			this.tableLayout.addView(tr);
		}
		
		this.container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		this.container.addView(this.tableLayout);
		this.addView(this.container);*/
	}
	
	public void changeBackgroundColor(int backgroundColor){
		TextView titleView = (TextView)this.relativeLayout.getChildAt(0);
		/*ViewGroup row = (ViewGroup)this.tableLayout.getChildAt(0);	
		TextView titleView = (TextView)row.getChildAt(0);*/
		titleView.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		ImageView linkView = (ImageView)this.relativeLayout.getChildAt(1);
		linkView.setBackgroundResource((backgroundColor!=Color.WHITE)?R.drawable.multiple_entity_arrow_white:R.drawable.multiple_entity_arrow_black);
		/*TextView linkView = (TextView)row.getChildAt(1);
		linkView.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);*/
		/*int childrenNo = this.tableLayout.getChildCount();
		for (int i=1;i<childrenNo;i++){		
			TableRow tableRowView = (TableRow)this.tableLayout.getChildAt(i);
			TextView rowView = (TextView) tableRowView.getChildAt(0);
			rowView.setBackgroundDrawable((backgroundColor!=Color.WHITE)?getResources().getDrawable(R.drawable.cellshape_white):getResources().getDrawable(R.drawable.cellshape_black));
			rowView.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
		}*/
	}
	
	public String getTitle(){
		return this.label.getText().toString();
	}
	
	public EntityDefinition getEntityDefinition(){
		return this.entityDefinition;
	}
	
	public void setTitle(EntityDefinition entityDef){
		this.entityDefinition = entityDef;
	}
	
	/*private String convertValueToString(Value value, NodeDefinition nodeDef){
		String valueToReturn = null;
		if (value!=null){
			if (value instanceof TextValue){
				TextValue textValue = (TextValue)value;
				valueToReturn = textValue.getValue();
			} else if (value instanceof NumberValue){
				NumberValue<?> numberValue = (NumberValue<?>)value;
				if (((NumberAttributeDefinition) nodeDef).isInteger()){
					valueToReturn = String.valueOf(numberValue.getValue().intValue());	
				} else {
					valueToReturn = String.valueOf(numberValue.getValue().doubleValue());
				}
			} else if (value instanceof BooleanValue){
				BooleanValue booleanValue = (BooleanValue)value;
				if (booleanValue.getValue()!=null)
					valueToReturn = String.valueOf(booleanValue.getValue());
			} else if (value instanceof Code){
				Code codeValue = (Code)value;
				CodeAttributeDefinition codeDef = (CodeAttributeDefinition)nodeDef;
				if (codeValue.getCode()!=null && !codeValue.getCode().equals("null") && !codeValue.getCode().equals("")){
					try{
						valueToReturn = ApplicationManager.getSurvey().getCodeList(codeDef.getList().getName()).findItem(codeValue.getCode()).getLabel(null);//codeValue.getCode();		
					} catch (NullPointerException e){
						valueToReturn = codeValue.getCode();	
					}
				}
			} else if (value instanceof RealRange){
				RealRange rangeValue = (RealRange)value;
				valueToReturn = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			} else if (value instanceof IntegerRange){
				IntegerRange rangeValue = (IntegerRange)value;
				valueToReturn = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
			} else if (value instanceof Coordinate){
				Coordinate coordinateValue = (Coordinate)value;
				if (coordinateValue.getX()==null && coordinateValue.getY()==null){
					valueToReturn = "";
				} else if (coordinateValue.getX()==null) {
					valueToReturn = getResources().getString(R.string.coordinateSeparator)+coordinateValue.getY();
				} else if (coordinateValue.getY()==null) {
					valueToReturn = coordinateValue.getX()+getResources().getString(R.string.coordinateSeparator);
				} else {
					valueToReturn = coordinateValue.getX()+getResources().getString(R.string.coordinateSeparator)+coordinateValue.getY();	
				}
			} else if (value instanceof Date){
				Date dateValue = (Date)value;
				String day = "";
				String month = "";
				String year = "";
				if (dateValue.getDay()!=null)
					day = dateValue.getDay().toString();
				if (dateValue.getMonth()!=null)
					month = dateValue.getMonth().toString();
				if (dateValue.getYear()!=null)
					year = dateValue.getYear().toString();
				valueToReturn = year+getResources().getString(R.string.dateSeparator)+month+getResources().getString(R.string.dateSeparator)+day;
			} else if (value instanceof Time){
				Time timeValue = (Time)value;
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
				valueToReturn = hour+getResources().getString(R.string.timeSeparator)+minute;
			} else if (value instanceof TaxonOccurrence){
				TaxonOccurrence taxonValue = (TaxonOccurrence)value;
				String code = "";
				String sciName = "";
				String vernName = "";
				String vernLang = "";
				String langVariant = "";
				if (taxonValue.getCode()!=null)
					code = taxonValue.getCode();
				if (taxonValue.getScientificName()!=null)
					sciName = taxonValue.getScientificName();
				if (taxonValue.getVernacularName()!=null)
					vernName = taxonValue.getVernacularName();
				if (taxonValue.getLanguageCode()!=null)
					vernLang = taxonValue.getLanguageCode();
				if (taxonValue.getLanguageVariety()!=null)
					langVariant = taxonValue.getLanguageVariety();
				valueToReturn = code+getResources().getString(R.string.taxonSeparator)+
						sciName+getResources().getString(R.string.taxonSeparator)+
						vernName+getResources().getString(R.string.taxonSeparator)+
						vernLang+getResources().getString(R.string.taxonSeparator)+
						langVariant;
			} else if (value instanceof File){
				File fileValue = (File)value;
				if (fileValue.getFilename()!=null)
					valueToReturn = fileValue.getFilename();
			}
		}
		return valueToReturn;
	}*/
	
	/*public int getInstanceNo(){
		return this.instanceNo;
	}*/
}
