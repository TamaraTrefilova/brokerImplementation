package edu.uw.trefilovatm.cp130_0.broker;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class test1<T> {
	List<T> list = new ArrayList<>();
	Consumer<T> consumer;
	
	public void add(T element) {
		list.add(element);
	}
	
	public void setCallback(Consumer<T> consumer) {
		this.consumer = consumer;
	}
	
	public void doItAll() {
		for(T t:list) {
			consumer.accept(t);
		}		
	}
	
	public static int counter = 0;
	
	public static void main(String args[]) {
		test1<Integer> ttt  = new test1<>();
		
		ttt.add(5);
		ttt.add(1);
		ttt.add(20);
		
		Consumer<Integer> aaa = new Consumer<Integer>() {
			@Override
			public void accept(Integer t) {
				System.out.println("This is element "+t);
			}
		};		 
		
		ttt.setCallback(aaa);
		ttt.doItAll();
		
		counter = 0;
		
		Consumer<Integer> bbb = new Consumer<Integer>() {
			@Override
			public void accept(Integer t) {
				counter+=t;
			}
		};		 
		ttt.setCallback(bbb);		
		ttt.doItAll();
		
		System.out.println(counter);
	}
}
