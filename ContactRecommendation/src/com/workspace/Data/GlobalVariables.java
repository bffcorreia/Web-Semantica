package com.workspace.Data;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class GlobalVariables 
{	
	static public final List<Contato> listaContatos = new ArrayList<Contato>();
	static public final List<Chamada> listaChamadas = new ArrayList<Chamada>();
	static public final List<Mensagem> listaMensagens = new ArrayList<Mensagem>();
	static public final List<Event> listaEventos = new ArrayList<Event>();
	static public final List<Application> listaApplications = new ArrayList<Application>();  
	static public List<String> findString;
	
	public static Model model;
	
	static Contato findContactByName(String name)
	{
		for(int i = 0; i<listaContatos.size(); i++)
		{
			if(listaContatos.get(i).getNome().equals(name))
				return listaContatos.get(i);
		}
		
		return null;
	}

	static Contato findContactByNumber(String number)
	{
		String aux;
		for(int i = 0; i<listaContatos.size(); i++)
		{
			for(int j = 0; j<listaContatos.get(i).getTelefones().size(); j++)
			{
				aux = listaContatos.get(i).getTelefones().get(j).getTelefone();
				
				if(aux.equals(number))
					return listaContatos.get(i);
				else if(number.length() > aux.length())
				{
					if(number.substring(4, number.length()).equals(aux))
						return listaContatos.get(i);
				}
				else if(number.length() < aux.length())
				{
					if(aux.subSequence(4,  aux.length()).equals(number))
						return listaContatos.get(i);
				}
			}
		}
		
		return null;
	}
	
	public static List<Chamada> findCallsByContact(Contato c)
	{
		List<Chamada> aux = new ArrayList<Chamada>();
		
		for(int i = 0; i<listaChamadas.size(); i++)
		{
			if(listaChamadas.get(i).getContato().equals(c))
				aux.add(listaChamadas.get(i));
		}
		
		return aux;
	}
	
	public static List<Mensagem> findSMSByContact(Contato c)
	{
		List<Mensagem> aux = new ArrayList<Mensagem>();
		
		for(int i = 0; i<listaMensagens.size(); i++)
		{
			if(listaMensagens.get(i).getThread().equals(c))
				aux.add(listaMensagens.get(i));
		}
		
		return aux;
	}
}
