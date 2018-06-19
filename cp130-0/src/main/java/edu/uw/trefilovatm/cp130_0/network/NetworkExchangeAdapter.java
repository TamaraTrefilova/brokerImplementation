package edu.uw.trefilovatm.cp130_0.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.springframework.util.StringUtils;

import edu.uw.ext.framework.exchange.ExchangeAdapter;
import edu.uw.ext.framework.exchange.ExchangeEvent;
import edu.uw.ext.framework.exchange.StockExchange;
import edu.uw.ext.framework.exchange.StockQuote;
import edu.uw.ext.framework.order.MarketBuyOrder;
import edu.uw.ext.framework.order.MarketSellOrder;

public class NetworkExchangeAdapter implements ExchangeAdapter {

	private static Logger log = Logger.getLogger(NetworkExchangeAdapter.class.getName());
	private final StockExchange exchange;
	private final int commandPort;
	private final int multicastPort;
	private final InetAddress group;
	private final DatagramSocket udpSocket;


	public NetworkExchangeAdapter(StockExchange exchange, String multicastIP, int multicastPort, int commandPort) 
			throws UnknownHostException, SocketException {
		log.info("exchange created");
		log.info("multucastIP "+multicastIP);
		log.info("multicastPort "+multicastPort);
		log.info("commandPort "+commandPort);
		group = InetAddress.getByName(multicastIP);
		this.exchange = exchange; 
		this.multicastPort = multicastPort;
		this.commandPort = commandPort;
		udpSocket = new DatagramSocket();
		exchange.addExchangeListener(this);
		
		startServer();
	}

	private volatile boolean acceptingConnections = true;
	
	private void startServer() {
		Thread server = new Thread() {
			@Override
			public void run() {
				ServerSocket server = null;
				try {
					server = new ServerSocket(commandPort);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				acceptingConnections = true;
				while(acceptingConnections) {
						System.out.println("Client has been accepted.");
						Socket client;
						try {
							client = server.accept();
							handleRequest(client);
						} catch (IOException e) {
							e.printStackTrace();
							acceptingConnections=false;
						}
				}
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		server.start();
	}

	protected void handleRequest(final Socket client) {
		Thread requestHandler = new Thread() {
			@Override
			public void run() {
				log.info("Handler started");
				try {
					handleCommand(client);					
				} catch(Exception e) {
					e.printStackTrace();
					log.severe("Handler failed "+e.getMessage());
				} finally {
					try {
						client.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				log.info("Handler finished.");
			}
		};
		requestHandler.start();
	}

	protected void handleCommand(Socket client) throws Exception {
		try(InputStreamReader isr = new InputStreamReader(client.getInputStream())) {
			try(BufferedReader reader = new BufferedReader(isr)) {
				try(PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
					String command = reader.readLine();
					System.out.println("Command:"+command);
					String [] args = command.split(NetworkConstants.DELIM);
					switch(args[0]) {
					case NetworkConstants.GET_STATE_CMD: 
						if(exchange.isOpen()) {
							out.println(NetworkConstants.OPEN);
						} else {
							out.println(NetworkConstants.CLOSED);
						}
						break;
					case NetworkConstants.GET_TICKERS_CMD: 
						String [] tickers = exchange.getTickers();
						out.println(StringUtils.arrayToDelimitedString(tickers, NetworkConstants.DELIM));
						break;
					case NetworkConstants.GET_QUOTE_CMD:
						StockQuote quote = exchange.getQuote(args[1]);
						if(quote==null) {
							out.println("-1");
						} else {
							out.println(""+quote.getPrice());
						}
						break;
					case NetworkConstants.EXECUTE_TRADE_CMD:
						switch(args[1]) {
						case NetworkConstants.BUY_ORDER: {
							MarketBuyOrder order = new MarketBuyOrder(args[2], Integer.parseInt(args[4].trim()), args[3]);
							int price = exchange.executeTrade(order);
							out.println(price);
						}
							break;
						case NetworkConstants.SELL_ORDER: {
							MarketSellOrder sellorder = new MarketSellOrder(args[2], Integer.parseInt(args[4].trim()), args[3]);
							int price = exchange.executeTrade(sellorder);
							out.println(price);
						}
							break;
						default:
							throw new Exception("Couldn't handle command "+command);
						}
						break;
					default:
						throw new Exception("Couldn't handle command "+command);
					}
					out.flush();
				}
			}
		}
	}

	private void multicastEvent(ExchangeEvent event) throws IOException {
		String eventStr=null;
		switch(event.getEventType()) {
		case CLOSED:
			eventStr = NetworkConstants.CLOSED_EVNT;
			break;
		case OPENED:
			eventStr = NetworkConstants.OPEN_EVNT;
			break;
		case PRICE_CHANGED:
			eventStr = NetworkConstants.PRICE_CHANGE_EVNT+NetworkConstants.DELIM+event.getTicker()+NetworkConstants.DELIM+event.getPrice();
			break;
		}
        byte [] data = eventStr.getBytes();
        udpSocket.send(new DatagramPacket(data, data.length, group, multicastPort));
	}

	private void sendEvent(ExchangeEvent event) {
		try {
			multicastEvent(event);
		} catch (IOException e) {
			log.severe("Couldn't multicast an event "+event);
		}
	}
	
	@Override
	public void exchangeOpened(ExchangeEvent event) {
		sendEvent(event);
	}

	@Override
	public void exchangeClosed(ExchangeEvent event) {
		sendEvent(event);
	}

	@Override
	public void priceChanged(ExchangeEvent event) {
		sendEvent(event);
	}

	@Override
	public void close() throws Exception {
		acceptingConnections = false;
	}
}
