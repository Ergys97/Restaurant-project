package reservationSystem;


import java.util.ArrayList;
import reservationUtility.*;


public class Main {
	
	
	private static final String INTRO = "---Ristorante di Ergys & Enrico---";
	private static final String BENVENUTO = "Benvenuto nella gestione del ristorante! \nScegliere l'utente: ";
	private static final String[] OPZIONI = {"Gestore","Addetto delle prenotazioni", "Magazziniere"};
	
	private static final String MENU_GESTORE = "Menu gestore";
	private static final String[] OPZIONIGESTORE = {"Visualizza struttura ristorante","Crea e visualizza ingredienti", "Crea e visualizza ricetta", 
													"Crea e visualizza piatto", "Crea e visualizza menu tematici", "Visualizza menu alla carta","Azzera dati Ristorante"};
	
	private static final String MENU_ADDETTO = "Menu addetto delle prenotazioni";
	private static final String[] OPZIONIADDETTO = {"Raccogli prenotazioni", "Visualizza prenotazioni"};
	
	private static final String MENU_MAGAZZINIERE = "Menu magazziniere";
	private static final String[] OPZIONIMAGAZZINIERE = {"Visualizza elementi nel magazzino", "Elabora lista della spesa"};
	
	
	public static void main(String[] args) {
		
		avvio();
		System.out.println("\nProgramma chiuso correttamente!");
		
		
	}
	
	
public static void avvio() {
		
		System.out.println(INTRO);
		MyMenu menu = new MyMenu(BENVENUTO, OPZIONI);
		NotificaIngredientiPrenotazione notificaPrenotazioni = new NotificaIngredientiPrenotazione(new SerializableFileOperation());
		GestioneElencoPrenotazioni gestioneElencoPrenotaioni = new  GestioneElencoPrenotazioni(new SerializableFileOperation());
		gestioneElencoPrenotaioni.aggiungiOsservatore(notificaPrenotazioni);
		int scelta;
		
		do {
			
			
			scelta = menu.scegli();		
			
			switch (scelta) {
			
			case 1: //utente = gestore
				MyMenu menuGestore = new MyMenu(MENU_GESTORE, OPZIONIGESTORE);
				gestioneRistorante(menuGestore);
				
				break;
		
			case 2: //utente = addetto prenotazioni
				MyMenu menuAddetto = new MyMenu(MENU_ADDETTO, OPZIONIADDETTO);
				gestionePrenotazioni(menuAddetto, gestioneElencoPrenotaioni);
				
				break;
				
				
			
			case 3: //utente = magazziniere
				MyMenu menuMagazziniere = new MyMenu(MENU_MAGAZZINIERE, OPZIONIMAGAZZINIERE);
				gestioneMagazzino(menuMagazziniere, notificaPrenotazioni);
				
				break;
			
			
			
			}
			
		}while(scelta!=0);
		
	}

	
	public static void gestioneRistorante(MyMenu menu) {
		IterazioneUtente iUtente = new IterazioneUtente();
		SerializableFileOperation gestoreFile = new SerializableFileOperation();
		UtilityElenchi uElenchi = new UtilityElenchi();
		
		
		StrutturaRistorante base = (StrutturaRistorante) gestoreFile.loadStrutturaRistorante(SerializableFileOperation.FILE_STRUTTURA_RISTORANTE);

		
		if(base == null || !base.isInizializzato()) {
		
			base = iUtente.creaStrutturaRistorante();
			
			gestoreFile.storeSingleFile(SerializableFileOperation.FILE_STRUTTURA_RISTORANTE, base);
			gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_INGREDIENTI, base.getIngredienti());
			gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_BEVANDE, base.getBevande());
			gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_GENERI_EXTRA, base.getAlimentiExtra());
				
		}
			
		int scelta;

		
		do {
			
			scelta=menu.scegli();
			
			switch(scelta) {
			
			case 1:   //inizializza dati ristorante, quanto inizializzato non può più essere modificato!!
				
				StrutturaRistorante strutturaRistorante = (StrutturaRistorante) gestoreFile.loadSingleFile(SerializableFileOperation.FILE_STRUTTURA_RISTORANTE);
				iUtente.stampaValoriRistorante(strutturaRistorante);
				
				break;
				
			case 2:	  //aggiungi e visualizza ingredienti
				
			ArrayList<Ingrediente> ingredienti = (ArrayList<Ingrediente>) gestoreFile.loadSingleFile(SerializableFileOperation.FILE_INGREDIENTI);
			 strutturaRistorante = (StrutturaRistorante) gestoreFile.loadSingleFile(SerializableFileOperation.FILE_STRUTTURA_RISTORANTE);	
			 iUtente.stampaAlimento(ingredienti);	
			 ArrayList<Ingrediente> mIngredienti = iUtente.aggiungiIngredienti(ingredienti, strutturaRistorante.isInizializzato());
			gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_INGREDIENTI, mIngredienti);
				break;
				
			case 3:   //crea ricetta e visualizza ricetta
				ArrayList<Ricetta>   ricette = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_RICETTE);
				ingredienti = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_INGREDIENTI);
				strutturaRistorante = (StrutturaRistorante) gestoreFile.loadSingleFile(SerializableFileOperation.FILE_STRUTTURA_RISTORANTE);
				iUtente.stampaRicette(ricette);	
				ArrayList<Ricetta> newRicette = iUtente.aggiungiRicette(ricette,ingredienti,strutturaRistorante.isInizializzato(),strutturaRistorante.getCaricoLavoroXPersona());
				gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_RICETTE, newRicette);	
				
				
				
				
					
				break;
				
				
			case 4:   // crea piatto e visualizzalo

				
				ArrayList<Piatto> piatti = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_PIATTI);
				ArrayList<Piatto>   piattiDisponibili = uElenchi.piattiDisponibili(piatti);
				ricette = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_RICETTE);
				ingredienti = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_INGREDIENTI);
				 strutturaRistorante = (StrutturaRistorante) gestoreFile.loadSingleFile(SerializableFileOperation.FILE_STRUTTURA_RISTORANTE);
				iUtente.stampaPiatti(piatti);
				
				ArrayList<Piatto> piattiNuovi=	iUtente.aggiungiPiatto(ricette, piatti, ingredienti, strutturaRistorante.isInizializzato());
				 gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_PIATTI, piattiNuovi);
				
			
				
				break;
				
						
			case 5:  // crea menu tematico
				
				
				ArrayList<MenuTematico> listaMenu = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_MENU_TEMATICI);
				 piatti = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_PIATTI);
				 piattiDisponibili = uElenchi.piattiDisponibili(piatti);
				 strutturaRistorante = (StrutturaRistorante) gestoreFile.loadSingleFile(SerializableFileOperation.FILE_STRUTTURA_RISTORANTE);
				iUtente.stampaMenu(listaMenu);
				
				 ArrayList<MenuTematico> newListaMenu =   iUtente.aggiungiMenuTematico(listaMenu,piattiDisponibili,strutturaRistorante.isInizializzato(),strutturaRistorante.getCaricoLavoroXPersona());
				gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_MENU_TEMATICI, newListaMenu);
				
				break;
				
				
			case 6: //visualizza menu alla carta
				
				 piatti = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_PIATTI);
				 piattiDisponibili = uElenchi.piattiDisponibili(piatti);
				 iUtente.stampaPiatti(piattiDisponibili);
				break;
			
			
			
			case 7: 
				
				
				gestoreFile.storeSingleFile(SerializableFileOperation.FILE_STRUTTURA_RISTORANTE, new StrutturaRistorante());
				gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_BEVANDE, new ArrayList<Bevanda>());
				gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_INGREDIENTI, new ArrayList<Ingrediente>());
				gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_GENERI_EXTRA, new ArrayList<GeneriAlimentari>());
				gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_RICETTE, new ArrayList<Ricetta>());
				gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_MENU_TEMATICI, new ArrayList<MenuTematico>());
				gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_PIATTI, new ArrayList<Piatto>());
				gestoreFile.storeMultipleFile(SerializableFileOperation.FILE_PRENOTAZIONI, new ArrayList<Prenotazione>());
				gestoreFile.storeSingleFile(SerializableFileOperation.FILE_LISTA_SPESA, new ListaSpesa());
				
				
			break;
			
			}
			
		
		}while(scelta!=0);
		
		
	}

			
	

	
	public static void gestionePrenotazioni(MyMenu menu, GestioneElencoPrenotazioni gestioneElencoPrenotazioni) {
		
		SerializableFileOperation gestoreFile = new SerializableFileOperation();
		gestioneElencoPrenotazioni.setGestoreFile(gestoreFile);
		ArrayList<Prenotazione> elencoPrenotazioni = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_PRENOTAZIONI);
		StrutturaRistorante strutturaRistorante =  gestoreFile.loadStrutturaRistorante(SerializableFileOperation.FILE_STRUTTURA_RISTORANTE);
		EliminaAlimentiPrenotazione eliminaAlimenti = new EliminaAlimentiPrenotazione(gestoreFile, strutturaRistorante, SerializableFileOperation.FILE_INGREDIENTI, SerializableFileOperation.FILE_BEVANDE, SerializableFileOperation.FILE_GENERI_EXTRA);
		GestionePrenotazione gestionePrenotazione = new GestionePrenotazione(elencoPrenotazioni);
		PrenotazioneToListaSpesa prenotazioneToLista = new PrenotazioneToListaSpesa(strutturaRistorante, gestoreFile);
		gestionePrenotazione.aggiungiOsservatore(eliminaAlimenti);
		gestionePrenotazione.aggiungiOsservatore(gestioneElencoPrenotazioni);
		gestionePrenotazione.aggiungiOsservatore(prenotazioneToLista);
		ArrayList<Piatto> elencoPiatti = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_PIATTI);
		ArrayList<MenuTematico> elencoMenu = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_MENU_TEMATICI);
		
		
		IterazioneUtente iUtente = new IterazioneUtente();
		//Utility uTility = new Utility();
		
		int scelta;
		
		do {
			scelta=menu.scegli();
			
			switch(scelta) {
			
			case 1: //raccogli prenotazioni
				
				
				elencoPrenotazioni = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_PRENOTAZIONI);
				gestionePrenotazione.setElencoPrenotazioni(elencoPrenotazioni);
				Prenotazione nuovaPrenotazione = iUtente.creaPrenotazione(strutturaRistorante.getNumPostiASedere(), strutturaRistorante.getCaricoLavoroSostenibile(), elencoMenu, elencoPiatti, elencoPrenotazioni);
				gestionePrenotazione.valutaPrenotazione(nuovaPrenotazione);
				
				
				
				
				break;
				
			
			case 2: // visualizza prenotazioni
				
				ArrayList<Prenotazione> myPrenotazioni = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_PRENOTAZIONI);
				iUtente.stampaPrenotazioni(myPrenotazioni);
				break;
			
			}
			
			
			
			
		}while(scelta!=0);
		
		
	}
	
	public static void gestioneMagazzino(MyMenu menu, NotificaIngredientiPrenotazione notificaPrenotaione) {
		SerializableFileOperation gestoreFile = new SerializableFileOperation();
		notificaPrenotaione.setGestoreFile(gestoreFile);
		IterazioneUtente iUtente = new IterazioneUtente();
		
		int scelta;
		
		do {
			notificaPrenotaione.checkPrenotazioni();
			scelta=menu.scegli();
			
			switch(scelta) {
			
			case 1: //visualizza ingredienti magazzino
			ArrayList<Ingrediente> ingredienti = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_INGREDIENTI);
			ArrayList<Bevanda> bevande = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_BEVANDE);
			ArrayList<GeneriAlimentari> generi = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_GENERI_EXTRA);
			ListaSpesa lista = new ListaSpesa(ingredienti, bevande, generi);
			iUtente.stampaListaSpesa(lista);
				
				
			
				
				
				break;
				
			
			case 2: //elabora lista spesa
				 ingredienti = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_INGREDIENTI);
				 bevande = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_BEVANDE);
				 generi = gestoreFile.loadMultipleFile(SerializableFileOperation.FILE_GENERI_EXTRA);
				 lista = new ListaSpesa(ingredienti, bevande, generi);
				
				
				RegistroMagazzino registroMagazzino = new RegistroMagazzino(lista, gestoreFile);
				registroMagazzino.ripristinoAlimenti(SerializableFileOperation.FILE_INGREDIENTI, SerializableFileOperation.FILE_BEVANDE, SerializableFileOperation.FILE_GENERI_EXTRA);
				
				break;
			
			}
			
			
			
		}while(scelta!=0);
		
		
	}

	

}
