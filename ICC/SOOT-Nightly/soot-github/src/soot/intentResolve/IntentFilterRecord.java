package soot.intentResolve;

import java.util.ArrayList;

import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import static com.sleepycat.persist.model.DeleteAction.NULLIFY;
import static com.sleepycat.persist.model.Relationship.ONE_TO_ONE;
import static com.sleepycat.persist.model.Relationship.ONE_TO_MANY;
import static com.sleepycat.persist.model.Relationship.MANY_TO_ONE;
import static com.sleepycat.persist.model.Relationship.MANY_TO_MANY;


@Entity
public class IntentFilterRecord
{
	@PrimaryKey(sequence="IF_ID_Sequence")
		private long recordID;

	@SecondaryKey(relate= MANY_TO_ONE)
		private String actionName;

	private String intentTarget;

	private ArrayList<String> intentCategories;

	private ArrayList<myUri> intentDataURIs;

	private ArrayList<String> intentDataTypes;


	public IntentFilterRecord(String action, String target, ArrayList<String> categories,
			ArrayList<myUri> uris, ArrayList<String> types){
		this.actionName = action;
		this.intentTarget = target;
		this.intentCategories = new ArrayList<String>(categories);
		this.intentDataURIs = new ArrayList<myUri>(uris);
		this.intentDataTypes = new ArrayList<String>(types);
		
	}

	public IntentFilterRecord(){
		this("","",
						new ArrayList<String>(), 
						new ArrayList<myUri>(), new ArrayList<String>());

	}

	public void setAction(String action){
		this.actionName = action;
		return;
	}
	
	public void setTarget(String target){
		this.intentTarget = target;
		return;
	}
	public void addCategory(String category){
		this.intentCategories.add(category);
		return;
	}
	public void addDataType(String dataType){
		this.intentDataTypes.add(dataType);
		return;
	}


	public String getActionName(){
		return actionName;
	}
	public String getTarget(){
		return intentTarget;
	}

	public ArrayList<String> getCategories(){
		return intentCategories;
	}

	public ArrayList<myUri> getDataURIs(){
		return intentDataURIs;
	}

	public ArrayList<String> getDataTypes(){
		return intentDataTypes;
	}

	public void printElem(){
		System.out.println("action:"+actionName);
		System.out.println("target:"+intentTarget);
		for(String category:intentCategories){
			System.out.println("category:"+category);
		}
		System.out.println("data profiles count:"+ intentDataTypes.size());
		return;
	}
	public boolean matchesIntent(String action, ArrayList<String> categories, String type){
		if(action!=null && action.equals(actionName)){
			//action matches
			if(categories.size()!=0){
				for(String iCategory:categories){
					if(!intentCategories.contains(iCategory)){
						return false;
					}
				}
			}
			if(type!=null){
				if(intentDataTypes.contains(type)){
					return true;
				}
			}

		}
		return false;
	}
}

