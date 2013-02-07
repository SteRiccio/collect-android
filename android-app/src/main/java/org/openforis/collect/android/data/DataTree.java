package org.openforis.collect.android.data;

import java.util.ArrayList;
import java.util.List;

import org.openforis.collect.android.R;

import android.content.Context;
import android.util.Log;

public class DataTree {

	private DataTreeNode root;
	
	private Context ctx;

	public DataTree(Context context, List<FieldValue> rootData) {
	    root = new DataTreeNode(0,0, "", null, rootData);
	    root.children = new ArrayList<DataTreeNode>();
	    root.parent = null;
	    ctx = context;
	}
	
	public void addChildToRoot(DataTreeNode dataNode){
		root.children.add(dataNode);
		//Log.e("rootCHILDRENsize","=="+root.children.size());
	}
	
	public DataTreeNode getChild(String path){
		//Log.e("path","=="+path);
		DataTreeNode searchedNode = null;
		String[] pathArray = path.split(ctx.getResources().getString(R.string.valuesSeparator2));
		if (pathArray.length>1){
			DataTreeNode child = root;
			for (int i=1;i<pathArray.length;i++){
				String[] idArray = pathArray[i].split(",");
				int id = Integer.valueOf(idArray[0]);
				int instanceNo = Integer.valueOf(idArray[1]);
				child = child.getChildNode(id,instanceNo);				
			}	
			if ((child!=null)&&(child!=root)){
				searchedNode = child;
			}
		}		
		/*if (child!=null)
			Log.e("child",child.idNo+"=="+child.instanceNo);
		else
			Log.e("child","NULL");
		if (child.equals(root)){
			child = null;
		}*/
		return searchedNode;
	}
	
	public void setChild(String path, DataTreeNode child){
		String[] pathArray = path.split("...");
		DataTreeNode tempNode = root;
		for (int i=1;i<pathArray.length-1;i++){
			String[] idArray = pathArray[i].split(",");
			int id = Integer.valueOf(idArray[0]);
			int instanceNo = Integer.valueOf(idArray[1]);
			tempNode = tempNode.getChildNode(id,instanceNo);
		}
		tempNode.updateNode(child);
	}
	
	public void addChild(String path, DataTreeNode child){
		//Log.e("pathToAddChild","=="+path);
		String[] pathArray = path.split(";");
		DataTreeNode tempNode = root;
		for (int i=1;i<pathArray.length-1;i++){
			String[] idArray = pathArray[i].split(",");
			int id = Integer.valueOf(idArray[0]);
			int instanceNo = Integer.valueOf(idArray[1]);
			tempNode = tempNode.getChildNode(id,instanceNo);
		}
		//Log.e("parentNodeFound","=="+tempNode.getNodePath());
		tempNode.addChildNode(child, false);
	}
	
	public void printTree(){
		int level = 0;
		Log.e("nodeLevel"+level,"=="+root.getNodePath());
		level++;
		Log.e("rootchildren.size","=="+root.children.size());
		for (DataTreeNode child : root.children){
			printTree(child, level);
		}
	}
	
	private void printTree(DataTreeNode node, int treeLevel){
		Log.e("nodeLevel"+treeLevel,"=="+node.getNodePath());
		treeLevel++;
		/*if (node.getNodeValues()!=null){
			Log.e("children.size",node.getNodeValues().size()+"=="+node.children.size());	
		} else {
			Log.e("children.size","NULL"+"=="+node.children.size());
		}*/
		if (node.children!=null){
			for (DataTreeNode child : node.children){
				printTree(child, treeLevel);
			}
		}
	}
	
	public DataTreeNode getRoot(){
		return this.root;
	}	
}