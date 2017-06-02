package com.atnuk.processes;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.atnuk.utils.DateTimeUtils;
import com.atnuk.utils.EMSRunningOrderEvent;
import com.atnuk.utils.StringTools;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

@Component
public class TranslateAsRun {

	@Autowired
	StringTools stringTools;
	@Autowired
	DateTimeUtils dateTimeUtils;
	@Autowired
	EMSRunningOrderEvent emsRunningOrderEvent;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void translate(Exchange ex) {
		ArrayList<GenericFile> list = ex.getIn().getBody(ArrayList.class);
		if (list.size() > 1) {
			File asRunFile = (File) list.get(0).getFile();
			File emsROFile = (File) list.get(1).getFile();
			File emsAsRunFile = new File("temp");;

			char asRunFileDelimiter = '\t';
			char emsFileDelimiter = ',';
			char quote = '^';
			char doubleQuote = '"';

			List<String[]> asRunEvents;
			List<String[]> emsRunningOrder;
			List<String[]> tmpemsAsRunEvents = new ArrayList<String[]>();
			List<String[]> emsAsRunEvents = new ArrayList<String[]>();
			List<String> emsAsRunEvent;

			System.out.println(ex.getIn().getHeaders());

			CSVReader reader;
			CSVWriter writer;
			try {
				reader = new CSVReader(new FileReader(asRunFile), asRunFileDelimiter, quote, doubleQuote);
				asRunEvents = reader.readAll();
				reader.close();

				reader = new CSVReader(new FileReader(emsROFile), emsFileDelimiter, quote, doubleQuote, 4,false);
				emsRunningOrder = reader.readAll();
				reader.close();

				Predicate<String[]> filterPlayEvents = events -> !(events[2].equals("Play"));
				asRunEvents.removeIf(filterPlayEvents);

				for (int i = 0; i < asRunEvents.size(); i++) {
					String[] oaEvent = asRunEvents.get(i);
					String[] nextEvent = { "2050.01.01 06:00:00:00" };
					if (i != (asRunEvents.size() - 1)) {
						nextEvent = asRunEvents.get(i + 1);
					}
					emsAsRunEvent = new ArrayList<>();
					if (oaEvent[6].startsWith("PROG_")) {
						emsAsRunEvent.add("3");
						emsAsRunEvent.add(stringTools.extractTitle(oaEvent[6]));
						emsAsRunEvent.add("0");
						emsAsRunEvent.add(" ");
						emsAsRunEvent.add(dateTimeUtils.Timehhmmss(oaEvent[0]));
						emsAsRunEvent.add(dateTimeUtils.Timehhmmss(nextEvent[0]));

						tmpemsAsRunEvents.add(emsAsRunEvent.toArray(new String[0]));
					} else if (emsRunningOrderEvent.isAdvert(oaEvent, emsRunningOrder)) {
						emsAsRunEvent.add("2");
						emsAsRunEvent.add(emsRunningOrderEvent.GetAdvertDetails("Program").trim());
						emsAsRunEvent.add(emsRunningOrderEvent.GetAdvertDetails("SpotID").trim());
						emsAsRunEvent.add(emsRunningOrderEvent.GetAdvertDetails("ClockNo").trim());
						emsAsRunEvent.add(dateTimeUtils.Timehhmmss(oaEvent[0]));
						emsAsRunEvent.add(dateTimeUtils.getDuration(oaEvent[0], (nextEvent[0])));

						tmpemsAsRunEvents.add(emsAsRunEvent.toArray(new String[0]));
					}
				}
				int noofEMSAsRunEvents = tmpemsAsRunEvents.size();
				for (int i = 0; i < noofEMSAsRunEvents; i++) {
					String[] tempEvent = tmpemsAsRunEvents.get(i);
					String[] nextEvent = { "2" };
					if (i != (noofEMSAsRunEvents - 1)) {
						nextEvent = tmpemsAsRunEvents.get(i + 1);
					}
					if (tempEvent[0].equals("3") && nextEvent[0].equals("3") && tempEvent[1].equals(nextEvent[1])) {
						nextEvent[4] = tempEvent[4];
						
					} else {
						emsAsRunEvents.add(tempEvent);
					}
				}
				String creationDate =  new SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis());
				String creationTime =  new SimpleDateFormat("HHmmss").format(System.currentTimeMillis());
				String[] headerEvent = {"1", ex.getIn().getHeader("TXDate").toString(), ex.getIn().getHeader("ChannelName").toString(), creationDate, creationTime};
				emsAsRunEvents.add(0, headerEvent);
				
//				writer = new CSVWriter(new FileWriter(emsAsRunFile),';');
				writer = new CSVWriter(new FileWriter(emsAsRunFile),';', CSVWriter.NO_QUOTE_CHARACTER, 
					    CSVWriter.NO_ESCAPE_CHARACTER, 
					    System.getProperty("line.separator"));
				writer.writeAll(emsAsRunEvents,false);
				writer.close();
				ex.getIn().setBody(emsAsRunFile);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

	}

}
