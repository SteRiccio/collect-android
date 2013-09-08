package org.openforis.collect.android.misc;

import android.view.View;

public class ViewBacktrack {
	private View v;
	private String formScreenId;
	
	public ViewBacktrack(View v, String formScreenId){
		this.setView(v);
		this.setFormScreenId(formScreenId);
	}

	public View getView() {
		return v;
	}

	public void setView(View v) {
		this.v = v;
	}

	public String getFormScreenId() {
		return formScreenId;
	}

	public void setFormScreenId(String formScreenId) {
		this.formScreenId = formScreenId;
	}
}