package edu.uw.trefilovatm.cp130_0.account;

import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.ext.framework.dao.DaoFactory;
import edu.uw.ext.framework.dao.DaoFactoryException;

public class DaoFactoryImpl implements DaoFactory {

	static final Persistor persistor = new FilePersistor();

	@Override
	public AccountDao getAccountDao() throws DaoFactoryException {
		AccountDao accountDao = new AccountDaoImpl(persistor);
		return accountDao;
	}
}
