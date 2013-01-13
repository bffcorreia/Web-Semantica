package com.workspace.Data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class TelefonesInit {

	private String _IDContato;
	 
    private Context _ctx;

    public TelefonesInit(String IDContato, Context Contexto)
    {
          this._IDContato = IDContato;
          this._ctx = Contexto;          
    }

    public List<Telefone> getTelefones()
    {
          Cursor C_Telefones =this._ctx.getContentResolver().query
        		  (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + _IDContato, null, null);

          int IndexTelefone;

          List<Telefone> Telefones = new ArrayList<Telefone>();    

          while(C_Telefones.moveToNext())
          {
                Telefone Telefone = new Telefone();                

                IndexTelefone = C_Telefones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);                

                Telefone.setTelefone(C_Telefones.getString(IndexTelefone)); 

                Telefones.add(Telefone);

          }          

          C_Telefones.close();

     return Telefones;

    }
}
