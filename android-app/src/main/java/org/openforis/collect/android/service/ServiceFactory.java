package org.openforis.collect.android.service;

import org.openforis.collect.android.database.DatabaseWrapper;
import org.openforis.collect.android.database.SQLDroidDataSource;
import org.openforis.collect.manager.CodeListManager;
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
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * 
 * @author S. Ricci
 *
 */
public class ServiceFactory {

	private static SurveyManager surveyManager;
	private static UserManager userManager;
	private static SQLDroidDataSource dataSource;
	private static CodeListManager codeListManager;

	static {
		dataSource = new SQLDroidDataSource();
    	dataSource.setUrl(DatabaseWrapper.CONNECTION_URL);
    	JdbcDaoSupport.init(dataSource);
    	
    	codeListManager = new CodeListManager();
    	CodeListItemDao codeListItemDao = new CodeListItemDao();
		codeListManager.setCodeListItemDao(codeListItemDao);
		
		 //instantiating managers
	    ExpressionFactory expressionFactory = new ExpressionFactory();
    	Validator validator = new Validator();
    	CollectSurveyContext collectSurveyContext = new CollectSurveyContext(expressionFactory, validator/*, null*/);
    	//CollectSurveyContext collectSurveyContext = new CollectSurveyContext(expressionFactory, validator);
    	
    	surveyManager = new SurveyManager();
    	surveyManager.setCollectSurveyContext(collectSurveyContext);
    	//surveyManager.setSurveyDao(new SurveyDao(collectSurveyContext));
    	SurveyDao surveyDao = new SurveyDao();
    	surveyDao.setSurveyContext(collectSurveyContext);	        	
    	surveyManager.setSurveyWorkDao(new SurveyWorkDao());
    	surveyManager.setSurveyDao(surveyDao);
    	surveyManager.setCodeListManager(codeListManager);
    	
    	userManager = new UserManager();
    	userManager.setUserDao(new UserDao());
    	userManager.setRecordDao(new RecordDao());
	}
	
	public static void init() {
		surveyManager.init();
	}
	
	public static SQLDroidDataSource getDataSource() {
		return dataSource;
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
