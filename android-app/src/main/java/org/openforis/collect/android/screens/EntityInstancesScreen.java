package org.openforis.collect.android.screens;

import java.util.List;

import org.openforis.collect.android.R;
import org.openforis.collect.android.fields.BooleanField;
import org.openforis.collect.android.fields.CoordinateField;
import org.openforis.collect.android.fields.EntityLink;
import org.openforis.collect.android.fields.Field;
import org.openforis.collect.android.fields.SummaryList;
import org.openforis.collect.android.fields.SummaryTable;
import org.openforis.collect.android.fields.TaxonField;
import org.openforis.collect.android.fields.UIElement;
import org.openforis.collect.android.management.ApplicationManager;
import org.openforis.collect.android.management.BaseActivity;
import org.openforis.collect.android.messages.AlertMessage;
import org.openforis.collect.android.misc.RunnableHandler;
import org.openforis.collect.android.misc.ViewBacktrack;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.model.Entity;
import org.openforis.idm.model.EntityBuilder;
import org.openforis.idm.model.Node;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class EntityInstancesScreen extends BaseActivity implements OnClickListener {
	
	private static final String TAG = "EntityInstancesScreen";

	private ScrollView sv;			
    private LinearLayout ll;
	
	private Intent startingIntent;
	private String parentFormScreenId;
	private String breadcrumb;
	private String screenTitle;
	private int intentType;
	//private int fieldsNo;
	private int idmlId;
	//public int currInstanceNo;
	
	public Entity parentEntity;
	public Entity parentEntitySingleAttribute;
	public Entity parentEntityMultipleAttribute;
	//public PhotoField currentPictureField;
	//public CoordinateField currentCoordinateField;
	//private String photoPath;
	//private String latitude;
	//private String longitude;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	Log.i(getResources().getString(R.string.app_name),TAG+":onCreate");
    		
        	ApplicationManager.formScreenActivityList.add(this);
        	
    		this.startingIntent = getIntent();
    		this.breadcrumb = this.startingIntent.getStringExtra(getResources().getString(R.string.breadcrumb));
    		this.screenTitle = this.startingIntent.getStringExtra(getResources().getString(R.string.screenTitle));
    		this.intentType = this.startingIntent.getIntExtra(getResources().getString(R.string.intentType),-1);
    		this.idmlId = this.startingIntent.getIntExtra(getResources().getString(R.string.idmlId),-1);
    		//this.currInstanceNo = this.startingIntent.getIntExtra(getResources().getString(R.string.instanceNo),-1);
    		//this.numberOfInstances = this.startingIntent.getIntExtra(getResources().getString(R.string.numberOfInstances),-1);
    		this.parentFormScreenId = this.startingIntent.getStringExtra(getResources().getString(R.string.parentFormScreenId));;
        } catch (Exception e){
    		RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onCreate",
    				Environment.getExternalStorageDirectory().toString()
    				+getResources().getString(R.string.logs_folder)
    				+getResources().getString(R.string.logs_file_name)
    				+System.currentTimeMillis()
    				+getResources().getString(R.string.log_file_extension));
		}
	}
	
    @Override
	public void onResume()
	{
		super.onResume();
		Log.i(getResources().getString(R.string.app_name),TAG+":onResume");
		long startTime = System.currentTimeMillis();
		try{
			//EntityInstancesScreen.this.parentEntitySingleAttribute = EntityInstancesScreen.this.findParentEntity(EntityInstancesScreen.this.getFormScreenId());
			//EntityInstancesScreen.this.parentEntityMultipleAttribute = EntityInstancesScreen.this.findParentEntity(EntityInstancesScreen.this.parentFormScreenId);
	
			EntityInstancesScreen.this.sv = new ScrollView(EntityInstancesScreen.this);
			EntityInstancesScreen.this.ll = new LinearLayout(EntityInstancesScreen.this);
			EntityInstancesScreen.this.ll.setOrientation(android.widget.LinearLayout.VERTICAL);
			EntityInstancesScreen.this.sv.addView(ll);
	
			if (!EntityInstancesScreen.this.breadcrumb.equals("")){				
				TextView breadcrumb = new TextView(EntityInstancesScreen.this);
				if (EntityInstancesScreen.this.intentType != getResources().getInteger(R.integer.singleEntityIntent)){
					if (EntityInstancesScreen.this.intentType == getResources().getInteger(R.integer.multipleEntityIntent)){
						breadcrumb.setText(EntityInstancesScreen.this.breadcrumb/*.substring(0, EntityInstancesScreen.this.breadcrumb.lastIndexOf(" "))/*+" "+(EntityInstancesScreen.this.currInstanceNo+1)*/);	
					} else{
						breadcrumb.setText(EntityInstancesScreen.this.breadcrumb);//+" "+(EntityInstancesScreen.this.currInstanceNo+1));	
					}
				}
				else
					breadcrumb.setText(EntityInstancesScreen.this.breadcrumb);
	    		breadcrumb.setTextSize(getResources().getInteger(R.integer.breadcrumbFontSize));
	    		breadcrumb.setSingleLine();
	    		HorizontalScrollView scroller = new HorizontalScrollView(EntityInstancesScreen.this);
	    		scroller.addView(breadcrumb);
	    		EntityInstancesScreen.this.ll.addView(scroller);
	    		//FormScreen.this.ll.addView(breadcrumb);
	    		EntityInstancesScreen.this.ll.addView(ApplicationManager.getDividerLine(this));
	    		
	    		TextView screenTitle = new TextView(EntityInstancesScreen.this);
	    		screenTitle.setText(EntityInstancesScreen.this.screenTitle);
	    		screenTitle.setTextSize(getResources().getInteger(R.integer.screenTitleFontSize));
	    		EntityInstancesScreen.this.ll.addView(screenTitle);
	    		EntityInstancesScreen.this.ll.addView(ApplicationManager.getDividerLine(this));
			}
			
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(EntityInstancesScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.idmlId), -1));
			Log.e("nodeDef",EntityInstancesScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.idmlId), -1)+"=="+nodeDef.getName());
			//EntityInstancesScreen.this.parentEntitySingleAttribute = EntityInstancesScreen.this.findParentEntity(String.valueOf(EntityInstancesScreen.this.startingIntent.getIntExtra(getResources().getString(R.string.idmlId), -1)));
			Log.e("call","=="+this.getFormScreenId());
			Log.e("screenId","=="+this.getFormScreenId());
			
			EntityInstancesScreen.this.parentEntitySingleAttribute = EntityInstancesScreen.this.findParentEntity(this.getFormScreenId());
			if (EntityInstancesScreen.this.parentEntitySingleAttribute==null){
				EntityInstancesScreen.this.parentEntitySingleAttribute = EntityInstancesScreen.this.findParentEntity2(this.getFormScreenId());
			}
			if (ApplicationManager.currentRecord.getRootEntity().getId()!=nodeDef.getId()){
				try{
					Node<?> foundNode = EntityInstancesScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
					
					if (foundNode==null){
						EntityBuilder.addEntity(EntityInstancesScreen.this.parentEntitySingleAttribute, ApplicationManager.getSurvey().getSchema().getDefinitionById(nodeDef.getId()).getName(), 0);
					}
				} catch (IllegalArgumentException e){
					//Log.e("illegalargumentexception","parenEntity=="+EntityInstancesScreen.this.parentEntitySingleAttribute.getParent());
					EntityInstancesScreen.this.parentEntitySingleAttribute = EntityInstancesScreen.this.parentEntitySingleAttribute.getParent();
					Node<?> foundNode = EntityInstancesScreen.this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
					if (foundNode==null){
						EntityBuilder.addEntity(EntityInstancesScreen.this.parentEntitySingleAttribute, ApplicationManager.getSurvey().getSchema().getDefinitionById(nodeDef.getId()).getName(), 0);
					}
					//e.printStackTrace();
				} catch (NullPointerException e){
					e.printStackTrace();
				}
			}
			Entity tempEntity = EntityInstancesScreen.this.parentEntitySingleAttribute;
			boolean error = false;
			try {
				Log.e("parentEntitySingleAttribute11",nodeDef.getName()+"=="+EntityInstancesScreen.this.parentEntitySingleAttribute.getName()+"|"+EntityInstancesScreen.this.parentEntitySingleAttribute.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(nodeDef.getId()).getName(), 0));
				EntityInstancesScreen.this.parentEntitySingleAttribute = (Entity) EntityInstancesScreen.this.parentEntitySingleAttribute.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(nodeDef.getId()).getName(), 0);
				Log.e("parentEntitySingleAttribute12",nodeDef.getName()+"=="+EntityInstancesScreen.this.parentEntitySingleAttribute.getName());
				error = false;
			} catch (IllegalArgumentException e){
				error = true;
				Log.e("parentEntitySingleAttribute21",nodeDef.getName()+"=="+EntityInstancesScreen.this.parentEntitySingleAttribute.getName());
				EntityInstancesScreen.this.parentEntitySingleAttribute = EntityInstancesScreen.this.parentEntitySingleAttribute.getParent();
				Log.e("parentEntitySingleAttribute22",nodeDef.getName()+"=="+EntityInstancesScreen.this.parentEntitySingleAttribute.getName());
			} catch (ClassCastException e){
				error = true;
				Log.e("parentEntitySingleAttribute31",nodeDef.getName()+"=="+EntityInstancesScreen.this.parentEntitySingleAttribute.getName());
				EntityInstancesScreen.this.parentEntitySingleAttribute = EntityInstancesScreen.this.parentEntitySingleAttribute.getParent();
				Log.e("parentEntitySingleAttribute32",nodeDef.getName()+"=="+EntityInstancesScreen.this.parentEntitySingleAttribute.getName());
			} catch (NullPointerException e){
				error = true;
				e.printStackTrace();				
			}
			Log.e("eRROR==","=="+error);
			Log.e("tempEntity==null","=="+(tempEntity==null));
			if (tempEntity!=null){
				Log.e("tempEntity.getname()","=="+(tempEntity.getName()));	
			}
			if (!error){
				EntityInstancesScreen.this.parentEntitySingleAttribute = tempEntity;
			}
	/*		try{
				Log.e("count","=="+EntityInstancesScreen.this.parentEntitySingleAttribute.getCount(entityDef.getName()));
				Log.e("nodeDefEntityInstance",nodeDef.getId()+"=="+nodeDef.getName());
				Log.e("parentEntitySingleAttributeEntityInstance","==´"+parentEntitySingleAttribute.getName());					
			} catch (Exception e){
//				e.printStackTrace();
			}*/
			EntityDefinition entityDef = (EntityDefinition)nodeDef;

			for (int e=0;e<EntityInstancesScreen.this.parentEntitySingleAttribute.getCount(entityDef.getName());e++){
				SummaryList summaryListView = new SummaryList(EntityInstancesScreen.this, entityDef, 45, EntityInstancesScreen.this,e);
				summaryListView.setOnClickListener(EntityInstancesScreen.this);
				summaryListView.setId(nodeDef.getId());
				EntityInstancesScreen.this.ll.addView(summaryListView);
			}
			
			//if (this.intentType==getResources().getInteger(R.integer.multipleEntityIntent)){ 				
			//if (ApplicationManager.currentRecord.getRootEntity().getId()!=this.idmlId){
			if (nodeDef.isMultiple()){
				this.ll.addView(arrangeButtonsInLine(new Button(this), getResources().getString(R.string.addInstanceButton), this, true));				
			}/* else {
				SummaryList temp = new SummaryList(EntityInstancesScreen.this, entityDef, 45, EntityInstancesScreen.this,0);
				ViewBacktrack viewBacktrack = new ViewBacktrack(temp,EntityInstancesScreen.this.getFormScreenId(temp.getInstanceNo()));
				ApplicationManager.selectedViewsBacktrackList.add(viewBacktrack);			
				this.startActivity(this.prepareIntentForNewScreen(temp));
			}*/
			//}	
			//}	
			setContentView(EntityInstancesScreen.this.sv);
				
			int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);		
			changeBackgroundColor(backgroundColor);

	    	/*sv.post(new Runnable() {
	    	    @Override
	    	    public void run() {
	    	    	if (ApplicationManager.selectedView!=null){
	    	    		if (ApplicationManager.isToBeScrolled){
	    	    			sv.scrollTo(0, ApplicationManager.selectedView.getTop());
	    	            	ApplicationManager.isToBeScrolled = false;
	    	    		}
	    	    } 
	    	    } 	
	    	});*/
	
			
		} catch (Exception e){
			RunnableHandler.reportException(e,getResources().getString(R.string.app_name),TAG+":onResume",
					Environment.getExternalStorageDirectory().toString()
					+getResources().getString(R.string.logs_folder)
					+getResources().getString(R.string.logs_file_name)
					+System.currentTimeMillis()
					+getResources().getString(R.string.log_file_extension));
		}
		Log.e("onRESUME time","=="+(System.currentTimeMillis()-startTime));
	}
    
    @Override
    public void onPause(){    
		Log.i(getResources().getString(R.string.app_name),TAG+":onPause");
		/*if (ApplicationManager.selectedView instanceof SummaryTable){
			SummaryTable temp = (SummaryTable)ApplicationManager.selectedView;
			if (this.idmlId==temp.nodeDefinition.getId()){
				ApplicationManager.isToBeScrolled = true;	
			}
		} else if (ApplicationManager.selectedView instanceof SummaryList){
			SummaryList temp = (SummaryList)ApplicationManager.selectedView;
			if (this.idmlId==temp.nodeDefinition.getId()){
				ApplicationManager.isToBeScrolled = true;	
			}
		}*/
		super.onPause();
    }

	@Override
	public void onClick(View arg0) {
		/*if (arg0 instanceof Button){
			Button btn = (Button)arg0;
			if (btn.getId()==getResources().getInteger(R.integer.leftButtonMultipleAttribute)){
				refreshMultipleAttributeScreen(true);
			} else if (btn.getId()==getResources().getInteger(R.integer.rightButtonMultipleAttribute)){
				refreshMultipleAttributeScreen(false);				
			} else if (btn.getId()==getResources().getInteger(R.integer.leftButtonMultipleEntity)){
				refreshEntityScreen(true);
			} else if (btn.getId()==getResources().getInteger(R.integer.rightButtonMultipleEntity)){
				refreshEntityScreen(false);
			}
		} else if (arg0 instanceof TextView){
			TextView tv = (TextView)arg0;
			Object parentView = arg0.getParent().getParent().getParent().getParent();
			if (parentView instanceof SummaryList){
				SummaryList temp = (SummaryList)arg0.getParent().getParent().getParent().getParent();
				ApplicationManager.selectedView = temp;
				ApplicationManager.isToBeScrolled = false;
				this.startActivity(this.prepareIntentForNewScreen(temp));				
			} else if (parentView instanceof SummaryTable){
				SummaryTable temp = (SummaryTable)parentView;
				ApplicationManager.selectedView = temp;
				ApplicationManager.isToBeScrolled = false;
				this.startActivity(this.prepareIntentForMultipleField(temp, tv.getId(), temp.getValues()));
			} else if (parentView instanceof EntityLink){
				EntityLink temp = (EntityLink)parentView;
				ApplicationManager.selectedView = temp;
				ApplicationManager.isToBeScrolled = false;
				this.startActivity(this.prepareIntentForEntityInstancesList(temp));
			}
			
		}*/
		//Log.e("arg0","=="+arg0.getClass());
		if (arg0 instanceof Button){
			Button btn = (Button)arg0;
			//Log.e("ADDING",btn.getId()+"ENTITY"+getResources().getInteger(R.integer.addButtonMultipleEntity));
			if (btn.getId()==getResources().getInteger(R.integer.addButtonMultipleEntity)){
				//Log.e("ADDING","ENTITY");
				addNewEntity();
			}
		} else if (arg0 instanceof TextView){
			Object parentView = arg0.getParent().getParent().getParent().getParent();
			//Log.e("parentView","=="+parentView.getClass());
			if (parentView instanceof SummaryList){
				SummaryList temp = (SummaryList)arg0.getParent().getParent().getParent().getParent();
				ViewBacktrack viewBacktrack = new ViewBacktrack(temp,EntityInstancesScreen.this.getFormScreenId(temp.getInstanceNo()));
				ApplicationManager.selectedViewsBacktrackList.add(viewBacktrack);
				//Log.e("clickedON",temp.getInstanceNo()+"=="+EntityInstancesScreen.this.getFormScreenId(temp.getInstanceNo()));
				//ApplicationManager.isToBeScrolled = false;				
				this.startActivity(this.prepareIntentForNewScreen(temp));
			}	
		}
	}
	
	private void addNewEntity(){
		NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+0, -1)).getParentDefinition();
		//Log.e("nodeDefEntityInstancesScreen",nodeDef.isMultiple()+"=="+nodeDef.getName());
		if (!nodeDef.isMultiple()){	
			AlertMessage.createPositiveNegativeDialog(EntityInstancesScreen.this, true, null,
					getResources().getString(R.string.addNewEntityTitle), 
					getResources().getString(R.string.addNewEntityMessage),
						getResources().getString(R.string.yes),
						getResources().getString(R.string.no),
			    		new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								/*Node<?> foundNode = EntityInstancesScreen.this.parentEntitySingleAttribute.getParent().get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo+1);
								while (foundNode!=null){
									EntityInstancesScreen.this.currInstanceNo++;
									foundNode = EntityInstancesScreen.this.parentEntitySingleAttribute.getParent().get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo+1);
								}
								EntityInstancesScreen.this.currInstanceNo++;	
								refreshEntityScreenFields();*/
								
							}
						},
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
							}
						},
						null).show();				
		} else {
			AlertMessage.createPositiveNegativeDialog(EntityInstancesScreen.this, true, null,
					getResources().getString(R.string.addNewEntityTitle), 
					getResources().getString(R.string.addNewEntityMessage),
						getResources().getString(R.string.yes),
						getResources().getString(R.string.no),
			    		new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								/*Node<?> foundNode = EntityInstancesScreen.this.parentEntityMultipleAttribute.getParent().get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo+1);
								while (foundNode!=null){
									EntityInstancesScreen.this.currInstanceNo++;
									foundNode = EntityInstancesScreen.this.parentEntityMultipleAttribute.getParent().get(FormScreen.this.parentEntitySingleAttribute.getName(), FormScreen.this.currInstanceNo+1);
								}
								EntityInstancesScreen.this.currInstanceNo++;	
								refreshEntityScreenFields();*/
								/*NodeDefinition currentScreenNodeDef = ApplicationManager.getSurvey().getSchema().getDefinitionById(this.idmlId);
								if (currentScreenNodeDef.getMaxCount()!=null){
									if (currentScreenNodeDef.getMaxCount()<=this.currInstanceNo){			
										EntityInstancesScreen.this.currInstanceNo--;
										AlertMessage.createPositiveDialog(FormScreen.this, true, null,
												getResources().getString(R.string.maxCountTitle), 
												getResources().getString(R.string.maxCountMessage),
													getResources().getString(R.string.okay),
										    		new DialogInterface.OnClickListener() {
														@Override
														public void onClick(DialogInterface dialog, int which) {
															
														}
													},
													null).show();
										return;
									}	
								}*/
								
								/*View firstView = this.ll.getChildAt(0);
								if (firstView instanceof HorizontalScrollView){
									ViewGroup scrollbarView = ((ViewGroup)this.ll.getChildAt(0));
									TextView breadcrumb = (TextView)scrollbarView.getChildAt(0);
									breadcrumb.setText(this.breadcrumb.substring(0, this.breadcrumb.lastIndexOf(" "))+" "+(this.currInstanceNo+1));
									breadcrumb.setTextSize(getResources().getInteger(R.integer.breadcrumbFontSize));
									breadcrumb.setSingleLine();
								}
								this.breadcrumb = this.breadcrumb.substring(0, this.breadcrumb.lastIndexOf(" "))+" "+(this.currInstanceNo+1);

								TextView screenTitle = new TextView(FormScreen.this);
								screenTitle.setText(FormScreen.this.screenTitle);
								screenTitle.setTextSize(getResources().getInteger(R.integer.screenTitleFontSize));
								FormScreen.this.ll.addView(screenTitle);
								
								this.ll.removeAllViews();
								this.ll.addView(firstView,0);
								this.ll.addView(screenTitle,1);*/
								
								//refreshing values of fields in the entity
								
								Entity parentEntity = EntityInstancesScreen.this.findParentEntity(EntityInstancesScreen.this.getFormScreenId());
								
								//Log.e("parentEntity0",parentEntity.getName()+"=="+EntityInstancesScreen.this.getFormScreenId());
								int currentInstanceNo = 0;
								while (parentEntity!=null){
									currentInstanceNo++;
									//Log.e("parentEntity"+currentInstanceNo,parentEntity.getName()+"=="+EntityInstancesScreen.this.getFormScreenId());
									parentEntity = EntityInstancesScreen.this.findParentEntity(EntityInstancesScreen.this.getFormScreenId(currentInstanceNo));
								}
								if (parentEntity==null){
									//Log.e("orginalpathTOSearchForParent","=="+EntityInstancesScreen.this.getFormScreenId());
									String path = EntityInstancesScreen.this.getFormScreenId().substring(0,EntityInstancesScreen.this.getFormScreenId().lastIndexOf(getResources().getString(R.string.valuesSeparator2)));
									//Log.e("pathTOSearchForParent","=="+path);
									parentEntity = EntityInstancesScreen.this.findParentEntity(path);
									try{
										EntityBuilder.addEntity(parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(EntityInstancesScreen.this.idmlId).getName());
										//Log.e("entity","added without exception");
									} catch (IllegalArgumentException e){
										//Log.e("entity","added in exception");
										parentEntity = parentEntity.getParent();
										EntityBuilder.addEntity(parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(EntityInstancesScreen.this.idmlId).getName());
									}
									//Log.e("foundParentEntity","=="+parentEntity.getName());
									parentEntity = EntityInstancesScreen.this.findParentEntity(EntityInstancesScreen.this.getFormScreenId());
									//Log.e("newParentEntity","=="+parentEntity.getName());
								}
								
//								Log.e("refreshEntityScreen","this.getFormScreenId()=="+this.getFormScreenId());
//								Log.e("refreshEntityScreen","this.parentFormScreenId=="+this.parentFormScreenId);
								EntityInstancesScreen.this.parentEntitySingleAttribute = EntityInstancesScreen.this.findParentEntity(EntityInstancesScreen.this.getFormScreenId());
								EntityInstancesScreen.this.parentEntityMultipleAttribute = EntityInstancesScreen.this.findParentEntity(EntityInstancesScreen.this.parentFormScreenId);
								EntityInstancesScreen.this.onResume();
							}
						},
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
							}
						},
						null).show();
		}
	}
	
	private Intent prepareIntentForNewScreen(SummaryList summaryList){
		Intent intent = new Intent(this,FormScreen.class);
		if (!this.breadcrumb.equals("")){
			String title = "";
			String entityTitle = "";
			if (summaryList.getEntityDefinition().isMultiple()){
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle()+" "+0/*(this.currInstanceNo+1)*/;
				entityTitle = summaryList.getTitle()/*+" "+(this.currInstanceNo+1)*/;
			} else {
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle();
				entityTitle = summaryList.getTitle();
			}
			intent.putExtra(getResources().getString(R.string.breadcrumb), title);
			intent.putExtra(getResources().getString(R.string.screenTitle), entityTitle);
		} else {
			intent.putExtra(getResources().getString(R.string.breadcrumb), summaryList.getTitle());
			intent.putExtra(getResources().getString(R.string.screenTitle), summaryList.getTitle());
		}
		
		if (summaryList.getEntityDefinition().isMultiple()){
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.multipleEntityIntent));	
		} else {
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.singleEntityIntent));
		}		
		intent.putExtra(getResources().getString(R.string.idmlId), summaryList.getId());
		intent.putExtra(getResources().getString(R.string.instanceNo), summaryList.getInstanceNo());
		Log.e("1summaryList.getId()","=="+summaryList.getId());
		Log.e("1summaryList.getInstanceNo()","=="+summaryList.getInstanceNo());
		Log.e("1this.getFormScreenId()","=="+this.getFormScreenId(summaryList.getInstanceNo()));
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), this.getFormScreenId(summaryList.getInstanceNo()));
        List<NodeDefinition> entityAttributes = summaryList.getEntityDefinition().getChildDefinitions();
        int counter = 0;
        for (NodeDefinition formField : entityAttributes){
			intent.putExtra(getResources().getString(R.string.attributeId)+counter, formField.getId());
			counter++;
        }
		return intent;
	}
	
	private Intent prepareIntentForEntityInstancesList(EntityLink entityLink){
		Intent intent = new Intent(this,EntityInstancesScreen.class);
		Log.e("========","prepareIntentForEntityInstancesList");
		/*if (!this.breadcrumb.equals("")){
			String title = "";
			String entityTitle = "";
			if (summaryList.getEntityDefinition().isMultiple()){
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle()+" "+(this.currInstanceNo+1);
				entityTitle = summaryList.getTitle()+" "+(this.currInstanceNo+1);
			} else {
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle();
				entityTitle = summaryList.getTitle();
			}
			intent.putExtra(getResources().getString(R.string.breadcrumb), title);
			intent.putExtra(getResources().getString(R.string.screenTitle), entityTitle);
		} else {
			intent.putExtra(getResources().getString(R.string.breadcrumb), summaryList.getTitle());
			intent.putExtra(getResources().getString(R.string.screenTitle), summaryList.getTitle());
		}
		
		if (summaryList.getEntityDefinition().isMultiple()){
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.multipleEntityIntent));	
		} else {
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.singleEntityIntent));
		}		
		intent.putExtra(getResources().getString(R.string.idmlId), summaryList.getId());
		intent.putExtra(getResources().getString(R.string.instanceNo), summaryList.getInstanceNo());
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), this.getFormScreenId());
        List<NodeDefinition> entityAttributes = summaryList.getEntityDefinition().getChildDefinitions();
        int counter = 0;
        for (NodeDefinition formField : entityAttributes){
			intent.putExtra(getResources().getString(R.string.attributeId)+counter, formField.getId());
			counter++;
        }*/
		return intent;
	}
	
	/*private Intent prepareIntentForNewScreen(SummaryList summaryList){
		Intent intent = new Intent(this,EntityInstancesScreen.class);
		if (!this.breadcrumb.equals("")){
			String title = "";
			String entityTitle = "";
			if (summaryList.getEntityDefinition().isMultiple()){
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle()+" "+(this.currInstanceNo+1);
				entityTitle = summaryList.getTitle();//+" "+(this.currInstanceNo+1);
			} else {
				title = this.breadcrumb+getResources().getString(R.string.breadcrumbSeparator)+summaryList.getTitle();
				entityTitle = summaryList.getTitle();
			}
			intent.putExtra(getResources().getString(R.string.breadcrumb), title);
			intent.putExtra(getResources().getString(R.string.screenTitle), entityTitle);
		} else {
			intent.putExtra(getResources().getString(R.string.breadcrumb), summaryList.getTitle());
			intent.putExtra(getResources().getString(R.string.screenTitle), summaryList.getTitle());
		}
		
		if (summaryList.getEntityDefinition().isMultiple()){
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.multipleEntityIntent));	
		} else {
			intent.putExtra(getResources().getString(R.string.intentType), getResources().getInteger(R.integer.singleEntityIntent));
		}		
		intent.putExtra(getResources().getString(R.string.idmlId), summaryList.getId());
		intent.putExtra(getResources().getString(R.string.instanceNo), summaryList.getInstanceNo());
		intent.putExtra(getResources().getString(R.string.parentFormScreenId), this.getFormScreenId());
        List<NodeDefinition> entityAttributes = summaryList.getEntityDefinition().getChildDefinitions();
        int counter = 0;
        for (NodeDefinition formField : entityAttributes){
			intent.putExtra(getResources().getString(R.string.attributeId)+counter, formField.getId());
			counter++;
        }
		return intent;
	}*/
	
    private void changeBackgroundColor(int backgroundColor){
		getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));

		boolean hasBreadcrumb = !this.breadcrumb.equals("");
		if (hasBreadcrumb){
			ViewGroup scrollbarViews = ((ViewGroup)this.ll.getChildAt(0));
			TextView breadcrumb = (TextView)scrollbarViews.getChildAt(0);
			breadcrumb.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);	
		}
		
		boolean hasTitle = !this.screenTitle.equals("");
		if (hasTitle){
			View dividerLine = (View)this.ll.getChildAt(1);
			dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
			TextView screenTitle = (TextView)this.ll.getChildAt(2);
			screenTitle.setTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
			dividerLine = (View)this.ll.getChildAt(3);
			dividerLine.setBackgroundColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);			
		}
		
		int viewsNo = this.ll.getChildCount();
		int start = (hasBreadcrumb)?1:0;
		for (int i=start;i<viewsNo;i++){
			View tempView = this.ll.getChildAt(i);
			if (tempView instanceof Field){
				Field field = (Field)tempView;
				field.setLabelTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
				if (tempView instanceof BooleanField){
					BooleanField tempBooleanField = (BooleanField)tempView;
					tempBooleanField.setChoiceLabelsTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
				} else if (tempView instanceof TaxonField){
					TaxonField tempTaxonField = (TaxonField)tempView;
					tempTaxonField.setFieldsLabelsTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
				} else if (tempView instanceof CoordinateField){
					CoordinateField tempCoordinateField = (CoordinateField)tempView;
					tempCoordinateField.setCoordinateLabelTextColor((backgroundColor!=Color.WHITE)?Color.WHITE:Color.BLACK);
				}
			}
			else if (tempView instanceof UIElement){
				if (tempView instanceof SummaryList){
					SummaryList tempSummaryList = (SummaryList)tempView;
					tempSummaryList.changeBackgroundColor(backgroundColor);
				} else if (tempView instanceof SummaryTable){
					SummaryTable tempSummaryTable = (SummaryTable)tempView;
					tempSummaryTable.changeBackgroundColor(backgroundColor);
				}
			}
		}
    }
    
    /*private RelativeLayout arrangeButtonsInLine(Button btnLeft, String btnLeftLabel, Button btnRight, String btnRightLabel, OnClickListener listener, boolean isForEntity){
		RelativeLayout relativeButtonsLayout = new RelativeLayout(this);
	    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    relativeButtonsLayout.setLayoutParams(lp);
		btnLeft.setText(btnLeftLabel);
		btnRight.setText(btnRightLabel);
		
		btnLeft.setOnClickListener(listener);
		btnRight.setOnClickListener(listener);
		
		RelativeLayout.LayoutParams lpBtnLeft = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpBtnLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		btnLeft.setLayoutParams(lpBtnLeft);
		relativeButtonsLayout.addView(btnLeft);
		
		RelativeLayout.LayoutParams lpBtnRight = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		lpBtnRight.addRule(RelativeLayout.RIGHT_OF,btnLeft.getId());
		lpBtnRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		btnRight.setLayoutParams(lpBtnRight);
		relativeButtonsLayout.addView(btnRight);
		
		if (!isForEntity){
			btnLeft.setId(getResources().getInteger(R.integer.leftButtonMultipleAttribute));
			btnRight.setId(getResources().getInteger(R.integer.rightButtonMultipleAttribute));	
		} else {
			btnLeft.setId(getResources().getInteger(R.integer.leftButtonMultipleEntity));
			btnRight.setId(getResources().getInteger(R.integer.rightButtonMultipleEntity));
		}
		
		return relativeButtonsLayout;
    }*/
    
    /*public String getFormScreenId(){
    	if (this.parentFormScreenId.equals("")){
    		return this.idmlId+getResources().getString(R.string.valuesSeparator1)+this.currInstanceNo;
    	} else 
    		return this.parentFormScreenId+getResources().getString(R.string.valuesSeparator2)+this.idmlId+getResources().getString(R.string.valuesSeparator1)+this.currInstanceNo;
    }*/
	
	private Entity findParentEntity(String path){
		Log.e("pathENTITYINSTANCE","=="+path);
		Entity parentEntity = ApplicationManager.currentRecord.getRootEntity();
		String screenPath = path;
		String[] entityPath = screenPath.split(getResources().getString(R.string.valuesSeparator2));
		try{
			Log.e("entityPath.length","=="+entityPath.length);
			for (int i=0;i<entityPath.length-1;i++){
				Log.e("i"+i,"=="+entityPath[i]);
			}
			for (int m=1;m<entityPath.length;m++){
				String[] instancePath = entityPath[m].split(getResources().getString(R.string.valuesSeparator1));				
				int id = Integer.valueOf(instancePath[0]);
				int instanceNo = Integer.valueOf(instancePath[1]);
				Log.e("id"+id,"instanceNo"+instanceNo);
				
				/*try{
					parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
				} catch (IllegalArgumentException e){
					parentEntity = parentEntity.getParent();
					parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
				}*/
				if (parentEntity!=null)
					Log.e("1returned parententity"+(m-1),"=="+parentEntity.getName());
				else
					Log.e("2returned parententity"+(m-1),"==NULL");
				parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
				if (parentEntity!=null)
					Log.e("3returned parententity"+m,"=="+parentEntity.getName());
				else
					Log.e("4returned parententity"+m,"==NULL");
			}			
		} catch (ClassCastException e){
			
		} catch (IllegalArgumentException e){
			
		}
		if (parentEntity!=null)
			Log.e("returned parententity","=="+parentEntity.getName());
		else
			Log.e("returned parententity","==NULL");
		return parentEntity;
	}
	
	private Entity findParentEntity2(String path){
		Log.e("pathENTITYINSTANCE","=="+path);
		Entity parentEntity = ApplicationManager.currentRecord.getRootEntity();
		String screenPath = path;
		String[] entityPath = screenPath.split(getResources().getString(R.string.valuesSeparator2));
		try{
			Log.e("entityPath.length","=="+entityPath.length);
			for (int i=0;i<entityPath.length-1;i++){
				Log.e("i"+i,"=="+entityPath[i]);
			}
			for (int m=0;m<entityPath.length-1;m++){
				String[] instancePath = entityPath[m].split(getResources().getString(R.string.valuesSeparator1));				
				int id = Integer.valueOf(instancePath[0]);
				int instanceNo = Integer.valueOf(instancePath[1]);
				Log.e("id"+id,"instanceNo"+instanceNo);
				
				/*try{
					parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
				} catch (IllegalArgumentException e){
					parentEntity = parentEntity.getParent();
					parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
				}*/
				if (parentEntity!=null)
					Log.e("5returned parententity"+(m-1),"=="+parentEntity.getName());
				else
					Log.e("6returned parententity"+(m-1),"==NULL");
				parentEntity = (Entity) parentEntity.get(ApplicationManager.getSurvey().getSchema().getDefinitionById(id).getName(), instanceNo);
				if (parentEntity!=null)
					Log.e("7returned parententity"+m,"=="+parentEntity.getName());
				else
					Log.e("8returned parententity"+m,"==NULL");
			}			
		} catch (ClassCastException e){
			
		} catch (IllegalArgumentException e){
			
		}
		if (parentEntity!=null)
			Log.e("9returned parententity","=="+parentEntity.getName());
		else
			Log.e("10returned parententity","==NULL");
		return parentEntity;
	}
	
	/*private void refreshEntityScreen(boolean isPreviousEntity){
		//setting current instance number of the entity
		if (isPreviousEntity){//scroll left to previous entity
			if (this.currInstanceNo>0){
				this.currInstanceNo--;
			} else {
				return;
			}
		} else {//scroll right to next (or new) entity
			this.currInstanceNo++;
		}
		
		NodeDefinition currentScreenNodeDef = ApplicationManager.getSurvey().getSchema().getDefinitionById(this.idmlId);
		if (currentScreenNodeDef.getMaxCount()!=null){
			if (currentScreenNodeDef.getMaxCount()<=this.currInstanceNo){			
				this.currInstanceNo--;
				AlertMessage.createPositiveDialog(EntityInstancesScreen.this, true, null,
						getResources().getString(R.string.maxCountTitle), 
						getResources().getString(R.string.maxCountMessage),
							getResources().getString(R.string.okay),
				    		new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							},
							null).show();
				return;
			}	
		}
		
		View firstView = this.ll.getChildAt(0);
		if (firstView instanceof TextView){
			TextView screenTitle = (TextView)firstView;
			screenTitle.setText(this.breadcrumb.substring(0, this.breadcrumb.lastIndexOf(" "))+" "+(this.currInstanceNo+1));
		}
		this.breadcrumb = this.breadcrumb.substring(0, this.breadcrumb.lastIndexOf(" "))+" "+(this.currInstanceNo+1);
		
		this.ll.removeAllViews();
		this.ll.addView(firstView,0);
		
		//refreshing values of fields in the entity 
		Entity parentEntity = this.findParentEntity(this.getFormScreenId());
		if (parentEntity==null){
			String path = this.getFormScreenId().substring(0,this.getFormScreenId().lastIndexOf(getResources().getString(R.string.valuesSeparator2)));
			parentEntity = this.findParentEntity(path);
			EntityBuilder.addEntity(parentEntity, ApplicationManager.getSurvey().getSchema().getDefinitionById(this.idmlId).getName());
			parentEntity = this.findParentEntity(this.getFormScreenId());
		}
		
//		Log.e("refreshEntityScreen","this.getFormScreenId()=="+this.getFormScreenId());
//		Log.e("refreshEntityScreen","this.parentFormScreenId=="+this.parentFormScreenId);
		this.parentEntitySingleAttribute = this.findParentEntity(this.getFormScreenId());
		this.parentEntityMultipleAttribute = this.findParentEntity(this.parentFormScreenId);
		/*if (parentEntitySingleAttribute!=null)
			Log.e("refreshEntityScreen","parentEntitySingleAttribute=="+parentEntitySingleAttribute.getName()+"=="+parentEntitySingleAttribute.getIndex());
		if (parentEntityMultipleAttribute!=null)
			Log.e("refreshEntityScreen","parentEntityMultipleAttribute=="+parentEntityMultipleAttribute.getName()+"=="+parentEntityMultipleAttribute.getIndex());
		
		String loadedValue = "";
		ArrayList<String> tableColHeaders = new ArrayList<String>();
		tableColHeaders.add("Value");
		for (int i=0;i<this.fieldsNo;i++){
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+i, -1));
			if (nodeDef instanceof EntityDefinition){
				if (ApplicationManager.currentRecord.getRootEntity().getId()!=nodeDef.getId()){
    				Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
    				if (foundNode==null){
    					EntityBuilder.addEntity(this.parentEntitySingleAttribute, ApplicationManager.getSurvey().getSchema().getDefinitionById(nodeDef.getId()).getName());
    				}
				}
				
				EntityDefinition entityDef = (EntityDefinition)nodeDef;
				for (int e=0;e<this.parentEntitySingleAttribute.getCount(entityDef.getName());e++){
					SummaryList summaryListView = new SummaryList(this, entityDef, calcNoOfCharsFitInOneLine(),
    						this,e);
    				summaryListView.setOnClickListener(this);
    				summaryListView.setId(nodeDef.getId());
    				this.ll.addView(summaryListView);	
				}			  				
			}else {					
				if (nodeDef instanceof TextAttributeDefinition){
    				loadedValue = "";	    				

    				if (((TextAttributeDefinition) nodeDef).getType().toString().toLowerCase().equals("short")){
	    				if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					TextValue textValue = (TextValue)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (textValue!=null)
		    						loadedValue = textValue.getValue();	    				
		    				}
	        				final TextField textField= new TextField(this, nodeDef);
	        				textField.setOnClickListener(this);
	        				textField.setId(nodeDef.getId());
	        				//textField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				textField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	textField.setValue(0, s.toString(), EntityInstancesScreen.this.getFormScreenId(),true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(textField.getId(), textField);
	        				this.ll.addView(textField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
		    				if (foundNode!=null){
		    					TextValue textValue = (TextValue)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
		    					if (textValue!=null)
		    						loadedValue = textValue.getValue();	    				
		    				}
	        				final TextField textField= new TextField(this, nodeDef);
	        				textField.setOnClickListener(this);
	        				textField.setId(nodeDef.getId());
	        				//Log.e("this.parentFormScreenId",nodeDef.getName()+"=="+this.parentFormScreenId);
	        				//textField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
	        				textField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	textField.setValue(EntityInstancesScreen.this.currInstanceNo, s.toString(), EntityInstancesScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(textField.getId(), textField);
	        				this.ll.addView(textField);
	    				} else {//multiple attribute summary    			    		
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
        				}
    				} else {//memo field
    					if (!nodeDef.isMultiple()){
	    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					TextValue textValue = (TextValue)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (textValue!=null)
		    						loadedValue = textValue.getValue();	    				
		    				}
	        				final MemoField memoField= new MemoField(this, nodeDef);
	        				memoField.setOnClickListener(this);
	        				memoField.setId(nodeDef.getId());
	        				//memoField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				memoField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	memoField.setValue(0, s.toString(), EntityInstancesScreen.this.getFormScreenId(),true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(memoField.getId(), memoField);
	        				this.ll.addView(memoField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
		    				if (foundNode!=null){
		    					TextValue textValue = (TextValue)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
		    					if (textValue!=null)
		    						loadedValue = textValue.getValue();	    				
		    				}
	        				final MemoField memoField= new MemoField(this, nodeDef);
	        				memoField.setOnClickListener(this);
	        				memoField.setId(nodeDef.getId());
	        				//memoField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
	        				memoField.addTextChangedListener(new TextWatcher(){
	        			        public void afterTextChanged(Editable s) {        			            
	        			        	memoField.setValue(EntityInstancesScreen.this.currInstanceNo, s.toString(), EntityInstancesScreen.this.parentFormScreenId,true);
	        			        }
	        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
	        			    });
	        				ApplicationManager.putUIElement(memoField.getId(), memoField);
	        				this.ll.addView(memoField);
	    				} else {//multiple attribute summary    			    		
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
        				}
    				}
    			} else if (nodeDef instanceof NumberAttributeDefinition){
    				loadedValue = "";
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					if (((NumberAttributeDefinition) nodeDef).isInteger()){
	    						IntegerValue intValue = (IntegerValue)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (intValue!=null)
		    						loadedValue = intValue.getValue().toString();
	    					} else {
	    						RealValue realValue = (RealValue)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (realValue!=null)
		    						loadedValue = realValue.getValue().toString();
	    					}
	    				}
        				final NumberField numberField= new NumberField(this, nodeDef);
        				numberField.setOnClickListener(this);
        				numberField.setId(nodeDef.getId());
        				//numberField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
        				numberField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {        			            
        			        	numberField.setValue(0, s.toString(), EntityInstancesScreen.this.getFormScreenId(),true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(numberField.getId(), numberField);
        				this.ll.addView(numberField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					if (((NumberAttributeDefinition) nodeDef).isInteger()){
	    						IntegerValue intValue = (IntegerValue)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    						if (intValue!=null)
		    						loadedValue = intValue.getValue().toString();
	    					} else {
	    						RealValue realValue = (RealValue)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    						if (realValue!=null)
		    						loadedValue = realValue.getValue().toString();
	    					}
	    				}
        				final NumberField numberField= new NumberField(this, nodeDef);
        				numberField.setOnClickListener(this);
        				numberField.setId(nodeDef.getId());
        				//numberField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
        				numberField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {        			            
        			        	numberField.setValue(EntityInstancesScreen.this.currInstanceNo, s.toString(), EntityInstancesScreen.this.parentFormScreenId,true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(numberField.getId(), numberField);
        				this.ll.addView(numberField);
    				} else {//multiple attribute summary    			    		
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof BooleanAttributeDefinition){
    				loadedValue = "";
    				if (!nodeDef.isMultiple()){
    					
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					BooleanValue boolValue = (BooleanValue)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
        					if (boolValue!=null){
        						if (boolValue.getValue()!=null)
        							loadedValue = boolValue.getValue().toString();
        					}
	    				}
	    				
        				BooleanField boolField = null;// new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
        				if (loadedValue.equals("")){
        					boolField = new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
        				} else if (loadedValue.equals("false")) {
    						boolField = new BooleanField(this, nodeDef, false, true, getResources().getString(R.string.yes), getResources().getString(R.string.no));
    					} else {
    						boolField = new BooleanField(this, nodeDef, true, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
    					}
        				boolField.setOnClickListener(this);
        				boolField.setId(nodeDef.getId());
        				if (loadedValue.equals("")){
        					//boolField.setValue(0, null, FormScreen.this.getFormScreenId(),false);	
        				} else {
        					//boolField.setValue(0, Boolean.valueOf(loadedValue), FormScreen.this.getFormScreenId(),false);	
        				}	        				
        				ApplicationManager.putUIElement(boolField.getId(), boolField);
        				this.ll.addView(boolField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
    					if (foundNode!=null){
    						BooleanValue boolValue = (BooleanValue)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    					if (boolValue!=null){
        						if (boolValue.getValue()!=null)
        							loadedValue = boolValue.getValue().toString();
        					}
	    				}
    					//BooleanField boolField = new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
    					BooleanField boolField = null;// new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
        				if (loadedValue.equals("")){
        					boolField = new BooleanField(this, nodeDef, false, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
        				} else if (loadedValue.equals("false")) {
    						boolField = new BooleanField(this, nodeDef, false, true, getResources().getString(R.string.yes), getResources().getString(R.string.no));
    					} else {
    						boolField = new BooleanField(this, nodeDef, true, false, getResources().getString(R.string.yes), getResources().getString(R.string.no));
    					}
    					boolField.setOnClickListener(this);
    					boolField.setId(nodeDef.getId());
    					if (loadedValue.equals("")){
    						//boolField.setValue(this.currInstanceNo, null, this.parentFormScreenId,false);
    					} else {
    						//boolField.setValue(this.currInstanceNo, Boolean.valueOf(loadedValue), this.parentFormScreenId,false);
    					}
        				ApplicationManager.putUIElement(boolField.getId(), boolField);
        				this.ll.addView(boolField);
    				} else {//multiple attribute summary    			    		
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof CodeAttributeDefinition){
    				loadedValue = "";
    				CodeAttributeDefinition codeAttrDef = (CodeAttributeDefinition)nodeDef;
    				ArrayList<String> options = new ArrayList<String>();
    				ArrayList<String> codes = new ArrayList<String>();
    				options.add("");
    				codes.add("null");
    				CodeListManager codeListManager = ServiceFactory.getCodeListManager();
					CodeList list = codeAttrDef.getList();
					if ( ! list.isExternal() ) {
						List<CodeListItem> codeListItemsList = codeListManager.loadRootItems(list);
	    				for (CodeListItem codeListItem : codeListItemsList){
	    					codes.add(codeListItem.getCode());
	    					if (codeListItem.getLabel(null)==null){
	    						options.add(codeListItem.getLabel(ApplicationManager.selectedLanguage));
	    					} else {
	    						options.add(codeListItem.getLabel(null));	    						
	    					}
	    				}
					}
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					Code codeValue = (Code)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
        					if (codeValue!=null){
        						loadedValue = codeValue.getCode();
        					}
	    				}
        				CodeField codeField = new CodeField(this, nodeDef, codes, options, loadedValue,EntityInstancesScreen.this.getFormScreenId());
        				codeField.setOnClickListener(this);
        				codeField.setId(nodeDef.getId());
        				//codeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
        				ApplicationManager.putUIElement(codeField.getId(), codeField);
        				this.ll.addView(codeField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
    					if (foundNode!=null){
	    					Code codeValue = (Code)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
        					if (codeValue!=null){
        						loadedValue = codeValue.getCode();
        					}
	    				}
        				CodeField codeField = new CodeField(this, nodeDef, codes, options, loadedValue,this.parentFormScreenId);
        				codeField.setOnClickListener(this);
        				codeField.setId(nodeDef.getId());
        				//Log.e("onResume",this.parentFormScreenId+"=="+this.currInstanceNo);
        				//codeField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
        				ApplicationManager.putUIElement(codeField.getId(), codeField);
        				this.ll.addView(codeField);
    				} else {//multiple attribute summary
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof CoordinateAttributeDefinition){
    				String loadedValueLon = "";
    				String loadedValueLat = "";
    				if (!nodeDef.isMultiple()){
        				final CoordinateField coordField= new CoordinateField(this, nodeDef);
        				if (this.currentCoordinateField!=null){
        					if (this.longitude==null)
        						this.longitude = "";
        					if (this.latitude==null)
        						this.latitude = "";
        					String srsId = null;
        					if (EntityInstancesScreen.this.currentCoordinateField.srs!=null){						
        						srsId = EntityInstancesScreen.this.currentCoordinateField.srs.getId();
        					}
        					coordField.setValue(0, this.longitude, this.latitude, srsId, this.parentFormScreenId,false);
    		    			this.currentCoordinateField = null;
    		    			this.longitude = null;
    		    			this.latitude = null;
    		    		}
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					Coordinate coordValue = (Coordinate)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    					if (coordValue!=null){
	    						if (coordValue.getX()!=null)
	    							loadedValueLon = coordValue.getX().toString();
	    						if (coordValue.getY()!=null)
	    							loadedValueLat = coordValue.getY().toString();
	    					}	    				
	    				}
	    				//coordField = new CoordinateField(this, nodeDef);
        				coordField.setOnClickListener(this);
        				coordField.setId(nodeDef.getId());
        				//coordField.setValue(0, loadedValueLon, loadedValueLat, FormScreen.this.getFormScreenId(),false);
        				ApplicationManager.putUIElement(coordField.getId(), coordField);
        				this.ll.addView(coordField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					final CoordinateField coordField= new CoordinateField(this, nodeDef);
        				if (this.currentCoordinateField!=null){
        					if (this.longitude==null)
        						this.longitude = "";
        					if (this.latitude==null)
        						this.latitude = "";
        					String srsId = null;
        					if (EntityInstancesScreen.this.currentCoordinateField.srs!=null){						
        						srsId = EntityInstancesScreen.this.currentCoordinateField.srs.getId();
        					}
        					coordField.setValue(this.currInstanceNo, this.longitude, this.latitude, srsId, this.parentFormScreenId,false);
    		    			this.currentCoordinateField = null;
    		    			this.longitude = null;
    		    			this.latitude = null;
    		    		}
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					Coordinate coordValue = (Coordinate)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    					if (coordValue!=null){
	    						if (coordValue.getX()!=null)
	    							loadedValueLon = coordValue.getX().toString();
	    						if (coordValue.getY()!=null)
	    							loadedValueLat = coordValue.getY().toString();
	    					}   				
	    				}
        				//coordField= new CoordinateField(this, nodeDef);
        				coordField.setOnClickListener(this);
        				coordField.setId(nodeDef.getId());
        				//coordField.setValue(this.currInstanceNo, loadedValueLon, loadedValueLat, this.parentFormScreenId,false);
        				ApplicationManager.putUIElement(coordField.getId(), coordField);
        				this.ll.addView(coordField);
    				} else {//multiple attribute summary    			    		
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof RangeAttributeDefinition){
    				loadedValue = "";
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
	    					if (rangeAttrDef.isReal()){
	    						RealRange rangeValue = (RealRange)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (rangeValue!=null){
		    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
		    							loadedValue = "";
		    						} else if (rangeValue.getFrom()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						} else if (rangeValue.getTo()==null){
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						}		    						
		    					}	
	    					} else {
	    						IntegerRange rangeValue = (IntegerRange)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (rangeValue!=null){
		    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
		    							loadedValue = "";
		    						} else if (rangeValue.getFrom()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						} else if (rangeValue.getTo()==null){
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						}		    						
		    					}	
	    					}		    							
	    				}
        				final RangeField rangeField= new RangeField(this, nodeDef);
        				rangeField.setOnClickListener(this);
        				rangeField.setId(nodeDef.getId());
        				//rangeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
        				rangeField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {        			            
        			        	rangeField.setValue(0, s.toString(),  EntityInstancesScreen.this.getFormScreenId(),true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(rangeField.getId(), rangeField);
        				this.ll.addView(rangeField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
	    					if (rangeAttrDef.isReal()){
	    						RealRange rangeValue = (RealRange)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (rangeValue!=null){
		    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
		    							loadedValue = "";
		    						} else if (rangeValue.getFrom()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						} else if (rangeValue.getTo()==null){
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						}		    						
		    					}	
	    					} else {
	    						IntegerRange rangeValue = (IntegerRange)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    						if (rangeValue!=null){
		    						if (rangeValue.getFrom()==null && rangeValue.getTo()==null){
		    							loadedValue = "";
		    						} else if (rangeValue.getFrom()==null){
		    							loadedValue = getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						} else if (rangeValue.getTo()==null){
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator);
		    						} else {
		    							loadedValue = rangeValue.getFrom()+getResources().getString(R.string.rangeSeparator)+rangeValue.getTo();
		    						}		    						
		    					}	
	    					}  				
	    				}
        				final RangeField rangeField= new RangeField(this, nodeDef);
        				rangeField.setOnClickListener(this);
        				rangeField.setId(nodeDef.getId());
        				//rangeField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
        				rangeField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {        			            
        			        	rangeField.setValue(EntityInstancesScreen.this.currInstanceNo, s.toString(), EntityInstancesScreen.this.parentFormScreenId,true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(rangeField.getId(), rangeField);
        				this.ll.addView(rangeField);
    				} else {//multiple attribute summary    			    		
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof DateAttributeDefinition){
    				loadedValue = "";
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					Date dateValue = (Date)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    					if (dateValue!=null){
	    						if (dateValue.getMonth()==null && dateValue.getDay()==null && dateValue.getYear()==null){
	    							loadedValue = "";
	    						} else if (dateValue.getMonth()==null && dateValue.getDay()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator);		    							
	    						} else if (dateValue.getMonth()==null && dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						} else if (dateValue.getDay()==null && dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator);
	    						} else if (dateValue.getMonth()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getDay();		    							
	    						} else if (dateValue.getDay()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator);
	    						} else if (dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						} else {
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						}
	    					}
	    				}

        				final DateField dateField= new DateField(this, nodeDef);
        				dateField.setOnClickListener(this);
        				dateField.setId(nodeDef.getId());
        				//dateField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
        				dateField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {        			            
        			        	dateField.setValue(0, s.toString(), EntityInstancesScreen.this.getFormScreenId(),true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(dateField.getId(), dateField);
        				this.ll.addView(dateField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					Date dateValue = (Date)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    					if (dateValue!=null){
	    						if (dateValue.getMonth()==null && dateValue.getDay()==null && dateValue.getYear()==null){
	    							loadedValue = "";
	    						} else if (dateValue.getMonth()==null && dateValue.getDay()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator);		    							
	    						} else if (dateValue.getMonth()==null && dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						} else if (dateValue.getDay()==null && dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator);
	    						} else if (dateValue.getMonth()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+getResources().getString(R.string.dateSeparator)+dateValue.getDay();		    							
	    						} else if (dateValue.getDay()==null){
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator);
	    						} else if (dateValue.getYear()==null){
	    							loadedValue = getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						} else {
	    							loadedValue = dateValue.getYear()+getResources().getString(R.string.dateSeparator)+dateValue.getMonth()+getResources().getString(R.string.dateSeparator)+dateValue.getDay();
	    						}
	    					}
	    				}
        				final DateField dateField= new DateField(this, nodeDef);
        				dateField.setOnClickListener(this);
        				dateField.setId(nodeDef.getId());
        				//dateField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
        				dateField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {        			            
        			        	dateField.setValue(EntityInstancesScreen.this.currInstanceNo, s.toString(), EntityInstancesScreen.this.parentFormScreenId,true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(dateField.getId(), dateField);
        				this.ll.addView(dateField);
    				}
    			} else if (nodeDef instanceof TimeAttributeDefinition){
    				loadedValue = "";
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					Time timeValue = (Time)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    					if (timeValue!=null){
	    						if (timeValue.getHour()==null && timeValue.getMinute()==null){
	    							loadedValue = "";
	    						} else if (timeValue.getHour()==null){
	    							loadedValue = getResources().getString(R.string.timeSeparator)+timeValue.getMinute();
	    						} else if (timeValue.getMinute()==null){
	    							loadedValue = timeValue.getHour()+getResources().getString(R.string.timeSeparator);
	    						} else {
	    							loadedValue = timeValue.getHour()+getResources().getString(R.string.timeSeparator)+timeValue.getMinute();
	    						}		    						
	    					}	    				
	    				}
        				final TimeField timeField= new TimeField(this, nodeDef);
        				timeField.setOnClickListener(this);
        				timeField.setId(nodeDef.getId());
        				//timeField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
        				timeField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {
        			        	timeField.setValue(0, s.toString(), EntityInstancesScreen.this.getFormScreenId(),true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(timeField.getId(), timeField);
        				this.ll.addView(timeField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					Time timeValue = (Time)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    					if (timeValue!=null){
	    						if (timeValue.getHour()==null && timeValue.getMinute()==null){
	    							loadedValue = "";
	    						} else if (timeValue.getHour()==null){
	    							loadedValue = getResources().getString(R.string.timeSeparator)+timeValue.getMinute();
	    						} else if (timeValue.getMinute()==null){
	    							loadedValue = timeValue.getHour()+getResources().getString(R.string.timeSeparator);
	    						} else {
	    							loadedValue = timeValue.getHour()+getResources().getString(R.string.timeSeparator)+timeValue.getMinute();
	    						}		    						
	    					}	   				
	    				}
        				final TimeField timeField= new TimeField(this, nodeDef);
        				timeField.setOnClickListener(this);
        				timeField.setId(nodeDef.getId());
        				//timeField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
        				timeField.addTextChangedListener(new TextWatcher(){
        			        public void afterTextChanged(Editable s) {        			            
        			        	timeField.setValue(EntityInstancesScreen.this.currInstanceNo, s.toString(), EntityInstancesScreen.this.parentFormScreenId,true);
        			        }
        			        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        			        public void onTextChanged(CharSequence s, int start, int before, int count){}
        			    });
        				ApplicationManager.putUIElement(timeField.getId(), timeField);
        				this.ll.addView(timeField);
    				} else {//multiple attribute summary    			    		
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
    			} else if (nodeDef instanceof TaxonAttributeDefinition){
    				TaxonAttributeDefinition taxonAttrDef = (TaxonAttributeDefinition)nodeDef;
    				ArrayList<String> options = new ArrayList<String>();
    				ArrayList<String> codes = new ArrayList<String>();
    				options.add("");
    				codes.add("null");
    				
    				String code = "";
    				String sciName = "";
    				String vernName = "";
    				String vernLang = "";
    				String langVariant = "";
    				if (!nodeDef.isMultiple()){
    					Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
	    				if (foundNode!=null){
	    					TaxonOccurrence taxonValue = (TaxonOccurrence)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
	    					if (taxonValue!=null){
	    						code = taxonValue.getCode();
	    	    				sciName = taxonValue.getScientificName();
	    	    				vernName = taxonValue.getVernacularName();
	    	    				vernLang = taxonValue.getLanguageCode();
	    	    				langVariant = taxonValue.getLanguageVariety();
	    					}	    				
	    				}
        				final TaxonField taxonField= new TaxonField(this, nodeDef, codes, options, vernLang);
        				taxonField.setOnClickListener(this);
        				taxonField.setId(nodeDef.getId());
        				//taxonField.setValue(0, code, sciName, vernName, vernLang, langVariant, FormScreen.this.getFormScreenId(),false);
        				ApplicationManager.putUIElement(taxonField.getId(), taxonField);
        				this.ll.addView(taxonField);
    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
    					Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
	    				if (foundNode!=null){
	    					TaxonOccurrence taxonValue = (TaxonOccurrence)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
	    					if (taxonValue!=null){
	    						code = taxonValue.getCode();
	    	    				sciName = taxonValue.getScientificName();
	    	    				vernName = taxonValue.getVernacularName();
	    	    				vernLang = taxonValue.getLanguageCode();
	    	    				langVariant = taxonValue.getLanguageVariety();	    						
	    					}	   				
	    				}
	    				final TaxonField taxonField= new TaxonField(this, nodeDef, codes, options, vernLang);
	    				taxonField.setOnClickListener(this);
	    				taxonField.setId(nodeDef.getId());
	    				//taxonField.setValue(this.currInstanceNo, code, sciName, vernName, vernLang, langVariant, this.parentFormScreenId,false);
        				ApplicationManager.putUIElement(taxonField.getId(), taxonField);
        				this.ll.addView(taxonField);
    				} else {//multiple attribute summary    			    		
						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
    					summaryTableView.setOnClickListener(this);
        				summaryTableView.setId(nodeDef.getId());
        				this.ll.addView(summaryTableView);
    				}
				} else if (nodeDef instanceof FileAttributeDefinition){
					FileAttributeDefinition fileDef = (FileAttributeDefinition)nodeDef;
					List<String> extensionsList = fileDef.getExtensions();
					
					if (extensionsList.contains("jpg")||extensionsList.contains("jpeg")){
						loadedValue = "";
	    				if (!nodeDef.isMultiple()){
	        				final PhotoField photoField= new PhotoField(this, nodeDef);
	        				if (this.currentPictureField!=null){
	    		    			photoField.setValue(0, this.photoPath, EntityInstancesScreen.this.getFormScreenId(),false);
	    		    			this.currentPictureField = null;
	    		    			this.photoPath = null;
	    		    		}
	        				Node<?> foundNode = this.parentEntitySingleAttribute.get(nodeDef.getName(), 0);
		    				if (foundNode!=null){
		    					File fileValue = (File)this.parentEntitySingleAttribute.getValue(nodeDef.getName(), 0);
		    					if (fileValue!=null){
		    						loadedValue = fileValue.getFilename();
		    					}
		    				}
	        				photoField.setOnClickListener(this);
	        				photoField.setId(nodeDef.getId());
	        				//photoField.setValue(0, loadedValue, FormScreen.this.getFormScreenId(),false);
	        				ApplicationManager.putUIElement(photoField.getId(), photoField);
	        				this.ll.addView(photoField);
	    				} else if (this.intentType==getResources().getInteger(R.integer.multipleAttributeIntent)){
	        				final PhotoField photoField= new PhotoField(this, nodeDef);
	        				if (this.currentPictureField!=null){
	        					photoField.setValue(this.currInstanceNo, this.photoPath, this.parentFormScreenId,false);
	    		    			this.currentPictureField = null;
	    		    			this.photoPath = null;
	    		    		}
	        				Node<?> foundNode = this.parentEntityMultipleAttribute.get(nodeDef.getName(), this.currInstanceNo);
		    				if (foundNode!=null){
		    					File fileValue = (File)this.parentEntityMultipleAttribute.getValue(nodeDef.getName(), this.currInstanceNo);
		    					if (fileValue!=null){
		    						loadedValue = fileValue.getFilename();
		    					}
		    				}
	        				photoField.setOnClickListener(this);
	        				photoField.setId(nodeDef.getId());
	        				//photoField.setValue(this.currInstanceNo, loadedValue, this.parentFormScreenId,false);
	        				ApplicationManager.putUIElement(photoField.getId(), photoField);
	        				this.ll.addView(photoField);
	    				} else {//multiple attribute summary    			    		
    						SummaryTable summaryTableView = new SummaryTable(this, nodeDef, tableColHeaders, parentEntitySingleAttribute, this);
        					summaryTableView.setOnClickListener(this);
            				summaryTableView.setId(nodeDef.getId());
            				this.ll.addView(summaryTableView);
        				}
					}
				}
			}    				
		}
		
		if (this.intentType==getResources().getInteger(R.integer.multipleEntityIntent)){ 				
			if (ApplicationManager.currentRecord.getRootEntity().getId()!=this.idmlId){
				this.ll.addView(arrangeButtonsInLine(new Button(this),getResources().getString(R.string.previousInstanceButton),new Button(this),getResources().getString(R.string.nextInstanceButton),this, true));
			}	
		}
		
		int backgroundColor = ApplicationManager.appPreferences.getInt(getResources().getString(R.string.backgroundColor), Color.WHITE);		
		changeBackgroundColor(backgroundColor);
		
		for (int i=0;i<this.fieldsNo;i++){
			NodeDefinition nodeDef = ApplicationManager.getNodeDefinition(this.startingIntent.getIntExtra(getResources().getString(R.string.attributeId)+i, -1));
			if (nodeDef!=null){
				if (nodeDef instanceof TextAttributeDefinition){
					loadedValue = "";
					if (((TextAttributeDefinition) nodeDef).getType().toString().toLowerCase().equals("short")){
						TextValue textValue = (TextValue)parentEntity.getValue(nodeDef.getName(), 0);							
						if (textValue!=null)
							if (textValue.getValue()!=null)
								loadedValue = textValue.getValue();
    					TextField textField = (TextField) ApplicationManager.getUIElement(nodeDef.getId());
    					if (textField!=null)
    						textField.setValue(0, loadedValue, this.getFormScreenId(), false);
					} else {
						TextValue textValue = (TextValue)parentEntity.getValue(nodeDef.getName(), 0);							
						if (textValue!=null)
							if (textValue.getValue()!=null)
								loadedValue = textValue.getValue();
    					MemoField memoField = (MemoField) ApplicationManager.getUIElement(nodeDef.getId());
    					if (memoField!=null)
    						memoField.setValue(0, loadedValue, this.getFormScreenId(), false);
					}								
				} else if (nodeDef instanceof NumberAttributeDefinition){
					loadedValue = "";
					if (((NumberAttributeDefinition) nodeDef).isInteger()){
						IntegerValue intValue = (IntegerValue)parentEntity.getValue(nodeDef.getName(), 0);
						if (intValue!=null)
							if (intValue.getValue()!=null)
								loadedValue = intValue.getValue().toString();	
					} else {
						RealValue realValue = (RealValue)parentEntity.getValue(nodeDef.getName(), 0);					
						if (realValue!=null)
							if (realValue.getValue()!=null)
								loadedValue = realValue.getValue().toString();
					}					
					NumberField numberField = (NumberField) ApplicationManager.getUIElement(nodeDef.getId());
					if (numberField!=null)
						numberField.setValue(0, loadedValue, this.getFormScreenId(), false);
				}  else if (nodeDef instanceof BooleanAttributeDefinition){
					loadedValue = "";
					BooleanValue boolValue = (BooleanValue)parentEntity.getValue(nodeDef.getName(), 0);
					if (boolValue!=null)
						if (boolValue.getValue()!=null)
							loadedValue = boolValue.getValue().toString();
					BooleanField boolField = (BooleanField) ApplicationManager.getUIElement(nodeDef.getId());
					if (boolField!=null){
						
						if (loadedValue.equals("")){
							boolField.setValue(0, null, this.getFormScreenId(), false);
						} else {
							boolField.setValue(0, Boolean.valueOf(loadedValue), this.getFormScreenId(), false);
						}
					}					
				} else if (nodeDef instanceof CodeAttributeDefinition){
					loadedValue = "";
					Code codeValue = (Code)parentEntity.getValue(nodeDef.getName(), 0);
					if (codeValue!=null)
						if (codeValue.getCode()!=null)
							loadedValue = codeValue.getCode();
					CodeField codeField = (CodeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (codeField!=null){
						//Log.e("refreshENTITY",this.getFormScreenId()+"=="+0);
						codeField.setValue(0, loadedValue, this.getFormScreenId(), false);
					}
						
				} else if (nodeDef instanceof CoordinateAttributeDefinition){
					String loadedValueLat = "";
					String loadedValueLon = "";
					String loadedSrsId = "";
					Coordinate coordValue = (Coordinate)parentEntity.getValue(nodeDef.getName(), 0);
					if (coordValue!=null){
						if (coordValue.getX()!=null)
							loadedValueLon = coordValue.getX().toString();
						if (coordValue.getY()!=null)
							loadedValueLat = coordValue.getY().toString();
						if (coordValue.getSrsId()!=null)
							loadedSrsId = coordValue.getSrsId().toString();
					}
						
					CoordinateField coordField = (CoordinateField) ApplicationManager.getUIElement(nodeDef.getId());
					if (coordField!=null)
						coordField.setValue(0, loadedValueLon, loadedValueLat, loadedSrsId, this.getFormScreenId(), false);
				} else if (nodeDef instanceof RangeAttributeDefinition){
					String from = "";
					String to = "";
					
					RangeAttributeDefinition rangeAttrDef = (RangeAttributeDefinition)nodeDef;
					if (rangeAttrDef.isReal()){
						RealRange rangeValue = (RealRange)parentEntity.getValue(nodeDef.getName(), 0);
						if (rangeValue!=null){
							if (rangeValue.getFrom()!=null)
								from = rangeValue.getFrom().toString();
							if (rangeValue.getTo()!=null)
								to = rangeValue.getTo().toString();						
						}
					} else {
						IntegerRange rangeValue = (IntegerRange)parentEntity.getValue(nodeDef.getName(), 0);
						if (rangeValue!=null){
							if (rangeValue.getFrom()!=null)
								from = rangeValue.getFrom().toString();
							if (rangeValue.getTo()!=null)
								to = rangeValue.getTo().toString();						
						}
					}
											
					RangeField rangeField = (RangeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (rangeField!=null)
						rangeField.setValue(0, from+getResources().getString(R.string.rangeSeparator)+to, this.getFormScreenId(), false);
				} else if (nodeDef instanceof DateAttributeDefinition){
					String day = "";
					String month = "";
					String year = "";
					Date dateValue = (Date)parentEntity.getValue(nodeDef.getName(), 0);
					if (dateValue!=null){
						loadedValue = formatDate(dateValue);
					}
					DateField dateField = (DateField) ApplicationManager.getUIElement(nodeDef.getId());
					if (dateField!=null)
						dateField.setValue(0, loadedValue, this.getFormScreenId(), false);
				} else if (nodeDef instanceof TimeAttributeDefinition){
					String hour = "";
					String minute = "";
					Time timeValue = (Time)parentEntity.getValue(nodeDef.getName(), 0);
					if (timeValue!=null){
						if (timeValue.getHour()!=null)
							hour = timeValue.getHour().toString();
						if (timeValue.getMinute()!=null)
							minute = timeValue.getMinute().toString();
					}						
					TimeField timeField = (TimeField) ApplicationManager.getUIElement(nodeDef.getId());
					if (timeField!=null)
						timeField.setValue(0, hour+getResources().getString(R.string.timeSeparator)+minute, this.getFormScreenId(), false);					
				} else if (nodeDef instanceof TaxonAttributeDefinition){
    				String code = "";
    				String sciName = "";
    				String vernName = "";
    				String vernLang = "";
    				String langVariant = "";
					TaxonOccurrence taxonValue = (TaxonOccurrence)parentEntity.getValue(nodeDef.getName(), this.currInstanceNo);
					if (taxonValue!=null){
						code = taxonValue.getCode();
	    				sciName = taxonValue.getScientificName();
	    				vernName = taxonValue.getVernacularName();
	    				vernLang = taxonValue.getLanguageCode();
	    				langVariant = taxonValue.getLanguageVariety();	    						
					}
					TaxonField taxonField = (TaxonField) ApplicationManager.getUIElement(nodeDef.getId());
					if (taxonField!=null)
						taxonField.setValue(0, code, sciName, vernName, vernLang, langVariant, this.getFormScreenId(), false);
				} else if (nodeDef instanceof FileAttributeDefinition){
					String fileName = "";
					File fileValue = (File)parentEntity.getValue(nodeDef.getName(), 0);
					if (fileValue!=null){
						if (fileValue.getFilename()!=null)
							fileName = fileValue.getFilename();
					}						
					PhotoField photoField = (PhotoField) ApplicationManager.getUIElement(nodeDef.getId());
					if (photoField!=null)
						photoField.setValue(0, fileName, this.getFormScreenId(), false);					
				}
			}
		}
		sv.post(new Runnable() {
    	    @Override
    	    public void run() {
				sv.scrollTo(0, 0);
			}	
    	});
	}*/
	
    public String getFormScreenId(){
    	if (this.parentFormScreenId.equals("")){
    		//Log.e("1ENTITYinstanceScreenID","=="+removeDuplicates(this.idmlId+getResources().getString(R.string.valuesSeparator1)+"0"));
    		return removeDuplicates(this.idmlId+getResources().getString(R.string.valuesSeparator1)+"0"/*this.currInstanceNo*/);    		
    	} else {
    		//Log.e("2ENTITYinstanceScreenID","=="+removeDuplicates(this.parentFormScreenId+getResources().getString(R.string.valuesSeparator2)+this.idmlId+getResources().getString(R.string.valuesSeparator1)+"0"));
    		return removeDuplicates(this.parentFormScreenId+getResources().getString(R.string.valuesSeparator2)+this.idmlId+getResources().getString(R.string.valuesSeparator1)+"0"/*this.currInstanceNo*/);
    	}    		
    }
    
    public String getFormScreenId(int instanceNo){
    	if (this.parentFormScreenId.equals("")){
    		//Log.e("3ENTITYinstanceScreenID","=="+removeDuplicates(this.idmlId+getResources().getString(R.string.valuesSeparator1)+instanceNo));
    		return removeDuplicates(this.idmlId+getResources().getString(R.string.valuesSeparator1)+instanceNo/*this.currInstanceNo*/);    		
    	} else {
    		//Log.e("4ENTITYinstanceScreenID","=="+removeDuplicates(this.parentFormScreenId+getResources().getString(R.string.valuesSeparator2)+this.idmlId+getResources().getString(R.string.valuesSeparator1)+instanceNo));
    		return removeDuplicates(this.parentFormScreenId+getResources().getString(R.string.valuesSeparator2)+this.idmlId+getResources().getString(R.string.valuesSeparator1)+instanceNo/*this.currInstanceNo*/);
    	}    		
    }
    
    public String removeDuplicates(String text){
        String[] tablica = text.split(";");
        for (int i=0;i<tablica.length;i++){
        	if (tablica[i]!=null){
        		String piece1 = tablica[i];
            	String firstNumber1 = tablica[i].split(",")[0];
            	//Log.e("piece1",firstNumber1+"=="+piece1);
            	for (int j=i+1;j<tablica.length;j++){
            		if (tablica[j]!=null){
            			String piece2 = tablica[j];
                		String firstNumber2 = tablica[j].split(",")[0];
                		//Log.e("piece2",firstNumber2+"=="+piece2);
                		if (piece1.equals(piece2) || firstNumber2.equals(firstNumber1)){
                			//Log.e("i"+i+"==null","==="+j);
                			tablica[i] = null;
                		}	
            		}            	
            	}
        	}
        	
        }
        String newText = "";
        for (int i=0;i<tablica.length;i++){
        	//Log.e("tablica[i]!=null","=="+(tablica[i]!=null));
        	if (tablica[i]!=null){
        		if (i==tablica.length-1){
        			newText += tablica[i];
        		} else{
        			newText += tablica[i]+";";
        		}
        	}
        		
        }
        //Log.e(text,"without duplicate: " + newText);
        return newText;
    }
    
    private RelativeLayout arrangeButtonsInLine(Button btnAdd, String btnAddLabel, OnClickListener listener, boolean isForEntity){
		RelativeLayout relativeButtonsLayout = new RelativeLayout(this);
	    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	    relativeButtonsLayout.setLayoutParams(lp);
		btnAdd.setText(btnAddLabel);
		
		btnAdd.setOnClickListener(listener);
		
		LinearLayout ll = new LinearLayout(this);
		ll.addView(btnAdd);
		relativeButtonsLayout.addView(ll);
		
		if (!isForEntity){
			btnAdd.setId(getResources().getInteger(R.integer.addButtonMultipleAttribute));
		} else {
			btnAdd.setId(getResources().getInteger(R.integer.addButtonMultipleEntity));
		}
		
		return relativeButtonsLayout;
    }
}