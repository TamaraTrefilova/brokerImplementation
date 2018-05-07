package edu.uw.trefilovatm.cp130_0.broker;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.broker.BrokerException;
import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.MarketBuyOrder;
import edu.uw.ext.framework.order.MarketSellOrder;
import edu.uw.ext.framework.order.Order;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;
import edu.uw.trefilovatm.cp130_0.account.AccountDaoImpl;
import edu.uw.trefilovatm.cp130_0.account.AccountImpl;

/**
 * The broker class. This is the class used by investors to create and access
 * their account, and to obtain quotes and place orders.
 *
 * @author tamara
 */
public class SimpleBroker implements Broker {

	private static Logger log = Logger.getLogger(SimpleBroker.class.getName());
	private String name;
	private AccountManager acctMngr;
	private StockExchange exch;
//	private OrderManagerImpl orderMngr;
	private static Map<String, OrderManager> orderManagerMap = new HashMap<>();
	private static OrderQueue<Boolean, Order> marketOrders = new OrderQueueImpl<Boolean, Order>(null, null);

	public SimpleBroker(String name, AccountManager acctMngr, StockExchange exch) {
		log.info("Creating a boker " + this.name);
		this.name = name;
		this.acctMngr = acctMngr;
		this.exch = exch;
		if(exch!=null) {
			for(String str:exch.getTickers()) {
				if(orderManagerMap.containsValue(str)==false) {
					orderManagerMap.put(str, new OrderManagerImpl(str));
				}
			}
		}
	}

	/*
	 * typo??
	 */
	// protected SimpleBroker(String name, StockExchange exch, AccountManager
	// acctMngr) {
	//
	// }

	/**
	 * Get the name of this broker.
	 *
	 * @return the broker's name
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Create an account with the broker.
	 *
	 * @param username
	 *            the user or account name for the account
	 * @param password
	 *            the password for the new account
	 * @param balance
	 *            the initial account balance in cents
	 *
	 * @return the new account
	 *
	 * @exception BrokerException
	 *                if unable to create account
	 */

	@Override
	public Account createAccount(String username, String password, int balance) throws BrokerException {
		log.info("Creating an account " + username);
		try {
			this.acctMngr.createAccount(username, password, balance);
		} catch (AccountException e) {
			throw new BrokerException("Problems with account creating ", e);
		}
		return null;
	}

	/**
	 * Delete an account with the broker.
	 *
	 * @param username
	 *            the user or account name for the account
	 *
	 * @exception BrokerException
	 *                if unable to delete account
	 */
	@Override
	public void deleteAccount(String username) throws BrokerException {
		try {
			this.acctMngr.deleteAccount(username);
		} catch (AccountException e) {
			throw new BrokerException("Problems with account deleting ", e);
		}

	}

	/**
	 * Locate an account with the broker. The username and password are first
	 * verified and the account is returned.
	 *
	 * @param username
	 *            the user or account name for the account
	 * @param password
	 *            the password for the new account
	 *
	 * @return the account
	 *
	 * @exception BrokerException
	 *                username and/or password are invalid
	 */
	@Override
	public Account getAccount(String username, String password) throws BrokerException {
		Account accnt = new AccountImpl();
		try {
			accnt = this.acctMngr.getAccount(username);
		} catch (AccountException e) {
			throw new BrokerException("Invalid account name " + username, e);
		}
		try {
			this.acctMngr.validateLogin(username, password);
		} catch (AccountException e) {
			throw new BrokerException("Invalid password " + password + "for account " + username, e);
		}
		return accnt;
	}

	/**
	 * Get a price quote for a stock.
	 *
	 * @param ticker
	 *            the stocks ticker symbol
	 *
	 * @return the stocks current price
	 *
	 * @exception BrokerException
	 *                if unable to obtain quote
	 */

	@Override
	public StockQuote requestQuote(String ticker) throws BrokerException {
		StockQuote stockQuote = this.exch.getQuote(ticker);
		if (stockQuote == null) {
			throw new BrokerException("Unable to obtain the quote for " + ticker);
		}
		return stockQuote;
	}

	private void checkInvariants() {

	}

	public void exchangeClosed(ExchangeEvent event) {

	}


	public void priceChanged(ExchangeEvent event) {

	}
	
	public void exchangeOpen(ExchangeEvent event) {

	}
	
	protected void initializeOrderManagers() {
		
	}

	private OrderManager orderManagerLookUp(String ticker) {
		return null;
		
	}
	/**
	 * Place a market buy order with the borker.
	 *
	 * @param order
	 *            the order being placed with the broker
	 *
	 * @exception BrokerException
	 *                if unable to place order
	 */

	@Override
	public void placeOrder(MarketBuyOrder order) throws BrokerException {
		// TODO Auto-generated method stub

	}	
	
	/**
	 * Place a market sell order with the borker.
	 *
	 * @param order
	 *            the order being placed with the broker
	 *
	 * @exception BrokerException
	 *                if unable to place order
	 */

	@Override
	public void placeOrder(MarketSellOrder order) throws BrokerException {
		// TODO Auto-generated method stub

	}


	
	/**
	 * Place a stop buy order with the borker.
	 *
	 * @param order
	 *            the order being placed with the broker
	 *
	 * @exception BrokerException
	 *                if unable to place order
	 */

	@Override
	public void placeOrder(StopBuyOrder order) throws BrokerException {
		// TODO Auto-generated method stub

	}

	/**
	 * Place a stop sell order with the borker.
	 *
	 * @param order
	 *            the order being placed with the broker
	 *
	 * @exception BrokerException
	 *                if unable to place order
	 */
	@Override
	public void placeOrder(StopSellOrder order) throws BrokerException {
		// TODO Auto-generated method stub

	}

	/**
	 * Release resources used by the broker.
	 *
	 * @exception BrokerException
	 *                if an error occurs during the close operation
	 */

	@Override
	public void close() throws BrokerException {
		// TODO Auto-generated method stub

	}

}
