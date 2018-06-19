package edu.uw.trefilovatm.cp130_0.network;

import java.net.SocketException;
import java.net.UnknownHostException;

import edu.uw.ext.framework.exchange.ExchangeAdapter;
import edu.uw.ext.framework.exchange.NetworkExchangeAdapterFactory;
import edu.uw.ext.framework.exchange.StockExchange;

public class ExchangeAdapterFactoryImpl implements NetworkExchangeAdapterFactory {

	@Override
	public ExchangeAdapter newAdapter(StockExchange exchange, String multicastIP, int multicastPort, int commandPort) {
		try {
			return new NetworkExchangeAdapter(exchange, multicastIP, multicastPort, commandPort);
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

}
