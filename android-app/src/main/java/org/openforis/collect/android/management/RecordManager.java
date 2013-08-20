package org.openforis.collect.android.management;

import org.openforis.collect.persistence.RecordDao;

public class RecordManager extends org.openforis.collect.manager.RecordManager {
	
	private RecordDao recordDao;
	
	private CodeListManager codeListManager;
	
	public RecordManager(boolean lockingEnabled, RecordDao recordDao) {
		super(lockingEnabled);
		super.setRecordDao(recordDao);
		this.setRecordDao(recordDao);
	}
	
	/*public RecordManager(RecordDao recordDao){
		super();
		super.setRecordDao(recordDao);
		this.setRecordDao(recordDao);
	}*/
	
	public RecordDao getRecordDao() {
		return recordDao;
	}

	public void setRecordDao(RecordDao recordDao) {
		this.recordDao = recordDao;
	}

	public CodeListManager getCodeListManager() {
		return codeListManager;
	}

	public void setCodeListManager(CodeListManager codeListManager) {
		this.codeListManager = codeListManager;
	}
}
