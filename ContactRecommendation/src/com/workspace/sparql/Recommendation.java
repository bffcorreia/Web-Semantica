package com.workspace.sparql;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.ontology.Onty;
import com.workspace.Data.GlobalVariables;

public class Recommendation 
{
	private final static double[]  fixedWeight = {0.15, 0.005, 0.3, 0.015, 0.5, 0.03};
	
	public static ArrayList<String> contacts = new ArrayList<String>();
	public static ArrayList<String> recommendation = new ArrayList<String>();
	public static ArrayList<Double> totalWeights = new ArrayList<Double>();
	
	public Recommendation()
	{
		getContactUris();
		ArrayList<Double> aux = new ArrayList<Double>();
		
		double max;
		
		int indice;
		
		for(int i = 0; i<5; i++)
		{
			max = Collections.max(totalWeights);
			
			if(max > 0)
			{
				aux.add(max);
				
				indice = Collections.indexOfSubList(totalWeights, aux);
				
				recommendation.add(contacts.get(indice));
				
				System.out.println(Queries.getNameByUri(recommendation.get(i)));
				System.out.println(totalWeights.get(indice));
				
				aux.remove(0);
				
				contacts.remove(indice);
				
				totalWeights.remove(indice);
			}
		}
		
		
	}
	
	static public void getContactUris()
	{
		String queryString = 
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT DISTINCT ?uri " +
	    		"WHERE { ?uri rdf:type ?type . " +
	    		"FILTER (str(?type) = '"+Onty.NS+"Contact' ) . }";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    String uri;
	    int weight;
	    double total;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results
	        
	        while(results.hasNext()) 
	        {
				QuerySolution sol = results.nextSolution();
				total = 0.0;
				
				try 
				{
					uri = sol.get("uri").toString();
					
					contacts.add(uri);
					
					//weight = getNumberCalls(uri);
					
					//total += weight*fixedWeight[0];
					
					weight = getNumberSms(uri);
					
					total += weight*fixedWeight[1];
					
					//weight = getNumberCallsHour(uri);
					
					//total += weight*fixedWeight[2];
					
					//weight = getNumberSmsHour(uri);
					
					//total += weight*fixedWeight[3];
					
					//weight = getNumberCallsDate(uri);
					
					//total += weight*fixedWeight[4];
					
					//weight = getNumberSmsDate(uri);
					
					//total += weight*fixedWeight[5];
					
					System.out.println(total);
					
					totalWeights.add(total);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	    } 
	    catch (Exception e) 
	    {
	        System.err.print("Error:"+e.getMessage());
	    } 
	    
        qexec.close();
	}
	
	static public int getNumberCalls(String contactUri)
	{
		int nCalls = 0;
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT ?Call " +
	    		"WHERE { ?Call rdf:type ?class . " +
	    				"?Call foaf:contact ?contactUri . " +
	    				"FILTER (str(?class) = '"+Onty.NS+"Call' && str(?contactUri) = '"+contactUri+"' ) . }";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {
				results.nextSolution();
				
				nCalls ++;
	        }
	    } 
	    catch (Exception e) 
	    {
	        System.err.print("Error:"+e.getMessage());
	    } 
	    
        qexec.close();
		
		return nCalls;
	}

	static public int getNumberSms(String contactUri)
	{
		int nSms = 0;
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT ?thread " +
	    		"WHERE { ?x foaf:thread ?thread . " +
	    				"?x foaf:contact ?contactUri . " +
	    				"FILTER ( str(?contactUri) = '"+contactUri+"' ) . }";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {
				results.nextSolution();
				
				nSms ++;
				System.out.println(nSms);
	        }
	    } 
	    catch (Exception e) 
	    {
	        System.err.print("Error:"+e.getMessage());
	    } 
	    
        qexec.close();
		
