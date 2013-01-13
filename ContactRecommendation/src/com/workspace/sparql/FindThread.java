package com.workspace.sparql;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import android.widget.EditText;

import com.workspace.Data.GlobalVariables;
import com.workspace.comparators.MyComparator;
import com.workspace.contactrecommendation.MainActivity;

public class FindThread extends Thread
{
	EditText find;
	
	public FindThread(EditText find)
	{
		this.find = find;
		
		this.start();
	}
	

    @Override
    public void run() 
    {
    	//Create new findString
    	GlobalVariables.findString = new ArrayList<String>();
    	
    	//Remove insignificant chars from input and split
    	String[] aux = find.getText().toString().split(" ");
    	
    	//Get all combinations from input
    	findSplit(aux, 0, aux.length, aux.length);

    	//Sort the combinations by length
    	Collections.sort(GlobalVariables.findString, new MyComparator());
    	
    	for(int i = 0; i<GlobalVariables.findString.size(); i++)
    		System.out.println(GlobalVariables.findString.get(i));
    	
    	//Create hashtable with label keys
    	Hashtable<String, ArrayList<String>> tmp = new Hashtable<String, ArrayList<String>>();
    	
    	tmp.put("class", new ArrayList<String>());
    	tmp.put("unknown", new ArrayList<String>());
    	tmp.put("property", new ArrayList<String>());
    	tmp.put("date", new ArrayList<String>());
    	tmp.put("reserved", new ArrayList<String>());
    	
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	
    	String find;
    	
    	for(int i = 0; i<GlobalVariables.findString.size(); i++)
    	{	
    		//Verify if string is inside known values from hashtable
    		boolean flag = findHashtableValue(tmp, i);
    		
    		//System.out.println("FLAG: "+flag+" STRING: "+GlobalVariables.findString.get(i));
    		
    		if(!flag)
    		{
    			find = GlobalVariables.findString.get(i);
    			
        		//Find if input is known
        		String[] devolve = Queries.findLabel(find);
        		
        		if(devolve != null)
        		{	
        			//verificar se dentro da hashtable existe algum valor que contenha o label
        			tmp = removeHashtableValues(tmp, i);
        			
        			if(removeFindStringValues(find, i+1))
        				i--;
        			
        			//add it to respective key
        			tmp.get(devolve[1]).add(devolve[0]);
        		}
        		else
        		{
        			if(find.toLowerCase().equals("after") || find.toLowerCase().equals("before") || find.toLowerCase().equals("between"))
        			{
        				//verificar se dentro da hashtable existe algum valor que contenha o label
            			tmp = removeHashtableValues(tmp, i);
            			
            			if(removeFindStringValues(find, i+1))
            				i--;
            			
            			//add it to respective key
            			tmp.get("reserved").add(find);
        			}
        			else
        			{
            			try 
            			{
							dateFormat.parse(find);
							
							//verificar se dentro da hashtable existe algum valor que contenha o label
	            			tmp = removeHashtableValues(tmp, i);
	            			
	            			if(removeFindStringValues(find, i+1))
	            				i--;
	            			
	            			//add it to respective key
							
							tmp.get("date").add(find);
							
						} 
            			catch (ParseException e) 
            			{
            				tmp.get("unknown").add(GlobalVariables.findString.get(i));
						}
        			}
        		}
    		}
    	}
    	
    	//PRINT HASHTABLE
		for(String key: tmp.keySet())
		{
			ArrayList<String> auxFind = tmp.get(key);
			
			System.out.println(key);
			
			for(int j = 0; j<auxFind.size(); j++)
			{
				System.out.println(auxFind.get(j));
			}
		}
		
		callStandard(tmp);
		
    }
    
    public void findSplit(String[] input, int i, int j, int size)
	{	
		if(i == size)
			return;
		if(j == i)
		{
			findSplit(input, i+1, size, size);
			return;
		}
		
		String aux = "";
		
		for(int k = i; k<j; k++)
			aux += input[k] + " ";
		
		GlobalVariables.findString.add(aux.trim());
		
		findSplit(input, i, j-1, size);
	}
    
