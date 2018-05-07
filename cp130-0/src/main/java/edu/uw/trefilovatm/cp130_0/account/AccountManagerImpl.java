
package edu.uw.trefilovatm.cp130_0.account;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Logger;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.dao.AccountDao;

/**
 *
 * Implements Account interface. Stands for the basic account operations;
 * create, delete, authentication and persistence.
 * 
 * @author tamara
 *
 */

public class AccountManagerImpl implements AccountManager {

	private static Logger log = Logger.getLogger(AccountManagerImpl.class.getName());

	private final AccountDao dao;

	public AccountManagerImpl(AccountDao dao) {
		this.dao = dao;
	}

	@Override
	public void close() throws AccountException {
		dao.close();
	}

	@Override
	public Account createAccount(String accountName, String password, int balance) throws AccountException {
		log.info("Creating account "+ accountName);
		Account acc = dao.getAccount(accountName);
		if (acc != null) {
			throw new AccountException("Account " + accountName + " is already exists.");
		}
		Account account = new AccountImpl();
		account.setName(accountName);
		setPasswordHash(password, account);
		account.setBalance(balance);
		dao.setAccount(account);
		return account;
	}

	private void setPasswordHash(String password, Account account) throws AccountException {
		log.info("Setting password for"+ account.getName());
		try {
			byte[] pswd = hashPassword(password);
			account.setPasswordHash(pswd);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new AccountException("Problems with hashing password", e);
		}
	}

	private byte[] hashPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		log.info("Hashing password for"+ password);
		MessageDigest msg = MessageDigest.getInstance("SHA-1");
		byte[] strToByte = password.getBytes("UTF-8");
		byte[] pswd = msg.digest(strToByte);
		return pswd;
	}

	@Override
	public void deleteAccount(String accountName) throws AccountException {
		log.info("Deleting account "+accountName);
		dao.deleteAccount(accountName);
	}

	@Override
	public Account getAccount(String accountName) throws AccountException {
		log.info("Retrieving account "+accountName);
		Account account = dao.getAccount(accountName);
		return account;
	}

	@Override
	public void persist(Account account) throws AccountException {
		log.info("Persist account "+account.getName());
		dao.setAccount(account);
	}

	@Override
	/**
	 * Check whether a login is valid. An account must exist with the account name
	 * and the password must match.
	 *
	 * @param accountName
	 *            name of account the password is to be validated for
	 * @param password
	 *            password is to be validated
	 *
	 * @return true if password is valid for account identified by accountName
	 *
	 * @exception AccountException
	 *                if error occurs accessing accounts
	 */
	public boolean validateLogin(String accountName, String paswd) throws AccountException {
		
		log.info("Validate password for account "+accountName);
		Account account = dao.getAccount(accountName);
		if (account == null) {
			return false;
		}
		byte[] hashPasswd = account.getPasswordHash();
		try {
			byte[] calculatedHash = hashPassword(paswd);
			return Arrays.equals(hashPasswd, calculatedHash);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new AccountException("Problems with hashing password", e);
		}
	}

}
