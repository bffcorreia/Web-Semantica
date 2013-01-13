package com.workspace.Data;

import java.util.Date;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import android.content.Context;

public class Teste {

	private Context ctx;
    
    public Teste(Context ctx)
	{
		this.ctx = ctx;
	}
    
    public Teste() 
    {
		
    	/*Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        try 
        {
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "bffcorreia@gmail.com", "gmailBruno1722*");
            Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_ONLY);
            
            Message message[] = inbox.getMessages();

            for (int i = 0; i < 25; i++) 
            {
                System.out.println("From:"+ message[i].getFrom()[0] + "");
                System.out.println("Subject: "+ message[i].getSubject() + "");
                String content = null;

                
                try 
                {
                	System.out.println("Coiso");
                	if (message[i].isMimeType("text/plain"))
                    	message[i].getContent();
					System.out.println("Coiso1");
					System.out.println("content: "+content + "");
				} 
                catch (IOException e)
                {
					// TODO Auto-generated catch block
					System.out.println(e);
				}
            }
        } 
        catch (MessagingException e) {
            e.printStackTrace();
        }*/
    	
    	Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		
		try {
			Session session = Session.getDefaultInstance(props, null);
			Store store = session.getStore("imaps");

			store.connect("imap.gmail.com", "bffcorreia", "gmailBruno1722*");
					
			Folder inbox = store.getFolder("Inbox");
			inbox.open(Folder.READ_ONLY);

			FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.RECENT), false);

			Message[] messages = inbox.search(ft);

			for (Message message : messages) {
				String contact = message.getFrom()[0].toString();
				Date datetime = message.getReceivedDate();
				String subject = message.getSubject();
				String body = null;
				if (message.isMimeType("text/plain"))
                	body = message.getContent().toString();
				 
				System.out.println("Body: "+body);
				
				//Email novo = new Email(datetime, searchMailContacto(contact), eu_mail, body, subject, anexo);
				//mailsList.add(novo);
			}
			store.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
