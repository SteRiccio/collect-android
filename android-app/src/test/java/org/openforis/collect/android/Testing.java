package org.openforis.collect.android;

import org.junit.After;
import org.junit.Before;
import org.openforis.collect.persistence.UserDao;

import android.test.AndroidTestCase;
import android.util.Log;

public class Testing extends AndroidTestCase{

	@Before
	public void setUp() throws Exception {
		Log.e("setUp","=====");
	}
	
	@After
	public void tearDown() throws Exception {
		Log.e("tearDown","===");
	}

	protected UserDao userDao;

	public void testCRUD() throws Exception {
		/*this.userDao = new UserDao();
		JdbcDaoSupport jdbcDao = new JdbcDaoSupport();

		User user = new User();
		user.setEnabled(Boolean.TRUE);
		user.setName("user1");
		user.setPassword("pass1");
		user.addRole("role1");
		user.addRole("role2");
		
		userDao.insert(user);
		Integer id = user.getId();
		
		User reloaded = userDao.loadById(id);
		Assert.assertNotNull(reloaded);
		
		Assert.assertEquals(user, reloaded);
		
		userDao.delete(id);
		
		reloaded = userDao.loadById(id);
		Assert.assertNull(reloaded);
		
		JdbcDaoSupport.close();*/
	}
}
