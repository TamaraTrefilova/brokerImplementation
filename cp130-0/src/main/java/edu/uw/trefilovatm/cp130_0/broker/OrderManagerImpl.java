package edu.uw.trefilovatm.cp130_0.broker;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
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

	private BiPredicate<Integer, StopBuyOrder> filterBuy;
	private BiPredicate<Integer, StopSellOrder> filterStop;
	
	protected OrderQueueImpl<Integer, StopBuyOrder> stopBuyOrder;
	protected OrderQueueImpl<Integer, StopSellOrder> stopSellOrder;
	private String stockTickerSymbol;

	private int price;

	public OrderManagerImpl(String stockTickerSymbol) {
		this.stockTickerSymbol = stockTickerSymbol;
	}

	public OrderManagerImpl(String stockTickerSymbol, int price) {
		this.stockTickerSymbol = stockTickerSymbol;
		this.price = price;
		
        final Comparator<StopBuyOrder> ascending = new Comparator<StopBuyOrder>() {
			@Override
			public int compare(StopBuyOrder order1, StopBuyOrder order2) {
				if(order1.getPrice()<order2.getPrice()) {
					return 1;
				} else if(order1.getPrice()>order2.getPrice()) {
					return -1;
				} else {
					if(order1.getOrderId()>order2.getOrderId()) {
						return 1;
					} else if(order1.getOrderId()<order2.getOrderId()) {
						return -1;
					}
					return 0;
				}
			}
        };
        
    	final Comparator<StopSellOrder> descending = new Comparator<StopSellOrder>() {
			@Override
			public int compare(StopSellOrder order1, StopSellOrder order2) {
				if(order1.getPrice()>order2.getPrice()) {
					return 1;
				} else if(order1.getPrice()<order2.getPrice()) {
					return -1;
				} else {
					if(order1.getOrderId()>order2.getOrderId()) {
						return 1;
					} else if(order1.getOrderId()<order2.getOrderId()) {
						return -1;
					}
					return 0;
				}
			}
        };
        
        filterBuy = new BiPredicate<Integer, StopBuyOrder>() {
			@Override
			public boolean test(Integer price, StopBuyOrder order) {
				return order.getPrice()<=price;
			}
        };

        filterStop = new BiPredicate<Integer, StopSellOrder>() {
			@Override
			public boolean test(Integer price, StopSellOrder order) {
				return order.getPrice()>=price;
			}
        };
        
		this.stopBuyOrder = new OrderQueueImpl<Integer, StopBuyOrder>(price,  filterBuy, ascending);
		this.stopSellOrder = new OrderQueueImpl<Integer, StopSellOrder>(price, filterStop, descending);
		stopBuyOrder.setThreshold(price);
		stopSellOrder.setThreshold(price);
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
		this.price = price;
		stopBuyOrder.setThreshold(price);
		stopSellOrder.setThreshold(price);
	}

	/**
	 * Queue a stop buy order.
	 *
	 * @param order
	 *            the order to be queued
	 */
	@Override
	public void queueOrder(StopBuyOrder order) {
		stopBuyOrder.enqueue(order);
	}

	/**
	 * Queue a stop sell order.
	 *
	 * @param order
	 *            the order to be queued
	 */
	@Override
	public void queueOrder(StopSellOrder order) {
		stopSellOrder.enqueue(order);
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
		stopBuyOrder.setOrderProcessor(processor);
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
		stopSellOrder.setOrderProcessor(processor);
	}
}
