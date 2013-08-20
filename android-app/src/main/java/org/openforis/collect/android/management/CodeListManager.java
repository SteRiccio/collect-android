package org.openforis.collect.android.management;

import java.util.List;

import org.openforis.collect.persistence.CodeListItemDao;
import org.openforis.collect.persistence.DatabaseExternalCodeListProvider;
import org.openforis.idm.metamodel.CodeList;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.ExternalCodeListItem;
import org.openforis.idm.metamodel.PersistedCodeListItem;

import android.util.Log;

public class CodeListManager extends org.openforis.collect.manager.CodeListManager{
	
	
	private DatabaseExternalCodeListProvider provider;
	private CodeListItemDao codeListItemDao;
	
	public CodeListItemDao getCodeListItemDao() {
		return codeListItemDao;
	}

	public void setCodeListItemDao(CodeListItemDao codeListItemDao) {
		this.codeListItemDao = codeListItemDao;
	}

	public DatabaseExternalCodeListProvider getProvider() {
		return provider;
	}

	public void setProvider(DatabaseExternalCodeListProvider provider) {
		this.provider = provider;
	}		
	
	public CodeListManager(CodeListItemDao codeListItemDao){
		super();
		super.setCodeListItemDao(codeListItemDao);
		this.setCodeListItemDao(codeListItemDao);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends CodeListItem> List<T> loadChildItems(CodeListItem parent) {
		Log.e("MOBILE","loadChildItems");
		CodeList list = parent.getCodeList();
		if ( list.isExternal() ) {
			return (List<T>) getProvider().getChildItems((ExternalCodeListItem) parent);
		} else if ( list.isEmpty() ) {
			return (List<T>) getCodeListItemDao().loadChildItems((PersistedCodeListItem) parent);
		} else {
			return parent.getChildItems();
		}
	}


}
