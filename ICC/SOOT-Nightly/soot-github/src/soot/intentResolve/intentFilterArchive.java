//package edu.rpi.intentResolve;
package soot.intentResolve;
//import edu.rpi.intentResolve.parseXML;
import soot.intentResolve.parseXML;
import soot.intentResolve.IntentFilterRecord;
import soot.intentResolve.intentDB;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityIndex;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;



public class intentFilterArchive {
	public static void main(String[] args){
		parseXML xml = new parseXML(args[0]);
		xml.printall();
		
		/*
		intentDB.openIntentDB(false);
		EntityCursor<IntentFilterRecord> cs = 
			intentDB.retrieveEntries();
		IntentFilterRecord ifilter;
		for(ifilter=cs.first();
				ifilter != null;
				ifilter = cs.next()){
			ifilter.printElem();
		}*/

		/*ArrayList<IntentFilterRecord> arrIf = intentDB.retrieveFilters();
		for(IntentFilterRecord ifr:arrIf){
			ifr.printElem();
		}*/

	}
}
