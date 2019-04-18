package com.nordicid.testapplication;

import com.nordicid.samples.common.SamplesCommon;

import com.nordicid.nurapi.NurApi;
import com.nordicid.nurapi.NurApiListener;

import com.nordicid.nurapi.NurEventAutotune;
import com.nordicid.nurapi.NurEventClientInfo;
import com.nordicid.nurapi.NurEventDeviceInfo;
import com.nordicid.nurapi.NurEventEpcEnum;
import com.nordicid.nurapi.NurEventFrequencyHop;
import com.nordicid.nurapi.NurEventIOChange;
import com.nordicid.nurapi.NurEventInventory;
import com.nordicid.nurapi.NurEventNxpAlarm;
import com.nordicid.nurapi.NurEventProgrammingProgress;
import com.nordicid.nurapi.NurEventTagTrackingChange;
import com.nordicid.nurapi.NurEventTagTrackingData;
import com.nordicid.nurapi.NurEventTraceTag;
import com.nordicid.nurapi.NurEventTriggeredRead;
import com.nordicid.nurapi.NurTag;
import com.nordicid.nurapi.NurTagStorage;

/**
 * This example shows how to run continuous inventory in asynchronous stream.
 * - Inventory is used to read multiple tag's EPC codes in reader field of view
 */
public class Example {
	
	// We store unique read tags in this storage
	static NurTagStorage uniqueTags = new TagCounter();
	
	// API access
	static NurApi api = null;	
	
	// stream event count
	static int eventCount = 0;
	
	static Config config = Config.getInstance();
	static Poster poster = new Poster();

	public static void main(String[] args) {
		if (config.getPrefs().node("Debug").getBoolean("PostSamples", false)) {
			postSampleData();
			return;
		}
		
		try {
			// Create and connect new NurApi object
			// To change connection parameters, please modify SamplesCommon.java
			api = SamplesCommon.createAndConnectNurApi(config.getPrefs().node("App").get("ScannerHost", "172.16.32.36"), config.getPrefs().node("App").getInt("ScannerPort", 4333));
			api.setListener(apiListener);
		} 
		catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		
		try {
			// Clear tag storage
			api.clearIdBuffer(true);
			
			System.out.println("Starting inventory stream for 30 secs");
			
			// Start inventory stream, see inventoryStreamEvent() below
			api.startInventoryStream();
			
			// Let it run for 30 sec
			Thread.sleep(Example.config.getPrefs().node("App").getInt("ScannerRuntimeSec", 60) * 1000);
			
			System.out.println(String.format("Total %d stream events", eventCount));
			System.out.println(String.format("Inventoried total %d unique tags", uniqueTags.size()));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		System.out.println("See you again!.");
		try {
			// Disconnect the connection
			api.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Dispose the NurApi
		api.dispose();
	}
	
	static NurApiListener apiListener = new NurApiListener() {
		
		//@Override
		public void triggeredReadEvent(NurEventTriggeredRead arg0) {
		}
		
		public void traceTagEvent(NurEventTraceTag arg0) {
		}
		
		public void programmingProgressEvent(NurEventProgrammingProgress arg0) {
		}
		
		public void logEvent(int arg0, String arg1) {
		}
		
		// This event is fired when ever reader completes inventory round and tags are read 
		// NOTE: Depending on reader settings, tag amount and environment this event maybe fired very frequently
		public void inventoryStreamEvent(NurEventInventory arg0) {
			eventCount++;
			
			NurTagStorage apiStorage = api.getStorage();
			
			// When accessing api owned storage from event, we need to lock it
			synchronized (apiStorage) {
				// Add inventoried tags to our unique tag storage
				for (int n=0; n<apiStorage.size(); n++) {
					NurTag tag = apiStorage.get(n);
					if (uniqueTags.addTag(tag)) {
						System.out.println(String.format(uniqueTags.size() + "# New unique tag '%s' RSSI %d", tag.getEpcString(), tag.getRssi()));
						poster.postTag(tag.getEpcString());
					}
				}
			}
			
			// If stream stopped, restart
			if (arg0.stopped) {
				try {
					System.out.println("Restarting stream");
					api.startInventoryStream();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		public void inventoryExtendedStreamEvent(NurEventInventory arg0) {
		}
		
		public void frequencyHopEvent(NurEventFrequencyHop arg0) {
		}
		
		public void disconnectedEvent() {
		}
		
		public void deviceSearchEvent(NurEventDeviceInfo arg0) {
		}
		
		public void debugMessageEvent(String arg0) {
		}
		
		public void connectedEvent() {
		}
		
		public void clientDisconnectedEvent(NurEventClientInfo arg0) {
		}
		
		public void clientConnectedEvent(NurEventClientInfo arg0) {
		}
		
		public void bootEvent(String arg0) {
		}
		
		public void IOChangeEvent(NurEventIOChange arg0) {
		}

		public void autotuneEvent(NurEventAutotune arg0) {
		}

		public void epcEnumEvent(NurEventEpcEnum arg0) {
		}

		public void nxpEasAlarmEvent(NurEventNxpAlarm arg0) {
		}

		public void tagTrackingChangeEvent(NurEventTagTrackingChange arg0) {
		}

		public void tagTrackingScanEvent(NurEventTagTrackingData arg0) {
		}
	};
	
	protected static void postSampleData() {
		System.out.println("Posting sample data...");
		String[] tags = config.getPrefs().node("Debug").get("SampleData", "").split(",");
		for (String tag: tags)
		{
			//if (((TagCounter)uniqueTags).addTag(tag) == false)
				//continue; // already present
			poster.postTag(tag);
			try {
				Thread.sleep(Example.config.getPrefs().node("Debug").getInt("PostIntervalMs", 100));
			}
			catch (InterruptedException e) {
				System.err.println(e);
			}
		}
		System.out.println("Posted samples to the HTTP backed: " + String.valueOf(tags.length));
	}
}
