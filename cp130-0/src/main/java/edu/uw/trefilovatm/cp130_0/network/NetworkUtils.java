package edu.uw.trefilovatm.cp130_0.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uw.ext.framework.order.AbstractOrder;
import edu.uw.ext.framework.order.Order;

public class NetworkUtils {
	static public byte [] serialize(Serializable object) throws IOException {
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			try(ObjectOutputStream out = new ObjectOutputStream(bos)) {
				  out.writeObject(object);
				  out.flush();
				  return bos.toByteArray();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	static public <T extends Serializable> T deserialize(byte [] input) throws IOException, ClassNotFoundException {
		try(ByteArrayInputStream bis = new ByteArrayInputStream(input)) {
			try(ObjectInput in = new ObjectInputStream(bis)) {
				  return (T) in.readObject(); 
			}
		}
	}
	
	public static void main(String [] args) throws IOException, ClassNotFoundException {
		System.out.println(fromJson(json("aaaaa"), String.class));
		
		Order order = new AbstractOrder("aaa", 232, "ORCL") {
			@Override
			public int valueOfOrder(int pricePerShare) {
				return 22;
			}
		};	    
		System.out.println(json(order));
	}
	
	static private ObjectMapper mapper = new ObjectMapper();
	
	static public String json(Object object) throws IOException {
		return mapper.writeValueAsString(object);
	}
	
	static public <T> T fromJson(String input, Class<T> cl) throws IOException, ClassNotFoundException {
		return mapper.readValue(input, cl);
	}		
}
