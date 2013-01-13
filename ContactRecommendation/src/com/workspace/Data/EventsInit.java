package com.workspace.Data;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class EventsInit 
{
	private Context ctx;
	
	public EventsInit(Context ctx)
	{
		this.ctx = ctx;
	}
	
	public void getCalendarEvents() 
	{
	    Cursor cursor = this.ctx.getContentResolver()
	            .query(
	                    Uri.parse("content://com.android.calendar/events"),
	                    new String[] { "calendar_id", "title", "description",
	                            "dtstart", "dtend", "eventLocation" }, null,
	                    null, null);
	    cursor.moveToFirst();
	    
	    Event event;

	    for(int i = 0; i < cursor.getCount(); i++) 
	    {
	    	event = new Event(cursor.getString(1), new Date(Long.parseLong(cursor.getString(3))),
	    			(cursor.getString(4) != null) ? new Date(Long.parseLong(cursor.getString(4))) : null);
	    	
	        cursor.moveToNext();
	        
	        GlobalVariables.listaEventos.add(event);
	    }
	}
}