		return nSms;
	}

	static public int getNumberCallsHour(String contactUri)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		Calendar before = Calendar.getInstance();
		Calendar after = Calendar.getInstance();
		
		String time1 = dateFormat.format(cal.getTime()).split(" ")[1];
		
		Date callDate = null;
		
		String date1, date2, date3;
		
		int nCalls = 0;
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT ?date " +
	    		"WHERE { ?Call rdf:type ?class . " +
	    				"?Call foaf:contact ?contactUri . " +
	    				"?Call foaf:date ?date . " +
	    				"FILTER (str(?class) = '"+Onty.NS+"Call' && str(?contactUri) = '"+contactUri+"' ). }";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {
	        	date2 = "";
	        	date3 = "";
	        	
	        	QuerySolution sol = results.nextSolution();
	        	
	        	callDate = dateFormat.parse(sol.get("date").toString());
	        	
	        	date1 = dateFormat.format((callDate)).split(" ")[0];
	        	
	        	date2 = date1 + " " + time1;
	        	date3 = date1 + " " + time1;
	        	
	        	before.setTime(dateFormat.parse(date2));
	        	
	        	before.add(Calendar.HOUR_OF_DAY, -1);
	        	
	        	after.setTime(dateFormat.parse(date3));
	        	
	        	after.add(Calendar.HOUR_OF_DAY, +1);
	        	
	        	System.out.println("before: "+dateFormat.format(before.getTime()));
	        	System.out.println("after: "+dateFormat.format(after.getTime()));
	        	
	        	if(callDate.after(dateFormat.parse(date2)))
	        	{
	        		if(callDate.before(dateFormat.parse(date3)))
	        			nCalls ++;
	        	}
	        }
	    } 
	    catch (Exception e) 
	    {
	        System.err.print("Error:"+e.getMessage());
	    } 
	    
        qexec.close();
		
		return nCalls;
	}

	static public int getNumberCallsDate(String contactUri)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		Calendar before = Calendar.getInstance();
		Calendar after = Calendar.getInstance();
		
		int day1, day2;
		
		day1 = cal.get(Calendar.DAY_OF_WEEK);
		
		String time1 = dateFormat.format(cal.getTime()).split(" ")[1];
		
		Date callDate = null;
		
		String date1, date2, date3;
		
		int nCalls = 0;
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT ?date " +
	    		"WHERE { ?Call rdf:type ?class . " +
	    				"?Call foaf:contact ?contactUri . " +
	    				"?Call foaf:date ?date . " +
	    				"FILTER (str(?class) = '"+Onty.NS+"Call' && str(?contactUri) = '"+contactUri+"' ). }";
		
		System.out.println(queryString);
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {
	        	date2 = "";
	        	date3 = "";
	        	
	        	QuerySolution sol = results.nextSolution();
	        	
	        	callDate = dateFormat.parse(sol.get("date").toString());
	        	
	        	cal.setTime(callDate);
	        	
	        	day2 = cal.get(Calendar.DAY_OF_WEEK);
	        	
	        	if(day1 == day2)
	        	{
	        		date1 = dateFormat.format((callDate)).split(" ")[0];
		        	
		        	date2 = date1 + " " + time1;
		        	date3 = date1 + " " + time1;
		        	
		        	before.setTime(dateFormat.parse(date2));
		        	
		        	before.add(Calendar.HOUR_OF_DAY, -1);
		        	
		        	after.setTime(dateFormat.parse(date3));
		        	
		        	after.add(Calendar.HOUR_OF_DAY, +1);
		        	
		        	System.out.println("before: "+dateFormat.format(before.getTime()));
		        	System.out.println("after: "+dateFormat.format(after.getTime()));
		        	
		        	if(callDate.after(dateFormat.parse(date2)))
		        	{
		        		if(callDate.before(dateFormat.parse(date3)))
		        			nCalls ++;
		        	}
	        	}
	        }
	    } 
	    catch (Exception e) 
	    {
	        System.err.print("Error:"+e.getMessage());
	    } 
	    
        qexec.close();
		
		return nCalls;
	}

	static public int getNumberSmsHour(String contactUri)
	{	
		int nCalls = 0;
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT ?thread " +
	    		"WHERE { " +
	    				"?Thread rdf:type ?threadType . " +
	    				"?Thread foaf:contact ?contactUri . " +
	    				"?Thread foaf:thread ?thread . " +
	    				"FILTER (str(?threadType) = '"+Onty.NS+"Thread' " +
	    						"&& str(?contactUri) = '"+contactUri+"' ). }";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {	
	        	QuerySolution sol = results.nextSolution();
	        	
	        	nCalls += getNumberSmsHour1(sol.get("thread").toString());
	        }
	    } 
	    catch (Exception e) 
	    {
	        System.err.print("Error:"+e.getMessage());
	    } 
	    
        qexec.close();
		
		return nCalls;
	}
	
	static public int getNumberSmsHour1(String smsUri)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		Calendar before = Calendar.getInstance();
		Calendar after = Calendar.getInstance();
		
		String time1 = dateFormat.format(cal.getTime()).split(" ")[1];
		
		Date callDate = null;
		
		String date1, date2, date3;
		
		int nCalls = 0;
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT ?date " +
	    		"WHERE { " +
	    				"<"+smsUri+"> foaf:date ?date . }";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {
	        	date2 = "";
	        	date3 = "";
	        	
	        	QuerySolution sol = results.nextSolution();
	        	
	        	System.out.println(sol.get("date"));
	        	
	        	callDate = dateFormat.parse(sol.get("date").toString());
	        	
	        	date1 = dateFormat.format((callDate)).split(" ")[0];
	        	
	        	date2 = date1 + " " + time1;
	        	date3 = date1 + " " + time1;
	        	
	        	before.setTime(dateFormat.parse(date2));
	        	
	        	before.add(Calendar.HOUR_OF_DAY, -1);
	        	
	        	after.setTime(dateFormat.parse(date3));
	        	
	        	after.add(Calendar.HOUR_OF_DAY, +1);
	        	
	        	System.out.println("before: "+dateFormat.format(before.getTime()));
	        	System.out.println("after: "+dateFormat.format(after.getTime()));
	        	
	        	if(callDate.after(before.getTime()))
	        	{
	        		if(callDate.before(after.getTime()))
	        			nCalls ++;
	        	}
	        }
	    } 
	    catch (Exception e) 
	    {
	        System.err.print("Error:"+e.getMessage());
	    } 
	    
        qexec.close();
		
		return nCalls;
	}

	static public int getNumberSmsDate(String contactUri)
	{	
		int nCalls = 0;

		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT ?thread " +
	    		"WHERE { " +
	    				"?Thread rdf:type ?threadType . " +
	    				"?Thread foaf:contact ?contactUri . " +
	    				"?Thread foaf:thread ?thread . " +
	    				"FILTER (str(?threadType) = '"+Onty.NS+"Thread' " +
	    						"&& str(?contactUri) = '"+contactUri+"' ). }";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {	
	        	QuerySolution sol = results.nextSolution();
	        	
	        	nCalls += getNumberSmsDate1(sol.get("thread").toString());
	        }
	    } 
	    catch (Exception e) 
	    {
	        System.err.print("Error:"+e.getMessage());
	    } 
	    
        qexec.close();
		
		return nCalls;
	}

	static public int getNumberSmsDate1(String smsUri)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		Calendar before = Calendar.getInstance();
		Calendar after = Calendar.getInstance();
		
		int day1, day2;
		
		day1 = cal.get(Calendar.DAY_OF_WEEK);
		
		System.out.println("day1: "+day1);
		
		String time1 = dateFormat.format(cal.getTime()).split(" ")[1];
		
		Date callDate = null;
		
		String date1, date2, date3;
		
		int nCalls = 0;
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT ?date " +
	    		"WHERE { " +
	    				"<"+smsUri+"> foaf:date ?date . }";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {
	        	date2 = "";
	        	date3 = "";
	        	
	        	QuerySolution sol = results.nextSolution();
	        	
	        	System.out.println(sol.get("date"));
	        	
	        	callDate = dateFormat.parse(sol.get("date").toString());
	        	
	        	cal.setTime(callDate);
	        	
	        	day2 = cal.get(Calendar.DAY_OF_WEEK);
	        	
	        	System.out.println("day2: "+day2);
	        	
	        	if(day1 == day2)
	        	{
		        	date1 = dateFormat.format((callDate)).split(" ")[0];
		        	
		        	date2 = date1 + " " + time1;
		        	date3 = date1 + " " + time1;
		        	
		        	before.setTime(dateFormat.parse(date2));
		        	
		        	before.add(Calendar.HOUR_OF_DAY, -1);
		        	
		        	after.setTime(dateFormat.parse(date3));
		        	
		        	after.add(Calendar.HOUR_OF_DAY, +1);
		        	
		        	System.out.println("before: "+dateFormat.format(before.getTime()));
		        	System.out.println("after: "+dateFormat.format(after.getTime()));
		        	
		        	if(callDate.after(before.getTime()))
		        	{
		        		if(callDate.before(after.getTime()))
		        			nCalls ++;
		        	}
	        	}
	        }
	    } 
	    catch (Exception e) 
	    {
	        System.err.print("Error:"+e.getMessage());
	    } 
	    
        qexec.close();
		
		return nCalls;
	}

}
