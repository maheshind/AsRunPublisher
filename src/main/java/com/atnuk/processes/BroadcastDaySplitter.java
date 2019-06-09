package com.atnuk.processes;

import java.io.BufferedReader;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atnuk.utils.DateTimeUtils;

@Component("BroadcastDaySplitter")
public class BroadcastDaySplitter {

	@Autowired
	DateTimeUtils dateTimeUtils;
	
	public Iterator<Message> splitMessage(Exchange exchange) {
		BufferedReader inputReader = exchange.getIn().getBody(BufferedReader.class);
		
		List<Message> messages = new ArrayList<Message>();
		String asRunEvent = null;
		StringBuffer previousDayEvents = new StringBuffer();
		StringBuffer currentDayEvents = new StringBuffer();
		String asRunFileName = (String) exchange.getIn().getHeader("CamelFileName");
		String currentDay, previousDay;

		//DateTimeUtils dateTimeUtils = new DateTimeUtils();
		try {
			while (null != (asRunEvent = inputReader.readLine())) {
				if (Integer.parseInt(asRunEvent.substring(11, 13)) < 6) {
					previousDayEvents.append(asRunEvent + System.lineSeparator());
				} else {
					currentDayEvents.append(asRunEvent + System.lineSeparator());
				}
			}
			currentDay = asRunFileName.substring(16, 24);
			previousDay = dateTimeUtils.SubtractDay(currentDay);
			//messages.add(createNewOutput(previousDayEvents, asRunFileName.replace(currentDay, previousDay)));
			if (!exchange.getFromRouteId().equals("CurrentFile")) {
				messages.add(createNewOutput(currentDayEvents, asRunFileName,exchange.getContext()));
			}else if (exchange.getFromRouteId().equals("CurrentFile")) {
				messages.add(createNewOutput(previousDayEvents, asRunFileName.replace(currentDay, previousDay),exchange.getContext()));
			}
			
			previousDayEvents = new StringBuffer();

			inputReader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return messages.iterator();
	}

	private Message createNewOutput(StringBuffer sb, String fileName, CamelContext context) throws EOFException {
		//Message message = new DefaultMessage();
		Message message = new DefaultMessage(context);
		message.setBody(sb.toString());
		message.setHeader(Exchange.FILE_NAME, fileName);
		return message;
	}
}