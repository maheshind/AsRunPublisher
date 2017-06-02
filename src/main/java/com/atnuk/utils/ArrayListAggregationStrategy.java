package com.atnuk.utils;

//import java.util.ArrayList;
//
//import org.apache.camel.Exchange;
//import org.apache.camel.processor.aggregate.AggregationStrategy;
//import org.springframework.stereotype.Component;
//
//@Component
//class AsRunEMSAggregationStrategy implements AggregationStrategy {
//	 
//    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
//        Object newBody = newExchange.getIn().getBody();
//        ArrayList<Object> list = null;
//        if (oldExchange == null) {
//            list = new ArrayList<Object>();
//            list.add(newBody);
//            newExchange.getIn().setBody(list);
//            return newExchange;
//        } else {
//            list = oldExchange.getIn().getBody(ArrayList.class);
//            list.add(newBody);
//            return oldExchange;
//        }
//    }
//}

import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class ArrayListAggregationStrategy implements AggregationStrategy {

	public ArrayListAggregationStrategy() {
		super();
	}

	@SuppressWarnings("unchecked")
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		Message newIn = newExchange.getIn();
		Object newBody = newIn.getBody();
		ArrayList<Object> list = null;
		if (oldExchange == null) {
			list = new ArrayList<Object>();
			list.add(newBody);
			newIn.setBody(list);
			return newExchange;
		} else {
			Message in = oldExchange.getIn();
			list = in.getBody(ArrayList.class);
			list.add(newBody);
			oldExchange.getIn().setBody(list);
			return oldExchange;
		}
	}

}