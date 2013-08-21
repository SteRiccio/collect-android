package org.openforis.collect.android.management;

import java.util.List;

import org.openforis.collect.persistence.CodeListItemDao;
import org.openforis.collect.persistence.DatabaseExternalCodeListProvider;
import org.openforis.idm.metamodel.CodeList;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.metamodel.ExternalCodeListItem;
import org.openforis.idm.metamodel.PersistedCodeListItem;

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
		System.err.println("Load child items from mobile CodeListManager");
		CodeList list = parent.getCodeList();
		if ( list.isExternal() ) {
			return (List<T>) getProvider().getChildItems((ExternalCodeListItem) parent);
		} else if ( list.isEmpty() ) {
			System.err.println("Finish loading child items from mobile CodeListManager from CodeListDao");
			return (List<T>) getCodeListItemDao().loadChildItems((PersistedCodeListItem) parent);
		} else {
			System.err.println("Finish loading child items from mobile CodeListManager from parent.getChildItems");
			return parent.getChildItems();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends CodeListItem> List<T> loadRootItems(CodeList list) {
		System.err.println("Load root items from mobile CodeListManager");
		if ( list.isExternal() ) {
			return (List<T>) provider.getRootItems(list);
		} else if ( list.isEmpty() ) {
			System.err.println("Finish loading root items from mobile CodeListManager from CodeListDao");
			return (List<T>) codeListItemDao.loadRootItems(list);
		} else {
			System.err.println("Finish loading items from mobile CodeListManager from list.getItems");
			return list.getItems();
		}
	}
}
