package soot.intentResolve;

import java.util.ArrayList;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentNotFoundException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityIndex;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;



import soot.intentResolve.IntentFilterRecord;

public class intentDB{
	private static Environment intentEnv;
	private static EntityStore intentStore;
	private static EnvironmentConfig intentEnvConfig;
	private static StoreConfig intentStoreConfig;
	private static File intentFile;
	private static String storeFilePath="/home/alex/DroidBench-iccta/dbfile";

	/*public static boolean isOpen(){


	}*/

	public static int openIntentDB(boolean allowWrite){
		if(intentEnv == null){
			try{
				intentEnvConfig = new EnvironmentConfig();
				intentStoreConfig = new StoreConfig();

				intentEnvConfig.setAllowCreate(allowWrite);
				intentStoreConfig.setAllowCreate(allowWrite);
				intentFile = new File(storeFilePath);

				intentEnv = new Environment(intentFile, intentEnvConfig);
				intentStore = new EntityStore(intentEnv, "intentFilterStore", intentStoreConfig);
			}
			catch(EnvironmentNotFoundException enfe){
				return -1;
			}
			catch(DatabaseException dbe){
				System.err.println("Error opening environment and store: " +
					dbe.toString());
				return -1;
			}
			return 1;
		}
		else{
			return -1;
		}
	}

	public static void insertEntity(IntentFilterRecord record){
		try{
			PrimaryIndex<Long, IntentFilterRecord> pIndex = 
				intentStore.getPrimaryIndex(Long.class, IntentFilterRecord.class);
			pIndex.put(record);
		}
		catch(DatabaseException dbe){
			System.err.println("Error inserting entry: " +
				dbe.toString());
			System.exit(-1);
		}
		return;					
	}
	
	public static EntityCursor<IntentFilterRecord> retrieveEntries(String actionName){
		//retrieve all intent filter records that have the given action
		SecondaryIndex<String, Long, IntentFilterRecord> sIndex = 
			intentStore.getSecondaryIndex(intentStore.getPrimaryIndex(Long.class, IntentFilterRecord.class),
				String.class, "actionName");
		EntityCursor<IntentFilterRecord> cursor = sIndex.entities(actionName, true, actionName, true);
		return cursor;
	}

	public static ArrayList<IntentFilterRecord> retrieveFilters(String actionName){
		EntityCursor<IntentFilterRecord> cs = retrieveEntries(actionName);
		ArrayList<IntentFilterRecord> ifArray = new ArrayList<IntentFilterRecord>();
		IntentFilterRecord ifilter;
		for(ifilter=cs.first(); ifilter != null; ifilter = cs.next()){
			ifArray.add(ifilter);
		}
		cs.close();
		return ifArray;

	}

	public static ArrayList<IntentFilterRecord> retrieveFilters(){
		EntityCursor<IntentFilterRecord> cs = retrieveEntries();
		ArrayList<IntentFilterRecord> ifArray = new ArrayList<IntentFilterRecord>();
		IntentFilterRecord ifilter;
		for(ifilter=cs.first(); ifilter != null; ifilter = cs.next()){
			ifArray.add(ifilter);
		}
		cs.close();
		return ifArray;

	}


	public static EntityCursor<IntentFilterRecord> retrieveEntries(){
		//retrieve all intent filter records
		PrimaryIndex<Long, IntentFilterRecord> pIndex = 
			intentStore.getPrimaryIndex(Long.class, IntentFilterRecord.class);
		EntityCursor<IntentFilterRecord> cursor = pIndex.entities();
		return cursor;
	}

	public static void closeIntentDB(){
		if(intentStore!=null){
			try{
				intentStore.close();
			}
			catch(DatabaseException dbe) {
				System.err.println("Error closing store: " +
						dbe.toString());
				System.exit(-1);
			}
		}
		if(intentEnv!=null){
			try{
				intentEnv.close();
			}
            catch(DatabaseException dbe) {
                System.err.println("Error closing store: " +
                        dbe.toString());
                System.exit(-1);
			}
		}
		return;
	}

}
