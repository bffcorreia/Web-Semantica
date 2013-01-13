package com.workspace.Data;

import java.util.List;

public class Contato {

	private String ID;
    private String Nome;
    private String Email;
    private List<Telefone> Telefones;
	
    public String getID() {
		return ID;
	}
	
    public void setID(String iD) {
		ID = iD;
	}
	
	public String getNome() {
		return Nome;
	}
	
	public void setNome(String nome) {
		Nome = nome;
	}
	
	public String getEmail() {
		return Email;
	}
	
	public void setEmail(String email) {
		Email = email;
	}
	
	public List<Telefone> getTelefones() {
		return Telefones;
	}
	
	public void setTelefones(List<Telefone> telefones) {
		Telefones = telefones;
	} 
}
