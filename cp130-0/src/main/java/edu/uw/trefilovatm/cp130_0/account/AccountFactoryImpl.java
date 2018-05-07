package edu.uw.trefilovatm.cp130_0.account;

import java.util.logging.Logger;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountFactory;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.dao.DaoFactory;
import edu.uw.ext.framework.dao.DaoFactoryException;

public class AccountFactoryImpl implements AccountFactory {

	private final int MIN_BALANCE = 100000; 
	private static Logger log = Logger.getLogger(AccountFactoryImpl.class.getName());
	AccountManager manager = null;
	
	public AccountFactoryImpl() {
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see edu.uw.ext.framework.account.AccountFactory#newAccount(java.lang.String, byte[], int)
	 */
	public Account newAccount(String accountName, byte[] hashedPassword, int initialBalance) {
		log.info("Getting account "+ accountName);
		if(initialBalance<MIN_BALANCE) {
			return null;
		}	
		Account account = new AccountImpl();
		account.setBalance(initialBalance);
		account.setPasswordHash(hashedPassword);
		try {
			account.setName(accountName);
		} catch (AccountException e) {
			return null;
		}	
		account.registerAccountManager(getManager());
		return account;
	}
	
	private AccountManager getManager() {
		if(manager==null) {
			try(ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml")) {
		        DaoFactory daoFactory = appContext.getBean("DaoFactory", DaoFactory.class);
		        try {
					manager = new AccountManagerImpl(daoFactory.getAccountDao());
				} catch (DaoFactoryException e) {
					throw new RuntimeException(e);
				} 
			}
		}
		return manager;
	}

}
