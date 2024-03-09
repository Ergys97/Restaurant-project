package reservationSystem;

import java.util.ArrayList;


public class GestionePrenotazione implements Osservabile  {
	Prenotazione myPrenotazione;
	ArrayList<Prenotazione> elencoPrenotazioni;
	ArrayList<Osservatori> elencoOsservatori = new ArrayList<>();
	public GestionePrenotazione( ArrayList<Prenotazione> elencoPrenotazioni) {
		this.elencoPrenotazioni = elencoPrenotazioni;
	}
	public  boolean checkPrenotazione (Prenotazione prenotazione) {
		if(prenotazione == null  || !prenotazione.conformeAlCaricoLavoro(this.elencoPrenotazioni)) {
			return false;
		}
		else {
			return true;
		}
		
	}
	public void aggiungiOsservatore (Osservatori osservatore) {
		this.elencoOsservatori.add(osservatore);
	}
	
	public void rimuoviOsseratore (Osservatori osservatore) {
		this.elencoOsservatori.remove(osservatore);
	}
	
	
	public void valutaPrenotazione (Prenotazione prenotazione) {
		if(checkPrenotazione(prenotazione)) {
		for (Osservatori osservatori : elencoOsservatori) {
			osservatori.update(prenotazione);
		}
		}
	}
	public void setElencoPrenotazioni(ArrayList<Prenotazione> elencoPrenotazioni) {
		this.elencoPrenotazioni = elencoPrenotazioni;
	}
	

}
