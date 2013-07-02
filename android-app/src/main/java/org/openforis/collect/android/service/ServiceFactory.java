package org.openforis.collect.android.service;

import org.openforis.collect.android.config.Configuration;
import org.openforis.collect.android.database.DatabaseHelper;
import org.openforis.collect.android.database.SQLDroidDataSource;
import org.openforis.collect.manager.CodeListManager;
import org.openforis.collect.manager.RecordManager;
import org.openforis.collect.manager.SurveyManager;
import org.openforis.collect.manager.UserManager;
import org.openforis.collect.model.CollectSurveyContext;
import org.openforis.collect.persistence.CodeListItemDao;
import org.openforis.collect.persistence.RecordDao;
import org.openforis.collect.persistence.SurveyDao;
import org.openforis.collect.persistence.SurveyWorkDao;
import org.openforis.collect.persistence.UserDao;
import org.openforis.idm.metamodel.validation.Validator;
import org.openforis.idm.model.expression.ExpressionFactory;

/**
 * 
 * @author S. Ricci
 *
 */
public class ServiceFactory {

	private static RecordManager recordManager;
	private static SurveyManager surveyManager;
	private static UserManager userManager;
	private static SQLDroidDataSource dataSource;
	private static CodeListManager codeListManager;

	public static void init(Configuration config) {
		init(config, true);
	}
	
	public static void init(Configuration config, boolean updateDBSchema) {
		dataSource = new SQLDroidDataSource();
    	dataSource.setUrl(config.getDbConnectionUrl());
    	if ( updateDBSchema ) {
    		DatabaseHelper.updateDBSchema();
    	}
    	codeListManager = new CodeListManager();
    	CodeListItemDao codeListItemDao = new CodeListItemDao();
    	codeListItemDao.setDataSource(dataSource);
		codeListManager.setCodeListItemDao(codeListItemDao);
		
	    ExpressionFactory expressionFactory = new ExpressionFactory();
    	Validator validator = new Validator();
    	CollectSurveyContext collectSurveyContext = new CollectSurveyContext(expressionFactory, validator);
    	
    	surveyManager = new SurveyManager();
    	surveyManager.setCollectSurveyContext(collectSurveyContext);
    	SurveyDao surveyDao = new SurveyDao();
    	surveyDao.setSurveyContext(collectSurveyContext);
    	surveyDao.setDataSource(dataSource);
    	surveyManager.setSurveyWorkDao(new SurveyWorkDao());
    	surveyManager.setSurveyDao(surveyDao);
    	surveyManager.setCodeListManager(codeListManager);
    	
    	recordManager = new RecordManager();
    	RecordDao recordDao = new RecordDao();
    	recordDao.setDataSource(dataSource);
    	recordManager.setRecordDao(recordDao);
    	
		userManager = new UserManager();
    	UserDao userDao = new UserDao();
    	userDao.setDataSource(dataSource);
		userManager.setUserDao(userDao);
		userManager.setRecordDao(recordDao);

		surveyManager.init();
	}
	
	public static SQLDroidDataSource getDataSource() {
		return dataSource;
	}
	
	public static RecordManager getRecordManager() {
		return recordManager;
	}
	
	public static CodeListManager getCodeListManager() {
		return codeListManager;
	}
	
	public static SurveyManager getSurveyManager() {
		return surveyManager;
	}
	
	public static UserManager getUserManager() {
		return userManager;
	}

}
