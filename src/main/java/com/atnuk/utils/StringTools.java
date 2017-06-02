package com.atnuk.utils;

import org.springframework.stereotype.Component;

@Component
public class StringTools {

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

}
