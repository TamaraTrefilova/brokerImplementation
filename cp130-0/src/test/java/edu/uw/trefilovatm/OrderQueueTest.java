package edu.uw.trefilovatm;

import test.AbstractOrderQueueTest;
import edu.uw.ext.framework.broker.OrderQueue;

import java.util.Comparator;
import java.util.function.BiPredicate;

import edu.uw.ext.framework.order.Order;
import edu.uw.ext.framework.order.StopBuyOrder;
import edu.uw.ext.framework.order.StopSellOrder;
import edu.uw.trefilovatm.cp130_0.broker.OrderQueueImpl;

/*****************************************************************************
 * Replace these imports with the import of your implementing classes.       *
 *****************************************************************************/

/**
 * Concrete subclass of AbstractQueueTest, provides implementations of the 
 * createStopBuyOrderQueue, createStopSellOrderQueue and createAnyOrderQueue
 * methods which create instances of "my" OrderQueue implementation class, using
 * "my" Comparator implementations.
 */
public class OrderQueueTest extends AbstractOrderQueueTest {
    /**
     * Creates an instance of "my" OrderQueue implementation class, using
     * an instance of "my" implementation of Comparator that is intended to
     * order StopBuyOrders.
     *
     * @param filter the OrderDispatch filter to be used
     * 
     * @return a new OrderQueue instance
     */
    @Override
    protected final OrderQueue<Integer,StopBuyOrder> createStopBuyOrderQueue(
                        final BiPredicate<Integer, StopBuyOrder> filter) {
        /*********************************************************************
         * This needs to be an instance of your OrderQueue and Comparator.   *
         *********************************************************************/
        final Comparator<StopBuyOrder> ascending = new Comparator<StopBuyOrder>() {
			@Override
			public int compare(StopBuyOrder order1, StopBuyOrder order2) {
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
        return new OrderQueueImpl<>(0, filter, ascending);
    }

    /**
     * Creates an instance of "my" OrderQueue implementation class, using
     * an instance of "my" implementation of Comparator that is intended to
     * order StopSellOrders.
     *
     * @param filter the OrderDispatch filter to be used
     * 
     * @return a new OrderQueue instance
     */
    @Override
    protected final OrderQueue<Integer,StopSellOrder> createStopSellOrderQueue(
                          final BiPredicate<Integer, StopSellOrder> filter) {
        /*********************************************************************
         * This needs to be an instance of your OrderQueue and Comparator.   *
         *********************************************************************/
    	final Comparator<StopSellOrder> descending = new Comparator<StopSellOrder>() {
			@Override
			public int compare(StopSellOrder order1, StopSellOrder order2) {
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
        return new OrderQueueImpl<>(0, filter, descending);
//        final Comparator<StopSellOrder> descending = new 
//        return new OrderQueueImpl<>(0, (Integer t, filter, descending);
 
    }
    
    /**
     * Creates an instance of "my" OrderQueue implementation class, the queue
     * will order the Orders according to their natural ordering.
     *
     * @param filter the OrderDispatch filter to be used
     * 
     * @return a new OrderQueue instance
     */
    @Override
    protected final OrderQueue<Boolean,Order> createAnyOrderQueue(
                            final BiPredicate<Boolean, Order> filter) {
        /*********************************************************************
         * This needs to be an instance of your OrderQueue.                  *
         *********************************************************************/
        return new OrderQueueImpl<Boolean, Order>(true, (Boolean t, Order o)->t);
    }

}
