package com.atnuk.utils;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class DateTimeUtils {

	public String SubtractDay(String DateyyyyMMdd) throws ParseException,Exception {
	    String datePattern = "yyyyMMdd";
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
	    LocalDate indate = LocalDate.parse(DateyyyyMMdd, formatter);
		return indate.minusDays(1).format(formatter);
	}
	
	//yyyy.MM.dd hh:mm:ss:ff
	public String Timehhmmss(String yyyyMMdd_hhmmssff) throws ParseException,Exception{
		String returnTime = yyyyMMdd_hhmmssff.substring(11,13) + yyyyMMdd_hhmmssff.substring(14,16) + yyyyMMdd_hhmmssff.substring(17, 19);		
		return returnTime;
	}
	
	public String getDuration(String startTimeString, String endTimeString) throws ParseException,Exception {
		//double duration=0;
		String datePattern = "yyyy.MM.dd HH:mm:ss:SSS";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
		
		int startTimeFrames = Integer.valueOf(startTimeString.substring(startTimeString.lastIndexOf(':')+1));
		int endTimeFrames = Integer.valueOf(endTimeString.substring(endTimeString.lastIndexOf(':')+1));
		
		String startTimeMilli = String.format("%03d", startTimeFrames*40);
		String endTimeMilli = String.format("%03d", endTimeFrames*40);
		
		startTimeString = startTimeString.substring(0, startTimeString.lastIndexOf(':')+1) + startTimeMilli;
		endTimeString = endTimeString.substring(0, endTimeString.lastIndexOf(':')+1) + endTimeMilli;
		
		
		LocalDateTime startTime = LocalDateTime.parse(startTimeString, formatter);
	    LocalDateTime endTime = LocalDateTime.parse(endTimeString, formatter);
		
	    Duration duration = Duration.between(startTime, endTime);
	    
	    if (duration.getNano()>0) {
			duration = duration.plusSeconds(1);
		}
	    
		return String.valueOf(duration.getSeconds());
	}

}
