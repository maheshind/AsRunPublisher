package com.atnuk.utils;

import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class EMSRunningOrderEvent {
	
	private Boolean isAdvert = false;
	private List<String[]> emsRunningOrder;
	private String[] filteredEMSEvent;
	private HashMap<String, String> emsRunningOrderEvent = new HashMap<String, String>();
	private String[] asRunEvent;

	public Boolean isAdvert(String[] event,List<String[]> emsRunningOrder){
		this.asRunEvent = event;
		this.emsRunningOrder = emsRunningOrder;
		
		CheckandBuildAdvertDetails();
		
		return this.isAdvert;
	}
	
	public HashMap<String, String> GetAdvertDetails(){
		return emsRunningOrderEvent;
	}
	
	public String GetAdvertDetails(String key){
		return emsRunningOrderEvent.get(key);
	}
	
	private void CheckandBuildAdvertDetails(){
		String asRunSpotId;
		if (asRunEvent[6].length()>1) {
			asRunSpotId = asRunEvent[6].substring(1);
			
			filteredEMSEvent = emsRunningOrder.stream().filter(evnt -> evnt[5].equals(asRunSpotId)).findAny().orElse(null);
			if (filteredEMSEvent!=null) {
				emsRunningOrderEvent.put("Time", filteredEMSEvent[0]);
				emsRunningOrderEvent.put("Duration", filteredEMSEvent[1]);
				emsRunningOrderEvent.put("Break", filteredEMSEvent[2]);
				emsRunningOrderEvent.put("Program", filteredEMSEvent[3]);
				emsRunningOrderEvent.put("Seq", filteredEMSEvent[4]);
				emsRunningOrderEvent.put("SpotID", filteredEMSEvent[5]);
				emsRunningOrderEvent.put("SpotDuration", filteredEMSEvent[6]);
				emsRunningOrderEvent.put("ClockNo", filteredEMSEvent[7]);
				emsRunningOrderEvent.put("Commercial", filteredEMSEvent[8]);
				emsRunningOrderEvent.put("Product", filteredEMSEvent[9]);			
				isAdvert = true;
			}else{
				isAdvert = false;
			}
		}else{
			isAdvert=false;
		}
	}
}
