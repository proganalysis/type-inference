//package edu.rpi.intentResolve;
package soot.intentResolve;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.PrintStream;
import java.io.File;
import java.lang.String;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import soot.intentResolve.intentDB;

import java.net.URI;





public class parseXML {
	

	private DocumentBuilderFactory dbFactory;
	private DocumentBuilder dBuilder;
	private Document doc;
	private Node rootNode;
	private int depth;
	private String intentTarget;
	private ArrayList<String> intentActions;
	private ArrayList<String> intentCategories;
	private ArrayList<myUri> intentDataURIs;
	private ArrayList<String> intentDataTypes;
	private IntentFilterRecord dbEntry;




	public parseXML(String filename){
		try{
			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			File inputFile = new File(filename);
			doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			rootNode = doc.getDocumentElement();
			depth=0;
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	public String spaces(int n){
		int i=0;
		String str="";
		for(i=0;i<n;i++){
			str = str+"== ";
		}
		return str;
	}
	
	public void DFS(Node node){
		//do the thing
		if(node.getNodeType()==1){

			Element elem = (Element) node;
			switch(node.getNodeName()){
				case "activity":
					intentTarget = elem.getAttribute("android:name");
					break;
				case "receiver":
					intentTarget = elem.getAttribute("android:name");
					break;
				case "service":
					intentTarget = elem.getAttribute("android:name");
					break;
				case "provider":
					intentTarget = elem.getAttribute("android:name");
					intentCategories =new ArrayList<String>();
					intentDataURIs = new ArrayList<myUri>();
					intentDataTypes = new ArrayList<String>();
					intentActions = new ArrayList<String>();
					for (String aut:(elem.getAttribute("android:authorities")).split(";")){
						intentActions.add(aut);
					}
					break;
				case "intent-filter":
					intentActions = new ArrayList<String>();
					intentCategories =new ArrayList<String>();
					intentDataURIs = new ArrayList<myUri>();
					intentDataTypes = new ArrayList<String>();
					break;

				case "action":
					intentActions.add(elem.getAttribute("android:name"));
					break;

				case "category":
					intentCategories.add(elem.getAttribute("android:name"));
					break;

				case "data":
					intentDataTypes.add(elem.getAttribute("android:mimeType"));
					
					myUri thisUri = new myUri();
					thisUri.scheme = elem.getAttribute("android:scheme");
					thisUri.host = elem.getAttribute("android:host");
					thisUri.port = elem.getAttribute("android:port");
					thisUri.path = elem.getAttribute("android:path");
					thisUri.pathPattern = elem.getAttribute("android:pathPattern");
					thisUri.pathPrefix = elem.getAttribute("android:pathPrefix");
					if(!thisUri.isEmpty()){
						intentDataURIs.add(thisUri);
					}

					break;
				
							


			}
			/*System.out.println(spaces(depth)+"Name: ");
			if(node.getNodeName()!="data"){
				System.out.println(spaces(depth)+"Content: "+elem.getAttribute("android:name"));
			}
			*/
		}
		else{
			//System.out.println(spaces(depth)+"Text: "+node.getTextContent());
		}
		//recursion		
		Node currentNode = node.getFirstChild();
		depth++;
		while(currentNode!=null){
			DFS(currentNode);
			currentNode = currentNode.getNextSibling();
		}
		depth--;

		if(node.getNodeName()=="intent-filter"|| node.getNodeName()=="provider"){
			System.out.println("Target: "+intentTarget);
			System.out.println("Actions:");
			for(String action:intentActions){
				System.out.println(spaces(2)+action);
			}
			for(String category:intentCategories){
				System.out.println("Category:"+category);
			}
			/*System.out.println("Data Type:"+intentDataType);
			System.out.print("Data URI:");
			for(String uriPart:intentDataURI){
				System.out.print(" "+uriPart);
			}*/
			System.out.println("");
			System.out.println("");

			//converting the intent filter info to an entity object
			for(String actionName:intentActions){
				//each action makes a new row/entity in the DB
				dbEntry = new IntentFilterRecord(actionName, intentTarget, intentCategories,
						intentDataURIs, intentDataTypes);
				intentDB.insertEntity(dbEntry);
	
			}
		}
		return;
	}

	public void printall(){
		intentDB.openIntentDB(true);
		DFS(rootNode);
		intentDB.closeIntentDB();
		return;
	}

}
