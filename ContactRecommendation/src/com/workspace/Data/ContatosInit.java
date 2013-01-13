package com.workspace.Data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Contacts.People;
import android.provider.ContactsContract;

public class ContatosInit {
	
	 private Context ctx;    
	 
     public ContatosInit(Context contexto)
     {
           this.ctx = contexto;          
     }    
     
     public void getContatos()
     {
	     Cursor C_Contatos = this.ctx.getContentResolver().query
	    		 (ContactsContract.Contacts.CONTENT_URI,null, null, null, ContactsContract.Contacts.DISPLAY_NAME);
	     
	     //index das colunnas
	     int IndexID = C_Contatos.getColumnIndex(ContactsContract.Contacts._ID);
	     int IndexTemTelefone = C_Contatos.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
	
	     int IndexName = C_Contatos.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
	
	     Contato contato;     
	  	 
	     while(C_Contatos.moveToNext())
		 {
	    	 //verifica se o contato tem telefone
		     if(Integer.parseInt(C_Contatos.getString(IndexTemTelefone))>0)
		     { 
			     contato = new Contato();          
			     contato.setID(C_Contatos.getString(IndexID));            
			     contato.setNome(C_Contatos.getString(IndexName));       
		    
		         TelefonesInit _Telefone = new TelefonesInit(contato.getID(), this.ctx);
		         contato.setTelefones(_Telefone.getTelefones());
		         
		         GlobalVariables.listaContatos.add(contato);
		     }          
		           
		 } 
		   
		 C_Contatos.close();          
     }
}
