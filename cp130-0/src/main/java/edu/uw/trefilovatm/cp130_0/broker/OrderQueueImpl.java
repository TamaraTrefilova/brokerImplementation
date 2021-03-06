package edu.uw.trefilovatm.cp130_0.broker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import edu.uw.ext.framework.broker.OrderQueue;
import edu.uw.ext.framework.order.MarketBuyOrder;
import edu.uw.ext.framework.order.Order;

/**
 * A priority queue of orders, with the additional semantics of a dispatch
 * threshold. Only dispatchable orders, orders that meet the dispatch threshold,
 * may be dequeued. Dispatchable orders are orders that meet some externally
 * defined criteria in addition to being at the top of the queue. OrderQueues
 * are configured with a dispatch filter, upon construction, the dispatch filter
 * will implement the dispatch criteria. and is of type BiPredicate&lt;T,E&gt;.
 * 
 * @author tamara
 *
 * @param <T>
 *            the dispatch threshold type.
 * @param <E>
 *            the type held by the order queue.
 * 
 * @see java.util.function.BiPredicate
 */

public class OrderQueueImpl<T, E extends Order> implements OrderQueue<T, E> {
	
	private T threshold;
	private BiPredicate<T, E> filter;
	private Comparator <E>comp;
	private Consumer<E> orderProcessor;
	private TreeSet<E> queue;
	
	public OrderQueueImpl(T treshhold, BiPredicate<T, E> filter) {
		this.threshold =  treshhold;
		this.filter = filter;
		queue = new TreeSet<>();
	}

	public OrderQueueImpl(T treshhold, BiPredicate<T, E> filter, Comparator <E>comp) {
		this.threshold =  treshhold;
		this.filter = filter;
		queue = new TreeSet<>(comp); 
	}

	public static void main(String[] args) {
		BiPredicate<Integer, MarketBuyOrder> filterBuy = new BiPredicate<Integer, MarketBuyOrder>() {
			@Override
			public boolean test(Integer price, MarketBuyOrder order) {
				return false;
			}
		};

//		OrderQueueImpl<Integer, MarketBuyOrder> marketBuyOrder = new OrderQueueImpl<>(filterBuy);
	}

	/**
	 * Adds the specified order to the queue. Subsequent to adding the order
	 * dispatches any dispatchable orders.
	 *
	 * @param order
	 *            the order to be added to the queue
	 */

	@Override
	public void enqueue(E order) {
		queue.add(order);
		dispatchOrders();
	}

	/**
	 * Removes the highest dispatchable order in the queue. If there are orders in
	 * the queue but they do not meet the dispatch threshold an order will not be
	 * removed and null will be returned.
	 *
	 * @return the highest order in the queue, or null if there are no dispatchable
	 *         orders in the queue
	 */

	@Override
	public E dequeue() {
		// TODO Auto-generated method stub
		E order = null;
		if(!queue.isEmpty()) {
			order = queue.first();	
			if(filter.test(threshold, order)) {
				return queue.pollFirst();
			} 
		}
		return null;
	}

	/**
	 * Executes the callback for each dispatchable order. Each dispatchable order is
	 * in turn removed from the queue and passed to the callback. If no callback is
	 * registered the order is simply removed from the queue.
	 */
	@Override
	public void dispatchOrders() {
		List<E> ordersToDispatch = new ArrayList<>();
		for(E order:queue) {
			if(filter.test(getThreshold(), order)) {
				ordersToDispatch.add(order);
			}
		}
		for(E order:ordersToDispatch) {
			queue.remove(order);
			if(orderProcessor!=null) {
				orderProcessor.accept(order);
			}			
		}
	}	
	

	/**
	 * Registers the consumer to be used during order processing.
	 *
	 * @param proc
	 *            the consumer to be registered
	 */

	@Override
	public void setOrderProcessor(Consumer<E> proc) {
		this.orderProcessor = proc;
	}

	/**
	 * Adjusts the threshold and subsequently dispatches any dispatchable orders.
	 *
	 * @param threshold
	 *            - the new threshold
	 */

	@Override
	public void setThreshold(T threshold) {
		this.threshold = threshold; 
		dispatchOrders();
	}

	/**
	 * Obtains the current threshold value.
	 *
	 * @return the current threshold
	 */

	@Override
	public T getThreshold() {
		return this.threshold;
	}
}
