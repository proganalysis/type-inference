package rpi.edu.reimutils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import edu.rpi.AnnotatedValue;

class Pair {
	HashSet<String> doop = new HashSet<String>();
	HashSet<String> alm = new HashSet<String>();
}

public class Doop {

	static HashMap<String,Pair> ptSets = new HashMap<String,Pair>();
	
	public static void readDoop() throws IOException {
		String filename = "/Users/ana/proganalysis/projects/type-inference/trunk/object-immutability/tests/benchmarks/doopResults/luindex-app.txt";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			e.printStackTrace();
		}
		
        String line = null;

        while ((line = reader.readLine()) != null) {
             int i = line.indexOf(", ");
             if (i == -1) {
            	 throw new IOException("File "+filename+" not in correct format: "+line);
             }             
             String var = line.substring(2,i);
             String object = line.substring(i+2);
             // we skip non-benchmark vars and objects
             if (var.indexOf("lucene") == -1) continue; 
             if ( (object.indexOf("/new ")>=0) && (object.indexOf("lucene") >= 0) ) {
            	 Pair pair = ptSets.get(var);
            	 if (pair == null) {
            		 pair = new Pair();
            		 ptSets.put(var,pair);
            	 }
            	 pair.doop.add(object);
            	 //System.out.println("Added to doop set of var "+var);
            	 //System.out.println(object);
             }
             
        }
	}
	
	public static void compare(HashMap<AnnotatedValue,HashSet<AnnotatedValue>> almResult) {
		
		try {
			readDoop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		readAlm(almResult);
		
		int doop = 0;
		int alm = 0;
		int total = 0;
		
		for (String v : ptSets.keySet()) {
			Pair p = ptSets.get(v);
			if (p.alm.size() == 0) {
				System.out.println("Empty alm set for: "+v);
				for (String o : p.doop) 
					System.out.println("---- DOOP "+o);
			}
			else {
				doop += p.doop.size();
				alm += p.alm.size();
				total++;
			}
		}
		System.out.println("Avg doop: "+((double) doop)/((double) total));
		System.out.println("Avg alm: "+((double) alm)/((double) total));		
	}
	
	private static String doopVarName(AnnotatedValue v) {
		String name;
		if (v.getName().indexOf("parameter") == 0) {
			name = "@param"+v.getName().substring(9,10);
		}
		else if (v.getName().indexOf("this") == 0) {
			name = "@this";
		}
		else 
			name = v.getName();
		return name;
	}

	private static void readAlm(HashMap<AnnotatedValue, HashSet<AnnotatedValue>> almResult) {
		
		System.out.println("Started readAlm.");
		
		for (AnnotatedValue v : almResult.keySet()) {
			if (v.toString().indexOf("refHeaderAction") > -1) {
				System.out.println("Processing v: "+v);
			}
			
			HashMap<String,Integer> theMap = new HashMap<String,Integer>();
								
			for (AnnotatedValue object : almResult.get(v)) {
				//System.out.println(v+"     "+object.getName());
				if (v.toString().indexOf("refHeaderAction") > -1) 
					System.out.println("HERE3: Printing set for v: "+object);
				
				String obj = object.getEnclosingMethod().toString()+"/"+object.getName().substring(0,object.getName().lastIndexOf(" "));
				Integer i = theMap.get(obj);
				if (i == null) {
					theMap.put(obj,new Integer(0));
				}
				else {
					theMap.put(obj,i+1);
				}
			}
			if (v.getEnclosingMethod() == null) continue;
			
			if (v.toString().indexOf("refHeaderAction") > -1) 
				System.out.println("HERE4: Printed set for v, still going.");
			
			String var1 = v.getEnclosingMethod().toString()+"/"+doopVarName(v);
			String var2 = v.getEnclosingClass().toString()+"."+v.getEnclosingMethod().getName()+"/"+doopVarName(v);
			
			if (v.toString().indexOf("refHeaderAction") > -1) {
				System.out.println("HERE5: "+v+" and var1: "+var1);
				System.out.println("HERE5: "+v+" and var2: "+var2);
			}
			Pair p = null;
			if (ptSets.get(var1) != null) {
				p = ptSets.get(var1);
			}
			else if (ptSets.get(var2) != null) {
				p = ptSets.get(var2);
			}
			else {
				if (v.toString().indexOf("refHeaderAction") > -1) {
					System.out.println("HERE5: v not in Doop...");
				}
				//System.out.println(v + " does not have a Doop pt set? ");
				//System.out.println("      var1: "+var1);
				//System.out.println("      var2: "+var2);
			}
			if (p != null) {
				for (String s : theMap.keySet()) {
					Integer num = theMap.get(s);
					for (int i=0; i<num+1; i++) {
						p.alm.add(s+"/"+i);
						if (v.toString().indexOf("refHeaderAction") > -1) {
							System.out.println("Added "+s+"/"+i+" to the set for "+var1 + " or " + var2);
						}
					}				
				}
			}
		}
	}
	
}
