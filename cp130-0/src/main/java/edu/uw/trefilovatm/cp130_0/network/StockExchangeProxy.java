package edu.uw.trefilovatm.cp130_0.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.ExchangeListener;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.Order;

public class StockExchangeProxy implements StockExchange {

	private static Logger log = Logger.getLogger(StockExchangeProxy.class.getName());
	private final int multicastPort;
	private final String commandIP;
	private final int commandPort;
	private final InetAddress group;
	
	public StockExchangeProxy(String multicastIP, int multicastPort, String commandIP, int commandPort) throws UnknownHostException {
		log.info("multucastIP "+multicastIP);
		log.info("multicastPort "+multicastPort);
		log.info("commandIP "+commandIP);
		log.info("commandPort "+commandPort);
		group = InetAddress.getByName(multicastIP);
		this.multicastPort = multicastPort;
		this.commandIP = commandIP;
		this.commandPort = commandPort;
		
		Thread multi = new Thread ("multicast reader") {
			@Override
			public void run() {
				try {
					acceptMulticasts();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		multi.start();
	}

	private void acceptMulticasts() throws IOException {
		try(MulticastSocket socket = new MulticastSocket(multicastPort)) {
			socket.joinGroup(group);
	
			while(true) {
				DatagramPacket packet;
			    byte[] buf = new byte[256];
			    packet = new DatagramPacket(buf, buf.length);
			    socket.receive(packet);
	
			    try {
			    	handleMulticast(packet);
			    } catch(Exception e) {
			    	System.out.println("ERROR:"+e.getMessage());
			    }
			}
		}
	}

	private void handleMulticast(DatagramPacket packet) {
		String received = new String(packet.getData());
		System.out.println("Received: " + received);
		String [] args = received.split(":");
		switch(args[0]) {
		case NetworkConstants.OPEN_EVNT: {
		    ExchangeEvent event = ExchangeEvent.newOpenedEvent(this);
		    for(ExchangeListener listeners:listeners) {
		    	listeners.exchangeOpened(event);
		    }
		}
		    break;
		case NetworkConstants.CLOSED_EVNT: {
			ExchangeEvent event = ExchangeEvent.newClosedEvent(this);
		    for(ExchangeListener listeners:listeners) {
		    	listeners.exchangeClosed(event);
		    }
		}
		    break;
		case NetworkConstants.PRICE_CHANGE_EVNT: {
			String sharesStr = args[2].trim();
			int shares = Integer.parseInt(sharesStr);
			ExchangeEvent event = ExchangeEvent.newPriceChangedEvent(this, args[1], shares);
		    for(ExchangeListener listeners:listeners) {
		    	listeners.priceChanged(event);
		    }
		}
		    break;
		}
	}
	
	@Override
	public boolean isOpen() {
		String isOpen = askServer(NetworkConstants.GET_STATE_CMD);
		return isOpen.equals(NetworkConstants.OPEN);
	}

	private String askServer(String cmd) {
		try {
			return requestServer(cmd);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String requestServer(String cmd) throws UnknownHostException, IOException {
		try (Socket echoSocket = new Socket(commandIP, commandPort)) {
		    try(PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true)) {
		    	out.println(cmd);
		    	out.flush();
		    	System.out.println("Sent command:"+cmd);
			    try(InputStreamReader isr = new InputStreamReader(echoSocket.getInputStream())) {
			    	try (BufferedReader in = new BufferedReader(isr)) {
			    		String reply = in.readLine();
				    	System.out.println("Got answer:"+reply);
				    	return reply;
			    	}
			    }
		    }
		}
	}

	@Override
	public String[] getTickers() {
		String tickers = askServer(NetworkConstants.GET_TICKERS_CMD);
		return tickers.split(NetworkConstants.DELIM);
	}

	@Override
	public StockQuote getQuote(String ticker) {
		String quote = askServer(NetworkConstants.GET_QUOTE_CMD+NetworkConstants.DELIM+ticker);
		return "-1".equals(quote)?null:new StockQuote(ticker, Integer.parseInt(quote.trim()));
	}

	private final List<ExchangeListener> listeners = new ArrayList<>();
	
	@Override
	public void addExchangeListener(ExchangeListener l) {
		listeners.add(l);
	}

	@Override
	public void removeExchangeListener(ExchangeListener l) {
		listeners.remove(l);
	}

	@Override
	public int executeTrade(Order order) {
		StringBuilder builder = new StringBuilder();
		builder.append(NetworkConstants.EXECUTE_TRADE_CMD);
		builder.append(NetworkConstants.DELIM);
		builder.append(order.isBuyOrder()?NetworkConstants.BUY_ORDER:NetworkConstants.SELL_ORDER);
		builder.append(NetworkConstants.DELIM);
		builder.append(order.getAccountId());
		builder.append(NetworkConstants.DELIM);
		builder.append(order.getStockTicker());
		builder.append(NetworkConstants.DELIM);
		builder.append(order.getNumberOfShares());
		String executionPrice = askServer(builder.toString());
		return Integer.parseInt(executionPrice.trim());
	}

}
