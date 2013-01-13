package com.workspace.Data;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

public class ChamadasInit 
{
	private Context ctx;
	
	public ChamadasInit(Context ctx)
	{
		this.ctx = ctx;
	}

	public void getChamadas()
	{
		String[] projection = new String[] {CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.TYPE ,CallLog.Calls.CACHED_NAME};
        Uri contacts =  CallLog.Calls.CONTENT_URI;
 
        Cursor managedCursor = this.ctx.getContentResolver().query(contacts, projection, null, null, CallLog.Calls.DATE + " DESC");
        getColumnData(managedCursor);
	}
	
	private void getColumnData(Cursor cur)
    {
        try
        {
           if (cur.moveToFirst()) 
           {
             String name;
             String number;
             String type;
             long date;
             int nameColumn = cur.getColumnIndex(CallLog.Calls.CACHED_NAME);
             int numberColumn = cur.getColumnIndex(CallLog.Calls.NUMBER);
             int dateColumn = cur.getColumnIndex(CallLog.Calls.DATE);
             int typeColumn = cur.getColumnIndex(CallLog.Calls.TYPE);
             
             Chamada chamada;
             Contato contato;
             
             int i = 0;
             
             do 
             {
	             name = cur.getString(nameColumn);
	             number = cur.getString(numberColumn);
	             date = cur.getLong(dateColumn);
	             type = cur.getString(typeColumn);
	             
	             contato = GlobalVariables.findContactByName(name);
	             
	             if(contato != null)
	             {
	            	 chamada = new Chamada(contato,number,new Date(date), Integer.parseInt(type));
		             
		             GlobalVariables.listaChamadas.add(chamada);
		             i++;
	             }
	             
            }
            while (cur.moveToNext());
          }
      }
      finally{
    	  cur.close();
      }
    }
}
