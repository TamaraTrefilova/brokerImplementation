package edu.uw.trefilovatm.cp130_0.network;

import java.net.UnknownHostException;

import edu.uw.ext.framework.exchange.NetworkExchangeProxyFactory;
import edu.uw.ext.framework.exchange.StockExchange;

public class ExchangeProxyFactoryImpl implements NetworkExchangeProxyFactory {

	@Override
	public StockExchange newProxy(String multicastIP, int multicastPort, String commandIP, int commandPort) {		
		try {
			return new StockExchangeProxy(multicastIP, multicastPort, commandIP, commandPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

}
