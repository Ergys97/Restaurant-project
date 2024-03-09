package reservationSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;


public class EliminaAlimentiPrenotazione implements Osservatori {
	private Prenotazione prenotazione;
	private FileOperation gestoreFile;
	private File fileIngredienti;
	private File fileBevande;
	private File fileGeneri;

	private StrutturaRistorante strutturaRistorante;
	
	public EliminaAlimentiPrenotazione ( FileOperation _gestoreFile, StrutturaRistorante st, File ingredienti, File bevande, File generi) {
		this.gestoreFile = _gestoreFile;
		this.strutturaRistorante = st;
		this.fileGeneri = generi;
		this.fileBevande = bevande;
		this.fileIngredienti = ingredienti;

		
	}
	
	public EliminaAlimentiPrenotazione (Prenotazione prenotazione,FileOperation _gestoreFile, StrutturaRistorante st, File ingredienti, File bevande, File generi) {
		this.gestoreFile = _gestoreFile;
		this.strutturaRistorante = st;
		this.fileGeneri = generi;
		this.fileBevande = bevande;
		this.fileIngredienti = ingredienti;
		this.prenotazione = prenotazione;
	}
	
	public ArrayList<Ingrediente> diminuisciIngredientiPiatti(ArrayList<Ingrediente> elencoIngredienti)
	{
		UtilityElenchi uElenchi = new UtilityElenchi();
		for (Map.Entry<Piatto, Integer> entry : prenotazione.getPiattiPrenotati().entrySet()) {
					for (Ingrediente ingredientePiatto : entry.getKey().elencoIngredienti()) {
							 	if(uElenchi.contieneIngrediente(elencoIngredienti,ingredientePiatto)) {
							 		Ingrediente ingredienteEstratto = uElenchi.estraiIngrediente(elencoIngredienti, ingredientePiatto);
							 		elencoIngredienti.remove(ingredienteEstratto);
							 		int oldValue = ingredienteEstratto.getQuantita();
							 		int newValue = oldValue - entry.getValue();
							 		ingredienteEstratto.setQuantita(newValue);
							 		elencoIngredienti.add(ingredienteEstratto);
					}
			}
					
		}
			
		
		return elencoIngredienti;
		
		
			
			
		}
	
	
	
	
	
	public ArrayList<Ingrediente> diminuisciIngredientiMenuTematici (ArrayList<Ingrediente> elencoIngredienti){
		UtilityElenchi uElenchi = new UtilityElenchi();
		
		for (Map.Entry<MenuTematico, Integer> entry : this.prenotazione.getMenuPrenotati().entrySet()) {
				for (Piatto PiattoMenu : entry.getKey().getPiatti()) {
					 for (Ingrediente ingredientePiatto : PiattoMenu.elencoIngredienti()) {
							 	if(uElenchi.contieneIngrediente(elencoIngredienti,ingredientePiatto)) {
							 		Ingrediente ingredienteEstratto = uElenchi.estraiIngrediente(elencoIngredienti, ingredientePiatto);
							 		elencoIngredienti.remove(ingredienteEstratto);
							 		int oldValue = ingredienteEstratto.getQuantita();
							 		int newValue = oldValue - entry.getValue();
							 		ingredienteEstratto.setQuantita(newValue);
							 		elencoIngredienti.add(ingredienteEstratto);
					}
							
						}
						
					}
				}
			
			
		
		
		return elencoIngredienti;
		
	}
	
	
	public ArrayList<Bevanda> diminuisciQuantitaBevande(ArrayList<Bevanda> elencoBevande){
		UtilityElenchi uElenchi = new UtilityElenchi();
		
			for (Map.Entry<Bevanda, Integer> kv : this.strutturaRistorante.getConsumoProCapiteBevande().entrySet()) {
					if(uElenchi.contieneBevanda(elencoBevande, kv.getKey())) {
						Bevanda bevandaEstratta = uElenchi.estraiBevanda(elencoBevande, kv.getKey());
						elencoBevande.remove(bevandaEstratta);
						int oldValue = bevandaEstratta.getQuantita();
						int newValue = oldValue - (kv.getValue() * this.prenotazione.getNumCoperti());
						bevandaEstratta.setQuantita(newValue);
						elencoBevande.add(bevandaEstratta);	
				}
				
			}
			return elencoBevande;
			
		}
	
	public ArrayList<GeneriAlimentari> diminuisciQuantitaGeneriAlimentari(ArrayList<GeneriAlimentari> elencoGeneri){
		UtilityElenchi uElenchi = new UtilityElenchi();
		for (Map.Entry<GeneriAlimentari, Integer> kv : this.strutturaRistorante.getConsumoProCapiteAlimentiExtra().entrySet()) {
				if(uElenchi.contieneGenere(elencoGeneri, kv.getKey())) {
					GeneriAlimentari genereEstratto = uElenchi.estraiGenere(elencoGeneri, kv.getKey());
					int oldValue = genereEstratto.getQuantita();
					int newValue = oldValue -(kv.getValue() * this.prenotazione.getNumCoperti());
					elencoGeneri.remove(genereEstratto);
					genereEstratto.setQuantita(newValue);
					elencoGeneri.add(genereEstratto);
				
			}
			
		}
		return elencoGeneri;
	}
	
	private ArrayList<Ingrediente> diminuisciIngredientiPrenotazione(ArrayList<Ingrediente> elencoIngredienti){
		ArrayList<Ingrediente> ingredientiPiatti = this.diminuisciIngredientiPiatti(elencoIngredienti);
		ArrayList<Ingrediente> ingredientiTotali = this.diminuisciIngredientiMenuTematici(ingredientiPiatti);
		return ingredientiTotali;
	}
	
	public void diminuisciAlimentiPrenotazione() {
		ArrayList<Ingrediente> elencoIngredienti = this.gestoreFile.loadMultipleFile(fileIngredienti);
		ArrayList<Bevanda> elencoBevande = this.gestoreFile.loadMultipleFile(fileBevande);
		ArrayList<GeneriAlimentari> elencoGeneri = this.gestoreFile.loadMultipleFile(fileGeneri);
		ArrayList<Ingrediente> elencoIngredientiModificato = this.diminuisciIngredientiPrenotazione(elencoIngredienti);
		ArrayList<Bevanda> elencoBevandeModificato = this.diminuisciQuantitaBevande(elencoBevande);
		ArrayList<GeneriAlimentari> elencoGeneriModificato = this.diminuisciQuantitaGeneriAlimentari(elencoGeneri);
		this.gestoreFile.storeMultipleFile(fileIngredienti, elencoIngredientiModificato);
		this.gestoreFile.storeMultipleFile(fileBevande, elencoBevandeModificato);
		this.gestoreFile.storeMultipleFile(fileGeneri, elencoGeneriModificato);
	}

	@Override
	public void update(Object obj) {
		this.prenotazione = (Prenotazione) obj;
		diminuisciAlimentiPrenotazione();
		
		
	}

	
}