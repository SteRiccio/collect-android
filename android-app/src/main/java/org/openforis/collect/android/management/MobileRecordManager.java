package org.openforis.collect.android.management;

import org.openforis.collect.persistence.RecordDao;

public class MobileRecordManager extends org.openforis.collect.manager.RecordManager {
	
	private RecordDao recordDao;
	
	private MobileCodeListManager codeListManager;
	
	public MobileRecordManager(boolean lockingEnabled, RecordDao recordDao) {
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

	public MobileCodeListManager getCodeListManager() {
		return codeListManager;
	}

	public void setCodeListManager(MobileCodeListManager codeListManager) {
		this.codeListManager = codeListManager;
	}
}
