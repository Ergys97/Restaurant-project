package reservationSystem;

import java.io.Serializable;
import java.time.LocalDate;

public class Alimento implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String nome;
	private LocalDate dataScadenza;
	private int quantita;
	private String unitaMisura = " ";
	
	public Alimento(String _nome, int _quantita) {
		UtilityTime ut = new UtilityTime();
		this.nome = _nome;
		this.quantita = _quantita;
		this.dataScadenza = ut.creaDataScadenzaCasuale();
		
	}
	
	public Alimento(String _nome,int _quantita, LocalDate date) {
		this.nome = _nome;
		this.quantita = _quantita;
		this.dataScadenza = date;
	}
	
	public String getNome() {
		return this.nome;
	}
	
	public int getQuantita() {
		return this.quantita;
	}
	
	public LocalDate getDataScadenza() {
		return this.dataScadenza;
	}
	
	public void setQuantita(int value) {
		this.quantita = value;
		
	}
	public void setDataScadenza(LocalDate nuovaData) {
		this.dataScadenza = nuovaData;
	}

	public String getUnitaMisura() {
		return unitaMisura;
	}

	public void setUnitaMisura(String unitaMisura) {
		this.unitaMisura = unitaMisura;
	}
	
	

}
