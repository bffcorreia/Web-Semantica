package com.workspace.contactrecommendation;

import com.workspace.comparators.MyComparator;
import com.workspace.sparql.FindThread;
import com.workspace.sparql.Queries;
import com.workspace.sparql.Recommendation;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.ontology.Onty;
import com.workspace.Data.ApplicationInit;
import com.workspace.Data.ChamadasInit;
import com.workspace.Data.ContatosInit;
import com.workspace.Data.EventsInit;
import com.workspace.Data.GlobalVariables;
import com.workspace.Data.MensagensInit;
import com.workspace.Data.Teste;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class MainActivity extends Activity 
{
	ListView listView;
	
	EditText find;
	
	Button findButton;
	
	public static Hashtable<Integer, ArrayList<String>> list;
	
	public static EfficientAdapter adapter;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        find = (EditText) findViewById(R.id.EditText01);
        
        findButton = (Button) findViewById(R.id.btn_find);
        
        list = new Hashtable<Integer, ArrayList<String>>();
        
        //this.getData();
        new Onty();
        
        new Recommendation();
        
        Queries.getAllContacts();
        
        //new Recommendation();
        
        //Queries.getClasseByUri("http://www.owl-ontologies.com/Ontology1353891856.owl#com.workspace.Data.Contato@41027090", 0);
        
        //Queries.ClassReservedDate("Sms", "before", "2012-12-30 00:00:00");
        
        listView = (ListView) findViewById(R.id.contact_list);
        
        adapter = new EfficientAdapter(this);
        
        listView.setAdapter(adapter);
        
        addClickListeners();
    }
    
    public void getData()
    {   
    	ContatosInit c = new ContatosInit(this);
        
        c.getContatos();
        
        ApplicationInit app = new ApplicationInit(this);
        
        app.getPackages();
        
        EventsInit events = new EventsInit(this);
        
        events.getCalendarEvents();
        
        ChamadasInit ch = new ChamadasInit(this);
        
        ch.getChamadas();
        
        MensagensInit m = new MensagensInit(this);
        
        m.getMensagens();
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
    
    private void addClickListeners() {
    	findButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) 
            {
            	runOnUiThread(new Runnable() 
            	{
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
             });
        		
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
					list.clear();
					if(auxProperty.size() == 1)
					{
						if(auxUnknown.size() != 0)
						{
							for(int i = 0; i<auxUnknown.size(); i++)
							{
								if(Queries.ClassPropertyValue(auxClass.get(0), auxProperty.get(0), auxUnknown.get(i)))
								{
									adapter.notifyDataSetChanged();
									return;
								}
							}
						}
						else if(auxDate.size() == 1)
						{
							if(Queries.ClassPropertyValue(auxClass.get(0), auxProperty.get(0), auxDate.get(0)))
							{
								adapter.notifyDataSetChanged();
								return;
							}
						}
						else
						{
							if(Queries.ClassProperty(auxClass.get(0), auxProperty.get(0)))
							{
								adapter.notifyDataSetChanged();
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
								adapter.notifyDataSetChanged();
								return;
							}
						}
						else if(auxDate.size() == 2)
						{
							if(Queries.ClassReservedDateDate(auxClass.get(0), auxReserved.get(0), auxDate.get(0), auxDate.get(1)))
							{
								adapter.notifyDataSetChanged();
								return;
							}
						}
					}
					
					Queries.getClasse(auxClass.get(0));
					adapter.notifyDataSetChanged();
					return;
				}
				else if(auxClass.size() == 2)
				{
					list.clear();
					Queries.ClassClass(auxClass.get(0), auxClass.get(1));
					adapter.notifyDataSetChanged();
					return;
				}
				else
				{
					if(auxProperty.size() == 1)
					{
						
					}
					else
					{
						list.clear();
						for(int i = 0; i<auxUnknown.size(); i++)
						{
							if(Queries.unknownValue(auxUnknown.get(i)))
							{
								adapter.notifyDataSetChanged();
								return;
							}
						}
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
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    //EfficientAdapter, list of contacts/search
    public static class EfficientAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ContentResolver ctx;

		public EfficientAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
			ctx = context.getContentResolver();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			ArrayList<String> tmp = list.get(position);
			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.two_col_row, null);
				holder = new ViewHolder();
				holder.text2 = (TextView) convertView
						.findViewById(R.id.TextView02);
				holder.mark = (ImageView) convertView.findViewById(R.id.mark);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			String type = null;
			
			String write = "";
			
			for(int i = 0; i<tmp.size(); i = i + 2)
			{
				type = tmp.get(i);
				
				if(type.equals("type"))
				{
					if(tmp.get(i+1).equals("Sms"))
						holder.mark.setImageResource(R.drawable.msg);
					else if(tmp.get(i+1).equals("Call"))
						holder.mark.setImageResource(R.drawable.call);
					else if(tmp.get(i+1).equals("Event"))
						holder.mark.setImageResource(R.drawable.event);
					else if(tmp.get(i+1).equals("App"))
						holder.mark.setImageResource(R.drawable.app);
				}
				else if(type.equals("id"))
				{
					Bitmap image = loadContactPhoto
		            		(ctx, Long.parseLong(tmp.get(i+1)));
					
					if(image != null)
						holder.mark.setImageBitmap(image);
					else
						holder.mark.setImageResource(R.drawable.contact_icon);
				}
				else
				{
					write += tmp.get(i + 1)+"\n";
					
				}
			}
			holder.text2.setText(write);

			return convertView;
		}
		
		static class ViewHolder {
			ImageView mark;
			TextView text2;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}
		
		public static Bitmap loadContactPhoto(ContentResolver cr, long  id) 
		{
		    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
		    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
		    if (input == null) {
		        return null;
		    }
		    return BitmapFactory.decodeStream(input);
		}
    }
}
