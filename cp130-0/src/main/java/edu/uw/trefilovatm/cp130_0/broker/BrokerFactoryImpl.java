package edu.uw.trefilovatm.cp130_0.broker;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.broker.Broker;
import edu.uw.ext.framework.broker.BrokerFactory;
import edu.uw.ext.framework.exchange.StockExchange;


/**
 * Factory class for the creation of a broker.
 * Implementations of this class must provide a no argument constructor.
 *
 * @author tamara
 */
//No argument constructor
public class BrokerFactoryImpl implements BrokerFactory{
    /**
     * Instantiates a new broker instance.
     *
     * @param name the broker's name
     * @param acctMngr the account manager to be used by the broker
     * @param exch the exchange to be used by the broker
     *
     * @return a newly created broker instance
     *
     * @see Broker
     */

	@Override
	public Broker newBroker(String name, AccountManager acctMngr, StockExchange exch) {
		Broker broker = new SimpleBroker(name, acctMngr, exch);
		return broker;
	}

}
