package com.atnuk.utils;

import org.springframework.stereotype.Component;

@Component
public class StringTools {

//	public String extractTitle(String asRunNotes){
//	    String eventTitle = " ";
//	    int underscoreCount = org.apache.commons.lang3.StringUtils.countMatches(asRunNotes, '_');
//	    if (underscoreCount>2) {
//	    	eventTitle = asRunNotes.substring((asRunNotes.indexOf('_',5)+1), asRunNotes.lastIndexOf('_'));
//		} else {
//			eventTitle = asRunNotes.substring((asRunNotes.indexOf('_')+1));
//		}    
//		return eventTitle;
//	}

	public String extractTitle(String asRunNotes){
	    String eventTitle = " ";
	    int underscoreCount = org.apache.commons.lang3.StringUtils.countMatches(asRunNotes, '_');
	    if (underscoreCount>1) {
	    	eventTitle = asRunNotes.substring((asRunNotes.indexOf('_')+1), asRunNotes.lastIndexOf('_'));
		} else {
			eventTitle = asRunNotes.substring((asRunNotes.indexOf('_')+1));
		}    
		return eventTitle;
	}
	
	public String extractGenreID(String asRunNotes){
	    String genreID = " ";
	    int underscoreCount = org.apache.commons.lang3.StringUtils.countMatches(asRunNotes, '_');
	    if (underscoreCount>2) {
	    	genreID = asRunNotes.substring((asRunNotes.indexOf('_',4)+1), asRunNotes.indexOf('_',7));
		} else {
			genreID = asRunNotes.substring((asRunNotes.indexOf('_')+1));
		}    
		return genreID;
	}
}
