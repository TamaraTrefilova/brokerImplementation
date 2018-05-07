package edu.uw.trefilovatm.cp130_0.broker;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import edu.uw.ext.framework.broker.OrderManager;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;

/**
 * Maintains queues of different types of orders and requests the execution of
 * orders when price conditions allow their execution.
 *
 * @author tamara
 */
public class OrderManagerImpl implements OrderManager {

	private static Logger log = Logger.getLogger(OrderManagerImpl.class.getName());

	protected OrderQueueImpl<Integer, StopBuyOrder> stopBuyOrder;
	protected OrderQueueImpl<Integer, StopSellOrder> stopSellOrder;
	private String stockTickerSymbol;

	private int price;

	public OrderManagerImpl(String stockTickerSymbol) {
		this.stockTickerSymbol = stockTickerSymbol;
		this.stopBuyOrder = new OrderQueueImpl<Integer, StopBuyOrder>(price, null); 
		this.stopSellOrder = new OrderQueueImpl<Integer, StopSellOrder>(price, null);
	}

	public OrderManagerImpl(String stockTickerSymbol, int price) {
		this.stockTickerSymbol = stockTickerSymbol;
		this.price = price;
	}

	/**
	 * Gets the stock ticker symbol for the stock managed by this stock manager.
	 *
	 * @return the stock ticker symbol
	 */
	@Override
	public String getSymbol() {
		return stockTickerSymbol;
	}

	/**
	 * Respond to a stock price adjustment by setting threshold on dispatch filters.
	 *
	 * @param price
	 *            the new price
	 */
	@Override
	public void adjustPrice(int price) {
		// TODO Auto-generated method stub

	}

	/**
	 * Queue a stop buy order.
	 *
	 * @param order
	 *            the order to be queued
	 */
	@Override
	public void queueOrder(StopBuyOrder order) {
		// TODO Auto-generated method stub

	}

	/**
	 * Queue a stop sell order.
	 *
	 * @param order
	 *            the order to be queued
	 */
	@Override
	public void queueOrder(StopSellOrder order) {
		// TODO Auto-generated method stub

	}

	/**
	 * Registers the processor to be used during buy order processing. This will be
	 * passed on to the order queues as the dispatch callback.
	 *
	 * @param processor
	 *            the callback to be registered
	 */
	//lambda
	@Override
	public void setBuyOrderProcessor(Consumer<StopBuyOrder> processor) {
		// TODO Auto-generated method stub

	}

	/**
	 * Registers the processor to be used during sell order processing. This will be
	 * passed on to the order queues as the dispatch callback.
	 *
	 * @param processor
	 *            the callback to be registered
	 */
	//lambda
	@Override
	public void setSellOrderProcessor(Consumer<StopSellOrder> processor) {
		// TODO Auto-generated method stub

	}

}
