package com.workspace.Data;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class MensagensInit 
{
	private Context ctx;

	public MensagensInit(Context ctx) {
		this.ctx = ctx;
	}
	
	public void getMensagens()
	{
		Uri uri = Uri.parse("content://sms");
		Cursor c = this.ctx.getContentResolver().query(uri, null, null ,null,null);
		
		long data;
		String number; 
		String body;
		
		Mensagem msg;
		Contato cont;
		
		if(c.moveToFirst()){
	         for(int i=0;i<c.getCount();i++)
	         {
	        	 data= c.getLong(c.getColumnIndexOrThrow("date"));
	        	 
		         number=c.getString(c.getColumnIndexOrThrow("address"));
		         
		         body=c.getString(c.getColumnIndexOrThrow("body"));
		         
		         //System.out.println("number: "+number);
		         
		         if(number!=null)
		         {
			         cont = GlobalVariables.findContactByNumber(number);
			         
			         if(cont != null)
			         {
				         msg = new Mensagem(cont, new Date(data), body);
				         
				         GlobalVariables.listaMensagens.add(msg);
			         }
		         }
		         
		         c.moveToNext();
	       }
		}
		c.close();
	}
}
