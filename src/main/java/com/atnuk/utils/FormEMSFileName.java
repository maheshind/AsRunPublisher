package com.atnuk.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atnuk.utils.DateTimeUtils;

@Component("FormEMSFileName")
public class FormEMSFileName {

	@Autowired
	DateTimeUtils dateTimeUtils;
	LocalDate txdate = LocalDate.now();
	DateTimeFormatter asRunformat = DateTimeFormatter.ofPattern("yyyyMMdd");
	DateTimeFormatter emsformat = DateTimeFormatter.ofPattern("yyMMdd");

	public String getEMSROFileName(Exchange exchange) {

		String asRunFileName = (String) exchange.getIn().getHeader("CamelFileName");
		// DateTimeFormatter asRunformat =
		// DateTimeFormatter.ofPattern("yyyyMMdd");
		// DateTimeFormatter emsformat = DateTimeFormatter.ofPattern("yyMMdd");
		// LocalDate txdate = LocalDate.now();
		String emsFile;

		if (asRunFileName.length() > 25) {
			txdate = LocalDate.parse(asRunFileName.substring(16, 24), asRunformat);
		}
		emsFile = "ATN" + txdate.format(emsformat) + ".csv";

		return emsFile;
	}

	public String getTXDate() {
		return txdate.format(asRunformat);
	}

	public String getEMSAsRunFileName(Exchange exchange) {

		String EMSAsRunFileName = (String) exchange.getIn().getHeader("EMSAsRunFilePrefix") + txdate.format(asRunformat)
				+ (String) exchange.getIn().getHeader("ChannelCode") + ".txt";
		return EMSAsRunFileName;
	}
}