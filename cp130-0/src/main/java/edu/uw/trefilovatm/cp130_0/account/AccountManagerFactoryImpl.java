package edu.uw.trefilovatm.cp130_0.account;

import java.util.logging.Logger;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.account.AccountManagerFactory;
import edu.uw.ext.framework.dao.AccountDao;

public class AccountManagerFactoryImpl implements AccountManagerFactory{
	
	private static Logger log = Logger.getLogger(AccountManagerFactoryImpl.class.getName());
	public AccountManager newAccountManager(AccountDao dao) {
		log.info("Creating account manager");
		return new AccountManagerImpl(dao);
	}
}
