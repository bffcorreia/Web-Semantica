package com.workspace.Data;

import java.util.Date;

public class Chamada 
{
	private Contato contato;
	private String telefone;
	private Date data;
	private int type;

	public Chamada(Contato contato, String telefone, Date data, int type) 
	{
		this.contato = contato;
		this.telefone = telefone;
		this.data = data;
		this.type = type;
	}
	
	public Contato getContato() {
		return contato;
	}
	public void setContato(Contato contato) {
		this.contato = contato;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
