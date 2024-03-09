package reservationSystem;

import java.time.LocalDate;
import java.util.ArrayList;

public class GestioneElencoPrenotazioni implements Osservatori, Osservabile {
	private Prenotazione myPrenotaione;
	private FileOperation gestoreFile;
	
	public void setGestoreFile(FileOperation gestoreFile) {
		this.gestoreFile = gestoreFile;
	}
	ArrayList<Osservatori> elencoOsservatori = new ArrayList<>();
	
	
	public GestioneElencoPrenotazioni(FileOperation gestoreFile) {
		this.gestoreFile = gestoreFile;
	}
	private void salvaPrenotaione() {
	ArrayList<Prenotazione> elencoPrenotazioni = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_PRENOTAZIONI);
	elencoPrenotazioni.add(myPrenotaione);
	gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_PRENOTAZIONI, elencoPrenotazioni);
	}
	
	public void eliminaPrenotazioniPassate(){
		ArrayList<Prenotazione> elencoPrenotazioni = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_PRENOTAZIONI);
		for (Prenotazione prenotazione : elencoPrenotazioni) {
				if(prenotazione.getData().isBefore(LocalDate.now()))
					elencoPrenotazioni.remove(prenotazione);
		}
		gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_PRENOTAZIONI, elencoPrenotazioni);
		
	}
	
	public void eliminaPrenotazioniPassate(ArrayList<Prenotazione> elencoPrenotazioni){
		//ArrayList<Prenotazione> elencoPrenotazioni = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_PRENOTAZIONI);
		for (Prenotazione prenotazione : elencoPrenotazioni) {
				if(prenotazione.getData().isBefore(LocalDate.now()))
					elencoPrenotazioni.remove(prenotazione);
		}
		//gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_PRENOTAZIONI, elencoPrenotazioni);
		
	}
	
	@Override
	public void update(Object obj) {
		myPrenotaione = (Prenotazione) obj;
		this.eliminaPrenotazioniPassate();
		this.salvaPrenotaione();
		for (Osservatori osservatori : elencoOsservatori) {
			osservatori.update(true);
		}
		
	}
	@Override
	public void aggiungiOsservatore(Osservatori osservatore) {
		elencoOsservatori.add(osservatore);
		
	}
	@Override
	public void rimuoviOsseratore(Osservatori osservatore) {
		elencoOsservatori.remove(osservatore);
		
	}

}
