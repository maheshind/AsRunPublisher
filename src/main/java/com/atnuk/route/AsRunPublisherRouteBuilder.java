package com.atnuk.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.atnuk.processes.TranslateAsRun;
import com.atnuk.utils.ArrayListAggregationStrategy;
import com.atnuk.utils.FormEMSFileName;

@Component
public class AsRunPublisherRouteBuilder extends RouteBuilder {

	@Autowired
	FormEMSFileName formEMSFileName;

	@Autowired
	TranslateAsRun translateAsRun;
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.builder.RouteBuilder#configure()
	 */
	//
	@Override
	public void configure() throws Exception {
		from("file://{{oasys.asrun.out}}?scheduler=quartz2&scheduler.cron={{oasys.asrun.movecron}}&filter=#filterOldTXTFiles&readLock=changed&delay=3000&move={{oasys.asrun.archive}}&sortBy=file:name")
				.routeId("OldFiles").log(LoggingLevel.INFO, "Moving AsRun file ${file:name}")
				.transform(method("BroadcastDaySplitter", "splitMessage")).split(body())
				.to("file://{{asrunpublisher.asrun.in}}?fileExist=append");

		from("file://{{oasys.asrun.out}}?scheduler=quartz2&scheduler.cron={{oasys.asrun.copycron}}&filter=#filterTodaysTXTFile&noop=true&readLock=changed&delay=3000&idempotent=false")
				.routeId("CurrentFile").log(LoggingLevel.INFO, "Copying AsRun file ${file:name}")
				.transform(method("BroadcastDaySplitter", "splitMessage")).split(body())
				.to("file://{{asrunpublisher.asrun.in}}?fileExist=append");
		
		from("file://{{asrunpublisher.asrun.in}}?scheduler=quartz2&scheduler.cron={{asrunpublisher.asrun.cron}}&include=.*.txt&readLock=changed")
				.routeId("AsRunProcessor").log(LoggingLevel.INFO, "Starting to process ${file:name}")
				.setHeader("EMSFILE").method(formEMSFileName, "getEMSROFileName")
				.setHeader("TXDate").method(formEMSFileName, "getTXDate")
				.setHeader("ChannelName", simple("{{barb.channelname}}"))
				.setHeader("ChannelCode", simple("{{barb.stationcode}}"))
				.setHeader("EMSAsRunFilePrefix", simple("{{ems.interface.fileprefix}}"))
				.setHeader("EMSAsRunFILEName").method(formEMSFileName, "getEMSAsRunFileName")
				.log(LoggingLevel.INFO, "EMS File ${header.EMSFile}")
				.pollEnrich().simple("file://{{ems.rundownn.file.store}}?fileName=${header.EMSFile}&noop=true&readLock=none&sendEmptyMessageWhenIdle=true&idempotent=false").aggregationStrategy(new ArrayListAggregationStrategy())
				.bean(translateAsRun, "translate")
				.split(body())
				.to("file://{{asrunpublisher.asrun.out}}?fileName=${header.EMSAsRunFILEName}&fileExist=append");

		from("file://{{asrunpublisher.asrun.inredo}}?scheduler=quartz2&scheduler.cron={{asrunpublisher.asrun.cronredo}}&include=.*.txt&readLock=changed")
		.routeId("AsRunProcessorRedo").log(LoggingLevel.INFO, "Starting to process ${file:name}")
		.setHeader("EMSFILE").method(formEMSFileName, "getEMSROFileName")
		.setHeader("TXDate").method(formEMSFileName, "getTXDate")
		.setHeader("ChannelName", simple("{{barb.channelname}}"))
		.setHeader("ChannelCode", simple("{{barb.stationcode}}"))
		.setHeader("EMSAsRunFilePrefix", simple("{{ems.interface.fileprefix}}"))
		.setHeader("EMSAsRunFILEName").method(formEMSFileName, "getEMSAsRunFileName")
		.log(LoggingLevel.INFO, "Redo EMS File ${header.EMSFile}")
		.pollEnrich().simple("file://{{ems.rundownn.file.store}}?fileName=${header.EMSFile}&noop=true&readLock=none&sendEmptyMessageWhenIdle=true&idempotent=false").aggregationStrategy(new ArrayListAggregationStrategy())
		.bean(translateAsRun, "translate")
		.split(body())
		.to("file://{{asrunpublisher.asrun.out}}?fileName=${header.EMSAsRunFILEName}&fileExist=append");
	}


}
