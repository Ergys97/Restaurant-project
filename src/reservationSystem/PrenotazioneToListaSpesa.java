package reservationSystem;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class PrenotazioneToListaSpesa implements Osservatori {
	private Prenotazione prenotazione;
	private StrutturaRistorante strutturaRistorante;
	private FileOperation gestoreFile;
	public PrenotazioneToListaSpesa( StrutturaRistorante _strutturaRistorante, FileOperation gestoreFile) {
		this.strutturaRistorante =_strutturaRistorante;
		this.gestoreFile = gestoreFile;
	}
	
	public PrenotazioneToListaSpesa(StrutturaRistorante _strutturaRistorante, FileOperation gestoreFile, Prenotazione prenotazione) {
		this.strutturaRistorante =_strutturaRistorante;
		this.gestoreFile = gestoreFile;
		this.prenotazione = prenotazione;
	}
	
	private ArrayList<Bevanda> bevandePrenotate(){
		ArrayList<Bevanda> bevandeTotali = new ArrayList<>();
		for (Entry<Bevanda, Integer> kv : strutturaRistorante.getConsumoProCapiteBevande().entrySet()) {
			Bevanda bevandaSelezionata = kv.getKey();
			int quantita = kv.getValue() * this.prenotazione.getNumCoperti();
			bevandaSelezionata.setQuantita(quantita);
			bevandeTotali.add(bevandaSelezionata);
		}
		return bevandeTotali;
	}
	
	private ArrayList<GeneriAlimentari> generiPrenotati(){
		ArrayList<GeneriAlimentari> generiTotali = new ArrayList<>();
		for (Entry<GeneriAlimentari, Integer> kv : this.strutturaRistorante.getConsumoProCapiteAlimentiExtra().entrySet() ) {
			
			GeneriAlimentari genereSelezionato = kv.getKey();
			int quantita = kv.getValue() * this.prenotazione.getNumCoperti();
			genereSelezionato.setQuantita(quantita);
			generiTotali.add(genereSelezionato);
			
			
		}
		return generiTotali;
	}
	
	private ArrayList<Ingrediente> ingredientiPrenotati(){
		UtilityElenchi uElenchi = new UtilityElenchi();
		ArrayList<Ingrediente> ingredientiTotali = uElenchi.unisciElenchiIngredienti(this.ingredientiPiatti(), this.ingredientiMenu());
		return ingredientiTotali;
		
	}
	
	private ArrayList<Ingrediente> ingredientiPiatti(){
		UtilityElenchi uElenchi = new UtilityElenchi();
		 ArrayList<Ingrediente> ingredientiPrenotazione = new ArrayList<>();
			for (Map.Entry<Piatto, Integer> kv : this.prenotazione.getPiattiPrenotati().entrySet()) {
					for (Ingrediente ingrediente : kv.getKey().elencoIngredienti()) {
						if(uElenchi.contieneIngrediente(ingredientiPrenotazione, ingrediente)) {
							Ingrediente estratto = uElenchi.estraiIngrediente(ingredientiPrenotazione, ingrediente);
							int oldValue = estratto.getQuantita();
							ingredientiPrenotazione.remove(estratto);
							int newValue = oldValue + kv.getValue();
							estratto.setQuantita(newValue);
							ingredientiPrenotazione.add(estratto);
						}
						else {
							ingrediente.setQuantita(kv.getValue());
							ingredientiPrenotazione.add(ingrediente);
						}
						
					}
				
			}
			
			return ingredientiPrenotazione;
	}
	private ArrayList<Ingrediente> ingredientiMenu(){
			UtilityElenchi uElenchi = new UtilityElenchi();
			ArrayList<Ingrediente> ingredientiPrenotazione = new ArrayList<>();
			for (Map.Entry<MenuTematico, Integer> kv : this.prenotazione.getMenuPrenotati().entrySet()) {
					for (Piatto piatto : kv.getKey().getPiatti()) {
						for (Ingrediente ingrediente : piatto.elencoIngredienti()) {
							if(uElenchi.contieneIngrediente(ingredientiPrenotazione, ingrediente)) {
								Ingrediente estratto = uElenchi.estraiIngrediente(ingredientiPrenotazione, ingrediente);
								int oldValue = estratto.getQuantita();
								ingredientiPrenotazione.remove(estratto);
								int newValue = oldValue + kv.getValue();
								estratto.setQuantita(newValue);
								ingredientiPrenotazione.add(estratto);
								
							}
							else {
								ingrediente.setQuantita(kv.getValue());
								ingredientiPrenotazione.add(ingrediente);
							}
							
						}
						
					}
				
			}
			return ingredientiPrenotazione;
			
		
	}
	
	public ListaSpesa esegui () {
		ArrayList<Ingrediente> ingredienti = this.ingredientiPrenotati();
		ArrayList<Bevanda> bevande = this.bevandePrenotate();
		ArrayList<GeneriAlimentari> generi = this.generiPrenotati();
		return new ListaSpesa(ingredienti,bevande,generi);
		
	}

	@Override
	public void update(Object obj) {
		this.prenotazione = (Prenotazione) obj;
		ListaSpesa listaPrenotazione = this.esegui();
		ListaSpesa listaSalvata = gestoreFile.loadListaSpesa(SerializableFileOperation.FILE_LISTA_SPESA);
		listaSalvata.aggiungiElementiDaListaSpesa(listaPrenotazione);
		gestoreFile.storeSingleFile(SerializableFileOperation.FILE_LISTA_SPESA, listaSalvata);
	}
	
	
	
	
	
	

}