    private boolean removeFindStringValues(String string, int i) 
	{
		boolean flag = false;
		String[] find = string.split(" ");
		
		for(int j = i; j<GlobalVariables.findString.size(); j++)
		{
			for(int k = 0; k<find.length; k++)
			{
				if(GlobalVariables.findString.get(j).equals(find[k]))
				{
					GlobalVariables.findString.remove(j);
					j--;
					flag = true;
				}
			}
		}
		return flag;
	}

	private void callStandard(Hashtable<String, ArrayList<String>> tmp) 
	{
		ArrayList<String> auxClass = tmp.get("class");
		ArrayList<String> auxProperty = tmp.get("property");
		ArrayList<String> auxUnknown = tmp.get("unknown");
		ArrayList<String> auxDate = tmp.get("date");
		ArrayList<String> auxReserved = tmp.get("reserved");
		
		if(auxClass.size() == 1)
		{
			MainActivity.list.clear();
			if(auxProperty.size() == 1)
			{
				if(auxUnknown.size() != 0)
				{
					
					for(int i = 0; i<auxUnknown.size(); i++)
					{
						if(Queries.ClassPropertyValue(auxClass.get(0), auxProperty.get(0), auxUnknown.get(i)))
						{
							MainActivity.adapter.notifyDataSetChanged();
							return;
						}
					}
				}
				else if(auxDate.size() == 1)
				{
					if(Queries.ClassPropertyValue(auxClass.get(0), auxProperty.get(0), auxDate.get(0)))
					{
						MainActivity.adapter.notifyDataSetChanged();
						return;
					}
				}
				else
				{
					if(Queries.ClassProperty(auxClass.get(0), auxProperty.get(0)))
					{
						MainActivity.adapter.notifyDataSetChanged();
						return;
					}
				}
			}
			else if(auxReserved.size() == 1)
			{
				if(auxDate.size() == 1)
				{
					if(Queries.ClassReservedDate(auxClass.get(0), auxReserved.get(0), auxDate.get(0)))
					{
						MainActivity.adapter.notifyDataSetChanged();
						return;
					}
				}
				else if(auxDate.size() == 2)
				{
					if(Queries.ClassReservedDateDate(auxClass.get(0), auxReserved.get(0), auxDate.get(0), auxDate.get(1)))
					{
						MainActivity.adapter.notifyDataSetChanged();
						return;
					}
				}
			}
			
			Queries.getClasse(auxClass.get(0));
			MainActivity.adapter.notifyDataSetChanged();
			return;
		}
		else if(auxClass.size() == 2)
		{
			MainActivity.list.clear();
			Queries.ClassClass(auxClass.get(0), auxClass.get(1));
			MainActivity.adapter.notifyDataSetChanged();
			return;
		}
		else
		{
			if(auxProperty.size() == 1)
			{
				
			}
			else
			{
				MainActivity.list.clear();
				Queries.unknownValue(auxUnknown.get(0));
				MainActivity.adapter.notifyDataSetChanged();
				return;
			}
		}
		
	}

	private Hashtable<String, ArrayList<String>> removeHashtableValues(
			Hashtable<String, ArrayList<String>> tmp, int i) 
	{	
		String[] splitAux = GlobalVariables.findString.get(i).split(" ");
		
		ArrayList<String> auxFind;
		
		for(String key: tmp.keySet())
		{
			auxFind = tmp.get(key);
			
			for(int j = 0; j<auxFind.size(); j++)
			{
				//System.out.println("auxFind: "+auxFind.get(j));
				for(int x = 0; x<splitAux.length; x++)
				{
					//System.out.println("splitAux: "+splitAux[x]);
    				if(auxFind.get(j).contains(splitAux[x]))
    				{
    					//System.out.println("REMOVE");
    					tmp.get(key).remove(j);
    					j--;
    					break;
    				}
				}
			}
		}
		return tmp;
	}

	private boolean findHashtableValue(Hashtable<String, ArrayList<String>> tmp, int i) 
	{	
		String[] splitAux = GlobalVariables.findString.get(i).split(" ");
		
		ArrayList<String> auxFind;
		
		for(String key: tmp.keySet())
		{
			if(!key.equals("unknown"))
			{
				auxFind = tmp.get(key);
    			
    			for(int j = 0; j<auxFind.size(); j++)
    			{
    				for(int x = 0; x<splitAux.length; x++)
    				{
        				if(auxFind.get(j).contains(splitAux[x]))
        					return true;
    				}
    			}
			}
		}
		return false;
	}
}
