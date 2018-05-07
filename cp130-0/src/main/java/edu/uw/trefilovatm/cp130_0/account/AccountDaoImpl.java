package edu.uw.trefilovatm.cp130_0.account;

import java.util.logging.Logger;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.dao.AccountDao;

/**
 * 
 * Defines the methods needed to store and load accounts from a persistent
 * storage mechanism. The implementing class must provide a no argument
 * constructor.
 *
 * @author tamara
 *
 */
public class AccountDaoImpl implements AccountDao {	
	/**
	* 
	*/
	public AccountDaoImpl(Persistor persistor) {
		super();
		this.persistor = persistor;
	}

	private static Logger log = Logger.getLogger(AccountDaoImpl.class.getName());
	private final Persistor persistor;

	@Override
	public void close() throws AccountException {

	}

	@Override
	/**
	 * Remove the account.
	 *
	 * @param accountName
	 *            the name of the account to be deleted
	 *
	 * @exception AccountException
	 *                if operation fails
	 */
	public void deleteAccount(String accountName) throws AccountException {
		log.info("Deleting account " + accountName);
		persistor.delete(accountName);

	}

	@Override
	/**
	 * Remove all accounts. This is primarily available to facilitate testing.
	 *
	 * @exception AccountException
	 *                if operation fails
	 */
	public void reset() throws AccountException {
		log.info("Performing reset");
		persistor.reset();
	}

	@Override
	public Account getAccount(String accountName) {
		log.info("Looking up account " + accountName);
		return persistor.read(accountName);

	}

	@Override
	/**
	 * Adds or updates an account.
	 *
	 * @param account
	 *            the account to add/update
	 *
	 * @exception AccountException
	 *                if operation fails
	 */
	public void setAccount(Account account) throws AccountException {
		log.info("Saving account " + account.getName());
		persistor.write(account);
	}

}
