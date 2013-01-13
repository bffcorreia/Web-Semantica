package com.workspace.Data;

import java.util.Date;

public class Mensagem 
{
	private Contato thread;
	private Date data;
	private String body;
	
	public Mensagem(Contato thread, Date data, String body) 
	{
		this.thread = thread;
		this.data = data;
		this.body = body;
	}
	
	public Contato getThread() {
		return thread;
	}
	public void setThread(Contato thread) {
		this.thread = thread;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
