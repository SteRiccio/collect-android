package org.openforis.collect.android.management;

import org.openforis.idm.metamodel.BooleanAttributeDefinition;
import org.openforis.idm.metamodel.CoordinateAttributeDefinition;
import org.openforis.idm.metamodel.DateAttributeDefinition;
import org.openforis.idm.metamodel.NodeDefinition;
import org.openforis.idm.metamodel.NumberAttributeDefinition;
import org.openforis.idm.metamodel.RangeAttributeDefinition;
import org.openforis.idm.metamodel.TextAttributeDefinition;
import org.openforis.idm.metamodel.TimeAttributeDefinition;
import org.openforis.idm.metamodel.validation.ValidationResults;
import org.openforis.idm.metamodel.validation.Validator;
import org.openforis.idm.model.Attribute;
import org.openforis.idm.model.CoordinateAttribute;
import org.openforis.idm.model.DateAttribute;
import org.openforis.idm.model.IntegerAttribute;
import org.openforis.idm.model.Node;
import org.openforis.idm.model.RealAttribute;
import org.openforis.idm.model.TextAttribute;
import org.openforis.idm.model.TimeAttribute;

import android.util.Log;

public class ValidationManager {
	
	@SuppressWarnings("rawtypes")
	public static ValidationResults validateField(Node<? extends NodeDefinition> node){
		NodeDefinition nodeDef = node.getDefinition();
		ValidationResults results = null;
		Validator validator = null;
		Attribute attribute = null;
		if (nodeDef instanceof TextAttributeDefinition){    			    		
			attribute = (TextAttribute)node;
			validator = new Validator();
			results = validator.validate(attribute); 
			/*Log.e("VALIDATION FOR TEXT FIELD", "Errors: " + results.getErrors().size() + " : " + results.getErrors().toString());
			Log.d("VALIDATION FOR TEXT FIELD", "Warnings: "  + results.getWarnings().size() + " : " + results.getWarnings().toString());
			Log.e("VALIDATION FOR TEXT FIELD", "Fails: "  + results.getFailed().size() + " : " +  results.getFailed().toString());*/	    						
		}else if (nodeDef instanceof NumberAttributeDefinition){
			if(((NumberAttributeDefinition) nodeDef).isInteger()){
				attribute = (IntegerAttribute)node;
			}else if (((NumberAttributeDefinition) nodeDef).isReal()){
				attribute = (RealAttribute)node;
			}
			validator = new Validator();
			results = validator.validate(attribute); 
			/*Log.e("VALIDATION FOR NUMBER FIELD", "Errors: " + results.getErrors().size() + " : " + results.getErrors().toString());
			Log.d("VALIDATION FOR NUMBER FIELD", "Warnings: "  + results.getWarnings().size() + " : " + results.getWarnings().toString());
			Log.e("VALIDATION FOR NUMBER FIELD", "Fails: "  + results.getFailed().size() + " : " +  results.getFailed().toString());*/		
		}else if (nodeDef instanceof CoordinateAttributeDefinition){
			attribute = (CoordinateAttribute)node;
			validator = new Validator();
			results = validator.validate(attribute); 
			Log.e("VALIDATION FOR COORDINATE FIELD", "Errors: " + results.getErrors().size() + " : " + results.getErrors().toString());
			Log.d("VALIDATION FOR COORDINATE FIELD", "Warnings: "  + results.getWarnings().size() + " : " + results.getWarnings().toString());
			Log.e("VALIDATION FOR COORDINATE FIELD", "Fails: "  + results.getFailed().size() + " : " +  results.getFailed().toString());		
		}else if (nodeDef instanceof DateAttributeDefinition){
			attribute = (DateAttribute)node;
			validator = new Validator();
			results = validator.validate(attribute); 
			Log.e("VALIDATION FOR DATE FIELD", "Errors: " + results.getErrors().size() + " : " + results.getErrors().toString());
			Log.d("VALIDATION FOR DATE FIELD", "Warnings: "  + results.getWarnings().size() + " : " + results.getWarnings().toString());
			Log.e("VALIDATION FOR DATE FIELD", "Fails: "  + results.getFailed().size() + " : " +  results.getFailed().toString());			
		}else if (nodeDef instanceof TimeAttributeDefinition){
			attribute = (TimeAttribute)node;
			validator = new Validator();
			results = validator.validate(attribute); 
			Log.e("VALIDATION FOR TIME FIELD", "Errors: " + results.getErrors().size() + " : " + results.getErrors().toString());
			Log.d("VALIDATION FOR TIME FIELD", "Warnings: "  + results.getWarnings().size() + " : " + results.getWarnings().toString());
			Log.e("VALIDATION FOR TIME FIELD", "Fails: "  + results.getFailed().size() + " : " +  results.getFailed().toString());			
		}else if (nodeDef instanceof RangeAttributeDefinition){
			
		}else if (nodeDef instanceof BooleanAttributeDefinition){
			
		}else{
			
		}
		Log.i("VALIDATION","Attribute: " + attribute.getName() + " Attribute value is: " + attribute.getValue());
		return results;
	}

}
