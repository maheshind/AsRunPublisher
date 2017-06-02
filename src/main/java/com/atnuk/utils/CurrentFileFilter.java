package com.atnuk.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.component.file.*;
import org.springframework.stereotype.Component;

@Component("filterTodaysTXTFile")
public class CurrentFileFilter<T> implements GenericFileFilter<T> {
	public boolean accept(GenericFile<T> file) {
		Boolean valid = false;
		String currentDate =  new SimpleDateFormat("MM/dd/yyyy").format(System.currentTimeMillis());
		String fileDate = new SimpleDateFormat("MM/dd/yyyy").format(file.getLastModified());
		
		Date fileModifiedTime = new Date(file.getLastModified());
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm") ;
		
		try {
			if (file.getFileName().endsWith("oalog.txt") && currentDate.equals(fileDate) && timeFormat.parse(timeFormat.format(fileModifiedTime)).after(timeFormat.parse("03:00"))) {
				valid = true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return valid;
	}
}
