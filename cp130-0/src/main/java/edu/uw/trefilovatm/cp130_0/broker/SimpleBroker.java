package edu.uw.trefilovatm.cp130_0.broker;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.logging.Logger;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.broker.BrokerException;
import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.ExchangeListener;
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
public class SimpleBroker implements Broker, ExchangeListener {

	private static Logger log = Logger.getLogger(SimpleBroker.class.getName());
	private String name;
	private AccountManager acctMngr;
	private StockExchange exch;
	private Map<String, OrderManagerImpl> orderManagerMap = new HashMap<>();
	private AsyncOrderQueueImpl<Boolean, Order> marketOrders = new AsyncOrderQueueImpl<Boolean, Order>(false, 
			new BiPredicate<Boolean, Order>() {
				@Override
				public boolean test(Boolean t, Order u) {
					return t;
				}});

	Consumer<Order> processor = new Consumer<Order>(){
		@Override
		public void accept(Order order) {
			int price = exch.executeTrade(order);
			creditAccount(order.getAccountId(), order, price);
			log.info("Executed:"+order+" by "+price);
		}};

	Consumer<StopBuyOrder> processorBuy = new Consumer<StopBuyOrder>(){
		@Override
		public void accept(StopBuyOrder order) {
			int price = exch.executeTrade(order);
			creditAccount(order.getAccountId(), order, price);
			log.info("Executed stop buy:"+order+" by "+price);
		}};

	Consumer<StopSellOrder> processorSell = new Consumer<StopSellOrder>(){
		@Override
		public void accept(StopSellOrder order) {
			int price = exch.executeTrade(order);
			creditAccount(order.getAccountId(), order, price);
			log.info("Executed stop sell:"+order+" by "+price);
		}};
			
	private void creditAccount(String accountId, Order order, int price) {
		try {
			acctMngr.getAccount(accountId).reflectOrder(order, price);
		} catch (AccountException e) {
			e.printStackTrace();
		}
	}
		
	public SimpleBroker(String name, AccountManager acctMngr, StockExchange exch) {
		log.info("Creating a broker " + this.name);
		this.name = name;
		this.acctMngr = acctMngr;
		this.exch = exch;
		if(exch!=null) {
			for(String ticker:exch.getTickers()) {
				orderManagerLookUp(ticker);
			}
		}
		marketOrders.setOrderProcessor(processor);
		marketOrders.setThreshold(exch.isOpen());
		exch.addExchangeListener(this);
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
		Account account;
		try {
			account = acctMngr.createAccount(username, password, balance);
			account.registerAccountManager(acctMngr);
		} catch (AccountException e) {
			throw new BrokerException("Problems with account creating ", e);
		}
		return account;
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
		Account accnt = null;
		try {
			accnt = acctMngr.getAccount(username);
			if(accnt==null) {
				throw new BrokerException("Account is not exist");
			}
			accnt.registerAccountManager(acctMngr);
		} catch (AccountException e) {
			throw new BrokerException("Invalid account name " + username, e);
		}
		try {
			if(! acctMngr.validateLogin(username, password)) {
				throw new BrokerException("Invalid password " + password + "for account " + username);
			}
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
		marketOrders.setThreshold(false);
	}

	public void priceChanged(ExchangeEvent event) {
		OrderManager manager = orderManagerLookUp(event.getTicker());
		if(manager!=null) {
			manager.adjustPrice(event.getPrice());
		}
	}
	
	protected void initializeOrderManagers() {
	}

	private OrderManager orderManagerLookUp(String ticker) {
		OrderManagerImpl manager = orderManagerMap.get(ticker);
		if(manager==null) {
			orderManagerMap.put(ticker, manager = new OrderManagerImpl(ticker, exch.getQuote(ticker).getPrice()));
			manager.setBuyOrderProcessor(processorBuy);			
			manager.setSellOrderProcessor(processorSell);			
		}
		return manager;
	}
	/**
	 * Place a market buy order with the broker.
	 *
	 * @param order
	 *            the order being placed with the broker
	 *
	 * @exception BrokerException
	 *                if unable to place order
	 */

	@Override
	public void placeOrder(MarketBuyOrder order) throws BrokerException {
		marketOrders.enqueue(order);
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
		marketOrders.enqueue(order);
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
		orderManagerLookUp(order.getStockTicker()).queueOrder(order);
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
		orderManagerLookUp(order.getStockTicker()).queueOrder(order);
	}

	/**
	 * Release resources used by the broker.
	 *
	 * @exception BrokerException
	 *                if an error occurs during the close operation
	 */

	@Override
	public void close() throws BrokerException {
		for(OrderManagerImpl manager:orderManagerMap.values()) {
			manager.close();
		};
		marketOrders.close(); 
	}

	@Override
	public void exchangeOpened(ExchangeEvent event) {
		marketOrders.setThreshold(true);
	}

}
