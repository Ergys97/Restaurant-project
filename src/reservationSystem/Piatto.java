package reservationSystem;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import reservationUtility.*;


public class Piatto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Ricetta ricetta;
	private String nome;
	private LocalDate dataScadenza; 
	
	public Piatto( Ricetta ricetta, LocalDate data) {
		this.ricetta = ricetta;
		this.dataScadenza = data;
		this.nome = this.ricetta.getNome();	
		}

	public Ricetta getRicetta() {
		return ricetta;
	}

	public void setRicetta(Ricetta ricetta) {
		this.ricetta = ricetta;
	}

	public LocalDate getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(LocalDate data) {
		this.dataScadenza = data;
	}

	
	
	
	@Override
	public String toString() {
		return "[Piatto: " + nome+ "]"; 
	}
	
	public ArrayList<Ingrediente> elencoIngredienti(){
		return this.ricetta.getIngredienti();
	}
	
	public double caricoLavoro() {
		return this.ricetta.getCaricoLavoroPorzione();
	}
	
	
	
	public boolean isDisponibile() {
		
		if(this.dataScadenza.isAfter(LocalDate.now()) || this.dataScadenza.isEqual(LocalDate.now()))
			return true;
		return false;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
		
		
		
	}
	


