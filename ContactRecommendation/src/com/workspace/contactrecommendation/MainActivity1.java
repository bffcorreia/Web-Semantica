package com.workspace.contactrecommendation;

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
import android.os.Handler;
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

public class MainActivity1 extends Activity 
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
        
        Queries.getAllContacts();
        
        //new Recommendation();
        
        //Queries.getClasseByUri("http://www.owl-ontologies.com/Ontology1353891856.owl#com.workspace.Data.Contato@41027090", 0);
        
        //Queries.ClassReservedDate("Call", "after", "2011-12-10 00:00:00");
        
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
    
    private void addClickListeners() {
    	findButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) 
            {
            	new FindThread(find);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    //EfficientAdapter, list of contacts/search
    public static class EfficientAdapter extends BaseAdapter 
    {
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
