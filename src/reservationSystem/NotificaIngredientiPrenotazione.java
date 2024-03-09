package reservationSystem;

import java.util.ArrayList;

public class NotificaIngredientiPrenotazione implements Osservatori {
	
	
	private FileOperation gestoreFile;
	public void setGestoreFile(FileOperation gestoreFile) {
		this.gestoreFile = gestoreFile;
	}




	private Boolean notify = false;
	private IterazioneUtente iUtente = new IterazioneUtente();
	public NotificaIngredientiPrenotazione(FileOperation gestoreFile) {
		this.gestoreFile = gestoreFile;
	}
	
	
	private  void notificaListaPrenotazioni() {
			System.out.println(MessaggiApplicazione.INTESTAZIONE+  MessaggiApplicazione.MESSAGGIO_ELENCO_ALIMENTI_PRENOTAZIONI);
			ListaSpesa listaPrenotazioni = gestoreFile.loadListaSpesa(SerializableFileOperation.FILE_LISTA_SPESA);
			iUtente.stampaListaSpesa(listaPrenotazioni);
			gestoreFile.storeSingleFile(SerializableFileOperation.FILE_LISTA_SPESA, new ListaSpesa());
			
		
	}
	
	private void riptistinaAlimentiListaPrenotazioni() {
		ListaSpesa listaPrenotazioni = gestoreFile.loadListaSpesa(SerializableFileOperation.FILE_LISTA_SPESA);
		ArrayList<Ingrediente> elencoIngredienti  = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_INGREDIENTI);
		ArrayList<Bevanda> elencoBevande = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_BEVANDE);
		ArrayList<GeneriAlimentari> elencoGeneri = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_GENERI_EXTRA);
		ListaSpesa listaTotale = new ListaSpesa(elencoIngredienti, elencoBevande, elencoGeneri);
		listaTotale.aggiungiElementiDaListaSpesa(listaPrenotazioni);
		gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_GENERI_EXTRA, listaTotale.getElencoGeneri());
		gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_BEVANDE, listaTotale.getElencoBevande());
		gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_INGREDIENTI, listaTotale.getElencoIngredienti());
		
	}
	
	public void checkPrenotazioni() {
		if(notify.booleanValue()) {
			this.riptistinaAlimentiListaPrenotazioni();
			this.notificaListaPrenotazioni();
			this.notify=false;
			
		}
	}
	

	@Override
	public void update(Object obj) {
		this.notify = (Boolean) obj;

	}

}
