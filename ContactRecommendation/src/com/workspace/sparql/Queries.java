package com.workspace.sparql;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import android.os.Handler;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.function.FunctionRegistry;
import com.ontology.Onty;
import com.workspace.Data.GlobalVariables;
import com.workspace.contactrecommendation.MainActivity;

public class Queries 
{
	public static Hashtable<String, ArrayList<String>> labels;
	
	static public boolean ClassPropertyValue(String classe, String property, String value)
	{	
		if(classe.equals("Thread"))
			classe = "Sms";
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT DISTINCT * " +
	    		"WHERE { ?x " + "foaf:"+property+" ?value . " +
	    				"?x rdf:type foaf:"+classe+" . " +
	    				"?x ?y ?z . " +
	    				"FILTER ( regex (str(?value), '"+value+"', \"i\") ) . } ";
		
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    String result = null;
	    String result1 = null;
	    
	    ArrayList<String> aux = new ArrayList<String>();
	    ArrayList<String> uris = new ArrayList<String>();
	    
	    int i = 0;
	    boolean flag = false;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {
	        	flag = true;
	        	
				QuerySolution sol = results.nextSolution();
				
				try 
				{		
					result = sol.get("y").toString();
					result = result.substring(result.lastIndexOf("#")+1);
					result1 = sol.get("z").toString();
					result1 = result1.substring(result1.lastIndexOf("#")+1);
					
					System.out.println(result);
					System.out.println(result1);
					
					if(result.equals("type"))
					{
						uris.add(sol.get("x").toString());
						aux = new ArrayList<String>();
						MainActivity.list.put(i, aux);
						MainActivity.adapter.notifyDataSetChanged();
						i++;
					}
					
					aux.add(result);
					aux.add(result1);
					//getClasseByUri(classe, sol.get(classe).toString(), i);
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
        
        if(flag == false)
        	flag = RecommendClassPropertyValue(classe, property, value, uris, i);
        else
        	RecommendClassPropertyValue(classe, property, value, uris, i);
        
        return flag;
	}

	private static boolean RecommendClassPropertyValue(String classe, String property, String value, ArrayList<String> uris, int i) 
	{
		int dist;
		
		if(property.equals("number"))
			dist = 2;
		else
			dist = 5;
		
		String functionUri = "http://www.example.org/LevenshteinFunction"; 
		FunctionRegistry.get().put(functionUri , LevenshteinFilter.class); 
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT DISTINCT * " +
	    		"WHERE { ?x " + "foaf:"+property+" ?value . " +
	    				"?x rdf:type foaf:"+classe+" . " +
	    				"?x ?y ?z . " +
	    				"FILTER ( <"+functionUri+">(?value, \"" + value + "\") < "+dist+" ";
		
		int size = uris.size();
		
		if(size > 0)
			queryString += "&& ";
		
		for(int j = 0; j<size; j++)
		{
			queryString += "str(?x) != \""+uris.get(j)+"\" ";
			
			if(j != size -1)
				queryString += "&& "; 
		}
		
		queryString += ") }";
		
		System.out.println(queryString);
		
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    String result = null;
	    String result1 = null;
	    boolean flag = false;
	    
	    ArrayList<String> aux = new ArrayList<String>();
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {	
				QuerySolution sol = results.nextSolution();
				flag = true;
				
				try 
				{		
					result = sol.get("y").toString();
					result = result.substring(result.lastIndexOf("#")+1);
					result1 = sol.get("z").toString();
					result1 = result1.substring(result1.lastIndexOf("#")+1);
					
					System.out.println(result);
					System.out.println(result1);
					
					if(result.equals("type"))
					{
						uris.add(sol.get("x").toString());
						aux = new ArrayList<String>();
						MainActivity.list.put(i, aux);
						MainActivity.adapter.notifyDataSetChanged();
						i++;
					}
					
					aux.add(result);
					aux.add(result1);
					//getClasseByUri(classe, sol.get(classe).toString(), i);
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
        
        size = uris.size();
        
        if(size != 0 && classe.equals("Contact"))
        {
        	i = getCallByContact(uris.get(0), i);
        	getSmsByContact(uris.get(0), i);
        }
        
        return flag;
	}

	static public boolean ClassProperty(String classe, String property)
	{	
		String queryString;
		
		if(classe.equals("Thread"))
			classe = "Sms";
		
		if(classe.equals("Contact"))
		{
			queryString = 
					"PREFIX foaf: <"+Onty.NS + "> " +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		    		"SELECT DISTINCT * " +
		    		"WHERE { ?x " + "foaf:"+property+" ?z . " +
		    				"?x rdf:type foaf:"+classe+" . " +
		    				"?x foaf:id ?id . " +
		    				"?x ?y ?z . } ";
		}
		else
		{
			queryString = 
					"PREFIX foaf: <"+Onty.NS + "> " +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		    		"SELECT DISTINCT * " +
		    		"WHERE { ?x " + "foaf:"+property+" ?z . " +
		    				"?x rdf:type foaf:"+classe+" . " +
		    				"?x ?y ?z . }";
		}
		
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    String result = null;
	    String result1 = null;
	    ArrayList<String> aux = new ArrayList<String>();
	    int i = 0;
	    boolean flag = false;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {
	        	flag = true;
	        	
				QuerySolution sol = results.nextSolution();
				
				try 
				{	
					result = sol.get("y").toString();
					result = result.substring(result.lastIndexOf("#")+1);
					result1 = sol.get("z").toString();
					result1 = result1.substring(result1.lastIndexOf("#")+1);
					
					aux = new ArrayList<String>();
					MainActivity.list.put(i, aux);
					MainActivity.adapter.notifyDataSetChanged();
					i++;
					
					aux.add("type");
					aux.add(classe);
					
					if(classe.equals("Contact"))
					{
						aux.add("id");
						aux.add(sol.get("id").toString());
					}
					
					aux.add(result);
					aux.add(result1);
					//getClasseByUri(classe, sol.get(classe).toString(), i);
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
        return flag;
	}
	
	static public void ClassClass(String classe1, String classe2)
	{	
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT DISTINCT ?contact " +
	    		"WHERE { "+
	    				"?x foaf:contact ?contact . " +
	    				"?x rdf:type foaf:"+classe1+" . }";
		
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    int i = 0;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        while(results.hasNext())
	        {
		        QuerySolution sol = results.nextSolution();
		        
		        getContactByUri(sol.get("contact").toString(), i);
		        i++;
	        }
	    }
	    catch (Exception e) 
	    {
	        System.err.print("Error:"+e.getMessage());
	    } 
	    
	    qexec.close();
	}

	static public boolean ClassReservedDate(String classe, String reserved, String date)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(classe.equals("Thread"))
			classe = "Sms";
		
		String sinal;
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
	    		"SELECT DISTINCT ?y ?z ?data " +
	    		"WHERE { " +
	    				"?x rdf:type foaf:"+classe+" . " +
	    				"?x foaf:date ?data . " +
	    				"?x ?y ?z . }" +
	    				"LIMIT 30";
		
		System.out.println(queryString);
		
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    String result = null;
	    String result1 = null;
	    ArrayList<String> aux = new ArrayList<String>();
	    int i = 0;
	    boolean flag = false;
	    boolean flag_aux = true;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {
	        	flag = true;
	        	
				QuerySolution sol = results.nextSolution();
				
				try 
				{		
					result = sol.get("y").toString();
					result = result.substring(result.lastIndexOf("#")+1);
					result1 = sol.get("z").toString();
					result1 = result1.substring(result1.lastIndexOf("#")+1);
					
					System.out.println(result);
					System.out.println(result1);
					
					if(result.equals("date"))
					{
						if(reserved.equals("after"))
						{
							if(dateFormat.parse(result1).before(dateFormat.parse(date)))
							{
								aux = new ArrayList<String>();
								flag_aux = false;
								results.nextSolution();
								continue;
							}
						}
						else
						{
							if(dateFormat.parse(result1).after(dateFormat.parse(date)))
							{
								aux = new ArrayList<String>();
								flag_aux = false;
								results.nextSolution();
								continue;
							}
						}
						aux.add(result);
						aux.add(result1);
						if(classe.equals("Sms"))
						{
							MainActivity.list.put(i, aux);
							aux = new ArrayList<String>();
							i++;
						}
					}
					else
					{
						aux.add(result);
						aux.add(result1);
						
						if(result.equals("callType") && flag_aux)
						{
							//MainActivity.adapter.notifyDataSetChanged();
							MainActivity.list.put(i, aux);
							aux = new ArrayList<String>();
							i++;
						}
						
						flag_aux = true;
					}
					//getClasseByUri(classe, sol.get(classe).toString(), i);
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
        return flag;
	}
	
	public static boolean ClassReservedDateDate(String classe, String reserved, String date1, String date2) 
	{
		if(classe.equals("Thread"))
			classe = "Sms";
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT DISTINCT * " +
	    		"WHERE { ?x " + "foaf:date ?value . " +
	    				"?x rdf:type foaf:"+classe+" . " +
	    				"?x ?y ?z . " +
	    				"FILTER ( regex (str(?value), \""+date1+"\", \"i\") ) . } ";
		
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    String result = null;
	    String result1 = null;
	    ArrayList<String> aux = new ArrayList<String>();
	    int i = 0;
	    boolean flag = false;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {
	        	flag = true;
	        	
				QuerySolution sol = results.nextSolution();
				
				try 
				{		
					result = sol.get("y").toString();
					result = result.substring(result.lastIndexOf("#")+1);
					result1 = sol.get("z").toString();
					result1 = result1.substring(result1.lastIndexOf("#")+1);
					
					System.out.println(result);
					System.out.println(result1);
					
					if(result.equals("type"))
					{
						//MainActivity.adapter.notifyDataSetChanged();
						aux = new ArrayList<String>();
						MainActivity.list.put(i, aux);
						i++;
					}
					
					aux.add(result);
					aux.add(result1);
					//getClasseByUri(classe, sol.get(classe).toString(), i);
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
        return flag;
	}
	
	static public boolean unknownValue(String input)
	{	
		String queryString = "PREFIX foaf: <"+Onty.NS + "> " +
	    		"SELECT DISTINCT * " +
	    		"WHERE { ?x ?y ?input . " +
	    		"FILTER ( regex(str(?input), '"+input+"' , \"i\" ) ) }";
		
		
		
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    String result = null;
	    
	    ArrayList<String> uris = new ArrayList<String>();
	    
	    boolean flag = false;
	    
	    int i = 0;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {
	        	flag = true;
	        	
				QuerySolution sol = results.nextSolution();
				
				try 
				{		
					result = sol.get("x").toString();
					
					uris.add(result);
					
					System.out.println(result);
					getClasseByUri(result, i);
					i++;
					
					//System.out.println(result1.substring(result1.lastIndexOf("#")+1));
					
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
        
        if(uris.size() > 0)
        {
	        if(uris.get(0).substring(uris.get(0).lastIndexOf(".")+1, uris.get(0).lastIndexOf("@")).equals("Contato"))
			{
				i = getCallByContact(uris.get(0), i);
				getSmsByContact(uris.get(0), i);
			}
        }
        else
        {
        	if(flag == false)
        		flag = recommendationUnknownValue(input);
        }
        
        return flag;
	}
	
	private static boolean recommendationUnknownValue(String input) 
	{
		int dist;
		
		try
		{
			Integer.parseInt(input);
			dist = 2;
		}
		catch(Exception e)
		{
			dist = 5;
		}
		
		String functionUri = "http://www.example.org/LevenshteinFunction"; 
		FunctionRegistry.get().put(functionUri , LevenshteinFilter.class); 
		
		String queryString = "PREFIX foaf: <"+Onty.NS + "> " +
	    		"SELECT DISTINCT * " +
	    		"WHERE { ?x ?y ?input . " +
	    		"FILTER ( <"+functionUri +">(?input, \"" + input + "\") < "+dist+" )}";
		
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    String result = null;
	    
	    ArrayList<String> uris = new ArrayList<String>();
	    
	    boolean flag = false;
	    
	    int i = 0;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        while(results.hasNext()) 
	        {
	        	flag = true;
	        	
				QuerySolution sol = results.nextSolution();
				
				try 
				{		
					result = sol.get("x").toString();
					
					uris.add(result);
					
					System.out.println(result);
					getClasseByUri(result, i);
					i++;
					
					//System.out.println(result1.substring(result1.lastIndexOf("#")+1));
					
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
        
        if(uris.size() > 0)
        {
	        if(uris.get(0).substring(uris.get(0).lastIndexOf(".")+1, uris.get(0).lastIndexOf("@")).equals("Contato"))
			{
				i = getCallByContact(uris.get(0), i);
				getSmsByContact(uris.get(0), i);
			}
        }
        
        return flag;
	}

	public static String[] findLabel(String input)
	{
		String queryString = 
							 "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
	     					 "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT DISTINCT ?x ?type "+ 
	    		"WHERE { ?x rdfs:label ?label . " +
	    		"		 ?x rdf:type ?type . " +
	    		"FILTER( regex (str(?label), '^"+input+"', \"i\")) .}";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    String result = null;
	    String result1 = null;
	    String[] devolve = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results
	        
	        while(results.hasNext()) 
	        {
				QuerySolution sol = results.nextSolution();
				try 
				{
					result = sol.get("x").toString();
					result1 = sol.get("type").toString();
					
					result1 = convertType(result1);
					
					qexec.close();
					
					devolve = new String[2];
					
					devolve[0] = result.substring(result.lastIndexOf("#")+1);
					devolve[1] = result1;
					
					return devolve;
					
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
        
        return devolve;
	}
	
	static public ResultSet getAllContacts()
	{
		String queryString = "PREFIX foaf: <"+Onty.NS + "> " +
	    		"SELECT DISTINCT ?id ?cname " +
	    		"WHERE { ?Contact foaf:name ?cname . " +
	    				"?Contact foaf:id ?id . } " +
	    				"ORDER BY ASC (?cname)";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        int i = 0;
	        
	        // Output query results	
	        for(i= 0; i< Recommendation.recommendation.size(); i++)
	        	getContactByUri(Recommendation.recommendation.get(i), i);
	        
	        ArrayList<String> aux;
	        
	        while(results.hasNext()) 
	        {
				QuerySolution sol = results.nextSolution();
				aux = new ArrayList<String>();
				try 
				{
					aux.add("type");
					aux.add("Contact");
					aux.add("id");
					aux.add(sol.get("id").toString());
					aux.add("Name");
					aux.add(sol.get("cname").toString());
					MainActivity.list.put(i, aux);
					i++;
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
        return results;
	}

	static public ResultSet getClassCall()
	{
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT DISTINCT ?contact ?callNumber ?date ?callType " +
	    		"WHERE { ?Call foaf:contact ?contact . " +
	    				"?Call foaf:callNumber ?callNumber . " +
	    				"?Call foaf:date ?date . " +
	    				"?Call foaf:callType ?callType . " +
	    				"?Call rdf:type foaf:Call . } " +
	    				"ORDER BY DESC (?date)";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        int i = 0;
	        ArrayList<String> aux = new ArrayList<String>();
	        
	        while(results.hasNext()) 
	        {
				QuerySolution sol = results.nextSolution();
				try 
				{	
					aux.add("type");
					aux.add("Call");
					
					aux.add("name");
					aux.add(getNameByUri(sol.get("contact").toString()));
					
					aux.add("callNumber");
					aux.add(sol.get("callNumber").toString());
					
					aux.add("date");
					aux.add(sol.get("date").toString());
					
					aux.add("callType");
					aux.add(sol.get("callType").toString());
					
					MainActivity.list.put(i, aux);
					//MainActivity.adapter.notifyDataSetChanged();
					
					aux = new ArrayList<String>();
					i++;
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
        return results;
	}

	static public ResultSet getClassThread()
	{
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT DISTINCT ?contact ?thread " +
	    		"WHERE { ?Thread foaf:contact ?contact . " +
	    				"?Thread foaf:thread ?thread . " +
	    				"?Thread rdf:type foaf:Thread . }";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        int i = 0;
	        
	        while(results.hasNext()) 
	        {
				QuerySolution sol = results.nextSolution();
				try 
				{	
					String name = getNameByUri(sol.get("contact").toString());
					
					getSmsByUri(sol.get("thread").toString(), name, i);
					
					i++;
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
        return results;
	}
	
	static public ResultSet getClassContact()
	{
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT DISTINCT * " +
	    		"WHERE { ?Contact foaf:name ?cname . " +
	    				"?Contact foaf:id ?id . " +
	    				"?Contact rdf:type foaf:Contact . " +
	    				"?Contact ?y ?z . } " +
	    				"ORDER BY ASC (?cname)";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    String result = null;
	    String result1 = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        int i = 0;
	        for(i= 0; i< Recommendation.recommendation.size(); i++)
	        	getContactByUri(Recommendation.recommendation.get(i), i);
	        
	        ArrayList<String> aux = new ArrayList<String>();
	        
	        while(results.hasNext()) 
	        {
				QuerySolution sol = results.nextSolution();
				try 
				{
					result = sol.get("y").toString();
					result1 = sol.get("z").toString();
					
					result = result.substring(result.lastIndexOf("#")+1);
					result1 = result1.substring(result1.lastIndexOf("#")+1);
					
					//System.out.println(result);
					//System.out.println(result1);
					
					aux.add(result);
					aux.add(result1);
					
					if(result.equals("type"))
					{
						MainActivity.list.put(i, aux);
						MainActivity.adapter.notifyDataSetChanged();
						
						aux = new ArrayList<String>();
						i++;
					}
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
        return results;
	}
	
	static public ResultSet getClassEvent()
	{
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT DISTINCT * " +
	    		"WHERE { " +
	    				"?Event foaf:title ?title . " +
	    				"?Event foaf:date ?date . " +
	    				"?Event rdf:type foaf:Event . " +
	    				"?Event ?y ?z . } " +
	    				"ORDER BY DESC (?date)";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    String result = null;
	    String result1 = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        int i = 0;
	        ArrayList<String> aux = new ArrayList<String>();
	        
	        while(results.hasNext()) 
	        {
				QuerySolution sol = results.nextSolution();
				
				try 
				{
					result = sol.get("y").toString();
					result1 = sol.get("z").toString();
					
					result = result.substring(result.lastIndexOf("#")+1);
					result1 = result1.substring(result1.lastIndexOf("#")+1);
					
					aux.add(result);
					aux.add(result1);
					
					if(result.equals("type"))
					{	
						MainActivity.list.put(i, aux);
						MainActivity.adapter.notifyDataSetChanged();
						
						aux = new ArrayList<String>();
						i++;
					}
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
        return results;
	}

	static public ResultSet getClassApp()
	{
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT DISTINCT ?appName ?packageName ?versionCode ?versionName " +
	    		"WHERE { " +
	    				"?App foaf:appName ?appName . " +
	    				"?App foaf:packageName ?packageName . " +
	    				"?App foaf:versionCode ?versionCode . " +
	    				"?App foaf:versionName ?versionName . " +
	    				"?App rdf:type foaf:App . } " +
	    				"ORDER BY ASC (?date)";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        int i = 0;
	        ArrayList<String> aux = new ArrayList<String>();
	        
	        while(results.hasNext()) 
	        {
				QuerySolution sol = results.nextSolution();
				
				try 
				{	
					aux.add("type");
					aux.add("App");
					
					aux.add("appName");
					aux.add(sol.get("appName").toString());
					
					aux.add("versionName");
					aux.add(sol.get("versionName").toString());
					
					aux.add("versionCode");
					aux.add(sol.get("versionCode").toString());
					
					aux.add("packageName");
					aux.add(sol.get("packageName").toString());
					
					MainActivity.list.put(i, aux);
					MainActivity.adapter.notifyDataSetChanged();
					
					aux = new ArrayList<String>();
					i++;
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
        return results;
	}
	
 	static public void getClasse(String className)
	{
		if(className.equals("Contact"))
			getClassContact();
		else if(className.equals("Event"))
			getClassEvent();
		else if(className.equals("App"))
			getClassApp();
		else if(className.equals("Call"))
			getClassCall();
		else if(className.equals("Thread"))
			getClassThread();
	}
	
 	static public String getNameByUri(String uri)
 	{
 		String name = null;
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
			    		"SELECT DISTINCT ?name " +
			    		"WHERE { "+
			    				"<"+uri+"> foaf:name ?name . }";
		
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
				
				try 
				{	
					name = sol.get("name").toString();
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
 		
 		return name;
 	}
 	
 	static public String getContactInThreadByUri(String uri)
 	{
 		String contactUri = null;
 		
 		String queryString = 
 						"PREFIX foaf: <"+Onty.NS + "> " +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			    		"SELECT DISTINCT ?contact " +
			    		"WHERE { "+
			    				"?x foaf:contact ?contact . " +
			    				"?x foaf:thread ?thread . " +
			    				"?x rdf:type foaf:Thread . " +
			    				"FILTER (str(?thread) = '"+uri+"' ) .}";
		
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
				
				try 
				{	
					contactUri = sol.get("contact").toString();
					
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
 		
 		return contactUri;
 	}
 	
	static public void getClasseByUri(String uri, int i)
	{	
		String uriType = uri.substring(uri.lastIndexOf(".")+1, uri.lastIndexOf("@"));
		
		if(uriType.equals("Chamada"))
			getCallByUri(uri, i);
		else if(uriType.equals("Application"))
			getAppByUri(uri, i);
		else if(uriType.equals("Contato"))
			getContactByUri(uri, i);
		else if(uriType.equals("Event"))
			getEventByUri(uri, i);
		else if(uriType.equals("Mensagem"))
			getSmsByUri(uri, null, i);
	}
	
	private static boolean getSmsByUri(String uri, String name, int i) 
	{
		boolean flag = false;
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
			    		"SELECT DISTINCT ?date ?text " +
			    		"WHERE { "+
			    				"<"+uri+"> foaf:date ?date . " +
			    				"<"+uri+"> foaf:text ?text .}";
		
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results
	        ArrayList<String> aux = new ArrayList<String>();
	        
	        while(results.hasNext()) 
	        {
	        	flag = true;
				QuerySolution sol = results.nextSolution();
				
				try 
				{	
					aux = new ArrayList<String>();
					MainActivity.list.put(i, aux);
					i++;
					
					aux.add("type");
					aux.add("Sms");
					
					aux.add("name");
					
					if(name != null)
						aux.add(name);
					else
						aux.add(getNameByUri(getContactInThreadByUri(uri)));
					
					aux.add("date");
					aux.add(sol.get("date").toString());
					
					aux.add("body");
					aux.add(sol.get("text").toString());
					MainActivity.adapter.notifyDataSetChanged();
					
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
        return flag;
		
	}

	private static boolean getEventByUri(String uri, int i) 
	{
		boolean flag = false;
		
		String queryString = 
			    		"SELECT DISTINCT ?y ?z " +
			    		"WHERE { "+
			    				"<"+uri+"> ?y ?z . }";
		
		System.out.println(queryString);
		
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    String result, result1;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results
	        ArrayList<String> aux = new ArrayList<String>();
	        
	        while(results.hasNext()) 
	        {
	        	flag = true;
				QuerySolution sol = results.nextSolution();
				
				try 
				{	
					result = sol.get("y").toString();
					result1 = sol.get("z").toString();
					
					result = result.substring(result.lastIndexOf("#")+1);
					
					result1 = result1.substring(result1.lastIndexOf("#")+1);
					
					System.out.println(result);
					System.out.println(result1);
					
					aux.add(result);
					aux.add(result1);
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	        MainActivity.list.put(i, aux);
	        MainActivity.adapter.notifyDataSetChanged();
	    } 
	    catch (Exception e) 
	    {
	        System.err.print("Error:"+e.getMessage());
	    }
	    
        qexec.close();
        return flag;
		
	}

	private static boolean getContactByUri(String uri, int i) 
	{
		boolean flag = false;
		
		String queryString = 
			    		"SELECT DISTINCT ?y ?z " +
			    		"WHERE { "+
			    				"<"+uri+"> ?y ?z . }";
		
		System.out.println(queryString);
		
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    String result, result1;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results
	        ArrayList<String> aux = new ArrayList<String>();
	        
	        while(results.hasNext()) 
	        {
	        	flag = true;
				QuerySolution sol = results.nextSolution();
				
				try 
				{	
					result = sol.get("y").toString();
					result1 = sol.get("z").toString();
					
					result = result.substring(result.lastIndexOf("#")+1);
					
					result1 = result1.substring(result1.lastIndexOf("#")+1);
					
					aux.add(result);
					aux.add(result1);
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	        MainActivity.list.put(i, aux);
	        MainActivity.adapter.notifyDataSetChanged();
	    } 
	    catch (Exception e) 
	    {
	        System.err.print("Error:"+e.getMessage());
	    }
	    
        qexec.close();
        return flag;
	}

	private static boolean getAppByUri(String uri, int i) 
	{
		boolean flag = false;
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
			    		"SELECT DISTINCT ?appName ?packageName ?versionCode ?versionName " +
			    		"WHERE { "+
			    				"<"+uri+"> foaf:appName ?appName . " +
			    				"<"+uri+"> foaf:packageName ?packageName . " +
			    				"<"+uri+"> foaf:versionCode ?versionCode . " +
			    				"<"+uri+"> foaf:versionName ?versionName . }";
		
		System.out.println(queryString);
		
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results
	        ArrayList<String> aux = new ArrayList<String>();
	        
	        while(results.hasNext()) 
	        {
	        	flag = true;
				QuerySolution sol = results.nextSolution();
				
				try 
				{	
					aux = new ArrayList<String>();
					MainActivity.list.put(i, aux);
					i++;
					
					aux.add("type");
					aux.add("App");
					
					aux.add("appName");
					aux.add(sol.get("appName").toString());
					
					aux.add("packageName");
					aux.add(sol.get("packageName").toString());
					
					aux.add("versionCode");
					aux.add(sol.get("versionCode").toString());
					
					aux.add("versionName");
					aux.add(sol.get("versionName").toString());

					MainActivity.adapter.notifyDataSetChanged();
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
        return flag;
	}

	private static boolean getCallByUri(String uri, int i) 
	{
		boolean flag = false;
		
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
			    		"SELECT DISTINCT ?contact ?callNumber ?callType ?date " +
			    		"WHERE { "+
			    				"<"+uri+"> foaf:contact ?contact . " +
			    				"<"+uri+"> foaf:callNumber ?callNumber . " +
			    				"<"+uri+"> foaf:callType ?callType . " +
			    				"<"+uri+"> foaf:date ?date . }";
		
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results
	        ArrayList<String> aux = new ArrayList<String>();
	        
	        while(results.hasNext()) 
	        {
	        	flag = true;
				QuerySolution sol = results.nextSolution();
				
				try 
				{	
					aux = new ArrayList<String>();
					MainActivity.list.put(i, aux);
					i++;
					
					aux.add("type");
					aux.add("Call");
					
					aux.add("name");
					
					aux.add(getNameByUri(sol.get("contact").toString()));
					
					aux.add("callNumber");
					aux.add(sol.get("callNumber").toString());
					
					aux.add("date");
					aux.add(sol.get("date").toString());
					
					aux.add("callType");
					aux.add(sol.get("callType").toString());

					MainActivity.adapter.notifyDataSetChanged();
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
        return flag;
	}

	private static int getCallByContact(String uri, int i)
	{	
		String queryString = 
						"PREFIX foaf: <"+Onty.NS + "> " +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			    		"SELECT DISTINCT ?x " +
			    		"WHERE { "+
			    				"?x foaf:contact ?contact . " +
			    				"?x rdf:type foaf:Call . " +
			    				"FILTER ( str(?contact) = '"+uri+"' ) }" +
			    				"LIMIT 10";
		
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
				
				try 
				{	
					//MainActivity.adapter.notifyDataSetChanged();
					getCallByUri(sol.get("x").toString(), i);
					i++;
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
        return i;
	}

	private static void getContactByNumber(String number, int i)
	{	
		String queryString = 
				"PREFIX foaf: <"+Onty.NS + "> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	    		"SELECT DISTINCT * " +
	    		"WHERE { ?Contact foaf:name ?cname . " +
	    				"?Contact foaf:id ?id . " +
	    				"?Contact foaf:number ?number" +
	    				"?Contact rdf:type foaf:Contact . " +
	    				"FILTER (str(?number) = '"+number+" ) } ";
	    
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results	
	        
	        ArrayList<String> aux = new ArrayList<String>();
	        
	        while(results.hasNext()) 
	        {
				QuerySolution sol = results.nextSolution();
				try 
				{
					aux.add("type");
					aux.add("Contact");
					
					aux.add("id");
					aux.add(sol.get("id").toString());
					
					aux.add("name");
					aux.add(sol.get("cname").toString());
					
					aux.add("number");
					aux.add(sol.get("number").toString());
					
					MainActivity.list.put(i, aux);
					MainActivity.adapter.notifyDataSetChanged();
					
					aux = new ArrayList<String>();
					i++;
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
	
	private static boolean getSmsByContact(String uri, int i)
	{
		boolean flag = false;
		
		String queryString = 
						"PREFIX foaf: <"+Onty.NS + "> " +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			    		"SELECT DISTINCT ?sms " +
			    		"WHERE { "+
			    				"?x foaf:contact ?contact . " +
			    				"?x foaf:thread ?sms . " +
			    				"?x rdf:type foaf:Thread . " +
			    				"FILTER ( str(?contact) = '"+uri+"' ) }" +
			    				"LIMIT 10";
		
		Query query = QueryFactory.create(queryString);
	    
	    QueryExecution qexec = QueryExecutionFactory.create(query, GlobalVariables.model);
	    
	    ResultSet results = null;
	    
	    try 
	    {
	        results = qexec.execSelect();
	        
	        // Output query results
	        
	        while(results.hasNext()) 
	        {
	        	flag = true;
				QuerySolution sol = results.nextSolution();
				
				try 
				{	
					//MainActivity.adapter.notifyDataSetChanged();
					getSmsByUri(sol.get("sms").toString(), null, i);
					i++;
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
        return flag;
	}
	
	private static String convertType(String result1) 
	{	
		String devolve = result1.substring(result1.lastIndexOf("#")+1);
		
		if(devolve.matches("(?i).*class.*"))
			return "class";
		else if(devolve.matches("(?i).*property.*"))
			return "property";
		return null;
	}

	
}
