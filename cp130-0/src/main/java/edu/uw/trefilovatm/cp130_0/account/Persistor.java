package edu.uw.trefilovatm.cp130_0.account;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;

public interface Persistor {

	   /**
     * Lookup an account in based on account name.
     *
     * @param accountName the name of the desired account
     *
     * @return the account if located otherwise null
     */
    Account read(String accountName);

    /**
     * Adds or updates an account.
     *
     * @param account the account to add/update
     *
     * @exception AccountException if operation fails
     */
    void write(Account account) throws AccountException;

    /**
     * Remove the account.
     *
     * @param accountName the name of the account to be deleted
     *
     * @exception AccountException if operation fails
     */
    void delete(String accountName) throws AccountException;

    /**
     * Remove all accounts.  This is primarily available to facilitate testing.
     *
     * @exception AccountException if operation fails
     */
    void reset() throws AccountException;

}
